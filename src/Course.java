import java.util.TreeSet;

public class Course implements Comparable<Course> {
    private String courseID;
    private String courseName;
    private int creditHour;
    private TreeSet<String> semesters = new TreeSet<>();

    //default constructor
    public Course(){
        this("ABC123", "Default Course", 1);
    }

    //constructor
    public Course (String courseID, String courseName, int creditHour){
        this.courseID = courseID;
        this.courseName = courseName;
        this.creditHour = creditHour;
    }

    //getter
    public String getCourseID(){
        return courseID;
    }

    public String getCourseName(){
        return courseName;
    }

    public int getCreditHour(){
        return creditHour;
    }

    public String getCourseTitle() {
        return this.courseID + " - " + this.courseName;
    }

    //setter
    public void setCourseID(String courseID){
        this.courseID = courseID;
    }

    public void setCourseName(String courseName){
        this.courseName = courseName;
    }

    public void setCreditHour(int creditHour){
        this.creditHour = creditHour;
    }

    public void addSemesterCourse(String semesterCourse){
        this.semesters.add(semesterCourse);
    }

    public void removeSemesterCourse(String semesterCourse){
        semesters.remove(semesterCourse);
    }

    public void onDelete(){
        for (String semester: this.semesters) {
            SemesterManager.getInstance().removeSemesterCourse(semester, this.courseID);
        }
    }

    @Override
    public int compareTo(Course o) {
        return this.getCourseID().compareTo(o.getCourseID());
    }

    
    @Override
    public String toString() {
        return this.getCourseTitle() + " (" + creditHour + " Credits)";
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Course && this.compareTo((Course) obj) == 0;
    }
}


