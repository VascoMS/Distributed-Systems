package pt.tecnico.distledger.userclient.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import pt.ulisboa.tecnico.distledger.contract.user.*;

public class UserService {

    /*TODO: The gRPC client-side logic should be here.
        This should include a method that builds a channel and stub,
        as well as individual methods for each remote operation of this service. */
    private ManagedChannel channel;
    private UserServiceGrpc.UserServiceBlockingStub stub;

    public void createChannelAndStub(String host, int port) {

        final String target = host + ":" + port;

        this.channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();

        this.stub = UserServiceGrpc.newBlockingStub(channel);
    }

    public void shutdownChannel(){
        this.channel.shutdownNow();
    }

    public void createAccount(String username) {
        try{
            stub.createAccount(CreateAccountRequest.newBuilder().setUserId(username).build());
            System.out.println("OK");
        } catch (StatusRuntimeException e) {
            System.out.println(e.getStatus().getDescription());
        }
    }

    public void deleteAccount(String username) {
        try{
            stub.deleteAccount(DeleteAccountRequest.newBuilder().setUserId(username).build());
            System.out.println("OK");
        } catch (StatusRuntimeException e) {
            System.out.println(e.getStatus().getDescription());
        }
    }

    public void balance(String Username) {
        try{
           int balance = stub.balance(BalanceRequest.newBuilder().setUserId(username).build()).getValue();
           System.out.println("OK");
           System.out.println(balance);

        } catch (StatusRuntimeException e) {
           System.out.println(e.getStatus().getDescription());
        }
    }

    public void transferTo(String from, String dest, int amount) {
        try{
            stub.transferTo(TransferToRequest.newBuilder().setAccountFrom(from).setAccountTo(dest).setAmount(amount).build());
            System.out.println("OK");
        } catch (StatusRuntimeException e) {
            System.out.println(e.getStatus().getDescription());
        }
    }
}
