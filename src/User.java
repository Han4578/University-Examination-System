public abstract class User implements Comparable<User> {
    enum ReturnState {LOG_OUT, EXIT}
    private String userID;
    private String name;
    private String password;
    private String email;
    private String phoneNumber;
    
    
    public User() {
        this("U000000", "Default", "12345", "", "");
    }
    
    public User(String userID, String name, String password, String email, String phoneNumber) {
        this.userID = userID;
        this.name = name;
        this.password = password;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public User(String[] params) {
        this.userID = params[0];
        this.name = params[1];
        this.password = params[2];
        this.email = params[3];
        this.phoneNumber = params[4];
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public String getUserID() {
        return this.userID;
    }

    public String getPassword() {
        return this.password;
    }


    public void setPassword(String password) {
        this.password = password;
    }


    public String getEmail() {
        return this.email;
    }


    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }


    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public static String getNameInput(String message) {
        while (true) {
            String name = Input.getStringInput(message);
            if (name.isBlank()) System.out.println("Name Cannot Be Empty\n");
            else if (name.contains(UserManager.DELIMITER)) System.out.println("Name Cannot Contain " + UserManager.DELIMITER + '\n'); 
            else return name;
        }
    }

    public void changeName() {
        this.setPassword(User.getNameInput("Enter New Name: "));
        System.out.println("New Name Has Been Set");
    }

    public static String getPasswordInput(String message) {
        while (true) {
            String password = Input.getStringInput(message);
            if (password.isBlank()) System.out.println("Password Cannot Be Empty\n");
            else if (password.contains(UserManager.DELIMITER)) System.out.println("Password Cannot Contain " + UserManager.DELIMITER + '\n');
            else return password;
        }
    }

    public void changePassword() {
        this.setPassword(User.getPasswordInput("Enter New Password: "));
        System.out.println("New Password Has Been Set");
    }

    public static String getEmailInput(String message) {
        while (true) {
            String email = Input.getStringInput(message);
            if (email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) return email;
            System.out.println("Invalid Email Address Format\n");
        }
    }

    public void changeEmail() {
        String email = User.getEmailInput("Enter New Email(Optional, abc123@example.com): ");
        this.setEmail((email.isBlank())? "Not Set": email);
        System.out.println("New Email Has Been Set");
    }

    public static String getPhoneNumberInput(String message) {
        while (true) {
            String phoneNumber = Input.getStringInput(message);
            if (phoneNumber.isBlank() || phoneNumber.matches("[0-9]+")) return phoneNumber;
            System.out.println("Phone Number Can Only Contain Numbers\n");
        }
    }

    public void changePhoneNumber() {
        String phoneNumber = User.getPhoneNumberInput("Enter New Phone Number(Optional): ");
        this.setPhoneNumber((phoneNumber.isBlank())? "Not Set": phoneNumber);
        System.out.println("New Phone Number has been set");
    }

    public abstract ReturnState listOptions();

    public abstract void delete();

    public abstract void editProfile();

    public abstract void editProfileAsAdmin();

    public abstract void showProfile();

    public abstract String[] getParameters();

    @Override
    public String toString() {
        return String.join(" ", this.getParameters());
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof User && this.getUserID().equals(((User) obj).getUserID());
    }

    @Override
    public int hashCode() {
        return this.getUserID().hashCode();
    }

    @Override
    public int compareTo(User user) {
        return this.getUserID().compareTo(user.getUserID());
    }
}
