package pt.tecnico.distledger.server.domain;

import pt.tecnico.distledger.server.domain.operation.CreateOp;
import pt.tecnico.distledger.server.domain.operation.DeleteOp;
import pt.tecnico.distledger.server.domain.operation.Operation;
import pt.tecnico.distledger.server.domain.operation.TransferOp;
import pt.ulisboa.tecnico.distledger.contract.user.OperationResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerState {
    private List<Operation> ledger;
    private Map<String,Integer> userAccounts;
    private boolean serverAvailable;

    public serverAvailable() {
        this.ledger = new ArrayList<>();
        this.userAccounts = new HashMap<>();
        this.userAccounts.put("broker",1000);
        this.serverAvailable = true;
    }

    /* TODO: Here should be declared all the server state attributes
         as well as the methods to access and interact with the state. */

    public boolean getServerAvailable() {
        return this.serverAvailable;
    }

    public OperationResult createAccount(String username) {
        if(!getServerAvailable()){
            return OperationResult.SERVER_OFF;
        }
        if(userAccounts.containsKey(username)){
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

    public synchronized int getBalance(String userId){
        return userAccounts.get(userId);
    }

    public OperationResult transferTo(String from, String to, int amount) {
        if(!getServerAvailable()){
            return OperationResult.SERVER_OFF;
        }
        if(!userAccounts.containsKey(from)){
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

    public OperationResult activateServer() {
        if(getServerAvailable()){
            return OperationResult.SERVER_ALREADY_ACTIVE;
        }
        else{
            this.serverAvailable = true;
            return OperationResult.OK;
        }
    }

    public OperationResult deactivateServer() {
        if(!getServerAvailable()){
            return OperationResult.SERVER_ALREADY_INACTIVE;
        }
        else{
            this.serverAvailable = false;
            return OperationResult.OK;
        }
    }

    public String getLedger() {
        String ledger = "ledgerState {";
        for(Operation operation : this.ledger){
            ledger += "\n" + operation.toString();
        }
        ledger += "\n}";
    }
}
