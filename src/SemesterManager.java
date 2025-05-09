import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.TreeMap;
import java.util.Map.Entry;

public class SemesterManager {
    private static SemesterManager instance = new SemesterManager();
    private TreeMap<String,TreeMap<String,SemesterCourse>> semesters = new TreeMap<>();

    private SemesterManager() {

    }
    
    public static SemesterManager getInstance() {
        return instance;
    }

    public void manage() {
        while(true){
            switch (
                Input.getIntInput(
                    """

                    Manage Semesters: 
                    1. Create new Semester
                    2. Manage Semester
                    3. Remove Semester
                    4. Show Semesters
                    5. Back

                    Your Input: \
                    """, 1, 5
                )
            ) {
                case 1:
                    this.createNewSemester(this.getSemesterInput("Enter the semester you want to create (YYYYMM): "));
                    break;
                case 2:
                    this.manageSemester(this.getSemesterInput("Input semester to manage (YYYYMM): "));
                    break;
                case 3:
                    this.removeSemester(this.getSemesterInput("Enter the semester you want to remove (YYYYMM): "));
                    break;
                case 4:
                    System.out.println("Show Semesters");
                    if (this.semesters.size() > 0) {
                        for (String semester: this.semesters.keySet()) {
                            this.showSemester(semester);
                        }
                    } else System.out.println("No Semesters Found");
                    break;
                case 5:
                    return;
            
                default:
                    break;
            }
        }
    }

    private void manageSemester(String semester){
        if (semester == null) return;
        if (!this.semesters.containsKey(semester)) {
            System.out.println("This semester does not exist");
            return;
        }
        
        TreeMap<String, SemesterCourse> semesterMap = this.semesters.get(semester);

        while(true){
            switch(
                Input.getIntInput(
                    String.format("""

                    Manage Semester %s:
                    1. Add new Semester Course
                    2. Manage Semester Course
                    3. Remove Semester Course
                    4. Show All Semester Courses
                    5. Back

                    Your Input: \
                    """, semester), 1, 5)
            ){
                case 1:
                    createSemesterCourse(semester, semesterMap);
                    break;
                case 2:
                    String courseToManage = Input.getStringInput("Select Course ID to manage: ");
                    if (semesterMap.containsKey(courseToManage)) semesterMap.get(courseToManage).manage();
                    else System.out.println("The selected course does not exist in this semester");
                    break;
                case 3:
                    String courseToRemove = Input.getStringInput("Select Course ID to remove: ");
                    if (semesterMap.containsKey(courseToRemove)) {
                        CourseManager.getInstance().getCourseByID(courseToRemove).removeSemesterCourse(semester);
                        this.removeSemesterCourse(semester, courseToRemove);
                    }
                    else System.out.println("The selected course does not exist in this semester");
                    break;
                case 4:
                    this.showSemester(semester);
                    break;
                case 5:
                    return;
            }
        }
    } 

    private void saveSemesters() {
        try {
            File file = new File("src/data/semesters.txt");
            File backup = new File("src/data/semesters.backup");
            File temp = new File("src/data/semesters.temp");

            backup.delete();
            if (!file.renameTo(backup)) throw new IOException("Semester File Could Not Be Backed Up");
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(temp));

            for (String semester: this.semesters.keySet()) {
                bufferedWriter.write(semester);
                bufferedWriter.newLine();
            }

            bufferedWriter.close();
            if (!temp.renameTo(file)) {
                if (backup.renameTo(file)) System.out.println("Semesters Could Not Be Saved, Previous Version Restored");
                else System.out.println("Semesters Could Not Be Saved. Previous Version is Backed Up but Could Not Be Restored");
            } else backup.delete();
            System.out.println("Semesters Saved Successfully");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Semesters Could Not Be Saved, " + e.getMessage());
        }
    }

    
    public void load() {
        try {
            File directory = new File("src/data/semester_courses");

            CourseManager courseManager = CourseManager.getInstance();
            UserManager userManager = UserManager.getInstance();

            File semesterFile = new File("src/data/semesters.txt");
            semesterFile.createNewFile();
            BufferedReader semesterReader = new BufferedReader(new FileReader(semesterFile));
            String semesterLine = "";
            while ((semesterLine = semesterReader.readLine()) != null) this.semesters.put(semesterLine.trim(), new TreeMap<>());
            semesterReader.close();
            
            for (File file: directory.listFiles()) {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                //read semester then course ID
                SemesterCourse semesterCourse = new SemesterCourse(reader.readLine().trim(), courseManager.getCourseByID(reader.readLine().trim()));
                String line = reader.readLine();
                if (line != null) {
                    for (String studentID: line.split(" ")) {
                        Student student = (Student) userManager.getUserByID(studentID);
                        semesterCourse.addStudent(student);
                        student.addSemesterCourse(semesterCourse);
                    }
    
                    while (reader.readLine() != null) {
                        Exam exam = new Exam(
                            reader.readLine().trim(), //IDexamID;
                            reader.readLine().trim(), // type;
                            reader.readLine().trim(), //venue;
                            Integer.valueOf(reader.readLine().trim()), //totalMarks;
                            Integer.valueOf(reader.readLine().trim()), //overall percentage;
                            reader.readLine().trim(), //exam date;
                            reader.readLine().trim(), //exam time;
                            Integer.valueOf(reader.readLine().trim()), //exam duration;
                            semesterCourse        //course;
                        );
                        String[] results = reader.readLine().split(" ");
                        for (int i = 0; i < results.length; i += 2) exam.setMarksObtained((Student) userManager.getUserByID(results[i]), Integer.valueOf(results[i + 1]));
                        for (String examinerID: reader.readLine().split(" ")) {
                            if (examinerID.isBlank()) continue;
                            Examiner examiner = (Examiner) userManager.getUserByID(examinerID);
                            examiner.addAssignedExam(exam);
                            exam.addExaminer(examiner);
                        }
                        semesterCourse.addExam(exam);
                    }
                }
                this.semesters.computeIfAbsent(semesterCourse.getSemester(), k -> new TreeMap<>()).put(semesterCourse.getCourseID(), semesterCourse);
                semesterCourse.getCourse().addSemesterCourse(semesterCourse.getSemester());
                reader.close();
            }
            System.out.println("Semester Courses Loaded From File");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Semester Course Files Could Not Be Loaded. Aborting...");
            System.exit(1);
        }
    }

    public SemesterCourse selectSemesterCourseFromInput() {
        System.out.println("Semesters");
        System.out.println(String.join("\n", this.semesters.keySet()));
        String semester = this.getSemesterInput("\nEnter Semester (YYYYMM): ");
        if (semester == null || !this.semesters.containsKey(semester)) {
            System.out.println("Semester Not Found");
            return null;
        }
        this.showSemester(semester);
        String course = Input.getStringInput("Enter Course ID: ");
        if (!this.semesters.get(semester).containsKey(course)) {
            System.out.println("Course Not Found in This Semester");
            return null;
        } else return this.semesters.get(semester).get(course);
    }

    private void createSemesterCourse(String semester, TreeMap<String, SemesterCourse> semesterSet) {
        Course selectedCourse = CourseManager.getInstance().selectCourseFromInput();
        if (selectedCourse == null) return;
        if (semesterSet.containsKey(selectedCourse.getCourseID())) {
            System.out.println("Course already exists in this semester");
            return;
        }
        SemesterCourse semesterCourse = new SemesterCourse(semester, selectedCourse);
        semesterSet.put(selectedCourse.getCourseID(), semesterCourse);
        selectedCourse.addSemesterCourse(semester);
        System.out.println("Semester Course Successfully Created");
        semesterCourse.save();
    }

    private void createNewSemester(String semester){
        if (semester == null) return;
        if(semesters.containsKey(semester))System.out.println("The Semester Already Exists");
        else {
            semesters.put(semester, new TreeMap<>());
            System.out.println("Semester Successfully Created");
            this.saveSemesters();
        }
    }

    private void removeSemester(String semester) {
        if (semester == null) return;
        if (!this.semesters.containsKey(semester)) {
            System.out.println("Semester Not Found");
            return;
        }
        if (!Input.getBooleanInput(String.format("Are You Sure You Want to Remove Semester %s? [Y/N]: ", semester), "Y", "N")) return;
        for (SemesterCourse semesterCourse: this.semesters.get(semester).values()) {
            semesterCourse.onDelete();
            semesterCourse.getCourse().removeSemesterCourse(semester);
        }
        this.semesters.remove(semester);
        System.out.println("Semester Successfully Removed");
        this.saveSemesters();
    }

    public void removeSemesterCourse(String semester, String course) {
        if (!Input.getBooleanInput(String.format("Are You Sure You Want to Delete Course %s in Semester %s? [Y/N]: ", course, semester), "Y", "N")) return;
        this.semesters.get(semester).remove(course).onDelete();
        System.out.println("Semester Course Successfully Deleted");
    }

    private String getSemesterInput(String message){
        while (true) {
            String input = Input.getStringInput(message);
            if (input.isBlank()) return null;
            if (input.matches("^\\d{4}(0[1-9]|1[0-2])$")) return input;
            System.out.println("Input does not match the semester format (YYYYMM)\n");
        }
    }

    private void showSemester(String semester){
        System.out.println(semester + " Courses");
        if (this.semesters.get(semester).size() > 0) {
            for(String courseID: this.semesters.get(semester).keySet()){
                System.out.println(CourseManager.getInstance().getCourseByID(courseID));
            }
        } else System.out.println("No Courses Found");
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Entry<String, TreeMap<String, SemesterCourse>> entry: this.semesters.entrySet()) {
            stringBuilder.append(entry.getKey()).append("\n");
            for (String courseID: entry.getValue().keySet()) {
                stringBuilder.append(CourseManager.getInstance().getCourseByID(courseID)).append("\n");
            }
        }
        return stringBuilder.toString();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof SemesterManager;
    }
}
