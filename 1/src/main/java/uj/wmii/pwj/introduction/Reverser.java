package uj.wmii.pwj.introduction;

public class Reverser {

    public String reverse(String input) {

        String reversed = "";
        if ( input != null )
        {
            String trimmed = input.trim();
            for (int i = trimmed.length() - 1; i >= 0; i--) {
                reversed += trimmed.charAt(i);
            }
        } else {
            reversed = null;
        }



        return reversed;
    }

    public String reverseWords(String input) {

        String[] words = input.split("\\s+");

        int left = 0;
        int right = words.length - 1;

        while (left < right) {
            // Zamiana miejscami elementów
            String temp = words[left];
            words[left] = words[right];
            words[right] = temp;

            // Przesunięcie indeksów
            left++;
            right--;
        }

        String reversed_words = "";

        if (words.length > 0) {
            for (int i = 0 ;  i < words.length - 1 ; i++ ) {
                reversed_words += words[i];
                reversed_words += " ";
            }

            reversed_words += words[ words.length - 1 ] ;
        }



        return reversed_words ;
    }

}
