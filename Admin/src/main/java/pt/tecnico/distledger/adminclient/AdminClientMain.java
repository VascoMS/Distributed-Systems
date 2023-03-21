package pt.tecnico.distledger.adminclient;

import pt.tecnico.distledger.adminclient.grpc.AdminService;

public class AdminClientMain {

    /** Set flag to true to print debug messages. 
	 * The flag can be set using the -Ddebug command line option. */
    private static final boolean DEBUG_FLAG = (System.getProperty("debug") != null);

    /** Helper method to print debug messages. */
	public static void debug(String debugMessage) {
		if (DEBUG_FLAG)
			System.err.println(debugMessage);
	}
    public static void main(String[] args) {

        System.out.println(AdminClientMain.class.getSimpleName());

        // final String host = args[0];
        // final int port = Integer.parseInt(args[1]);
        // final String target = host + ":" + port;
		// debug("Target: " + target);
        CommandParser parser = new CommandParser(new AdminService());
        parser.parseInput();

    }
}
