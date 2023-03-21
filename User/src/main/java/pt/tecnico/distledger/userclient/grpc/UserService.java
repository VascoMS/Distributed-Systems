package pt.tecnico.distledger.userclient.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import pt.ulisboa.tecnico.distledger.contract.user.*;
import pt.ulisboa.tecnico.distledger.contract.NamingServerServiceGrpc;
import pt.ulisboa.tecnico.distledger.contract.NamingServerDistLedger.*;

import static java.lang.Integer.parseInt;

public class UserService {


    private ManagedChannel channel;
    private UserServiceGrpc.UserServiceBlockingStub stub;

    public void createChannelAndStub(String host, int port) {

        String target = host + ":" + port;
        this.channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();

        this.stub = UserServiceGrpc.newBlockingStub(channel);
    }

    public void shutdownChannel(){
        this.channel.shutdownNow();
    }

    public void createAccount(String username) {
        try{
           CreateAccountResponse response = stub.createAccount(CreateAccountRequest.newBuilder().setUserId(username).build());
            System.out.println("OK");
            System.out.println(response);
        } catch (StatusRuntimeException e) {
            System.out.println(e.getStatus().getDescription());
        }
    }

    public void deleteAccount(String username) {
        try{
            DeleteAccountResponse response = stub.deleteAccount(DeleteAccountRequest.newBuilder().setUserId(username).build());
            System.out.println("OK");
            System.out.println(response);
        } catch (StatusRuntimeException e) {
            System.out.println(e.getStatus().getDescription());
        }
    }

    public void balance(String username) {
        try{
           BalanceResponse response = stub.balance(BalanceRequest.newBuilder().setUserId(username).build());
           System.out.println("OK");
           System.out.println(response);

        } catch (StatusRuntimeException e) {
           System.out.println(e.getStatus().getDescription());
        }
    }

    public void transferTo(String from, String dest, int amount) {
        try{
            TransferToResponse response = stub.transferTo(TransferToRequest.newBuilder().setAccountFrom(from).setAccountTo(dest).setAmount(amount).build());
            System.out.println("OK");
            System.out.println(response);
        } catch (StatusRuntimeException e) {
            System.out.println(e.getStatus().getDescription());
        }
    }

    public void lookup(String qualifier){
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 5001).usePlaintext().build();;

        NamingServerServiceGrpc.NamingServerServiceBlockingStub stub = NamingServerServiceGrpc.newBlockingStub(channel);

        String target = stub.lookup(LookupRequest.newBuilder().setServiceName("DistLedger").setQualifier(qualifier).build()).getServer(0).getServerTarget();

        String[] result = target.split(":");
        String host = result[0];
        int port = parseInt(result[1]);
        createChannelAndStub(host, port);
        channel.shutdownNow();
    }
}
