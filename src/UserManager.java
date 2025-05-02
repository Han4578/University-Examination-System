import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.TreeMap;

public class UserManager {
    final public static String DELIMITER = "|";
    final public static String ESCAPED_DELIMITER = "\\|";
    private static UserManager instance = new UserManager();
    enum userCategories {ADMIN, EXAMINER, STUDENT}
    private TreeMap<String, Admin> admins = new TreeMap<>();
    private TreeMap<String, Student> students = new TreeMap<>();
    private TreeMap<String, Examiner> examiners = new TreeMap<>();

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
                    4. Back

                    Your Input: \
                    """, 1, 4
                )
            ) {
                case 1:
                    this.manageList(students, userCategories.STUDENT);
                    break;
                case 2:
                    this.manageList(examiners, userCategories.EXAMINER);
                    break;
                case 3:
                    this.manageList(admins, userCategories.ADMIN);
                    break;
                case 4:
                    return;
                default:
                    break;
            }
        }
    }

    public <T extends User> void manageList(TreeMap<String, ? extends User> userList, userCategories category) {
        String displayCategory = category.toString().substring(0, 1).toUpperCase() + category.toString().substring(1).toLowerCase();

        while (true) {
            switch (
                Input.getIntInput(String.format(
                    """

                    Manage %1$s
                    1. Create %1$s
                    2. Delete %1$s
                    3. Show %1$s List
                    4. Edit %1$s
                    5. Back

                    Your Input: \
                    """, 
                    displayCategory), 1, 5)) {
                case 1:
                    switch (category) {
                        case ADMIN:
                            this.addUser(admins, Admin.createFromInput());
                            System.out.println("Admin Created");
                            this.save();
                            break;
                            case EXAMINER:
                            this.addUser(examiners, Examiner.createFromInput());
                            System.out.println("Examiner Created");
                            this.save();
                            break;
                            case STUDENT:
                            this.addUser(students, Student.createFromInput());
                            System.out.println("Student Created");
                            this.save();
                            break;
                        default:
                            break;
                    }
                    break;
                case 2:
                    String userIDtoRemove = Input.getStringInput(String.format("Enter %s ID to Remove: ", displayCategory));
                    if (userList.containsKey(userIDtoRemove)) {
                    if (!Input.getBooleanInput(String.format("Are You Sure You Want to Delete %s? (User ID: %s) [Y/N]: ", this.getUserByID(userIDtoRemove).getName(), userIDtoRemove), "Y", "N")) break;
                        this.deleteUser(userIDtoRemove);
                        System.out.printf("%s ID %s Deleted Successfully\n", displayCategory, userIDtoRemove);
                        this.save();
                    }
                    else System.out.printf("%s ID %s Not Found", displayCategory, userIDtoRemove);
                    break;
                case 3:
                    switch (category) {
                        case ADMIN:
                            System.out.println(Admin.getParameterTitle());
                            break;
                        case STUDENT:
                            System.out.println(Student.getParameterTitle());
                            break;
                        case EXAMINER:
                            System.out.println(Examiner.getParameterTitle());
                            break;
                        default:
                            break;
                    }

                    for (User user: userList.values()) {
                        for (String param: user.getParameters()) {
                            System.out.printf("%-21s", param);
                        }
                        System.out.println("\n");
                    }
                    break;
                case 4:
                    String userIDtoEdit = Input.getStringInput(String.format("Enter %s ID to Edit: ", displayCategory));
                    if (userList.containsKey(userIDtoEdit)) {
                        this.getUserByID(userIDtoEdit).editProfileAsAdmin();
                    }
                    else System.out.printf("%s ID %s Not Found", displayCategory, userIDtoEdit);
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
    
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            
            if (line == null) line = "0 0 0"; //If file is empty, default lastID to 0

            String[] lastIDs = line.split(" +");
            
            try {
                Admin.setLastID(Integer.valueOf(lastIDs[0]));
                Examiner.setLastID(Integer.valueOf(lastIDs[1]));
                Student.setLastID(Integer.valueOf(lastIDs[2]));
            } catch (NumberFormatException e) {
                System.out.println("Not a Number, User Last ID Could Not be Assigned, Aborting");
                System.exit(1);
            }
            
            while ((line = reader.readLine()) != null) {
                String[] params = line.split(ESCAPED_DELIMITER);
                if (params.length == 0) continue;
    
                switch (params[0].charAt(0)) {
                    case 'A':
                        if (params.length == Admin.NUMBER_OF_PARAMETERS) this.addUser(admins, new Admin(params));
                        else System.out.printf("Invalid Format, Make Sure There Are %d Parameters: %s\n", Admin.NUMBER_OF_PARAMETERS, line);
                        break;
                    case 'E':
                        if (params.length == Examiner.NUMBER_OF_PARAMETERS) this.addUser(examiners, new Examiner(params));
                        else System.out.printf("Invalid Format, Make Sure There Are %d Parameters: %s\n", Examiner.NUMBER_OF_PARAMETERS, line);
                        break;
                    case 'S':
                        if (params.length == Student.NUMBER_OF_PARAMETERS) this.addUser(students, new Student(params));
                        else System.out.printf("Invalid Format, Make Sure There Are %d Parameters: %s\n", Student.NUMBER_OF_PARAMETERS, line);
                        break;
                    default:
                        System.out.println("Invalid Format: " + line);
                        break;
                }
            }
    
            reader.close();
            System.out.println("Users Loaded From File");
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

            actual.createNewFile();

            //Back Up File Before Changing
            backup.delete();
            if (!actual.renameTo(backup)) throw new IOException("User File Could Not Be Backed Up, Aborting Save");

            //Write temp file
            FileWriter fileWriter = new FileWriter(temp);

            fileWriter.write(String.format("%d %d %d", Admin.getLastID(), Examiner.getLastID(), Student.getLastID()));

            for (Admin admin: admins.values()) {
                fileWriter.write("\n");
                fileWriter.write(String.join(UserManager.DELIMITER, admin.getParameters()));
            }
            for (Examiner examiner: examiners.values()) {
                fileWriter.write("\n");
                fileWriter.write(String.join(UserManager.DELIMITER, examiner.getParameters()));
            }
            for (Student student: students.values()) {
                fileWriter.write("\n");
                fileWriter.write(String.join(UserManager.DELIMITER, student.getParameters()));
            }

            fileWriter.close();
            //Rename Temp File to Actual File
            if (temp.renameTo(actual)) {
                System.out.println("User File Successfully Saved");
                backup.delete();
            }
            else {
                if (backup.renameTo(actual)) System.out.println("User File Could Not Be Saved, Previous Version is Restored.");
                throw new IOException("User File Could Not Be Saved, Previous Version is Saved in a Backup File but Could Not Be Restored.");
            };

        } catch (IOException e) {
            System.out.println("User File Could Not Be Saved");
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    public User getUserByID(String userID) {
        if (!userID.isBlank()) {
            switch (userID.charAt(0)) {
                case 'A':
                    if (admins.containsKey(userID)) return admins.get(userID);
                    break;
                case 'S':
                    if (students.containsKey(userID)) return students.get(userID);
                    break;
                case 'E':
                    if (examiners.containsKey(userID)) return examiners.get(userID);
                    break;
                default:
                    break;
            }
        }
        
        System.out.println("User Not Found");
        return null;
    }

    public User selectUserFromInput(String message) {
        return this.getUserByID(Input.getStringInput(message));
    }

    public <T extends User> void addUser(TreeMap<String, T> userList, T user) {
        userList.put(user.getUserID(), user);
    }
    
    public void deleteUser(String userID) {
        switch (userID.charAt(0)) {
            case 'A':
                if (!admins.containsKey(userID)) break;
                System.out.println("Admin Successfully Deleted.");
                admins.remove(userID).onDelete();
                return;
            case 'S':
                if (!students.containsKey(userID)) break;
                System.out.println("Student Successfully Deleted.");
                students.remove(userID).onDelete();
                return;
            case 'E':
                if (!examiners.containsKey(userID)) break;
                System.out.println("Examiner Successfully Deleted.");
                examiners.remove(userID).onDelete();
                return;
            default:
                break;
        }
        
        System.out.println("User Not Found");
    }
}
