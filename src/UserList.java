import java.io.FileWriter;
import java.io.IOException;
import java.util.TreeMap;

public class UserList <T extends User> {
    private TreeMap<String, T> userList = new TreeMap<String, T>();
    enum userCategories {USER, ADMIN, EXAMINER, STUDENT}
    userCategories userCategory;
    String displayCategory;
    
    public UserList() {
        this(userCategories.USER);
    }
    
    public UserList(userCategories userCategory) {
        this.userCategory = userCategory;
        this.displayCategory = userCategory.toString().substring(0, 1).toUpperCase() + userCategory.toString().substring(1).toLowerCase();
    }

    @SuppressWarnings("unchecked")
    public void manage() {
        while (true) {
            switch (
                Input.getIntInput(String.format(
                    """

                    1. Create %1$s
                    2. Delete %1$s
                    3. Show %1$s List
                    4. Edit %1$s
                    5. Cancel

                    Your Input: \
                    """, 
                    this.displayCategory), 1, 5)) {
                case 1:
                    switch (this.userCategory) {
                        case ADMIN:
                            this.addUser((T)Admin.createFromInput());
                            System.out.println("Admin Created");
                            break;
                        case EXAMINER:
                            this.addUser((T)Examiner.createFromInput());
                            System.out.println("Examiner Created");
                            break;
                        case STUDENT:
                            this.addUser((T)Student.createFromInput());
                            System.out.println("Student Created");
                            break;
                        default:
                            break;
                    }
                    break;
                case 2:
                    String userIDtoRemove = Input.getStringInput(String.format("Enter %s ID to Remove: ", this.displayCategory));
                    if (this.userExists(userIDtoRemove)) {
                    if (!Input.getBooleanInput(String.format("Are You Sure You Want to Delete %s? (User ID: %s) [Y/N]: ", this.getUserByID(userIDtoRemove).getName(), userIDtoRemove), "Y", "N")) break;
                        this.removeUser(userIDtoRemove).delete();
                        System.out.printf("%s ID %s Deleted Successfully", this.displayCategory, userIDtoRemove);
                    }
                    else System.out.printf("%s ID %s Not Found", this.displayCategory, userIDtoRemove);
                    break;
                case 3:
                    switch (this.userCategory) {
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

                    for (T user: this.userList.values()) {
                        for (String param: user.getParameters()) {
                            System.out.printf("%-21s", param);
                        }
                    }
                    System.out.println("\n");
                    break;
                case 4:
                    String userIDtoEdit = Input.getStringInput(String.format("Enter %s ID to Edit: ", this.displayCategory));
                    if (this.userExists(userIDtoEdit)) {
                        this.getUserByID(userIDtoEdit).editProfileAsAdmin();
                    }
                    else System.out.printf("%s ID %s Not Found", this.displayCategory, userIDtoEdit);
                    break;
                case 5:
                    return;
                default:
                    break;
            }
        }
    }

    public T getUserByID(String userID) {
        return userList.get(userID);
    }

    public boolean userExists(String userID) {
        return this.userList.containsKey(userID);
    }

    public void addUser(T user) {
        this.userList.put(user.getUserID(), user);
    }

    public T removeUser(String userID) {
        return this.userList.remove(userID);
    }

    public void save(FileWriter file) throws IOException {
        for (T user: this.userList.values()) {
            file.write("\n");
            file.write(String.join(UserManager.DELIMITER, user.getParameters()));   
        }
    }
}
