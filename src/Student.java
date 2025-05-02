import java.util.Map.Entry;
import java.util.TreeMap;

public class Student extends User {
    final public static int NUMBER_OF_PARAMETERS = 6;
    private static int lastID = 0;
    private String programme;
    TreeMap<String, TreeMap<String, SemesterCourse>> semesterCourses = new TreeMap<>();

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
        String programme = Input.getStringInput("Enter Programme(Optional): ", "Not Set");
        return new Student(String.format("S%06d", ++lastID), name, password, email, phoneNumber, programme);
    }

    public static String marksToGPA(int marks) {
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
                8. Show Results
                9. Edit Results
               10. Back

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
                    SemesterCourse semesterCourseToEnroll = SemesterManager.getInstance().selectSemesterCourseFromInput();

                    if (semesterCourseToEnroll == null) break;
                    if (semesterCourses.containsKey(semesterCourseToEnroll.getSemester()) && semesterCourses.get(semesterCourseToEnroll.getSemester()).containsKey(semesterCourseToEnroll.getCourseID())) {
                        System.out.println("Student is Already Enrolled in this Semester Course");
                        break;
                    }
                    semesterCourseToEnroll.addStudent(this);
                    this.addSemesterCourse(semesterCourseToEnroll);
                    System.out.println("Student Successfully Enrolled in this Semester Course");
                    semesterCourseToEnroll.save();
                    break;                
                case 7:
                    System.out.println("Unenroll Semester Course");
                    SemesterCourse semesterCourseToUnenroll = this.selectSemesterCourseFromInput();

                    if (semesterCourseToUnenroll == null) break;

                    semesterCourseToUnenroll.removeStudent(this);
                    this.removeSemesterCourse(semesterCourseToUnenroll);
                    System.out.println("Student Successfully Unenrolled in this Semester Course");
                    semesterCourseToUnenroll.save();    
                    break;                
                case 8:
                    this.showExamResults();
                    break;
                case 9:
                    SemesterCourse semesterCourseToEdit = this.selectSemesterCourseFromInput();
                    if (semesterCourseToEdit == null) break;
                    Exam examToEdit = semesterCourseToEdit.selectExamFromInput();         
                    if (examToEdit == null) break;
                    int newMarks = Input.getIntInput(String.format("Current Marks: %d\nEnter New Marks (Enter -1 to Set As 'Not Set'): ", examToEdit.getStudentMarks(this)), -1, examToEdit.getTotalMarks());

                    examToEdit.setMarksObtained(this, newMarks);
                    System.out.println("Results Successfully Changed");
                    examToEdit.getSemesterCourse().save();
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

    public void showExamResults() {
        float totalGradePoint = 0;
        int totalCreditHour = 0;

        for (Entry<String, TreeMap<String, SemesterCourse>> entry: this.semesterCourses.entrySet()) {
            System.err.println(entry.getKey() + " Results");
            for (SemesterCourse semesterCourse: entry.getValue().values()) {
                int convertedMarks = semesterCourse.getStudentMarks(this);
                String grade = Student.marksToGPA(convertedMarks);
                int creditHour = semesterCourse.getCreditHour();
    
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
                    case "Invalid":
                    default:
                        break;
                }
    
                System.out.printf("%s\t%s\n", semesterCourse.getCourse().getCourseTitle(), grade);
            }
        }

        System.out.printf("\nCGPA: %.02f\n", (totalCreditHour == 0)? 0: totalGradePoint / totalCreditHour);
    }

    public SemesterCourse selectSemesterCourseFromInput() {
        System.out.println(String.join("\n", this.semesterCourses.keySet()));
        String semester = Input.getStringInput("\nEnter Semester: ");
        if (!this.semesterCourses.containsKey(semester)) {
            System.out.println("The Student Does Not Have Semester Courses in This Semester");
            return null;
        }
        System.out.println(semester + " Enrolled Courses");
        for(String courseID: this.semesterCourses.get(semester).keySet()){
            System.out.println(CourseManager.getInstance().getCourseByID(courseID));
        }

        String courseID = Input.getStringInput("Enter Enrolled Course ID: ");
        if (this.semesterCourses.get(semester).containsKey(courseID)) return this.semesterCourses.get(semester).get(courseID);
        System.out.println("Course Not Found in This Semester");
        return null;
    }

    public static String getParameterTitle() {
        return String.format("%-20s %-20s %-20s %-20s %-20s %s", "User ID", "Name", "Password", "Email", "Phone Number", "Programme");
        
    }

    public String[] getParameters() {
        return new String[]{this.getUserID(), this.getName(), this.getPassword(), this.getEmail(), this.getPhoneNumber(), this.getProgramme()};
    }

    public void onDelete() {
        for (TreeMap<String, SemesterCourse> set: this.semesterCourses.values()) {
            for (SemesterCourse semesterCourse: set.values()) {
                semesterCourse.removeStudent(this);
                semesterCourse.save();
            }
        }
    }

    public void addSemesterCourse(SemesterCourse semesterCourse) {
        this.semesterCourses.computeIfAbsent(semesterCourse.getSemester(), k -> new TreeMap<>()).put(semesterCourse.getCourseID(), semesterCourse);
    }

    public void removeSemesterCourse(SemesterCourse semesterCourse) {
        this.semesterCourses.get(semesterCourse.getSemester()).remove(semesterCourse.getCourseID());
    }

    @Override
    public String toString() {
        return String.format(
            """
    
            User ID: %s
            Name: %s
            Password: %s
            Programme: %s
            Email: %s
            Phone Number: %s
            """
            , this.getUserID(), this.getName(), this.getPassword(), this.getProgramme(), this.getEmail(), this.getPhoneNumber());
    }
}
