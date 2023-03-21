package pt.tecnico.distledger.server;

import io.grpc.*;
import pt.tecnico.distledger.server.domain.ServerState;
import pt.tecnico.distledger.server.service.AdminServiceImpl;
import pt.tecnico.distledger.server.service.UserServiceImpl;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import pt.ulisboa.tecnico.distledger.contract.NamingServerDistLedger.*;
import pt.ulisboa.tecnico.distledger.contract.NamingServerServiceGrpc;
import static java.lang.Integer.parseInt;

import java.io.IOException;

public class ServerMain {

    public static void main(String[] args) throws IOException, InterruptedException {

        System.out.printf("Received %d arguments%n", args.length);
        for (int i = 0; i < args.length; i++) {
            System.out.printf("arg[%d] = %s%n", i, args[i]);
        }
        final int port = Integer.parseInt(args[0]);
        ServerState serverState = new ServerState(port, args[1]);
        final BindableService userImpl = new UserServiceImpl(serverState);
        final BindableService adminImpl = new AdminServiceImpl(serverState);

        // Create a new server to listen on port
        Server server = ServerBuilder.forPort(port).addService(userImpl).addService(adminImpl).build();

        // Start the server
        server.start();

        // Server threads are running in the background.
        System.out.println("Server started");

        // Do not exit the main thread. Wait until server is terminated.
        server.awaitTermination();

    }

}

