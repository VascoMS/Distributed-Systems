package pt.tecnico.distledger.adminclient;

import pt.tecnico.distledger.adminclient.grpc.AdminService;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import pt.ulisboa.tecnico.distledger.contract.NamingServerDistLedger.*;
import pt.ulisboa.tecnico.distledger.contract.NamingServerServiceGrpc;
import java.util.Scanner;
import static java.lang.Integer.parseInt;

public class CommandParser {

    private static final String SPACE = " ";
    private static final String ACTIVATE = "activate";
    private static final String DEACTIVATE = "deactivate";
    private static final String GET_LEDGER_STATE = "getLedgerState";
    private static final String GOSSIP = "gossip";
    private static final String HELP = "help";
    private static final String EXIT = "exit";

    private final AdminService adminService;

    public void debug(String debugMessage){
        AdminClientMain.debug(debugMessage);
    }

    public CommandParser(AdminService adminService) {
        this.adminService = adminService;
    }

    void parseInput() {


        //adminService.createChannelAndStub(host, port);

        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        while (!exit) {
            System.out.print("> ");
            String line = scanner.nextLine().trim();
            String cmd = line.split(SPACE)[0];

            switch (cmd) {
                case ACTIVATE:
                    this.activate(line);
                    adminService.shutdownChannel();
                    break;

                case DEACTIVATE:
                    this.deactivate(line);
                    adminService.shutdownChannel();
                    break;

                case GET_LEDGER_STATE:
                    this.dump(line);
                    adminService.shutdownChannel();
                    break;

                case GOSSIP:
                    this.gossip(line);
                    break;

                case HELP:
                    this.printUsage();
                    break;

                case EXIT:
                    exit = true;
                    break;

                default:
                    break;
            }

        }
    }

    private void activate(String line){
        String[] split = line.split(SPACE);

        if (split.length != 2){
            this.printUsage();
            return;
        }
        String server = split[1];
        debug(String.format("server: %s", server));
        this.lookup(server);
        adminService.activate();
    }

    private void deactivate(String line){
        String[] split = line.split(SPACE);

        if (split.length != 2){
            this.printUsage();
            return;
        }
        String server = split[1];
        debug(String.format("server: %s", server));
        this.lookup(server);
        adminService.deactivate();
    }

    private void dump(String line){
        String[] split = line.split(SPACE);

        if (split.length != 2){
            this.printUsage();
            return;
        }
        String server = split[1];
        debug(String.format("server: %s", server));
        this.lookup(server);
        adminService.dump();
    }

    private void lookup(String qualifier){
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 5001).usePlaintext().build();;

        NamingServerServiceGrpc.NamingServerServiceBlockingStub stub = NamingServerServiceGrpc.newBlockingStub(channel);

        String target = stub.lookup(LookupRequest.newBuilder().setServiceName("DistLedger").setQualifier(qualifier).build()).getServer(0).getServerTarget();

        String[] result = target.split(":");
        String host = result[0];
        int port = parseInt(result[1]);
        adminService.createChannelAndStub(host, port);
        channel.shutdownNow();
    }

    @SuppressWarnings("unused")
    private void gossip(String line){
        /* TODO Phase-3 */
        System.out.println("TODO: implement gossip command (only for Phase-3)");
    }

    private void printUsage() {
        System.out.println("Usage:\n" +
                "- activate <server>\n" +
                "- deactivate <server>\n" +
                "- getLedgerState <server>\n" +
                "- gossip <server>\n" +
                "- exit\n");
    }

}
