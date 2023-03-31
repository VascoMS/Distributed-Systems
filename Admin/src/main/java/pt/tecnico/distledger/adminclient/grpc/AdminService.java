package pt.tecnico.distledger.adminclient.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import pt.ulisboa.tecnico.distledger.contract.DistLedgerCommonDefinitions;
import pt.ulisboa.tecnico.distledger.contract.NamingServerDistLedger.*;
import pt.ulisboa.tecnico.distledger.contract.NamingServerServiceGrpc;
import pt.ulisboa.tecnico.distledger.contract.admin.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminService {

    private ManagedChannel channel;
    private AdminServiceGrpc.AdminServiceBlockingStub stub;
    private final Map<String, String> targets = new HashMap<>();
    private final List<Integer> prev = new ArrayList<>();

    public void createChannelAndStub(String target) {
        this.channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
        this.stub = AdminServiceGrpc.newBlockingStub(channel);
    }

    public void shutdownChannel(){
        this.channel.shutdownNow();
    }

    public void activate() {
        try{
            ActivateResponse response = stub.activate(ActivateRequest.getDefaultInstance());
            System.out.println("OK");
            System.out.println(response);
        } catch (StatusRuntimeException e) {
            System.out.println(e.getStatus().getDescription());
        }
    }

    public void deactivate() {
        try{
           DeactivateResponse response = stub.deactivate(DeactivateRequest.getDefaultInstance());
           System.out.println("OK");
           System.out.println(response);
        } catch (StatusRuntimeException e) {
           System.out.println(e.getStatus().getDescription());
        }
    }

    public void dump() {
        try{
           getLedgerStateResponse response = stub.getLedgerState(getLedgerStateRequest.newBuilder().setPrev(DistLedgerCommonDefinitions.Timestamp.newBuilder().addAllTimestamp(this.prev)).build());
           this.prev.clear();
           this.prev.addAll(response.getNew().getTimestampList());
           System.out.println("OK");
           System.out.println(response);
        } catch (StatusRuntimeException e) {
           System.out.println(e.getStatus().getDescription());
        }
    }

    public boolean lookup(String qualifier){
        if(!this.targets.containsKey(qualifier)) {
            ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 5001).usePlaintext().build();
            NamingServerServiceGrpc.NamingServerServiceBlockingStub stub = NamingServerServiceGrpc.newBlockingStub(channel);
            LookupResponse response = stub.lookup(LookupRequest.newBuilder().setServiceName("DistLedger")
                    .setQualifier(qualifier).build());
            channel.shutdownNow();
            if(response.getServerCount() == 0)
                return false;
            this.targets.put(qualifier,response.getServer(0).getServerTarget());
        }
        createChannelAndStub(targets.get(qualifier));
        return true;
    }
}
