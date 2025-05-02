import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class UniversityManager {
    static private User currentUser;
    
    public static void main(String[] args) {
        loadAll();
        boolean hasExited = false;

        while (!hasExited) {
            switch (
                Input.getIntInput(
                    """

                    Welcome to University Examination Manager!
                    1. Log In
                    2. Exit

                    Your Input: \
                    """, 1, 2)
            ) {
                case 1:
                    if (logIn() == User.ReturnState.EXIT) hasExited = true;
                    else setCurrentUser(null);
                    break;
                case 2:
                    hasExited = true;
                    break;            
                default:
                    break;
            }
        }

        System.out.println("\nExiting...");
    }

    private static void setCurrentUser(User currentUser) {
        UniversityManager.currentUser = currentUser;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static User.ReturnState logIn() {
        String userID = Input.getStringInput("\nEnter User ID: ");
        String password = Input.getStringInput("Enter Password: ");
        
        User user = UserManager.getInstance().getUserByID(userID);
        if (user == null) return User.ReturnState.LOG_OUT;

        if (!user.getPassword().equals(password)) {
            System.out.println("Incorrect Password");
            return User.ReturnState.LOG_OUT;
        }

        setCurrentUser(user);
        System.out.println("\nLog In Success! Welcome, " + user.getName());

        return user.listOptions();
    }

    public static void loadAll() {
        try {
            Files.createDirectories(Paths.get("src/data/semester_courses"));
        } catch (IOException e) {
            System.out.println("Folders Could Not Be Created. Aborting...");
            System.exit(1);
        }
        UserManager.getInstance().load();
        CourseManager.getInstance().load();
        SemesterManager.getInstance().load();
        Exam.load();
    }
}
