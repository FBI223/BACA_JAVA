//Marcin Sztukowski

package uj.wmii.pwj.collections;
import java.util.Random;

public class BattleshipImplementation implements BattleshipGenerator
{

    private static final int SIZE = 10;
    private static final char SHIP = '#';
    private static final char PSEUDO_SHIP = '-';
    private static final char WATER = '.';


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

    public static boolean is_area_safe(int x, int y, char[][] ship_array) {
        // Sprawdzenie czy punkt (x, y) jest w zakresie tablicy 10x10
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


    @Override
    public String generateMap(){


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


        String wynik = arrayToString(ship_array);


        return wynik ;
    }
}
