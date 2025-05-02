import java.util.TreeMap;

public class Examiner extends User {
    final public static int NUMBER_OF_PARAMETERS = 5;
    private static int lastID = 0;
    TreeMap<String, Exam> assignedExams = new TreeMap<>();

    public Examiner() {
        this("E000000", "Default Examiner", "password", "", "");
    }

    public Examiner(String userID, String name, String password, String email, String phoneNumber) {
        super(userID, name, password, email, phoneNumber);
    }

    public Examiner(String[] params) {
        super(params);

    }

    public static int getLastID() {
        return Examiner.lastID;
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
                    4. View Exams Assigned
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
                    Exam exam = this.selectExamFromAssigned();
                    if (exam == null) break;
                    exam.gradeExam();
                    break;
                case 4:
                    this.printExams();
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
                    8. Show Assigned Exams
                    9. Delete Examiner
                   10. Back

                    Your input: \
                    """, 1, 10
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
                    SemesterCourse semesterCourse = SemesterManager.getInstance().selectSemesterCourseFromInput();
                    if (semesterCourse == null) break;
                    Exam examToAdd = semesterCourse.selectExamFromInput();
                    if (examToAdd == null) break;
                    examToAdd.addExaminer(this);
                    this.addAssignedExam(examToAdd);
                    System.out.println("Examiner Successfully Assigned");
                    examToAdd.getSemesterCourse().save();
                    break;                
                case 7:
                    Exam examToRemove = this.selectExamFromAssigned();
                    if (examToRemove == null) break;
                    examToRemove.removeExaminer(this);
                    this.removeAssignedExam(examToRemove);
                    System.out.println("Examiner Successfully Unassigned");
                    examToRemove.getSemesterCourse().save();
                    break;                
                case 8:
                    this.printExams();
                    break;
                case 9:
                    this.delete();
                    break;
                case 10:
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
                    5. Back

                    Your input: \
                    """, 1, 5
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
                    return;                
                default:
                    break;
            }
        }        
    }

    public Exam selectExamFromAssigned() {
        String examID = Input.getStringInput("Enter Exam ID: ");
        if (!this.assignedExams.containsKey(examID)) {
            System.out.println("Exam Not Found");
            return null;
        }
        return this.assignedExams.get(examID);
    }

    public void printExams() {
        System.out.println("\nAssigned Exams");
        if (this.assignedExams.size() > 0) {
            for (Exam exam: this.assignedExams.values()) {
                System.out.println(exam.getDetailsString());
            }
        } else System.out.println("No Exams Assigned");
    }
    
	public void addAssignedExam(Exam exam) {
        if (exam == null) return;
        if (this.assignedExams.containsKey(exam.getExamID())) {
            System.out.println("Exam Has Already Been Assigned");
            return;
        }
        this.assignedExams.put(exam.getExamID(), exam);
	}
    
	public void removeAssignedExam(Exam exam) {
        this.assignedExams.remove(exam.getExamID());
	}
    
    public static String getParameterTitle() {
        return String.format("%-20s %-20s %-20s %-20s %s", "User ID", "Name", "Password", "Email", "Phone Number");
    }

    public String[] getParameters() {
        return new String[]{this.getUserID(), this.getName(), this.getPassword(), this.getEmail(), this.getPhoneNumber()};
    }

    public void onDelete() {
        for (Exam exam: this.assignedExams.values()) {
            exam.removeExaminer(this);
            exam.getSemesterCourse().save();
        }
    }

    @Override
    public String toString() {
        return String.format(
            """
    
            User ID: %s
            Name: %s
            Password: %s
            Email: %s
            Phone Number: %s
            """
            , this.getUserID(), this.getName(), this.getPassword(), this.getEmail(), this.getPhoneNumber());
    }

}
