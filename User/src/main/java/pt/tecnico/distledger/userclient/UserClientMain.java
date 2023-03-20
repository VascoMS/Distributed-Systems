package pt.tecnico.distledger.userclient;


import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Server;
import pt.tecnico.distledger.userclient.grpc.UserService;
import pt.ulisboa.tecnico.distledger.contract.NamingServerDistLedger.*;
import pt.ulisboa.tecnico.distledger.contract.NamingServerServiceGrpc;

import java.lang.invoke.MethodHandles;
import java.util.List;

public class UserClientMain {
    /** Set flag to true to print debug messages. 
	 * The flag can be set using the -Ddebug command line option. */
    private static final boolean DEBUG_FLAG = (System.getProperty("debug") != null);

    /** Helper method to print debug messages. */
	public static void debug(String debugMessage) {
		if (DEBUG_FLAG)
			System.err.println(debugMessage);
	}
    public static void main(String[] args) {

        System.out.println(UserClientMain.class.getSimpleName());

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 5001).usePlaintext().build();;

        NamingServerServiceGrpc.NamingServerServiceBlockingStub stub = NamingServerServiceGrpc.newBlockingStub(channel);

        String target = stub.lookup(LookupRequest.newBuilder().setServiceName("DistLedger").setQualifier("A").build()).getServer(0).getServerTarget();

		debug("Target: " + target);

        CommandParser parser = new CommandParser(new UserService());
        parser.parseInput(target);

    }
}
