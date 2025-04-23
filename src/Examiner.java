import java.util.ArrayList;
import java.util.TreeSet;

public class Examiner extends User {
    final public static int NUMBER_OF_PARAMETERS = 5;
    private static int lastID = 0;
    TreeSet<Exam> assignedExams = new TreeSet<>();

    public Examiner() {
        this("S000000", "Default Examiner", "password", "", "");
    }

    public Examiner(String userID, String name, String password, String email, String phoneNumber) {
        super(userID, name, password, email, phoneNumber);
    }

    public Examiner(String[] params) {
        super(params);

    }

    public static int getLastID() {
        return lastID;
    }

    public static void setLastID(int lastID) {
        Examiner.lastID = lastID;
    }

    public static Examiner createFromInput() {
        System.out.println("Create New Examiner\n");
        String name = User.getNameInput("Enter Name: ");
        String password = User.getPasswordInput("Enter Password: ");
        String email = User.getEmailInput("Enter Email(Optional): ");
        String phoneNumber = User.getPhoneNumberInput("Enter Phone Number(Optional): ");
        return new Examiner(String.format("E%06d", ++lastID), name, password, email, phoneNumber);
    }

    public User.ReturnState listOptions() {
        while (true) {
            switch (
                Input.getIntInput(
                    """

                    1. Show Profile
                    2. Edit Profile
                    3. Grade Exams
                    4. Save All
                    5. Log Out
                    6. Quit

                    Your Input: \
                    """, 1, 6
                )
            ) {
                case 1:
                    this.showProfile();
                    break;
                case 2:
                    this.editProfile();
                    break;
                case 3:
                    ArrayList<Exam> examList = new ArrayList<>(this.assignedExams);
                    int num = 0;

                    for (Exam exam: examList) {
                        System.out.printf("%d. %d %s\n", ++num, exam.getSemesterCourse().getSemester(), exam.getExamTitle());
                    }

                    int input = Input.getIntInput("\nSelect Exam to Grade(Enter 0 to Cancel): ", 0, num);
                    if (input > 0) examList.get(input - 1).gradeExam();
                    break;
                case 4:
                    UniversityManager.saveAll();
                    break;
                case 5:
                    System.out.println("\nLog Out Success!");
                    return User.ReturnState.LOG_OUT;
                case 6:
                    return User.ReturnState.EXIT;            
                default:
                    break;
            }
        }
    }

    public void showProfile() {
        System.out.printf(
        """

        User ID: %s
        Name: %s
        Password: %s
        Email: %s
        Phone Number: %s\n
        """
        , this.getUserID(), this.getName(), this.getPassword(), this.getEmail(), this.getPhoneNumber());
    }

    public void editProfileAsAdmin() {
        while (true) {            
            System.out.printf("\nEdit Profile of Examiner %s:", this.getUserID());           
            switch (
                Input.getIntInput(
                    """

                    1. Change Name
                    2. Change Password
                    3. Change Email
                    4. Change Phone Number
                    5. Show Profile
                    6. Add Assigned Exam
                    7. Remove Assigned Exam
                    8. Save All
                    9. Cancel

                    Your input: \
                    """, 1, 9
                )
            ) {
                case 1:
                    this.changeName();
                    break;
                case 2:
                    this.changePassword();
                    break;
                case 3:
                    this.changePassword();
                    break;
                case 4:
                    this.changePhoneNumber();
                    break;
                case 5:
                    this.showProfile();
                    break;               
                case 6:
                    Exam examToAssign = SemesterManager.selectSemesterCourseFromInput().selectExamFromInput();
                    this.assignedExams.add(examToAssign);
                    examToAssign.addExaminer(this);
                    System.out.println("Examiner Successfully Assigned");
                    break;                
                case 7:
                    Exam examToRemove = SemesterManager.selectSemesterCourseFromInput().selectExamFromInput();
                    this.assignedExams.remove(examToRemove);
                    examToRemove.removeExaminer(this);
                    System.out.println("Examiner Successfully Removed");
                    break;                
                case 8:
                    UniversityManager.saveAll();
                    break;
                case 9:
                    return;                
                default:
                    break;
            }
        }
    }
    
    public void editProfile() {
        
        while (true) {
            System.out.printf("\nEdit Profile");
            switch (
                Input.getIntInput(
                    """

                    1. Change Password
                    2. Change Email
                    3. Change Phone Number
                    4. Show Profile
                    5. Save
                    6. Cancel

                    Your input: \
                    """, 1, 6
                )
            ) {
                case 1:
                    this.changePassword();
                    break;
                case 2:
                    this.changeEmail();
                    break;
                case 3:
                    this.changePhoneNumber();
                    break;
                case 4:
                    this.showProfile();
                    break;                
                case 5:
                    UserManager.getInstance().save();
                    break;                
                case 6:
                    return;                
                default:
                    break;
            }
        }        
    }
    
    public static String getParameterTitle() {
        return String.format("%-20s %-20s %-20s %-20s %s", "User ID", "Name", "Password", "Email", "Phone Number");
    }

    public String[] getParameters() {
        return new String[]{this.getUserID(), this.getName(), this.getPassword(), this.getEmail(), this.getPhoneNumber()};
    }

    public void delete() {
        
    }
}
