import java.util.Scanner;

public class Input {
    private static Scanner scanner = new Scanner(System.in);

    public static int getIntInput(String message) {
        while (true) {
            System.out.print(message);

            if (!scanner.hasNextInt()) {
                System.out.println("Please enter a number");
                scanner.nextLine(); //clear buffer
                continue;
            }
            int number = scanner.nextInt();
            scanner.nextLine(); //clear buffer
            return number;
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

    public static String getStringInput(String message, String defaultString) {
        System.out.print(message);
        String result = scanner.nextLine();
        return (result.isBlank())? defaultString: result;
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
    
    public static String getDateInput(String message) {
        while (true) {
            String input = Input.getStringInput(message);
            if (input.isBlank()) return "Not Set";
            if (input.matches("^\\s*(0?[1-9]|[1-2]\\d|3[0-1])/(0?[1-9]|1[0-2]?)/\\d{4}\\s*$")) return input.trim();
            else System.out.println("Input does not match date format (DD/MM/YYYY)\n");
        }
    }

    public static String getTimeInput(String message) {
        while (true) {
            String input = Input.getStringInput(message);
            if (input.isBlank()) return "Not Set";
            if (input.matches("^\\s*([0-1]?\\d|2[0-3]):([0-5]?\\d)\\s*$")) return input.trim();
            else System.out.println("Input does not match time format (HH:MM)\n");
        }
    }
}
