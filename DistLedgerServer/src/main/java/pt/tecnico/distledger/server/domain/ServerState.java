package pt.tecnico.distledger.server.domain;

import io.grpc.ManagedChannelBuilder;
import io.grpc.ManagedChannel;
import io.grpc.StatusRuntimeException;
import pt.tecnico.distledger.server.domain.operation.CreateOp;
import pt.tecnico.distledger.server.domain.operation.DeleteOp;
import pt.tecnico.distledger.server.domain.operation.Operation;
import pt.tecnico.distledger.server.domain.operation.TransferOp;
import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions;
import pt.ulisboa.tecnico.distledger.contract.NamingServerDistLedger.*;
import pt.ulisboa.tecnico.distledger.contract.NamingServerServiceGrpc;
import pt.ulisboa.tecnico.distledger.contract.distledgerserver.DistLedgerCrossServerServiceGrpc;
import pt.ulisboa.tecnico.distledger.contract.distledgerserver.PropagateStateRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.Integer.parseInt;

public class ServerState {
    private final List<Operation> ledger;
    private final Map<String,Integer> userAccounts;
    private static final String primary = "A";
    private static final String secondary = "B";
    String serverQualifier;
    String serverTarget;
    String serviceName;
    private boolean serverAvailable;
    ManagedChannel namingServerChannel;
    NamingServerServiceGrpc.NamingServerServiceBlockingStub namingServerStub;
    ManagedChannel secondaryServerChannel;
    DistLedgerCrossServerServiceGrpc.DistLedgerCrossServerServiceBlockingStub secondaryServerStub;



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
        READ_ONLY
    }

    public enum AdminOperationResult {
        OK,
        SERVER_ALREADY_ACTIVE,
        SERVER_ALREADY_INACTIVE
    }

    public ServerState(int port, String qualifier) {
        this.ledger = new ArrayList<>();
        this.userAccounts = new HashMap<>();
        this.userAccounts.put("broker",1000);
        this.serverAvailable = true;
        this.serviceName = "DistLedger";
        this.serverTarget = "localhost:" + port;
        this.serverQualifier = qualifier;

        createChannelAndStubNamingServer();
        try {
            namingServerStub.register(RegisterRequest.newBuilder().setServiceName(serviceName).setQualifier(serverQualifier).setServerAddress(serverTarget).build());
        }catch (StatusRuntimeException e){
            System.err.println(e.getStatus().getDescription());
        }
        //shutdownNamingServerChannel();
    }

    public void createChannelAndStubNamingServer() {

        this.namingServerChannel = ManagedChannelBuilder.forAddress("localhost", 5001).usePlaintext().build();
        this.namingServerStub  = NamingServerServiceGrpc.newBlockingStub(namingServerChannel);

    }

    public boolean createChannelAndStubSecondaryServer(){
        LookupResponse result = namingServerStub.lookup(LookupRequest.newBuilder().setServiceName("DistLedger").setQualifier(secondary).build());
        if(result.getServerCount() == 0) {
            return false;
        }
        String[] target = result.getServer(0).getServerTarget().split(":");
        this.secondaryServerChannel = ManagedChannelBuilder.forAddress(target[0], parseInt(target[1])).usePlaintext().build();
        this.secondaryServerStub = DistLedgerCrossServerServiceGrpc.newBlockingStub(secondaryServerChannel);
        return true;
    }

    public void shutdownNamingServerChannel(){
        this.namingServerChannel.shutdownNow();
    }

    public boolean getServerAvailable() {
        return serverAvailable;
    }

    public OperationResult createAccount(String username) {
        if(!getServerAvailable()){
            return OperationResult.SERVER_OFF;
        }
        else if(this.serverQualifier.equals(secondary)) {
            return OperationResult.READ_ONLY;
        }
        else if(userAccounts.containsKey(username)){
            return OperationResult.ACCOUNT_ALREADY_EXISTS;
        }
        else{
            List<Operation> temporaryLedger = new ArrayList<>(ledger);
            temporaryLedger.add(new CreateOp(username));
            if(!propagateState(temporaryLedger)) {
                return OperationResult.READ_ONLY;
            }
            userAccounts.put(username, 0);
            Operation createOp = new CreateOp(username);
            ledger.add(createOp);
            return OperationResult.OK;
        }
    }
    
    public OperationResult balanceVerification(String userId){
        if(!getServerAvailable()){
            return OperationResult.SERVER_OFF;
        }
        else if(!userAccounts.containsKey(userId)){
            return OperationResult.NO_ACCOUNT_FOUND;
        }
        else return OperationResult.OK;
    }

    public int getBalance(String userId){
        return userAccounts.get(userId);
    }

    public OperationResult transferTo(String from, String to, int amount) {
        if(!getServerAvailable()){
            return OperationResult.SERVER_OFF;
        }
        else if(this.serverQualifier.equals(secondary))
            return OperationResult.READ_ONLY;
        else if(amount <= 0){
            return OperationResult.INVALID_AMOUNT;
        }
        else if(!userAccounts.containsKey(from)){
            return OperationResult.SENDER_NOT_FOUND;
        }
        else if(!userAccounts.containsKey(to)){
            return OperationResult.RECEIVER_NOT_FOUND;
        }
        else if(userAccounts.get(from) < amount){
            return OperationResult.NOT_ENOUGH_MONEY;
        }
        List<Operation> temporaryLedger = new ArrayList<>(ledger);
        temporaryLedger.add(new TransferOp(from, to, amount));
        if(!propagateState(temporaryLedger)) {
            return OperationResult.READ_ONLY;
        }
        TransferOp transferOp = new TransferOp(from, to, amount);
        ledger.add(transferOp);
        userAccounts.put(from, userAccounts.get(from) - amount);
        userAccounts.put(to, userAccounts.get(to) + amount);
        return OperationResult.OK;
    }

    public OperationResult deleteAccount(String username) {
        if(!getServerAvailable()){
            return OperationResult.SERVER_OFF;
        }
        else if(this.serverQualifier.equals(secondary))
            return OperationResult.READ_ONLY;
        else if(!userAccounts.containsKey(username)){
            return OperationResult.NO_ACCOUNT_FOUND;
        }
        else if(userAccounts.get(username) != 0){
            return OperationResult.AMOUNT_NOT_0;
        }
        else if(username.compareTo("broker") == 0){
            return OperationResult.DELETE_BROKER;
        }
        else {
            List<Operation> temporaryLedger = new ArrayList<>(ledger);
            temporaryLedger.add(new DeleteOp(username));
            if(!propagateState(temporaryLedger)) {
                return OperationResult.READ_ONLY;
            }
            DeleteOp deleteOp = new DeleteOp(username);
            ledger.add(deleteOp);
            userAccounts.remove(username);
            return OperationResult.OK;
        }
    }

    public AdminOperationResult activateServer() {
        if(getServerAvailable()){
            return AdminOperationResult.SERVER_ALREADY_ACTIVE;
        }
        else{
            serverAvailable = true;
            return AdminOperationResult.OK;
        }
    }

    public AdminOperationResult deactivateServer() {
        if(!getServerAvailable()){
            return AdminOperationResult.SERVER_ALREADY_INACTIVE;
        }
        else{
            serverAvailable = false;
            return AdminOperationResult.OK;
        }
    }

    public List<Operation> getLedger() {
        return ledger;
    }

    public boolean propagateState(List<Operation> temporaryLedger) {
        if(!createChannelAndStubSecondaryServer()){
            return false;
        }
        else {
            try {
                secondaryServerStub.propagateState(PropagateStateRequest
                        .newBuilder().setState(
                                DistLedgerCommonDefinitions.LedgerState
                                        .newBuilder().addAllLedger(temporaryLedger.stream()
                                                .map(Operation::getOperationMessageFormat)
                                                .collect(Collectors.toList()))
                                        .build())
                        .build());
                return true;
            } catch (StatusRuntimeException e) {
                return false;
            }
        }
    }

    private void registerOperation(DistLedgerCommonDefinitions.Operation operation) {
        if(operation.getType() == DistLedgerCommonDefinitions.OperationType.OP_CREATE_ACCOUNT)
            userAccounts.put(operation.getUserId(), 0);
        else if(operation.getType() == DistLedgerCommonDefinitions.OperationType.OP_DELETE_ACCOUNT)
            userAccounts.remove(operation.getUserId());
        else if(operation.getType() == DistLedgerCommonDefinitions.OperationType.OP_TRANSFER_TO) {
            userAccounts.put(operation.getUserId(), userAccounts.get(operation.getUserId()) - operation.getAmount());
            userAccounts.put(operation.getDestUserId(), userAccounts.get(operation.getDestUserId()) + operation.getAmount());
        }
    }

    public OperationResult updateState(DistLedgerCommonDefinitions.LedgerState ledgerState){
        if(!this.serverAvailable)
            return OperationResult.SERVER_OFF;
        this.ledger.clear();
        this.ledger.addAll(ledgerState.getLedgerList().stream()
                .map(op -> {
                        DistLedgerCommonDefinitions.OperationType type = op.getType();
                        if(type == DistLedgerCommonDefinitions.OperationType.OP_CREATE_ACCOUNT) {
                            return new CreateOp(op.getUserId());
                        }
                        else if(type == DistLedgerCommonDefinitions.OperationType.OP_DELETE_ACCOUNT) {
                            return new DeleteOp(op.getUserId());
                        }
                        else {
                            return new TransferOp(op.getUserId(), op.getDestUserId(), op.getAmount());
                        }
                    })
                .collect(Collectors.toList()));

        registerOperation(ledgerState.getLedger(ledgerState.getLedgerCount() - 1));
        return OperationResult.OK;
    }
}
