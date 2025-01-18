//Marcin Sztukowski

package Client;
import GameLogic.GameMaps;

import java.net.*;
import java.io.*;

// -mode client -port 5000 -map clientMap.txt -host 192.168.0.237
// -mode client -port 5000 -map clientMap.txt -host 172.20.10.2
// -mode client -port 5000 -map clientMap.txt -host 127.0.0.1

public class Client implements Runnable {
    private Socket socket = null;
    private DataOutputStream out = null;
    private DataInputStream in = null;

    private String SERVER_IP = "172.20.10.2";
    private int SERVER_PORT = 5000;
    private String MAP_LOCATION;

    private boolean isGameStarted = false;
    private boolean isGameOver = false;

    public Client( String host_name, int port_in , String map_path   ) {
        SERVER_PORT = port_in;
        MAP_LOCATION = map_path;
        SERVER_IP = host_name;
    }

    @Override
    public void run() {
        try {

            GameMaps gMaps = new GameMaps(MAP_LOCATION);

            socket = new Socket(SERVER_IP, SERVER_PORT);
            System.out.println("Connected to server at " + SERVER_IP + ":" + SERVER_PORT);


            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());

            boolean clientTurn = true;

            String answerToEnemyAttack = "";
            String wholeMessageToEnemy = "";
            String lasValidMessage = "";
            String lastReceivedMessage = "";

            int retryCount = 0;

            while (!isGameOver) {
                try {
                    if (clientTurn) {
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

                            String clientMessage = consoleInput.readLine();
                            wholeMessageToEnemy = answerToEnemyAttack + ';' + clientMessage ;

                            if ( !isGameStarted )
                            {
                                wholeMessageToEnemy = "start;" + clientMessage  ;
                            }

                            if (gMaps.validateCommand(wholeMessageToEnemy)) {

                                clientTurn = false;

                                gMaps.putlastShotCoords(clientMessage);
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


                                if ( !isGameStarted )
                                {
                                    isGameStarted = true;
                                }

                                if ( message.contains("ostatni zatopiony") )
                                {
                                    isGameOver = true;
                                    System.out.println("YOU WON !");
                                }

                            }
                        }

                        gMaps.printAllMaps();
                        clientTurn = true;
                    }

                } catch (SocketTimeoutException e) {

                    System.out.println("Socket timed out");
                    retryCount++;

                    if (clientTurn) {
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

            System.out.println("END OF GAME\nEND RESULTS:");
            gMaps.printAllMaps();

        } catch (IOException io) {
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
