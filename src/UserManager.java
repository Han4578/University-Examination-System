import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class UserManager {
    final public static String DELIMITER = "|";
    final public static String ESCAPED_DELIMITER = "\\|";
    private static UserManager instance = new UserManager();
    private UserList<Admin> admins = new UserList<>(UserList.userCategories.ADMIN);
    private UserList<Student> students = new UserList<>(UserList.userCategories.STUDENT);
    private UserList<Examiner> examiners = new UserList<>(UserList.userCategories.EXAMINER);

    private UserManager() {
        
    }

    public static UserManager getInstance() {
        return instance;
    }

    public void manage() {
        while (true) {
            switch (
                Input.getIntInput(
                    """

                    Manage Users
                    1. Manage Students
                    2. Mangage Examiners
                    3. Manage Admin
                    4. Save
                    5. Cancel

                    Your Input: \
                    """, 1, 5
                )
            ) {
                case 1:
                    students.manage();
                    break;
                case 2:
                    examiners.manage();
                    break;
                case 3:
                    admins.manage();
                    break;
                case 4:
                    this.save();
                    break;
                case 5:
                    return;
                default:
                    break;
            }
        }
    }

    public void load() {
        try {
            File file = new File("src/data/users.txt");
    
            try {
                file.createNewFile(); //create file if it doesnt exist
            } catch (IOException e) {
            System.out.println("File could not be created");
            e.printStackTrace();
            System.exit(1);
            }
    
            BufferedReader reader;
            reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            
            if (line == null) line = "0 0 0"; //If file is empty, default lastID to 0

            String[] lastIDs = line.split(" +");
            
            try {
                Admin.setLastID(Integer.valueOf(lastIDs[0]));
                Examiner.setLastID(Integer.valueOf(lastIDs[1]));
                Student.setLastID(Integer.valueOf(lastIDs[2]));
            } catch (NumberFormatException e) {
                System.out.println("Not a Number, Last ID Could Not be Assigned");
                System.exit(1);
            }
            
            while ((line = reader.readLine()) != null) {
                String[] params = line.split(ESCAPED_DELIMITER);
                if (params.length == 0) continue;
    
                switch (params[0].charAt(0)) {
                    case 'A':
                        if (params.length == Admin.NUMBER_OF_PARAMETERS) admins.addUser(new Admin(params));
                        else System.out.printf("Invalid Format, Make Sure There Are %d Parameters: %s\n", Admin.NUMBER_OF_PARAMETERS, line);
                        break;
                    case 'E':
                        if (params.length == Examiner.NUMBER_OF_PARAMETERS) examiners.addUser(new Examiner(params));
                        else System.out.printf("Invalid Format, Make Sure There Are %d Parameters: %s\n", Examiner.NUMBER_OF_PARAMETERS, line);
                        break;
                    case 'S':
                        if (params.length == Student.NUMBER_OF_PARAMETERS) students.addUser(new Student(params));
                        else System.out.printf("Invalid Format, Make Sure There Are %d Parameters: %s\n", Student.NUMBER_OF_PARAMETERS, line);
                        break;
                    default:
                        System.out.println("Invalid Format: " + line);
                        break;
                }
            }
    
            reader.close();
        } catch (IOException e) {
            System.out.println("User File Could Not Be Opened and Read");
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void save() {
        try {
            File temp = new File("src/data/users.tmp");
            File actual = new File("src/data/users.txt");
            File backup = new File("src/data/users.backup");

            //Back Up File Before Changing
            backup.delete();
            if (!actual.renameTo(backup)) throw new IOException("User File Could Not Be Backed Up, Aborting Save");

            //Write temp file
            FileWriter fileWriter = new FileWriter(temp);

            fileWriter.write(String.format("%d %d %d", Admin.getLastID(), Examiner.getLastID(), Student.getLastID()));

            admins.save(fileWriter);
            examiners.save(fileWriter);
            students.save(fileWriter);

            fileWriter.close();
            //Rename Temp File to Actual File
            if (temp.renameTo(actual)) {
                System.out.println("User File Successfully Saved");
                backup.delete();
            }
            else {
                if (backup.renameTo(actual)) System.out.println("User File Could Not Be Saved, Previous Version is Restored.");
                throw new IOException("User File Could Not Be Saved, Previous Version is Restored, Previous Version is Saved in a Backup File but Could Not Be Restored.");
            };

        } catch (IOException e) {
            System.out.println("User File Could Not Be Saved");
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    public User getUserByID(String userID) {
        switch (userID.charAt(0)) {
            case 'A':
                if (admins.userExists(userID)) return admins.getUserByID(userID);
                break;
            case 'S':
                if (students.userExists(userID)) return students.getUserByID(userID);
                break;
            case 'E':
                if (examiners.userExists(userID)) return examiners.getUserByID(userID);
                break;
            default:
                break;
        }
        
        System.out.println("User Not Found");
        return null;
    }

    public User selectUserFromInput() {
        return this.getUserByID(Input.getStringInput("Enter User ID: "));
    }
    
    public void deleteUser(String userID) {
        switch (userID.charAt(0)) {
            case 'A':
                if (!admins.userExists(userID)) break;
                admins.removeUser(userID);
                System.out.println("Admin Successfully Deleted.");
                return;    
            case 'S':
                if (students.userExists(userID)) break;
                students.removeUser(userID);
                System.out.println("Student Successfully Deleted.");
                return;    
            case 'E':
                if (examiners.userExists(userID)) break;
                examiners.removeUser(userID);
                System.out.println("Examiner Successfully Deleted.");
                return;    
            default:
                break;
        }
        
        System.out.println("User Not Found");
    }
}
