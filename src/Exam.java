import java.util.TreeSet;
import java.util.TreeMap;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Map.Entry;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

class Exam implements Comparable<Exam> {
	private static int totalID = 0;
	private String examID;
    private String examDate;
    private String examTime;
    private String examVenue;
    private String examType;
    private int examDuration;
    private int totalMarks;
    private int overallPercentage;
    private SemesterCourse semesterCourse;
    private TreeSet<Examiner> examiners = new TreeSet<>();
    private TreeMap<Student, Integer> results = new TreeMap<>();

    public Exam() {
        this("Not Set", "Not Set", 100, 0, "Not Set", "Not Set", 0, null);
    }
    
    public Exam(String type, String venue,int totalMarks, int overallPercentage, String examDate, String examTime, int examDuration, SemesterCourse course) {
		this(String.format("E%03d", ++Exam.totalID), type, venue, totalMarks, overallPercentage, examDate, examTime, examDuration, course);
        try {
            File file = new File("src/data/exam.txt");
            file.createNewFile();
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
            bufferedWriter.write(String.valueOf(Exam.totalID));
            bufferedWriter.close();
        } catch (IOException e) {
            System.out.println("Last Exam ID Could Not Be Saved: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public Exam(String examID, String type, String venue,int totalMarks, int overallPercentage, String examDate, String examTime, int examDuration, SemesterCourse course) {
        this.examID = examID;
        this.examType = type;
        this.examVenue = venue;
        this.totalMarks = totalMarks;
        this.overallPercentage = overallPercentage;
        this.examDate = examDate;
        this.examTime = examTime;
        this.examDuration = examDuration;
        this.semesterCourse = course;
    }

    public String getExamID(){
		return this.examID;
	}

	public int getOverallPercentage() {
    	return this.overallPercentage;
    }
    
	public String getType() {
    	return this.examType;
    }

    public String getVenue() {
    	return this.examVenue;
    }
    
    public String getDate() {
    	return this.examDate;
    }
    
    public String getTime() {
    	return this.examTime;
    }
    
    public int getDuration() {
    	return this.examDuration;
    }

	public int getTotalMarks(){
		return this.totalMarks;
	}

	public SemesterCourse getSemesterCourse(){
		return this.semesterCourse;
	}

    public boolean setTotalMarks(int newTotal) {
        if (newTotal < this.totalMarks) {
            for (int marks: this.results.values()) if (marks > newTotal) {
                System.out.println("New Total Marks Cannot be Less Than Student's Marks");
                return false;
            }
        }
    	this.totalMarks = newTotal;
        return true;
    }
    
    public void setOverallPercentage(int percentage) {
    	this.overallPercentage = percentage;
    }
    
	public void setType(String type) {
    	this.examType = type;
    }

    public void setVenue(String venue) {
    	this.examVenue = venue;
    }
    
    public void setDate(String date) {
    	this.examDate = date;
    }
    
    public void setTime(String time) {
    	this.examTime = time;
    }
    
    public void setDuration(int duration) {
    	this.examDuration = duration;
    }

    public void gradeExam(){
        do {
            System.out.println(this.getResultsString());
            User userInput = (User) UserManager.getInstance().selectUserFromInput("Enter Student ID: ");
            if(userInput == null) continue;
            if(!(userInput instanceof Student)) System.out.println("User is Not a Student");
            else {
                Student studentInput = (Student) userInput;
                if (!this.results.containsKey(studentInput)) System.out.println("No Student Found");
                else {
                    this.setMarksObtained(studentInput,Input.getIntInput("Enter Marks Obtained (0-100, -1 to Leave Blank): ", -1, 100));
                    System.out.println("Results Successfully Changed");
                    this.semesterCourse.save();	
                }
            }
        } while (Input.getBooleanInput("Continue Grading? [Y/N]: ", "Y", "N"));
	}
  
    public void addExaminer(Examiner examiner) {
    	this.examiners.add(examiner);
    }
    
    public void removeExaminer(Examiner examiner) {
        this.examiners.remove(examiner);
    }

    public void addStudent(Student student) {
        this.results.put(student, -1);
    }

    public void removeStudent(Student student) {
        this.results.remove(student);
    }

    public static void load() {
        try {
            File file = new File("src/data/exam.txt");
            file.createNewFile();
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String line = bufferedReader.readLine();
            if (line != null) Exam.totalID = Integer.valueOf(line);
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Exam ID Could Not Be Loaded. Aborting...");
            System.exit(1);
        }
    }
    
    public void save(BufferedWriter bufferedWriter) throws IOException{
        bufferedWriter.write(
            String.format(
            """

            %s
            %s
            %s
            %d
            %d
            %s
            %s
            %d
            """, this.examID, this.examType, this.examVenue, this.totalMarks, this.overallPercentage, this.examDate, this.examTime, this.examDuration)
        );
        
        for (Entry<Student, Integer> entry: this.results.entrySet()) bufferedWriter.write(entry.getKey().getUserID() + " " + entry.getValue() + " ");
        bufferedWriter.newLine();
        for (Examiner examiner: this.examiners) bufferedWriter.write(examiner.getUserID() + " ");
        bufferedWriter.newLine();
    }

    public String getDetailsString() {
        return String.format(
            """
            ID : %s
            Type : %s
            Date : %s
            Time : %s
            Duration : %d Minutes
            Venue : %s
            Total Marks : %d
            Overall Percentage : %d%%
            """, this.examID, this.examType, this.examDate, this.examTime, this.examDuration, this.examVenue, this.totalMarks, this.overallPercentage
        );
    }
    
    public String getExaminersString(){
    	StringBuilder allExaminers = new StringBuilder("Examiners\n----------\n");
        if (this.examiners.size() > 0) {
            for (Examiner examiner : this.examiners) {
              allExaminers.append(examiner.getUserID()).append(" : ").append(examiner.getName()).append('\n');
            }
        } else allExaminers.append("No Examiners Found");
		return allExaminers.toString();
    }
    
    public String getResultsString(){
    	StringBuilder allResults = new StringBuilder("Results\n----------\n");
        if (this.results.size() > 0) {
            for (Entry<Student, Integer> entry : this.results.entrySet()) {
                allResults.append(entry.getKey().getUserID()).append(" : ").append((entry.getValue() >= 0)? entry.getValue(): "Not Set").append("\n");
            }
        } else allResults.append("No Results Found");

		return allResults.toString();
    }

    public void setMarksObtained(Student student, int newMarks) {
        this.results.put(student, newMarks);
    }

    public int getStudentMarks(Student student) {
        return this.results.get(student);
    }
    
    public void onDelete() {
        for (Examiner examiner: this.examiners) {
            examiner.removeAssignedExam(this);
        }
    }
    
    @Override
    public String toString() {
        return String.join("\n", this.semesterCourse.getCourse().getCourseTitle(), this.getDetailsString() + this.getExaminersString() + this.getResultsString());
    }

    @Override
    public int compareTo(Exam o) {
        return this.examID.compareTo(o.getExamID());
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Exam && this.compareTo((Exam) obj) == 0;
    }
}
