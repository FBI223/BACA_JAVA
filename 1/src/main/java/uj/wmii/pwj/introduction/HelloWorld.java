package uj.wmii.pwj.introduction;

public class HelloWorld {
    public static void main(String[] args) {
        int len = args.length;
        if (len == 0) {
            System.out.println("No input parameters provided");
        } else {

            for (int i = 0; i < len; i++) {
                System.out.print(args[i]+"\n");
            }
        }

    }
}
