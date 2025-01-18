//Marcin Sztukowski

package uj.wmii.pwj.spreadsheet;


public class Spreadsheet {

    public static int charToNumber(char letter) {
        switch (letter) {
            case 'A': return 0;
            case 'B': return 1;
            case 'C': return 2;
            case 'D': return 3;
            case 'E': return 4;
            case 'F': return 5;
            case 'G': return 6;
            case 'H': return 7;
            case 'I': return 8;
            case 'J': return 9;
            case 'K': return 10;
            case 'L': return 11;
            case 'M': return 12;
            case 'N': return 13;
            case 'O': return 14;
            case 'P': return 15;
            case 'Q': return 16;
            case 'R': return 17;
            case 'S': return 18;
            case 'T': return 19;
            case 'U': return 20;
            case 'V': return 21;
            case 'W': return 22;
            case 'X': return 23;
            case 'Y': return 24;
            case 'Z': return 25;
            default: throw new IllegalArgumentException("Invalid character: " + letter);
        }
    }


    public String oblicz_wyrazenie (int gdzie_jestes_wiersz , int gdzie_jestes_kolumna , String[][] input_tablica )
    {

        String wynik_string = "" ;

        if ( input_tablica[gdzie_jestes_wiersz][gdzie_jestes_kolumna].charAt(0) == '=' )
        {
            int wynik_int = 0;
            String referencja = input_tablica[gdzie_jestes_wiersz][gdzie_jestes_kolumna] ;
            String komenda = input_tablica[gdzie_jestes_wiersz][gdzie_jestes_kolumna].substring(1,4)  ;


            // Znajdź początek operandów
            int poczatek = referencja.indexOf('(') + 1;
            // Znajdź przecinek
            int przecinek = referencja.indexOf(',');
            // Znajdź koniec operandów
            int koniec = referencja.indexOf(')');


            String operand_lewy = referencja.substring(poczatek,przecinek);
            String operand_prawy = referencja.substring(przecinek + 1,koniec);

            int lewa_strona =  0;
            int prawa_strona = 0;

            if ( operand_lewy.charAt(0) == '$' )
            {
                char kolumna_temp = operand_lewy.charAt(1);
                String wiersz_temp = operand_lewy.substring(2,operand_lewy.length());

                int wierszz = Integer.parseInt(wiersz_temp)-1;
                int kolumnaa =  charToNumber(kolumna_temp );

                lewa_strona =  Integer.parseInt(oblicz_wyrazenie(wierszz,kolumnaa,input_tablica) ) ;

            } else {
                lewa_strona = Integer.parseInt( operand_lewy );
            }

            if ( operand_prawy.charAt(0) == '$' )
            {
                char kolumna_temp = operand_prawy.charAt(1);
                String wiersz_temp = operand_prawy.substring(2,operand_prawy.length());

                int wierszz = Integer.parseInt(wiersz_temp)-1;
                int kolumnaa =  charToNumber(kolumna_temp );

                prawa_strona =  Integer.parseInt(oblicz_wyrazenie(wierszz,kolumnaa,input_tablica) ) ;

            } else {
                prawa_strona = Integer.parseInt( operand_prawy );
            }



            switch (komenda) {
                case "ADD":
                    wynik_int = lewa_strona + prawa_strona;
                    break;
                case "SUB":
                    wynik_int = lewa_strona - prawa_strona;
                    break;
                case "MUL":
                    wynik_int = lewa_strona * prawa_strona;
                    break;
                case "DIV":
                    wynik_int = lewa_strona / prawa_strona;
                    break;
                case "MOD":
                    wynik_int = lewa_strona % prawa_strona;
                    break;
            }

            wynik_string = String.valueOf(wynik_int);
            input_tablica[gdzie_jestes_wiersz][gdzie_jestes_kolumna] = wynik_string ;

        } else if ( input_tablica[gdzie_jestes_wiersz][gdzie_jestes_kolumna].charAt(0) == '$' )
        {
            String referencja = input_tablica[gdzie_jestes_wiersz][gdzie_jestes_kolumna] ;
            char kolumna_temp = referencja.charAt(1);
            String wiersz_temp = referencja.substring(2,referencja.length());

            int wierszz = Integer.parseInt(wiersz_temp)-1;
            int kolumnaa =  charToNumber(kolumna_temp );

            wynik_string = oblicz_wyrazenie(wierszz,kolumnaa,input_tablica)  ;
            input_tablica[gdzie_jestes_wiersz][gdzie_jestes_kolumna] = wynik_string ;

        } else {
            wynik_string = input_tablica[gdzie_jestes_wiersz][gdzie_jestes_kolumna] ;
        }


        return wynik_string ;

    }

    public String[][] calculate(String[][] input) {


        for ( int i = 0 ; i < input.length ; i++ )
        {
            for ( int j = 0 ; j < input[i].length ; j++ )
            {
                oblicz_wyrazenie(i, j, input);
            }
        }


        return input;
    }
}


