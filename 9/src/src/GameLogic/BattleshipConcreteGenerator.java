//Marcin Sztukowski

package GameLogic;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;


public class BattleshipConcreteGenerator
{
    private static final int SIZE = 10;
    private static final char SHIP = '#';
    private static final char PSEUDO_SHIP = '-';
    private static final char WATER = '.';

    public static List<List<int[]>> generateMapGiveInfo() {
        char[][] shipArray = new char[10][10];
        for (char[] chars : shipArray) {
            Arrays.fill(chars, '.');
        }

        List<List<int[]>> ships = new ArrayList<>();

        ships.add(placeShipGiveInfo(4, shipArray)); // Czteromasztowiec
        ships.add(placeShipGiveInfo(3, shipArray)); // Trzymasztowiec
        ships.add(placeShipGiveInfo(3, shipArray)); // Trzymasztowiec
        ships.add(placeShipGiveInfo(2, shipArray)); // Dwumasztowiec
        ships.add(placeShipGiveInfo(2, shipArray)); // Dwumasztowiec
        ships.add(placeShipGiveInfo(2, shipArray)); // Dwumasztowiec
        ships.add(placeShipGiveInfo(1, shipArray)); // Jednomasztowiec
        ships.add(placeShipGiveInfo(1, shipArray)); // Jednomasztowiec
        ships.add(placeShipGiveInfo(1, shipArray)); // Jednomasztowiec
        ships.add(placeShipGiveInfo(1, shipArray)); // Jednomasztowiec

        return ships;
    }

    public static List<int[]> placeShipGiveInfo(int shipSize, char[][] shipArray) {
        Random rand = new Random();
        boolean shipPlaced = false;

        List<int[]> positions = new ArrayList<>();

        while (!shipPlaced) {
            int x1 = -1;
            int y1 = -1;

            while (!is_area_safe(x1, y1, shipArray)) {
                x1 = rand.nextInt(10);
                y1 = rand.nextInt(10);
            }

            int xTemp = x1;
            int yTemp = y1;

            List<int[]> tempPositions = new ArrayList<>();
            tempPositions.add(new int[]{xTemp, yTemp});
            shipArray[xTemp][yTemp] = '-';

            int direction;
            int attempts = 0;
            int maxAttempts = 10;

            while (tempPositions.size() < shipSize && attempts < maxAttempts) {
                direction = rand.nextInt(4); // 0: prawo, 1: góra, 2: lewo, 3: dół
                switch (direction) {
                    case 0: // Prawo
                        if (is_area_safe(xTemp, yTemp + 1, shipArray) && shipArray[xTemp][yTemp + 1] != '-') {
                            tempPositions.add(new int[]{xTemp, yTemp + 1});
                            shipArray[xTemp][yTemp + 1] = '-';
                            yTemp++;
                        }
                        break;
                    case 1: // Góra
                        if (is_area_safe(xTemp - 1, yTemp, shipArray) && shipArray[xTemp - 1][yTemp] != '-') {
                            tempPositions.add(new int[]{xTemp - 1, yTemp});
                            shipArray[xTemp - 1][yTemp] = '-';
                            xTemp--;
                        }
                        break;
                    case 2: // Lewo
                        if (is_area_safe(xTemp, yTemp - 1, shipArray) && shipArray[xTemp][yTemp - 1] != '-') {
                            tempPositions.add(new int[]{xTemp, yTemp - 1});
                            shipArray[xTemp][yTemp - 1] = '-';
                            yTemp--;
                        }
                        break;
                    case 3: // Dół
                        if (is_area_safe(xTemp + 1, yTemp, shipArray) && shipArray[xTemp + 1][yTemp] != '-') {
                            tempPositions.add(new int[]{xTemp + 1, yTemp});
                            shipArray[xTemp + 1][yTemp] = '-';
                            xTemp++;
                        }
                        break;
                }
                attempts++;
            }


            if (tempPositions.size() == shipSize) {
                for (int[] pos : tempPositions) {
                    shipArray[pos[0]][pos[1]] = '#';
                    positions.add(pos);
                }
                shipPlaced = true;
            } else {

                for (int[] pos : tempPositions) {
                    shipArray[pos[0]][pos[1]] = '.';
                }
            }
        }

        return positions; // Zwróć współrzędne statku
    }


    public static boolean is_area_safe(int x, int y, char[][] ship_array) {
        if (x < 0 || x >= SIZE || y < 0 || y >= SIZE) {
            return false;
        }

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int newX = x + i;
                int newY = y + j;
                if (newX >= 0 && newX < SIZE && newY >= 0 && newY < SIZE) {
                    if (ship_array[newX][newY] == SHIP) {
                        return false;
                    }
                }
            }
        }

        return true;
    }


    public static String arrayToString(char[][] ship_array) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ship_array.length; i++) {
            for (int j = 0; j < ship_array[i].length; j++) {
                sb.append(ship_array[i][j]);
            }
        }
        return sb.toString();
    }



    public static char[][] convertShipInfoToMap(List<List<int[]>> shipInfo) {
        int SIZE = 10;
        char[][] map = new char[SIZE][SIZE];

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                map[i][j] = '.'; // Woda
            }
        }

        for (List<int[]> ship : shipInfo) {
            for (int[] position : ship) {
                int x = position[0];
                int y = position[1];
                map[x][y] = '#';
            }
        }
        return map;
    }


    public static void saveMapInfoToFile(String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // Generowanie informacji o statkach
            List<List<int[]>> mapInfo = generateMapGiveInfo();

            // Zapis współrzędnych do pliku
            for (List<int[]> ship : mapInfo) {
                for (int[] position : ship) {
                    writer.write(position[0] + "," + position[1]); // Zapisujemy współrzędne jako x,y
                    writer.newLine();
                }
                writer.newLine(); // Oddzielamy statki pustą linią
            }
        }
        System.out.println("Map info saved to file: " + filePath);
    }


    public static void place_ship( int ship_size ,char[][] ship_array )
    {
        Random rand = new Random();
        boolean ship_placed = false;

        while ( !ship_placed )
        {

            int x1 = -1;
            int y1 = -1;
            while ( !is_area_safe(x1,y1,ship_array) )
            {
                x1 = rand.nextInt( SIZE );
                y1 = rand.nextInt( SIZE );
            }

            int x_temp = x1;
            int y_temp = y1;

            int[] x_table = new int[ ship_size ];
            int[] y_table = new int[ ship_size ];
            int index_table = 1 ;

            x_table[ 0 ] = x_temp;
            y_table[ 0 ] = y_temp;

            ship_array[ x_temp ][ y_temp ] = '-';


            // 0 to prawo
            // 1 to gora
            // 2 to lewo
            // 3 to dol
            int kierunek;
            int ile_prob=0;
            int max_prob = 10;

            while  ( index_table < ship_size && ile_prob < max_prob )
            {
                kierunek = rand.nextInt( 4 );
                switch ( kierunek )
                {
                    case 0:
                        if ( is_area_safe(x_temp,y_temp+1,ship_array) &&  ship_array[ x_temp ][ y_temp+1 ] != PSEUDO_SHIP )
                        {
                            x_table[ index_table ] = x_temp;
                            y_table[ index_table ] = y_temp+1;
                            ship_array[ x_temp ][ y_temp+1 ] = PSEUDO_SHIP ;
                            index_table++;
                            y_temp = y_temp+1;
                        }
                        break;
                    case 1:
                        if ( is_area_safe(x_temp-1,y_temp,ship_array) &&  ship_array[ x_temp -1 ][ y_temp ] != PSEUDO_SHIP )
                        {
                            x_table[ index_table ] = x_temp-1;
                            y_table[ index_table ] = y_temp;
                            ship_array[ x_temp -1 ][ y_temp ] = PSEUDO_SHIP;
                            index_table++;
                            x_temp = x_temp-1;
                        }
                        break;
                    case 2:
                        if ( is_area_safe(x_temp,y_temp-1,ship_array) &&  ship_array[ x_temp ][ y_temp-1 ] != PSEUDO_SHIP )
                        {
                            x_table[ index_table ] = x_temp;
                            y_table[ index_table ] = y_temp-1;
                            ship_array[ x_temp ][ y_temp-1 ] = PSEUDO_SHIP;
                            index_table++;
                            y_temp = y_temp-1;
                        }
                        break;
                    case 3:
                        if ( is_area_safe(x_temp+1,y_temp,ship_array) &&  ship_array[ x_temp+1 ][ y_temp ] != PSEUDO_SHIP )
                        {
                            x_table[ index_table ] = x_temp+1;
                            y_table[ index_table ] = y_temp;
                            ship_array[ x_temp+1 ][ y_temp ] = '-';
                            index_table++;
                            x_temp = x_temp+1;
                        }
                        break;
                }
                ile_prob++;
            }
            if ( index_table == ship_size )
            {
                for ( int i = 0 ; i < ship_size ; i++ )
                {
                    ship_array[ x_table[i] ][  y_table[i]  ] = SHIP;
                }
                ship_placed = true;
            } else {

                for (int i = 0; i < index_table; i++) {
                    ship_array[x_table[i]][y_table[i]] = WATER;
                }

            }
        }
    }


    public static void saveMapToFile(String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            char[][] mapContent = generateArrayMap();

            for (int i = 0; i < mapContent.length; i++) {
                for (int j = 0; j < mapContent[i].length; j++) {
                    writer.write(mapContent[i][j]);
                }
                writer.newLine();
            }
        }
        System.out.println("Map saved to file: " + filePath);
    }


    public static String generateMap(){
        char[][] ship_array = generateArrayMap();
        return arrayToString(ship_array);
    }


    public static char[][] generateUnknownArrayMap(){
        char[][] ship_array = new char[10][10];
        for ( int i = 0; i < ship_array.length; i++ )
        {
            for ( int j = 0; j < ship_array[i].length; j++ )
            {
                ship_array[i][j] = '?';
            }
        }
        return ship_array;
    }


    public static char[][] generateArrayMap(){

        char[][] ship_array = new char[10][10];
        for ( int i = 0; i < ship_array.length; i++ )
        {
            for ( int j = 0; j < ship_array[i].length; j++ )
            {
                ship_array[i][j] = '.';
            }
        }


        place_ship(4,ship_array);

        place_ship(3,ship_array);
        place_ship(3,ship_array);

        place_ship(2,ship_array);
        place_ship(2,ship_array);
        place_ship(2,ship_array);

        place_ship(1,ship_array);
        place_ship(1,ship_array);
        place_ship(1,ship_array);
        place_ship(1,ship_array);

        return ship_array ;
    }

}
