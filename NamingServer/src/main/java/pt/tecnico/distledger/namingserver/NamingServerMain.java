package pt.tecnico.distledger.namingserver;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import pt.tecnico.distledger.namingserver.domain.NamingServer;
import pt.tecnico.distledger.namingserver.services.NamingServerServiceImpl;

public class NamingServerMain {

    public static void main(String[] args) {

        NamingServer namingServer = new NamingServer();
        final BindableService namingImpl = new NamingServerServiceImpl(namingServer);
        Server server = ServerBuilder.forPort(5001).addService(namingImpl).build();

    }

}
