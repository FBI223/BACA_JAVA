package uj.wmii.pwj.collections;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Stack;


public class BrainfuckInterpreter implements Brainfuck
{

    String code;
    private PrintStream out;
    private InputStream in;
    private int stackSize;

    public BrainfuckInterpreter(String code, PrintStream out ,InputStream in, int stackSize)
    {

        if ( code == null || code.length() == 0 || out == null || in == null || stackSize < 1 )
        {
            throw new IllegalArgumentException("Invalid arguments");
        } else {
            this.code = code;
            this.out = out;
            this.in = in;
            this.stackSize = stackSize;
        }
    }

    @Override
    public void execute()  {
        //int tape_size = 1024 ;
        byte[] tape = new byte[this.stackSize];  // Tablica typu byte o rozmiarze 1024
        int pointer_tape = 0;
        int pointer_code = 0;
        Stack<Integer> loopStack = new Stack<>();

        while ( pointer_code < code.length() )
        {

            char znak_polecenie = this.code.charAt(pointer_code);

            if ( znak_polecenie == '>' )
            {
                pointer_tape++;
            } else if ( znak_polecenie == '<' )
            {
                pointer_tape--;
            } else if ( znak_polecenie == '+' )
            {
                tape[pointer_tape]++;
            } else if ( znak_polecenie == '-' )
            {
                tape[pointer_tape]--;
            } else if ( znak_polecenie == '.' )
            {
                this.out.print( (char)tape[pointer_tape] );
            } else if ( znak_polecenie == ',' )
            {
                try {
                    int x = this.in.read();  // Odczytaj jeden bajt
                    tape[pointer_tape] = (byte) x;  // Zapisz odczytaną wartość w bieżącej komórce taśmy
                } catch (IOException e) {
                    throw new RuntimeException("Error reading input", e);
                }
            } else if ( znak_polecenie == '[' )
            {
                if ( tape[pointer_tape] == 0 )
                {

                    int loopLevel = 1;
                    while (loopLevel > 0)
                    {
                        pointer_code++;
                        if (pointer_code >= code.length()) {
                            throw new IllegalArgumentException("No matching ']' found for '['");
                        }
                        if (this.code.charAt(pointer_code) == '[') {
                            loopLevel++;
                        } else if (this.code.charAt(pointer_code) == ']') {
                            loopLevel--;
                        }
                    }

                } else {
                    loopStack.push(pointer_code);
                }

            }else if ( znak_polecenie == ']' )
            {
                if ( tape[pointer_tape] != 0 )
                {
                    pointer_code = loopStack.peek();
                } else {
                    loopStack.pop();
                }
            }


            pointer_code++;
        }



    }


}
