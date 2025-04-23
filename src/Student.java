import java.util.Map.Entry;
import java.util.ArrayList;
import java.util.TreeMap;

public class Student extends User {
    final public static int NUMBER_OF_PARAMETERS = 6;
    private static int lastID = 0;
    private String programme;
    TreeMap<SemesterCourse, TreeMap<Exam, Result>> semesterCourses = new TreeMap<>();

    public Student() {
        this("S000000", "Default Student", "password", "", "", "");
    }

    public Student(String userID, String name, String password, String email, String phoneNumber, String programme) {
        super(userID, name, password, email, phoneNumber);
        this.programme = programme;
    }

    public Student(String[] params) {
        super(params);
        this.programme = params[5];
    }

    public String getProgramme() {
        return this.programme;
    }

    public void setProgramme(String programme) {
        this.programme = programme;
    }

    public static int getLastID() {
        return lastID;
    }
    
    public static void setLastID(int lastID) {
        Student.lastID = lastID;
    }

    public static Student createFromInput() {
        System.out.println("Create New Student\n");
        String name = User.getNameInput("Enter Name: ");
        String password = User.getPasswordInput("Enter Password: ");
        String email = User.getEmailInput("Enter Email(Optional): ");
        String phoneNumber = User.getPhoneNumberInput("Enter Phone Number(Optional): ");
        String programme = Input.getStringInput("Enter Programme(Optional): ");
        return new Student(String.format("S%06d", ++lastID), name, password, email, phoneNumber, programme);
    }

    public static String marksToGPA(float marks) {
        int roundedMarks = Math.round(marks);
        if (roundedMarks > 100 || roundedMarks < 0) return "Invalid";

        if (roundedMarks >= 90) return "A+";
        if (roundedMarks >= 80) return "A";
        if (roundedMarks >= 75) return "A-";
        if (roundedMarks >= 70) return "B+";
        if (roundedMarks >= 65) return "B";
        if (roundedMarks >= 60) return "B-";
        if (roundedMarks >= 55) return "C+";
        if (roundedMarks >= 50) return "C";
        return "F";
    }

    public User.ReturnState listOptions() {
        while (true) {
            switch (Input.getIntInput(
                """

                Student Options
                1. Show Profile
                2. Edit Profile
                3. Show Exam Results
                4. Log Out
                5. Exit

                Your Input: \
                """, 1, 5)) {
                case 1:
                    this.showProfile();
                    break;
                case 2:
                    this.editProfile();
                    break;
                case 3:
                    this.showExamResults();
                    break;
                case 4:
                    System.out.println("\nLog Out Success!");
                    return User.ReturnState.LOG_OUT;
                case 5:
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
        Programme: %s
        Email: %s
        Phone Number: %s\n
        """
        , this.getUserID(), this.getName(), this.getPassword(), this.getProgramme(), this.getEmail(), this.getPhoneNumber());
    }

    public void editProfileAsAdmin() {
        while (true) {            
            System.out.printf("\nEdit Profile of Student %s:", this.getUserID());           
            switch (Input.getIntInput(
                """

                1. Change Name
                2. Change Password
                3. Change Email
                4. Change Phone Number
                5. Show Profile
                6. Enroll Semester Course
                7. Unenroll Semester Course
                8. Edit Results
                9. Save All
                10. Cancel

                Your input: \
                """, 1, 10)) {
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
                    System.out.println("Enroll Semester Course");
                    SemesterCourse semesterCourseToEnroll = SemesterManager.selectSemesterCourseFromInput();

                    if (semesterCourseToEnroll == null) break;
                    if (semesterCourses.containsKey(semesterCourseToEnroll)) {
                        System.out.println("Student is Already Enrolled in this Semester Course");
                        break;
                    }

                    semesterCourses.put(semesterCourseToEnroll, new TreeMap<>());
                    semesterCourseToEnroll.addStudent(this);
                    System.out.println("Student Successfully Enrolled in this Semester Course");
                    break;                
                case 7:
                    System.out.println("Unenroll Semester Course");
                    SemesterCourse semesterCourseToUnenroll = this.selectSemesterCourseFromInput();

                    if (semesterCourseToUnenroll == null) break;

                    semesterCourses.remove(semesterCourseToUnenroll);
                    semesterCourseToUnenroll.removeStudent(this);
                    System.out.println("Student Successfully Unenrolled in this Semester Course");
                    break;                
                case 8:
                    SemesterCourse semesterCourseToEdit = this.selectSemesterCourseFromInput();
                    Exam examToEdit = semesterCourseToEdit.selectExamFromInput();         
                    
                    Result result = this.semesterCourses.get(semesterCourseToEdit).get(examToEdit);

                    int newMarks = Input.getIntInput(String.format("Current Mark: %d\nEnter New Marks(Enter -1 to Cancel):", result.getMarksObtained()), -1, examToEdit.getTotalMarks());
                    if (newMarks == -1) break;

                    result.setMarksObtained(newMarks);
                    System.out.println("Results Successfully Changed");
                case 9:
                    UniversityManager.saveAll();
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

    public void showExamResults() {
        float totalGradePoint = 0;
        int totalCreditHour = 0;

        for (Entry<SemesterCourse, TreeMap<Exam, Result>> entry: semesterCourses.entrySet()) {
            float convertedMarks = 0;

            for (Entry<Exam, Result> result: entry.getValue().entrySet()) {
                //Formula: Total Contribution Marks * Marks Obtained / Total Exam Marks
                convertedMarks += result.getKey().getContributionPercentage() * result.getValue().getMarksObtained() / result.getKey().getTotalMarks();
            }

            String grade = Student.marksToGPA(convertedMarks);
            int creditHour = entry.getKey().getCreditHour();

            totalCreditHour += creditHour;
            switch (grade) {
                case "A+":
                case "A":
                    totalGradePoint += 4.0f * creditHour;
                    break;
                case "A-":
                    totalGradePoint += 3.67f * creditHour;
                    break;
                case "B+":
                    totalGradePoint += 3.33f * creditHour;
                    break;
                case "B":
                    totalGradePoint += 3.00f * creditHour;
                    break;
                case "B-":
                    totalGradePoint += 2.67f * creditHour;
                    break;
                case "C+":
                    totalGradePoint += 2.33f * creditHour;
                    break;
                case "C":
                    totalGradePoint += 3.0f * creditHour;
                    break;
                case "F":
                default:
                    break;
            }

            System.out.printf("%d\t%s\t%s\n", entry.getKey().getSemester(), entry.getKey().getCourseTitle(), grade);
        }
        System.out.printf("\nCGPA: %.02f\n", (totalCreditHour == 0)? 0: totalGradePoint / totalCreditHour);
    }

    public SemesterCourse selectSemesterCourseFromInput() {
        ArrayList<SemesterCourse> semesterCourseList = new ArrayList<>(semesterCourses.keySet());
        int num = 0;
        for (SemesterCourse semesterCourse: semesterCourseList) {
            System.out.printf("%d. %d\t%s\n", ++num, semesterCourse.getSemester(), semesterCourse.getCourseTitle());
        }

        int input = Input.getIntInput(String.format("Enter Number to Select Semester Course, Enter 0 to Cancel. [0 - %d]\nYour number: ", num), 0, num);
        return (input == 0)? null: semesterCourseList.get(input - 1);
    }

    public static String getParameterTitle() {
        return String.format("%-20s %-20s %-20s %-20s %-20s %s", "User ID", "Name", "Password", "Email", "Phone Number", "Programme");
        
    }

    public String[] getParameters() {
        return new String[]{this.getUserID(), this.getName(), this.getPassword(), this.getEmail(), this.getPhoneNumber(), this.getProgramme()};
    }

    public void delete() {
        for (Entry<SemesterCourse, TreeMap<Exam, Result>> entry: semesterCourses.entrySet()) {
            entry.getKey().removeStudent(this);
            for (Entry<Exam, Result> resultEntry: entry.getValue().entrySet()) resultEntry.getValue().delete(this);
        }
    }
}
