//Marcin Sztukowski

package uj.wmii.pwj.gvt;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;



public class Gvt {

    private final ExitHandler exitHandler;

    public Gvt(ExitHandler exitHandler) {

        this.exitHandler = exitHandler;
    }

    public static void main(String... args) {

        Gvt gvt = new Gvt(new ExitHandler());
        gvt.mainInternal(args);
    }



    public void mainInternal(String... args) {

        final Path GVT_DIR = Paths.get(".gvt");
        int n = args.length ;

        if ( n==0 )
        {
            String wyjscie = "Please specify command.";
            this.exitHandler.exit(1,wyjscie);
            return;
        }

        if ( !Files.exists(GVT_DIR ) && !args[0].equals("init") )
        {
            exitHandler.exit(-2, "Current directory is not initialized. Please use init command to initialize.");
            return;
        }





        PropertiesHandler.initializeExitHandler(exitHandler);
        String command = args[0];


        switch (command) {
            case "init" -> {
                try {
                    PropertiesHandler.init();
                } catch (IOException e) {
                    e.printStackTrace();
                    exitHandler.exit(-3, "Underlying system problem. See ERR for details.");
                }
            }
            case "add" -> {
                try {
                    if (n == 2) {
                        PropertiesHandler.add(args[1], "");
                    } else if (n == 4) {
                        PropertiesHandler.add(args[1], args[3]);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    exitHandler.exit(-3, "Underlying system problem. See ERR for details.");
                }
            }
            case "detach" -> {
                try {
                    if (n == 1) {
                        exitHandler.exit(30, "Please specify file to detach.");
                    } else if (n == 2) {
                        PropertiesHandler.detach(args[1], "");
                    } else if (n == 4) {
                        PropertiesHandler.detach(args[1], args[3]);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    exitHandler.exit(-3, "Underlying system problem. See ERR for details.");
                }
            }
            case "commit" -> {
                try {
                    if (n == 1) {
                        exitHandler.exit(50, "Please specify file to commit.");
                    } else if (n == 2) {
                        PropertiesHandler.commit(args[1], "");
                    } else if (n == 4) {
                        PropertiesHandler.commit(args[1], args[3]);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    exitHandler.exit(-3, "Underlying system problem. See ERR for details.");
                }
            }
            case "history" -> {
                try {
                    if (n == 1 || n == 2) {
                        PropertiesHandler.showAllNVersions();
                    } else {
                        PropertiesHandler.showLastNVersions(Integer.parseInt(args[2]));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    exitHandler.exit(-3, "Underlying system problem. See ERR for details.");
                }
            }
            case "version" -> {
                try {
                    if (n > 1) {
                        PropertiesHandler.showVersionDetails(args[1]);
                    } else {
                        PropertiesHandler.showVersionDetails("");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    exitHandler.exit(-3, "Underlying system problem. See ERR for details.");
                }
            }
            case "checkout" -> {
                try {
                    PropertiesHandler.checkout(args[1]);
                } catch (IOException e) {
                    e.printStackTrace();
                    exitHandler.exit(-3, "Underlying system problem. See ERR for details.");
                }
            }
            default -> {
                String wyjscie = String.format("Unknown command %s.", command);
                this.exitHandler.exit(1, wyjscie);
            }
        }

    }
}
