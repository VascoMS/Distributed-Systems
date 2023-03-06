package pt.tecnico.distledger.userclient.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import pt.ulisboa.tecnico.distledger.contract.user.UserServiceGrpc;

public class UserService {

    /*TODO: The gRPC client-side logic should be here.
        This should include a method that builds a channel and stub,
        as well as individual methods for each remote operation of this service. */
    private ManagedChannel channel;
    private UserServiceGrpc.UserServiceBlockingStub stub;

    public UserServiceGrpc.UserServiceBlockingStub createChannelAndStub(String host, int port) {

        final String target = host + ":" + port;

        this.channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();

        this.stub = UserServiceGrpc.newBlockingStub(channel);

        return this.stub;
    }

    public void shutdownChannel(){
        this.channel.shutdownNow();
    }
}
