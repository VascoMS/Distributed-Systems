package pt.tecnico.distledger.userclient;


import pt.tecnico.distledger.userclient.grpc.UserService;

import java.util.Scanner;

import static java.lang.Integer.parseInt;

public class CommandParser {

    private static final String SPACE = " ";
    private static final String CREATE_ACCOUNT = "createAccount";
    private static final String DELETE_ACCOUNT = "deleteAccount";
    private static final String TRANSFER_TO = "transferTo";
    private static final String BALANCE = "balance";
    private static final String HELP = "help";
    private static final String EXIT = "exit";

    private final UserService userService;

    public void debug(String debugMessage){
        UserClientMain.debug(debugMessage);
    }

    public CommandParser(UserService userService) {
        this.userService = userService;
    }

    void parseInput() {

        //  String[] result = target.split(":");
        //  String host = result[0];
        //  int port = parseInt(result[1]);
        //  userService.createChannelAndStub(host, port);

        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        while (!exit) {
            System.out.print("> ");
            String line = scanner.nextLine().trim();
            String cmd = line.split(SPACE)[0];

            try {
                switch (cmd) {
                    case CREATE_ACCOUNT:
                        this.createAccount(line);
                        userService.shutdownChannel();
                        break;

                    case DELETE_ACCOUNT:
                        this.deleteAccount(line);
                        userService.shutdownChannel();
                        break;

                    case TRANSFER_TO:
                        this.transferTo(line);
                        userService.shutdownChannel();
                        break;

                    case BALANCE:
                        this.balance(line);
                        userService.shutdownChannel();
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
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }

    }

    private void createAccount(String line) {
        String[] split = line.split(SPACE);

        if (split.length != 3) {
            this.printUsage();
            return;
        }

        String server = split[1];
        String username = split[2];
        debug(String.format("server: %s, username: %s", server, username));
        userService.lookup(server);
        userService.createAccount(username);
    }

    private void deleteAccount(String line) {
        String[] split = line.split(SPACE);

        if (split.length != 3) {
            this.printUsage();
            return;
        }
        String server = split[1];
        String username = split[2];
        debug(String.format("server: %s, username: %s", server, username));
        userService.lookup(server);
        userService.deleteAccount(username);
    }


    private void balance(String line) {
        String[] split = line.split(SPACE);

        if (split.length != 3) {
            this.printUsage();
            return;
        }
        String server = split[1];
        String username = split[2];
        debug(String.format("server: %s, username: %s", server, username));
        userService.lookup(server);
        userService.balance(username);
    }

    private void transferTo(String line) {
        String[] split = line.split(SPACE);

        if (split.length != 5) {
            this.printUsage();
            return;
        }
        String server = split[1];
        String from = split[2];
        String dest = split[3];
        Integer amount = Integer.valueOf(split[4]);
        debug(String.format("server: %s, from: %s, dest: %s, amount: %d", server, from, dest, amount));
        userService.lookup(server);
        userService.transferTo(from, dest, amount);
    }

    private void printUsage() {
        System.out.println("Usage:\n" +
                "- createAccount <server> <username>\n" +
                "- deleteAccount <server> <username>\n" +
                "- balance <server> <username>\n" +
                "- transferTo <server> <username_from> <username_to> <amount>\n" +
                "- exit\n");
    }
}
