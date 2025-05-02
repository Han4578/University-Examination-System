public class Admin extends User {
    final public static int NUMBER_OF_PARAMETERS = 5;
    private static int lastID = 0;

    public Admin() {
        this("A000000", "Default Admin", "password", "Not Set", "Not Set");
    }

    public Admin(String userID, String name, String password, String email, String phoneNumber) {
        super(userID, name, password, email, phoneNumber);
    }

    public Admin(String[] params) {
        super(params);

    }

    public static void setLastID(int lastID) {
        Admin.lastID = lastID;
    }

    public static int getLastID() {
        return lastID;
    }

    public static Admin createFromInput() {
        System.out.println("Create New Admin\n");
        String name = User.getNameInput("Enter Name: ");
        String password = User.getPasswordInput("Enter Password: ");
        String email = User.getEmailInput("Enter Email(Optional): ");
        String phoneNumber = User.getPhoneNumberInput("Enter Phone Number(Optional): ");
        return new Admin(String.format("A%06d", ++lastID), name, password, email, phoneNumber);
    }

    public User.ReturnState listOptions() {
        while (true) {
            switch (
                Input.getIntInput(
                    """

                    Admin Options
                    1. Show Profile
                    2. Edit Profile
                    3. Manage Users
                    4. Manage Courses
                    5. Manage Semesters
                    6. Log Out
                    7. Quit

                    Your Input: \
                    """, 1, 7
                )
            ) {
                case 1:
                    this.showProfile();
                    break;
                case 2:
                    this.editProfile();
                    break;
                case 3:
                    UserManager.getInstance().manage();
                    break;
                case 4:
                    CourseManager.getInstance().manage();
                    break;
                case 5:
                    SemesterManager.getInstance().manage();
                    break;
                case 6:
                    System.out.println("\nLog Out Success!");
                    return User.ReturnState.LOG_OUT;
                case 7:
                    return User.ReturnState.EXIT;            
                default:
                    break;
            }
        }
    }

    public void editProfileAsAdmin() {
        while (true) {            
            System.out.printf("\nEdit Profile of Admin %s:", this.getUserID());           
            
            switch (
                Input.getIntInput(
                    """

                    1. Change Name
                    2. Change Password
                    3. Change Email
                    4. Change Phone Number
                    5. Show Profile
                    6. Delete User
                    7. Back

                    Your input: \
                    """, 1, 7
                )
            ) {
                case 1:
                    this.changeName();
                    break;
                case 2:
                    this.changePassword();
                    break;
                case 3:
                    this.changeEmail();
                    break;
                case 4:
                    this.changePhoneNumber();
                    break;
                case 5:
                    this.showProfile();
                    break;   
                case 6:
                    if (UniversityManager.getCurrentUser().equals(this)) System.out.println("User is Currently Logged In and Cannot be Deleted.");
                    else this.delete();
                    break;            
                case 7:
                    return;                
                default:
                    break;
            }
        }
    }
    
    public void editProfile() {
        this.editProfileAsAdmin();        
    }

    public static String getParameterTitle() {
        return String.format("%-20s %-20s %-20s %-20s %s", "User ID", "Name", "Password", "Email", "Phone Number");
    }

    public String[] getParameters() {
        return new String[]{this.getUserID(), this.getName(), this.getPassword(), this.getEmail(), this.getPhoneNumber()};
    }


    public void onDelete() {

    }

    @Override
    public String toString() {
        return String.format("""

        User ID: %s
        Name: %s
        Password: %s
        Email: %s
        Phone Number: %s
        """
        , this.getUserID(), this.getName(), this.getPassword(), this.getEmail(), this.getPhoneNumber());
    }


}
