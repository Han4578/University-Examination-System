import java.util.TreeMap;
import java.util.Map;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;

public class CourseManager{
    private static CourseManager instance = new CourseManager();
    private TreeMap<String, Course> courses;

    private CourseManager(){
        this.courses = new TreeMap<>();
    }

    public static CourseManager getInstance(){
        return instance;
    }

    public void manage(){
        while (true){
            switch (Input.getIntInput(
            """
            ----------Manage Courses---------- 
            1. Add new course 
            2. Edit course 
            3. Show couses 
            4. Remove course
            5. Back
            
            Your Input: \
            """, 1, 5)) {
                case 1:
                    addCourse(inputCourse());
                    break;
                case 2:
                    editCourse();
                    break;
                case 3:
                    showCourses();
                    break;
                case 4:
                    removeCourse();
                    break;
                case 5:
                    return;
                default:
                    break;
            }
        }
        
    }

    public Course getCourseByID(String courseID){
        return courses.get(courseID);
    }

    public Course selectCourseFromInput() {
        String courseID = Input.getStringInput("Enter Course ID: ");
        if (!this.courses.containsKey(courseID)) {
            System.out.println("Course Not Found");
            return null;
        }
        return this.courses.get(courseID);
    }

    //option 1: add Courses to TreeMap
    private void addCourse(Course course){
        if (course != null){
            if(courses.containsKey(course.getCourseID())){
                System.out.println("Course with ID " + course.getCourseID() + " already exists!");
            }else{
                courses.put(course.getCourseID(), course);
                System.out.println("Course Added: " + course);
                save();
            }
        }
    }
    
    private Course inputCourse(){
        String courseID = Input.getStringInput("Enter Course ID: ");
        if (this.courses.containsKey(courseID)) {
            System.out.println("Course with ID " + courseID + " already exists!");
            return null;
        }
        if (courseID.isBlank()) {
            System.out.println("Course ID cannot be blank");
            return null;
        }
        String courseName = this.inputCourseName("Enter Course Name: ");
        int courseCreditHour = Input.getIntInput("Enter Course Credit Hours: ", 1, Integer.MAX_VALUE);

        return new Course(courseID, courseName, courseCreditHour);
    }

    //option 2: edit TreeMap courses data(course name and credit hours)
    private void editCourse(){
        String courseID = Input.getStringInput("\n----------Edit Course----------\nEnter Course ID to Edit: ");
        Course course = courses.get(courseID);
    
        if (course != null) {
            System.out.println("Editing Course Name: " + course.getCourseName() + "\nEditing Credit Hours: " + course.getCreditHour());

            
            while (true){
                boolean updated = false;
                switch(Input.getIntInput(
                    "\nOPTION:\n1. Edit course name\n2. Edit credit hour\n3. Back\nPlease choose what you want to edit (1-3): ",1,3)){
                    case 1:
                        String newName = this.inputCourseName("Enter New Course Name: ");
                        if (newName == null) break;
                        course.setCourseName(newName);
                        updated = true;
                        break;
                    case 2:
                        int newCreditHours = Input.getIntInput("Enter New Credit Hours: ", 1, Integer.MAX_VALUE);    
                        course.setCreditHour(newCreditHours);
                        updated = true;
                        break;
                    case 3:
                        System.out.println("Course Updated Successfully!!");
                        System.out.println("----------Latest course info----------\nCourse Name: " + course.getCourseName() + " - Credit Hours: " + course.getCreditHour());
                        return;
                    default:
                        System.out.println("Invalid input! Please Enter Again!");
                        break;
                    }
                if (updated) {
                    save();
                    System.out.println("Course Updated Successfully!");
                }
            }
        }else{
            System.out.println("Course ID Not Found!");
        }
    }
    
    //option 3: list out all the courses that inside TreeMap
    private void showCourses(){
        if (courses.isEmpty()){
            System.out.println("No such Courses!\n");
            return;
        }else{
            System.out.println("\n----------------ALL Courses---------------");
            for(Map.Entry<String, Course> entry : courses.entrySet()){
                String courseID = entry.getKey();
                Course course = entry.getValue();

                System.out.println("Course ID: " + courseID);
                System.out.println("Course Name: " + course.getCourseName());
                System.out.println("Credit Hours: " + course.getCreditHour());
                System.out.println("\n");
            }
        }

    }
    
    //option 4: remove courses inside TreeMap
    private void removeCourse(){
        String ID = Input.getStringInput("Enter Course ID: ");
        Course course = courses.get(ID);

        if (course != null && courses.containsKey(course.getCourseID())){
            if (!Input.getBooleanInput(String.format("Are Your Sure You Want to Remove Course %s? [Y/N]: ", course.getCourseTitle()), "Y", "N")) return;
            courses.remove(course.getCourseID());
            System.out.println("Course Removed Successfully: " + course.getCourseID());
            save();
        } else System.out.println("Course Does Not Exist!\n");
        
    }

    // save the TreeMap to course.txt file
    public void save(){
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/data/course.txt"))) {
            for (Map.Entry<String, Course> entry : courses.entrySet()) {
                Course course = entry.getValue();
                String line = "Course ID: " + course.getCourseID() +
                              "\nCourse Name: " + course.getCourseName() +
                              "\nCredit Hours: " + course.getCreditHour() + "\n";
                writer.write(line);
                writer.newLine();
            }
            System.out.println("All courses saved to file...");
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // load the all courses data from course.txt to TreeMap
    public void load() { 
        try {
            File file = new File("src/data/course.txt");
            file.createNewFile();
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            String courseID = null, courseName = null;
            int creditHour = 0;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Course ID: ")) {
                    courseID = line.substring(11).trim();
                } else if (line.startsWith("Course Name: ")) {
                    courseName = line.substring(13).trim();
                } else if (line.startsWith("Credit Hours: ")) {
                    creditHour = Integer.parseInt(line.substring(14).trim());

                // Once we have all 3, create Course and add to map
                if (courseID != null && courseName != null) {
                    Course course = new Course(courseID, courseName, creditHour);
                    courses.put(courseID, course);
                    // Reset for next course
                    courseID = null;
                    courseName = null;
                    creditHour = 0;
                }
                }
            }

            System.out.println("Courses loaded from file.");
            reader.close();
        } catch (IOException e) {
            System.out.println("Error reading from file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String inputCourseName(String message) {
        String cName = Input.getStringInput(message);
        if (cName.isBlank()) {
            System.out.println("Course Name cannot be blank");
            return null;
        }
        return cName;
    }
}
