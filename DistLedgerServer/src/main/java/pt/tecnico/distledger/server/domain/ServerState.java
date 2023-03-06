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

    public ServerState() {
        this.ledger = new ArrayList<>();
        this.userAccounts = new HashMap<>();
        this.userAccounts.put("broker",1000);
    }

    /* TODO: Here should be declared all the server state attributes
         as well as the methods to access and interact with the state. */

    public OperationResult createAccount(String username) {
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

    public synchronized int getBalance(String userId){
        if(userAccounts.containsKey(userId)){
            return userAccounts.get(userId);
        }
        else{
            return -1;
        }
    }

    public OperationResult transferTo(String from, String to, int amount) {
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
        if(!userAccounts.containsKey(username)){
            return OperationResult.NO_ACCOUNT_FOUND;
        }
        if(userAccounts.get(username) != 0){
            return OperationResult.AMOUNT_NOT_0;
        }
        else {
            DeleteOp deleteOp = new DeleteOp(username);
            ledger.add(deleteOp);
            userAccounts.remove(username);
            return OperationResult.OK;
        }
    }
}
