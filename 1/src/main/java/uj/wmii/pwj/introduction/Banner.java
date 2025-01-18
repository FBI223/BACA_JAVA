//Marcin Sztukowski

package uj.wmii.pwj.introduction;

public class Banner {


    // Funkcja zwracająca ASCII reprezentację litery
    public static String[] getAsciiLetter(char letter) {
        switch (Character.toUpperCase(letter)) {
            case 'A':
                return new String[]{

                        "   #   " ,
                        "  # #  " ,
                        " #   # " ,
                        "#     #" ,
                        "#######" ,
                        "#     #" ,
                        "#     #"
                };
            case 'B':
                return new String[]{
                        "###### ",
                        "#     #",
                        "#     #",
                        "###### ",
                        "#     #",
                        "#     #",
                        "###### "
                };
            case 'C':
                return new String[]{
                        " ##### ",
                        "#     #",
                        "#      ",
                        "#      ",
                        "#      ",
                        "#     #",
                        " ##### "
                };
            case 'D':
                return new String[]{
                        "###### ",
                        "#     #",
                        "#     #",
                        "#     #",
                        "#     #",
                        "#     #",
                        "###### "
                };
            case 'E':
                return new String[]{
                        "#######",
                        "#      ",
                        "#      ",
                        "#####  ",
                        "#      ",
                        "#      ",
                        "#######"
                };
            case 'F':
                return new String[]{
                        "#######",
                        "#      ",
                        "#      ",
                        "#####  ",
                        "#      ",
                        "#      ",
                        "#      "
                };
            case 'G':
                return new String[]{
                        " ##### ",
                        "#     #",
                        "#      ",
                        "#  ####",
                        "#     #",
                        "#     #",
                        " ##### "
                };
            case 'H':
                return new String[]{
                        "#     #",
                        "#     #",
                        "#     #",
                        "#######",
                        "#     #",
                        "#     #",
                        "#     #"
                };
            case 'I':
                return new String[]{
                        "###",
                        " # ",
                        " # ",
                        " # ",
                        " # ",
                        " # ",
                        "###"
                };
            case 'J':
                return new String[]{
                        "      #" ,
                        "      #" ,
                        "      #" ,
                        "      #" ,
                        "#     #" ,
                        "#     #" ,
                        " ##### "
                };
            case 'K':
                return new String[]{
                        "#    #" ,
                        "#   # " ,
                        "#  #  " ,
                        "###   " ,
                        "#  #  " ,
                        "#   # " ,
                        "#    #"
                };
            case 'L':
                return new String[]{
                        "#      ",
                        "#      ",
                        "#      ",
                        "#      ",
                        "#      ",
                        "#      ",
                        "#######"
                };
            case 'M':
                return new String[]{
                        "#     #",
                        "##   ##",
                        "# # # #",
                        "#  #  #",
                        "#     #",
                        "#     #",
                        "#     #"
                };
            case 'N':
                return new String[]{
                        "#     #",
                        "##    #",
                        "# #   #",
                        "#  #  #",
                        "#   # #",
                        "#    ##",
                        "#     #"
                };
            case 'O':
                return new String[]{
                        "#######",
                        "#     #",
                        "#     #",
                        "#     #",
                        "#     #",
                        "#     #",
                        "#######"
                };
            case 'P':
                return new String[]{
                        "###### ",
                        "#     #",
                        "#     #",
                        "###### ",
                        "#      ",
                        "#      ",
                        "#      "
                };
            case 'Q':
                return new String[]{
                        " ##### ",
                        "#     #",
                        "#     #",
                        "#     #",
                        "#   # #",
                        "#    # ",
                        " #### #"
                };
            case 'R':
                return new String[]{
                        "###### ",
                        "#     #",
                        "#     #",
                        "###### ",
                        "#   #  ",
                        "#    # ",
                        "#     #"
                };
            case 'S':
                return new String[]{
                        " ##### ",
                        "#     #",
                        "#      ",
                        " ##### ",
                        "      #",
                        "#     #",
                        " ##### "
                };
            case 'T':
                return new String[]{
                        "#######",
                        "   #   ",
                        "   #   ",
                        "   #   ",
                        "   #   ",
                        "   #   ",
                        "   #   "
                };
            case 'U':
                return new String[]{
                        "#     #",
                        "#     #",
                        "#     #",
                        "#     #",
                        "#     #",
                        "#     #",
                        " ##### "
                };
            case 'V':
                return new String[]{
                        "#     #",
                        "#     #",
                        "#     #",
                        "#     #",
                        " #   # ",
                        "  # #  ",
                        "   #   "
                };
            case 'W':
                return new String[]{
                        "#     #",
                        "#  #  #",
                        "#  #  #",
                        "#  #  #",
                        "#  #  #",
                        "#  #  #",
                        " ## ## "
                };
            case 'X':
                return new String[]{
                        "#     #",
                        " #   # ",
                        "  # #  ",
                        "   #   ",
                        "  # #  ",
                        " #   # ",
                        "#     #"
                };
            case 'Y':
                return new String[]{
                        "#     #",
                        " #   # ",
                        "  # #  ",
                        "   #   ",
                        "   #   ",
                        "   #   ",
                        "   #   "
                };
            case 'Z':
                return new String[]{
                        "#######",
                        "     # ",
                        "    #  ",
                        "   #   ",
                        "  #    ",
                        " #     ",
                        "#######"
                };
            default:
                return new String[]{
                        "       ",
                        "       ",
                        "       ",
                        "       ",
                        "       ",
                        "       ",
                        "       "
                };
        }
    }

    public String[] toBanner(String input) {

        if ( input != null )
        {
            String[] wynik = new String[ 7 ];

            for ( int i = 0; i < 7 ; i++ )
            {
                String wiersz = "" ;
                for ( int j = 0 ; j < input.length() ; j++)
                {
                    if ( input.charAt(j) != ' ' )
                    {
                        String[] literka_duza = getAsciiLetter(input.charAt(j) );
                        wiersz += literka_duza[i];
                        if ( j + 1 < input.length() && input.charAt(j+1) != ' '  )
                        {
                            wiersz += " ";
                        }
                    } else {
                        wiersz += "    ";
                    }

                }
                wynik[i] = wiersz;
            }
            return wynik;
        }

        return new String[0] ;
    }

}

