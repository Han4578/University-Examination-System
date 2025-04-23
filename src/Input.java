import java.util.Scanner;

public class Input {
    private static Scanner scanner = new Scanner(System.in);

    public static int getIntInput(String message) {
        while (true) {
            System.out.print(message);

            if (!scanner.hasNextInt()) {
                System.out.println("Please enter a number");
                scanner.next(); //clear buffer
                continue;
            }
            scanner.nextLine(); //clear buffer
            return scanner.nextInt();
        }
    }

    public static int getIntInput(String message, int from, int to) {
        int result = 0;

        while (true) {
            System.out.print(message);

            if (!scanner.hasNextInt()) {
                System.out.printf("Please enter a number between %d and %d, inclusive\n\n", from, to);
                scanner.nextLine(); //clear buffer
                continue;
            }

            result = scanner.nextInt();
            if (result >= from && result <= to) break;
            else System.out.println("Number out of range, please try again\n");
        }
        scanner.nextLine(); //clear buffer
        return result;
    }

    public static String getStringInput(String message) {
        System.out.print(message);
        return scanner.nextLine();
    }
    
    public static boolean getBooleanInput(String message, String trueOption, String falseOption) {
        while (true) {
            System.out.print(message);
            String input = scanner.nextLine();
            if (input.equals(trueOption)) return true;
            else if (input.equals(falseOption)) return false;
            else System.out.printf("Not an Option, Please Enter Either %s or %s.\n\n", trueOption, falseOption);
        }
    }

}
