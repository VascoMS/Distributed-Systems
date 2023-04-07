package pt.tecnico.distledger.adminclient;

import pt.tecnico.distledger.adminclient.grpc.AdminService;
import java.util.Scanner;

public class CommandParser {

    private static final String SPACE = " ";
    private static final String ACTIVATE = "activate";
    private static final String DEACTIVATE = "deactivate";
    private static final String GET_LEDGER_STATE = "getLedgerState";
    private static final String GOSSIP = "gossip";
    private static final String HELP = "help";
    private static final String EXIT = "exit";

    private final AdminService adminService;
    boolean exit = false;


    public void debug(String debugMessage){
        AdminClientMain.debug(debugMessage);
    }

    public CommandParser(AdminService adminService) {
        this.adminService = adminService;
    }

    void parseInput() {

        Scanner scanner = new Scanner(System.in);

        while (!exit) {
            System.out.print("> ");
            String line = scanner.nextLine().trim();
            String cmd = line.split(SPACE)[0];

            switch (cmd) {
                case ACTIVATE:
                    this.activate(line);
                    break;

                case DEACTIVATE:
                    this.deactivate(line);
                    break;

                case GET_LEDGER_STATE:
                    this.dump(line);
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
        debug(String.format("calling lookup with qualifier: %s", server));
        if(!adminService.lookup(server))
            exit = true;
        else {
            adminService.activate();
            adminService.shutdownChannel();
        }
    }

    private void deactivate(String line){
        String[] split = line.split(SPACE);

        if (split.length != 2){
            this.printUsage();
            return;
        }
        String server = split[1];
        debug(String.format("server: %s", server));
        debug(String.format("calling lookup with qualifier: %s", server));
        if(!adminService.lookup(server))
            exit = true;
        else {
            adminService.deactivate();
            adminService.shutdownChannel();
        }
    }

    private void dump(String line){
        String[] split = line.split(SPACE);

        if (split.length != 2){
            this.printUsage();
            return;
        }
        String server = split[1];
        debug(String.format("server: %s", server));
        debug(String.format("calling lookup with qualifier: %s", server));
        if(!adminService.lookup(server))
            exit = true;
        else {
            adminService.dump();
            adminService.shutdownChannel();
        }
    }
    @SuppressWarnings("unused")
    private void gossip(String line){
        String[] split = line.split(SPACE);

        if (split.length != 2){
            this.printUsage();
            return;
        }
        String server = split[1];
        debug(String.format("server: %s", server));
        debug(String.format("calling lookup with qualifier: %s", server));
        if(!adminService.lookup(server))
            exit = true;
        else {
            adminService.gossip();
            adminService.shutdownChannel();
        }
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
