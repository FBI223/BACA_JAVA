//Marcin Sztukowski

import Client.*;
import GameLogic.BattleshipConcreteGenerator;
import Server.*;

import java.net.InetAddress;
import java.nio.file.Path;
import java.nio.file.Paths;

public class App {

    public static void main(String[] args) {
        if (args.length < 4) {
            System.out.println("Usage: java App -mode [server|client] -port N -map map-file [-host hostName]");
            return;
        }

        String mode = null;
        int port = 0;
        String mapFile = null;
        String hostName = null; // tylko dla trybu klienta

        try {
            for (int i = 0; i < args.length; i++) {
                switch (args[i]) {
                    case "-mode":
                        mode = getArgument(args, ++i, "-mode");
                        if (!"server".equals(mode) && !"client".equals(mode)) {
                            throw new IllegalArgumentException("Invalid mode. Use 'server' or 'client'.");
                        }
                        break;
                    case "-port":
                        port = Integer.parseInt(getArgument(args, ++i, "-port"));
                        if (port <= 0 || port > 65535) {
                            throw new IllegalArgumentException("Port number must be between 1 and 65535.");
                        }
                        break;
                    case "-map":
                        mapFile = getArgument(args, ++i, "-map");
                        break;
                    case "-host":
                        hostName = getArgument(args, ++i, "-host");
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown parameter: " + args[i]);
                }
            }

            if ("server".equals(mode)) {
                startServer(port, mapFile);
            } else if ("client".equals(mode)) {
                if (hostName == null) {
                    throw new IllegalArgumentException("Host name is required in client mode.");
                }
                startClient(port, mapFile, hostName);
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static String getArgument(String[] args, int index, String paramName) {
        if (index >= args.length) {
            throw new IllegalArgumentException("Missing value for " + paramName);
        }
        return args[index];
    }

    private static void startServer(int port, String mapFile) throws Exception {
        System.out.println("Starting server on port " + port);
        Path currentPath = Paths.get("").toAbsolutePath();
        Path serverMapPath = currentPath.resolve("serverMap.txt");
        BattleshipConcreteGenerator.saveMapInfoToFile(serverMapPath.toString());
        //SERVER_IP = "172.20.10.2"
        //SERVER_IP = "192.168.0.237"
        //SERVER_IP = "127.0.0.1"
        Server server = new Server( "172.20.10.2" ,port, mapFile);
        server.run();
    }

    private static void startClient(int port, String mapFile, String hostName) throws Exception {
        System.out.println("Starting client connecting to " + hostName + " on port " + port);
        Path currentPath = Paths.get("").toAbsolutePath();
        Path clientMapPath = currentPath.resolve("clientMap.txt");
        BattleshipConcreteGenerator.saveMapInfoToFile(clientMapPath.toString());
        Client client = new Client(hostName,port , mapFile);
        client.run();
    }
}
