//Marcin Sztukowski

package Server;
import GameLogic.GameMaps;

import java.net.*;
import java.io.*;

public class Server implements Runnable {
    private Socket socket = null;
    private ServerSocket server = null;
    private DataInputStream in = null;
    private DataOutputStream out = null;

    private  String SERVER_IP = "127.0.0.1";
    //private  String SERVER_IP = "172.20.10.2";
    //private  String SERVER_IP = "192.168.0.237";

    private  int SERVER_PORT = 5000;
    private String MAP_LOCATION;

    private boolean isGameOver = false;


    public Server( String host_name ,int port_in , String map_path  ) {

            SERVER_IP = host_name;
            SERVER_PORT = port_in;
            MAP_LOCATION = map_path;
    }

    @Override
    public void run() {
        try {
            GameMaps gMaps = new GameMaps(MAP_LOCATION);

            server = new ServerSocket(SERVER_PORT, 1, InetAddress.getByName(SERVER_IP));
            System.out.println("Server started on " + SERVER_IP + ":" + SERVER_PORT);
            System.out.println("Waiting for a client...");
            socket = server.accept();
            System.out.println("Client accepted");

            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            boolean serverTurn = false;

            String answerToEnemyAttack = "";
            String wholeMessageToEnemy = "";
            String lasValidMessage = "";
            String lastReceivedMessage = "";

            int retryCount = 0;

            while (!isGameOver) {
                try {
                    if (serverTurn)
                    {
                        socket.setSoTimeout(0);
                        if ( answerToEnemyAttack.contains("ostatni zatopiony") )
                        {
                            wholeMessageToEnemy = "ostatni zatopiony";
                            out.writeUTF(wholeMessageToEnemy);
                            out.flush();
                            System.out.println("\nYOU LOST !");
                            isGameOver = true;
                        } else
                        {
                            System.out.print("Enter message: ");
                            BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));

                            String serverMessage = consoleInput.readLine();
                            wholeMessageToEnemy = answerToEnemyAttack + ';' + serverMessage ;

                            if (gMaps.validateCommand(wholeMessageToEnemy)) {
                                serverTurn = false;

                                gMaps.putlastShotCoords(serverMessage);
                                System.out.println("Sent message: " + wholeMessageToEnemy);
                                lasValidMessage = wholeMessageToEnemy;

                                out.writeUTF(wholeMessageToEnemy);
                                out.flush();
                                retryCount=0;
                            } else {
                                System.out.println("Invalid command. Try again.");
                            }
                        }

                    } else {
                        socket.setSoTimeout(5000);
                        String message = in.readUTF();

                        if ( !lastReceivedMessage.equals(message) )
                        {
                            if (gMaps.validateCommand(message)) {
                                retryCount=0;
                                lastReceivedMessage = message;
                                answerToEnemyAttack = gMaps.handleCommand(message);

                                System.out.println("Enemy: " + message);

                                if ( message.contains("ostatni zatopiony") )
                                {
                                    System.out.println("YOU WON !");
                                    isGameOver = true;
                                }
                            }
                        }

                        gMaps.printAllMaps();
                        serverTurn = true;

                    }

                } catch (SocketTimeoutException e) {
                    System.out.println("Socket timed out");
                    retryCount++;

                    if (serverTurn) {
                        out.writeUTF(lasValidMessage);
                        out.flush();
                        System.out.println("Last message resent.");
                    }

                    if (retryCount >= 3) {
                        System.out.println("Unable to restore connection. Ending game.");
                        isGameOver = true;
                    }
                } catch (IOException e) {
                    System.out.println("Error: " + e.getMessage());
                    isGameOver = true;
                }
            }
            System.out.println("\nEND OF GAME\nEND RESULTS:\n");
            gMaps.printAllMaps();
        }

        catch (IOException io) {
            System.out.println(io);
        } finally {
            try {
                System.out.println("Closing connection");
                if (socket != null) socket.close();
                if (in != null) in.close();
                if (out != null) out.close();
            } catch (IOException e) {
                System.out.println("Error while closing connection: " + e.getMessage());
            }
        }
    }
}
