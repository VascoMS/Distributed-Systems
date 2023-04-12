package pt.tecnico.distledger.server.domain;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import pt.tecnico.distledger.server.domain.operation.CreateOp;
import pt.tecnico.distledger.server.domain.operation.DeleteOp;
import pt.tecnico.distledger.server.domain.operation.Operation;
import pt.tecnico.distledger.server.domain.operation.TransferOp;
import pt.tecnico.distledger.server.domain.VectorClock;
import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions;
import pt.ulisboa.tecnico.distledger.contract.NamingServerDistLedger.LookupRequest;
import pt.ulisboa.tecnico.distledger.contract.NamingServerDistLedger.LookupResponse;
import pt.ulisboa.tecnico.distledger.contract.NamingServerDistLedger.RegisterRequest;
import pt.ulisboa.tecnico.distledger.contract.NamingServerServiceGrpc;
import pt.ulisboa.tecnico.distledger.contract.distledgerserver.DistLedgerCrossServerServiceGrpc;
import pt.ulisboa.tecnico.distledger.contract.distledgerserver.PropagateStateRequest;

import java.util.*;
import java.util.stream.Collectors;

public class ServerState {
    private final VectorClock valueTs;
    private final VectorClock replicaTs;
    private final List<Operation> ledger;
    private final Map<String, Integer> userAccounts;
    private final char serverQualifier;
    private ManagedChannel namingServerChannel;
    private NamingServerServiceGrpc.NamingServerServiceBlockingStub namingServerStub;
    private final Map<String, DistLedgerCrossServerServiceGrpc.DistLedgerCrossServerServiceBlockingStub> serverStubs;
    private final List<String> serverTargets;
    private final Map<String, ManagedChannel> serverChannels;
    private boolean serverAvailable;


    public ServerState(int port, String qualifier) {
        this.replicaTs = new VectorClock();
        this.valueTs = new VectorClock();
        this.ledger = new ArrayList<>();
        this.userAccounts = new HashMap<>();
        this.userAccounts.put("broker", 1000);
        this.serverAvailable = true;
        String serviceName = "DistLedger";
        String serverTarget = "localhost:" + port;
        this.serverQualifier = qualifier.charAt(0);
        this.serverTargets = new ArrayList<>();
        this.serverStubs = new HashMap<>();
        this.serverChannels = new HashMap<>();

        createChannelAndStubNamingServer();
        try {
            namingServerStub.register(RegisterRequest.newBuilder().setServiceName(serviceName).setQualifier((String.valueOf(serverQualifier))).setServerAddress(serverTarget).build());
        } catch (StatusRuntimeException e) {
            System.err.println(e.getStatus().getDescription());
        }
        shutdownNamingServerChannel();
    }

    public VectorClock getValueTs() {
        return valueTs;
    }

    public VectorClock getReplicaTs() {
        return replicaTs;
    }

    public int getId() {
        return (int) this.serverQualifier - (int) 'A';
    }


    public void createChannelAndStubNamingServer() {
        this.namingServerChannel = ManagedChannelBuilder.forAddress("localhost", 5001).usePlaintext().build();
        this.namingServerStub = NamingServerServiceGrpc.newBlockingStub(namingServerChannel);
    }

    public boolean lookupServer() {
        createChannelAndStubNamingServer();
        LookupResponse result = namingServerStub.lookup(LookupRequest.newBuilder().setServiceName("DistLedger").setQualifier("").build());
        shutdownNamingServerChannel();
        if (result.getServerCount() == 0)
            return false;
        result.getServerList().stream()
                .filter(sv -> sv.getQualifier().charAt(0) != this.serverQualifier)
                .filter(sv -> !serverTargets.contains(sv.getServerTarget()))
                .forEach(sv -> this.serverTargets.add(sv.getServerTarget()));
        return true;
    }

    public boolean createChannelsAndStubsReplicas() {
        if (!lookupServer()) {
            return false;
        }
        serverTargets.forEach(svTg -> {
            ManagedChannel channel = ManagedChannelBuilder.forTarget(svTg).usePlaintext().build();
            serverChannels.put(svTg, channel);
            serverStubs.put(svTg, DistLedgerCrossServerServiceGrpc.newBlockingStub(channel));
        });
        return true;
    }

    public void shutdownNamingServerChannel() {
        this.namingServerChannel.shutdownNow();
    }

    public boolean getServerAvailable() {
        return serverAvailable;
    }

    public NamingServerServiceGrpc.NamingServerServiceBlockingStub getNamingServerStub() {
        return namingServerStub;
    }

    public OperationResult createAccount(String username, List<Integer> prev) {
        if (!getServerAvailable()) {
            return OperationResult.SERVER_OFF;
        } else if (userAccounts.containsKey(username)) {
            return OperationResult.ACCOUNT_ALREADY_EXISTS;
        }  else {
            CreateOp op = new CreateOp(username);
            this.replicaTs.increment(getId());
            op.setTS(this.replicaTs);
            op.setPrev(new VectorClock(prev));
            if(this.valueTs.greaterEqual(prev)){
                op.setStableTrue();
                valueTs.merge(new VectorClock(op.getTS().getTimestamps()));
                userAccounts.put(username, 0);
            }
            ledger.add(op);
            return OperationResult.OK;
        }
    }

    public OperationResult balanceVerification(String userId, List<Integer> prev) {
        if (!getServerAvailable()) {
            return OperationResult.SERVER_OFF;
        } else if (!valueTs.greaterEqual(prev)) {
            return OperationResult.OUT_OF_DATE;
        } else if (!userAccounts.containsKey(userId)) {
            return OperationResult.NO_ACCOUNT_FOUND;
        } else return OperationResult.OK;
    }

    public int getBalance(String userId) {
        return userAccounts.get(userId);
    }

    public OperationResult transferTo(String from, String to, int amount, List<Integer> prev) {
        if (!getServerAvailable()) {
            return OperationResult.SERVER_OFF;
        } else if (amount <= 0) {
            return OperationResult.INVALID_AMOUNT;
        } else if (!userAccounts.containsKey(from)) {
            return OperationResult.SENDER_NOT_FOUND;
        } else if (!userAccounts.containsKey(to)) {
            return OperationResult.RECEIVER_NOT_FOUND;
        } else if (userAccounts.get(from) < amount) {
            return OperationResult.NOT_ENOUGH_MONEY;
        }
        TransferOp op = new TransferOp(from, to, amount);
        this.replicaTs.increment(getId());
        op.setTS(new VectorClock(this.replicaTs.getTimestamps()));
        op.setPrev(new VectorClock(prev));
        if(this.valueTs.greaterEqual(prev)){
            op.setStableTrue();
            valueTs.merge(new VectorClock(op.getTS().getTimestamps()));
            userAccounts.put(from, userAccounts.get(from) - amount);
            userAccounts.put(to, userAccounts.get(to) + amount);
        }
        ledger.add(op);
        return OperationResult.OK;
    }

    public OperationResult deleteAccount(String username) {
        if (!getServerAvailable()) {
            return OperationResult.SERVER_OFF;
        } else if (!userAccounts.containsKey(username)) {
            return OperationResult.NO_ACCOUNT_FOUND;
        } else if (userAccounts.get(username) != 0) {
            return OperationResult.AMOUNT_NOT_0;
        } else if (username.compareTo("broker") == 0) {
            return OperationResult.DELETE_BROKER;
        } else {
            Operation op = new DeleteOp(username);
            ledger.add(op);
            if (!propagateState()) {
                ledger.remove(op);
                return OperationResult.READ_ONLY;
            }
            userAccounts.remove(username);
            return OperationResult.OK;
        }
    }

    public AdminOperationResult activateServer() {
        if (getServerAvailable()) {
            return AdminOperationResult.SERVER_ALREADY_ACTIVE;
        } else {
            serverAvailable = true;
            return AdminOperationResult.OK;
        }
    }

    public AdminOperationResult deactivateServer() {
        if (!getServerAvailable()) {
            return AdminOperationResult.SERVER_ALREADY_INACTIVE;
        } else {
            serverAvailable = false;
            return AdminOperationResult.OK;
        }
    }

    public List<Operation> getLedger() {
        return ledger;
    }

    public AdminOperationResult gossip(){
        if(propagateState())
            return AdminOperationResult.OK;
        return AdminOperationResult.REPLICAS_UNREACHABLE;
    }

    public boolean propagateState() {
        if (!createChannelsAndStubsReplicas()) {
            return false;
        } else {
            try {
                serverStubs.forEach((svTg, stub) -> stub.propagateState(PropagateStateRequest
                        .newBuilder().setState(
                                DistLedgerCommonDefinitions.LedgerState
                                        .newBuilder().addAllLedger(ledger.stream()
                                                .map(Op -> Op.getOperationMessageFormat(Op.getPrev().getTimestamps(), Op.getTS().getTimestamps()))
                                                .collect(Collectors.toList()))
                                        .build()).setReplicaTs(DistLedgerCommonDefinitions.Timestamp.newBuilder()
                                .addAllTimestamp(replicaTs.getTimestamps()).build()).build()));
                this.serverChannels.forEach((svTg, channel) -> channel.shutdownNow());
                return true;
            } catch (StatusRuntimeException e) {
                return false;
            }
        }
    }
    private void registerOperation(DistLedgerCommonDefinitions.Operation operation) {
        if (operation.getType() == DistLedgerCommonDefinitions.OperationType.OP_CREATE_ACCOUNT)
            userAccounts.put(operation.getUserId(), 0);
        else if (operation.getType() == DistLedgerCommonDefinitions.OperationType.OP_TRANSFER_TO) {
            userAccounts.put(operation.getUserId(), userAccounts.get(operation.getUserId()) - operation.getAmount());
            userAccounts.put(operation.getDestUserId(), userAccounts.get(operation.getDestUserId()) + operation.getAmount());
        }
    }
    public void updateState(DistLedgerCommonDefinitions.LedgerState ledgerState, DistLedgerCommonDefinitions.Timestamp timestampReplicaTs) {
        ledgerState.getLedgerList().forEach(operation -> {
            if(!replicaTs.greaterEqual(operation.getTS().getTimestampList())){
                Operation op;
                if(operation.getType() == DistLedgerCommonDefinitions.OperationType.OP_CREATE_ACCOUNT){
                    op = new CreateOp(operation.getUserId());
                }
                else{
                    op = new TransferOp(operation.getUserId(), operation.getDestUserId(), operation.getAmount());
                }
                op.setTS(new VectorClock(operation.getTS().getTimestampList()));
                op.setPrev(new VectorClock(operation.getPrevTS().getTimestampList()));

                if(valueTs.greaterEqual(op.getPrev().getTimestamps())){
                    op.setStableTrue();
                    registerOperation(operation);
                    valueTs.merge(new VectorClock(op.getTS().getTimestamps()));
                }
                ledger.add(op);

            }
        });
        replicaTs.merge(new VectorClock(timestampReplicaTs.getTimestampList()));
        ledger.forEach(operation -> {
            if(valueTs.greaterEqual(operation.getPrev().getTimestamps()) && !operation.getStable()){
                operation.setStableTrue();
                registerOperation(operation.getOperationMessageFormat(operation.getPrev().getTimestamps(), operation.getTS().getTimestamps()));
                valueTs.merge(new VectorClock(operation.getTS().getTimestamps()));
            }
        });
    }

    public enum OperationResult {
        OK,
        ACCOUNT_ALREADY_EXISTS,
        NO_ACCOUNT_FOUND,
        RECEIVER_NOT_FOUND,
        SENDER_NOT_FOUND,
        NOT_ENOUGH_MONEY,
        AMOUNT_NOT_0,
        SERVER_OFF,
        DELETE_BROKER,
        INVALID_AMOUNT,
        READ_ONLY,
        OUT_OF_DATE
    }

    public enum AdminOperationResult {
        OK,
        SERVER_ALREADY_ACTIVE,
        SERVER_ALREADY_INACTIVE,
        REPLICAS_UNREACHABLE
    }
}
