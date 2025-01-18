//Marcin Sztukowski

package GameLogic;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GameMaps {
    private final int SIZE = 10;
    private char[][] myMapInit;
    private char[][] myMap;
    private char[][] enemyMap;
    List<List<int[]>> myMapInfo;
    boolean[] sunkShips = new boolean[SIZE];
    int[] lastShotCoords = new int[] {-1,-1};


    private static final String COMMAND_REGEX = "^(quit|retry|conection restored|start|pudlo|trafiony|trafiony zatopiony|ostatni zatopiony)(;[A-Ja-j][0-9])?$";

    public GameMaps(String mapFileName) throws IOException {
        loadMapInfoFromFile(mapFileName);
        myMap = BattleshipConcreteGenerator.convertShipInfoToMap(myMapInfo) ;
        myMapInit = BattleshipConcreteGenerator.convertShipInfoToMap(myMapInfo) ;
        enemyMap = BattleshipConcreteGenerator.generateUnknownArrayMap();

    }


    public void putlastShotCoords(String coordinates ) {


        int x = changeLetterToInt( coordinates.charAt(0) );
        int y = changeCharToInt(coordinates.charAt(1) );

        lastShotCoords[0] = x;
        lastShotCoords[1] = y;

    }


    public int changeLetterToInt(char letter) {
        return Character.toLowerCase(letter) - 'a';
    }

    public int changeCharToInt(char digit) {
        return digit - '0';
    }

    public boolean validateCommand(String command) {
        return Pattern.matches(COMMAND_REGEX, command);
    }

    public String[] parseCommand(String command) {
        Pattern pattern = Pattern.compile(COMMAND_REGEX);
        Matcher matcher = pattern.matcher(command);

        if (matcher.matches()) {
            String commandType = matcher.group(1); // retry, start, pudlo, itd.
            String coordinates = matcher.group(2) != null ? matcher.group(2).substring(1) : null; // Usunięcie średnika z ";A1"

            return new String[]{commandType, coordinates};
        }
        return null;
    }


    public String handleCommand(String command) {
        String[] splitedCommand = parseCommand(command);

        String infoLastShot = splitedCommand[0];
        String answerCodeString = "";

        handleMyShot(lastShotCoords[0], lastShotCoords[1] ,infoLastShot );

        if ( splitedCommand[1] != null ) {

            int x = changeLetterToInt(splitedCommand[1].charAt(0));
            int y = changeCharToInt(splitedCommand[1].charAt(1));
            int code = handleEnemyShot(x,y);


            if ( code == -1 )
            {
                answerCodeString = "pudlo" ;
            } else if ( code == 1 )
            {
                answerCodeString = "trafiony" ;
            } else if ( code == 2 )
            {
                answerCodeString = "trafiony zatopiony" ;
            } else if ( code == 3 )
            {
                answerCodeString = "ostatni zatopiony" ;
            } else {
                answerCodeString = "pudlo" ;
            }
        } else
        {
            answerCodeString = "end";
        }

        return answerCodeString;
    }


    public void loadMapInfoFromFile(String filePath) throws IOException {
        myMapInfo = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            List<int[]> currentShip = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    if (!currentShip.isEmpty()) {
                        myMapInfo.add(currentShip);
                        currentShip = new ArrayList<>();
                    }
                } else {
                    String[] parts = line.split(",");
                    int x = Integer.parseInt(parts[0]);
                    int y = Integer.parseInt(parts[1]);
                    currentShip.add(new int[]{x, y, 0});
                }
            }
            if (!currentShip.isEmpty()) {
                myMapInfo.add(currentShip);
            }
        }

    }

    public int[] getCoordinatesGiveShipCoords( int x , int y )
    {
        for (int i = 0; i < myMapInfo.size(); i++) {

            for (int j = 0 ; j < myMapInfo.get(i).size(); j++ ) {
                if ( myMapInfo.get(i).get(j)[0] == x && myMapInfo.get(i).get(j)[1] == y ) {
                    return myMapInfo.get(i).get(j);
                }
            }
        }
        return null;
    }

    public int getCoordinatesGiveShipNumber( int x , int y )
    {
        int coords = -1;
        for (int i = 0; i < myMapInfo.size(); i++) {

            for (int j = 0 ; j < myMapInfo.get(i).size(); j++ ) {
                if ( myMapInfo.get(i).get(j)[0] == x && myMapInfo.get(i).get(j)[1] == y ) {
                    return i;
                }
            }
        }
        return coords;
    }


    private void updateMap(char[][] map, int x, int y, int code) {
        if (code == -1) {
            map[x][y] = '~';
        } else if ( code == 1  )
        {
            map[x][y] = '@';
        } else if ( code == 2 || code == 3) {
            map[x][y] = '@';
        }
    }


    public int handleEnemyShot(int x, int y) {
        int code = 0;
        int noShip = getCoordinatesGiveShipNumber(x, y);
        if ( noShip != -1 )
        {
            if ( isShipSunk(noShip) )
            {
                code = 2;
            } else {
                int [] coords = getCoordinatesGiveShipCoords(x, y);

                if ( coords[2] == 1  )
                {
                    code = 2;
                } else
                {
                    coords[2] = 1;

                    if ( isShipSunk(noShip) )
                    {
                        sunkShips[noShip] = Boolean.TRUE;
                        code = 2;
                        if ( isFleetSunk() ) {code = 3;}
                    } else
                    {
                        code = 1;
                    }
                }
            }
        } else {
            code = -1;
        }

        updateMap(myMap,x, y, code);

        return code;
    }

    public void handleMyShot(int x, int y, String result) {

        int code = 0;

        if ( result.contains("pudlo") )
        {
            code = -1;
        } else if ( result.contains("trafiony") )
        {
            code = 1;
        } else if ( result.contains("trafiony zatopiony") )
        {
            code = 2;
        } else if (result.contains("ostatni zatopiony"))
        {
            code = 3;
        }

        updateMap(enemyMap,x, y, code);

    }

    public boolean isShipSunk(int noShip) {
        for (int i = 0; i < myMapInfo.get(noShip).size() ; i++) {
            if ( myMapInfo.get(noShip).get(i)[2] == 0 ) {
                return false;
            }
        }
        return true;
    }


    public boolean isFleetSunk() {
        for (boolean sunkShip : sunkShips) {
            if (!sunkShip) {
                return false;
            }
        }
        return true;
    }


    public void printMyMap() {
        System.out.println("My map");
        for (char[] row : myMap) {
            for (char c : row) {
                System.out.print(c + " ");
            }
            System.out.println();
        }
    }

    public void printEnemyMap() {
        System.out.println("Enemy map");
        for (char[] row : enemyMap) {
            for (char c : row) {
                System.out.print(c + " ");
            }
            System.out.println();
        }
    }

    public void printAllMaps()
    {
        System.out.println("------------------------------------------------");
        printMyMap();
        printEnemyMap();
        System.out.println("------------------------------------------------");
    }

}

