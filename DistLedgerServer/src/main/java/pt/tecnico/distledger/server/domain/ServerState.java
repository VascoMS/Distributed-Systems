package pt.tecnico.distledger.server.domain;

import io.grpc.ManagedChannelBuilder;
import io.grpc.ManagedChannel;
import pt.tecnico.distledger.server.domain.operation.CreateOp;
import pt.tecnico.distledger.server.domain.operation.DeleteOp;
import pt.tecnico.distledger.server.domain.operation.Operation;
import pt.tecnico.distledger.server.domain.operation.TransferOp;
import pt.ulisboa.tecnico.distledger.contract.NamingServerDistLedger.*;
import pt.ulisboa.tecnico.distledger.contract.NamingServerServiceGrpc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerState {
    private List<Operation> ledger;
    private Map<String,Integer> userAccounts;
    String serverQualifier;
    String serverTarget;
    String serviceName;
    private boolean serverAvailable;
    ManagedChannel namingServerChannel;
    NamingServerServiceGrpc.NamingServerServiceBlockingStub namingServerStub;



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
        INVALID_AMOUNT
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

        namingServerStub.register(RegisterRequest.newBuilder().setServiceName(serviceName).setQualifier(serverQualifier).setServerAddress(serverTarget).build());

        shutdownNamingServerChannel();
    }

    public void createChannelAndStubNamingServer() {

        this.namingServerChannel = ManagedChannelBuilder.forAddress("localhost", 5001).usePlaintext().build();;
        this.namingServerStub  = NamingServerServiceGrpc.newBlockingStub(namingServerChannel);

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
        else if(userAccounts.containsKey(username)){
            return OperationResult.ACCOUNT_ALREADY_EXISTS;
        }
        else{
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
            /*createChannelAndStubNamingServer();
            namingServerStub.register(RegisterRequest.newBuilder().setServiceName(serviceName).setQualifier(serverQualifier).setServerAddress(serverTarget).build());
            shutdownNamingServerChannel();*/
            return AdminOperationResult.OK;
        }
    }

    public AdminOperationResult deactivateServer() {
        if(!getServerAvailable()){
            return AdminOperationResult.SERVER_ALREADY_INACTIVE;
        }
        else{
            serverAvailable = false;
            /*createChannelAndStubNamingServer();
            namingServerStub.delete(DeleteRequest.newBuilder().setServiceName(serviceName).setTarget(serverTarget).build());
            shutdownNamingServerChannel();*/
            return AdminOperationResult.OK;
        }
    }

    public List<Operation> getLedger() {
        return ledger;
    }
}
