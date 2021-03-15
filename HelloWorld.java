import java.util.*;

public class HelloWorld {
    public static void main(String[] args) {
        System.out.println("Hello World!");

        Scanner input = new Scanner(System.in);

        int reps = input.nextInt();
        String token = input.nextLine();

        for (int i = 0; i < reps; i++) {
            System.out.println(token);
        }
    }
}