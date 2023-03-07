package pt.tecnico.distledger.adminclient.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions;
import pt.ulisboa.tecnico.distledger.contract.admin.*;


public class AdminService {

    /* TODO: The gRPC client-side logic should be here.
        This should include a method that builds a channel and stub,
        as well as individual methods for each remote operation of this service. */

    private ManagedChannel channel;
    private AdminServiceGrpc.AdminServiceBlockingStub stub;

    public void createChannelAndStub(String host, int port) {

        final String target = host + ":" + port;

        this.channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();

        this.stub = AdminServiceGrpc.newBlockingStub(channel);

    }

    public void shutdownChannel(){
        this.channel.shutdownNow();
    }

    public void activate() {
        try{
            stub.activate(ActivateRequest.getDefaultInstance());
            System.out.println("OK");
        } catch (StatusRuntimeException e) {
            System.out.println(e.getStatus().getDescription());
        }
    }

    public void deactivate() {
        try{
           stub.deactivate(DeactivateRequest.getDefaultInstance());
           System.out.println("OK");
        } catch (StatusRuntimeException e) {
           System.out.println(e.getStatus().getDescription());
        }
    }

    /*public void dump() {
        try{
           LedgerState ledger = stub.getLedgerState(getLedgerStateRequest.getDefaultInstance()).getLedgerState();
           System.out.println("OK");
           System.out.println(ledger);
        } catch (StatusRuntimeException e) {
           System.out.println(e.getStatus().getDescription());
        }
    }*/
}
