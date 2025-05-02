import java.util.TreeSet;
import java.util.stream.Collectors;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.TreeMap;

class SemesterCourse implements Comparable<SemesterCourse> {
    private String semester;
    private Course course;
    private TreeSet<Student> students = new TreeSet<>();
    private TreeMap<String,Exam> exams = new TreeMap<>();

    public SemesterCourse() {
        this("000000", null);
    }
    
    public SemesterCourse(String semester, Course course) {
    	this.semester = semester;
    	this.course = course;
    }

	public String getSemester() {
        return this.semester;
    }

	public int getCreditHour() {
        return this.course.getCreditHour();
    }

    public Course getCourse() {
        return this.course;
    }

	public void manage() {
        while(true){
            switch(
                Input.getIntInput(
                    String.format("""
                
                    Manage Semester Course %s:
                    1. Manage Student List
                    2. Manage Exams
                    3. Back

                    Your Input: \
                    """, this.course.getCourseTitle()), 1,3)){
                case 1:
                    this.manageStudentList();
                    break;
                case 2:
                    this.manageExams();
                    break;
                case 3:
                    return;  
            }
        }
	}

    public void manageStudentList() {
        while(true){
            switch(Input.getIntInput(
                """

                Manage Students
                1. Add Student 
                2. Remove Student
                3. Show Student List
                4. Back
                
                Your Input: \
                """,1,4)){
                case 1:
                    User userToAdd = UserManager.getInstance().selectUserFromInput("Enter Student ID: ");
                    if(userToAdd == null) break;

                    if(userToAdd instanceof Student){
                        Student studentInput = (Student) userToAdd;
                        if(!this.students.contains(studentInput)){
                            this.addStudent(studentInput);
                            studentInput.addSemesterCourse(this);
                            for (Exam exam: this.exams.values()) exam.addStudent(studentInput);
                            System.out.println("User sucessfully added");
                            this.save();
                        } else System.out.println("User already exists in the list");
                    } else System.out.println("User is not a student");
                    
                    break;
                    case 2:
                    User userToRemove = UserManager.getInstance().selectUserFromInput("Enter Student ID: ");
                    if(userToRemove == null) break;
                    
                    if(userToRemove instanceof Student){
                        Student studentInput = (Student) userToRemove;
                        if(this.students.contains(studentInput)){
                            this.removeStudent(studentInput);
                            studentInput.removeSemesterCourse(this);
                            for (Exam exam: this.exams.values()) exam.removeStudent(studentInput);
                            System.out.println("User successfully removed");
                            this.save();
                        } else System.out.println("User is not in the list");
                    } else System.out.println("User is not a student");

                    break;
                case 3:
                    System.out.println(this.getStudentsString());
                    break;
                case 4:
                    return;
            }
        }
    }
    
    public void manageExams() {
        while(true){
            switch(
                Input.getIntInput(
                    """

                    Manage Exams
                    1. Add Exam
                    2. Remove Exam
                    3. Edit Exam
                    4. View all Exam
                    5. Back
                    
                    Your Input: \
                    """,1,5)){
                case 1:
                    String type = Input.getStringInput("Enter exam type (Optional): ", "Not Set");
                    String venue = Input.getStringInput("Enter exam venue (Optional): ", "Not Set");
                    int totalMarks = Input.getIntInput("Enter total marks (1-100): ", 1, 100);
                    int overallPercentage = Input.getIntInput("Enter overall percentage (1-100)%: ", 1, 100);
                    String time = Input.getTimeInput("Enter exam time (HH:MM, Optional): ");
                    String date = Input.getDateInput("Enter exam date (DD/MM/YYYY, Optional): ");
                    int duration = Input.getIntInput("Enter exam duration (Minutes): ", 0, 60 * 24); // Max 1 day
                    Exam newExam = new Exam(type, venue, totalMarks, overallPercentage, time, date, duration, this);
                    this.addExam(newExam);
                    for (Student student: this.students) newExam.addStudent(student);
                    System.out.println("Exam sucessfully added");
                    this.save();
                    break;
                case 2:
                    Exam examInput = this.selectExamFromInput();
                    if(examInput != null){
                        this.removeExam(examInput);
                        System.out.println("Exam sucessfully removed");
                        this.save();
                    } else System.out.println("Exam not found");
                    break;
                case 3:
                    this.editExam(this.selectExamFromInput());
                    break;
                case 4:
                    System.out.println(this.getExamsString());
                    break;
                case 5:
                    return;
            }
        }
    }

    public void editExam(Exam exam) {
        if (exam == null) return;
        while(true){
            switch (Input.getIntInput(
                String.format("""

                Edit Exam %s
                1. Edit Exam Results
                2. Add Examiner 
                3. Remove Examiner
                4. Change Type
                5. Change Venue
                6. Change Overall Percentage
                7. Change Time
                8. Change Date
                9. Change Duration
               10. Change Total Marks
               11. View Exam Details
               12. View All Examiners
               13. View All Exam Results
               14. Back
                    
               Your Input: \
                """, exam.getExamID()),1,14)) {
                case 1 :
                    exam.gradeExam();
                    break;
                case 2:
                    User examinerToAdd = UserManager.getInstance().selectUserFromInput("Enter Examiner ID: ");
                    if (examinerToAdd == null) break;
                    if(examinerToAdd instanceof Examiner){
                        Examiner examinerInput = (Examiner) examinerToAdd;
                        exam.addExaminer(examinerInput);
                        examinerInput.addAssignedExam(exam);
                        System.out.println("Examiner added successfully");
                        this.save();
                    } else System.out.println("User is not an examiner");
                    break;
                case 3:
                    User examinerToRemove = UserManager.getInstance().selectUserFromInput("Enter Examiner ID: ");
                    if (examinerToRemove == null) break;
                    if(examinerToRemove instanceof Examiner){
                        Examiner examinerInput = (Examiner) examinerToRemove;
                        exam.removeExaminer(examinerInput);
                        examinerInput.removeAssignedExam(exam);
                        System.out.println("Examiner removed successfully");
                        this.save();
                    } else System.out.println("User is not an examiner");
                    break;
                case 4:
                    exam.setType(Input.getStringInput("Enter exam type (Optional): ", "Not Set"));
                    System.out.println("Exam type changed succesfully");
                    this.save();
                    break;
                case 5:
                    exam.setVenue(Input.getStringInput("Enter exam venue (Optional): ", "Not Set"));
                    System.out.println("Exam venue changed succesfully");
                    this.save();
                    break;
                case 6:
                    exam.setOverallPercentage(Input.getIntInput("Enter overall percentage (1-100)%: ", 1, 100));
                    System.out.println("Exam overall percentage changed succesfully");
                    this.save();
                    break;
                case 7:
                    exam.setTime(Input.getTimeInput("Enter new exam time (HH:MM, Optional): "));
                    System.out.println("Exam time changed succesfully");
                    this.save();
                    break;
                case 8:
                    exam.setDate(Input.getDateInput("Enter New Exam Date (DD/MM/YYYY, Optional): ")); 
                    System.out.println("Exam date changed succesfully");
                    this.save();
                    break;
                case 9:
                    //Max 1 day
                    exam.setDuration(Input.getIntInput("Enter exam duration (minutes): ", 0, 24 * 60));
                    System.out.println("Exam date changed succesfully");
                    this.save();
                    break;
                case 10:
                    if (!exam.setTotalMarks(Input.getIntInput("Enter total marks : ", 0, 100))) break;
                    System.out.println("Exam total Marks changed succesfully");
                    this.save();
                    break;
                case 11:
                    System.out.println(exam.getDetailsString());
                    break;
                case 12:
                    System.out.println(exam.getExaminersString());
                    break;
                case 13:				
                    System.out.println(exam.getResultsString());
                    break;	
                case 14:
                    return;
            }
            
        }
    }

    public void save() {
        try {
            File actual = new File("src/data/semester_courses/" + this.semester + this.course.getCourseID() + ".txt");
            File backup = new File("src/data/semester_courses/" + this.semester + this.course.getCourseID() + ".backup");
            File temp = new File("src/data/semester_courses/" + this.semester + this.course.getCourseID() + ".temp");
            actual.createNewFile();

            backup.delete();
            if (!actual.renameTo(backup)) throw new IOException(String.format("Semester Course %s %s Could Not Be Backed Up, Aborting Save", this.semester, this.course.getCourseID()));

            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(temp));

            bufferedWriter.write(this.semester);
            bufferedWriter.newLine();
            bufferedWriter.write(this.course.getCourseID());
            bufferedWriter.newLine();
            
            //Write student list
            bufferedWriter.write(String.join(" ", this.students.stream().map(Student::getUserID).collect(Collectors.toList())));
            bufferedWriter.newLine();

            for (Exam exam: this.exams.values()) exam.save(bufferedWriter);
            bufferedWriter.close();

            if (temp.renameTo(actual)) {
                backup.delete();
                System.out.println("Semester Course Successfully Saved");
            }
            else {
                if (backup.renameTo(actual)) System.out.printf("Semester Course %s %s File Could Not Be Saved, Previous Version is Restored.\n", this.semester, this.getCourseID());
                throw new IOException(String.format("Senester Course %s %s File Could Not Be Saved, Previous Version is Saved in a Backup File but Could Not Be Restored.", this.semester, this.getCourseID()));
            };
        } catch (IOException e) {
            System.out.printf("Semester Course %s %s Could Not Be Saved", this.semester, this.getCourseID());
        }
    }

    public void addStudent(Student student) {
        this.students.add(student);
        for (Exam exam: this.exams.values()) {
            exam.addStudent(student);
        }
    }
    
    public void removeStudent(Student student) {
        this.students.remove(student);
        for (Exam exam: this.exams.values()) {
            exam.removeStudent(student);
        }
    }

    public int getStudentMarks(Student student) {
        //Formula: Total Contribution Marks * Marks Obtained / Total Exam Marks
        float totalMarks = 0;
        int totalContribution = 0; //Must be 100 for result to be valid
        for (Exam exam: this.exams.values()) {
            if (exam.getStudentMarks(student) == -1) return -1;//-1 Means unassigned, no marks, so result invalid
            totalMarks += (float) exam.getOverallPercentage() * exam.getStudentMarks(student) / exam.getTotalMarks();
            totalContribution += exam.getOverallPercentage();
        }
        if (totalContribution != 100) return -1;
        else return Math.round(totalMarks / 100);
    }

    public String getCourseID() {
        return this.course.getCourseID();
    }
    
    public void addExam(Exam exam) {
    	this.exams.put(exam.getExamID(), exam);	
    }
    
    public void removeExam(Exam exam) {
    	this.exams.remove(exam.getExamID()).onDelete();
    }

    public Exam selectExamFromInput(){
        System.out.println(this.getExamsString());
  		String id = Input.getStringInput("Enter Exam ID: ");
		Exam exam = this.exams.get(id);
		if(exam == null){
			System.out.println("Exam does not exist");
            return null;
		}
  		return this.exams.get(id);
  	}

	private String getStudentsString(){
    	String allStudents = "Students\n----------\n";
		if(this.students.size() > 0){
    		for (Student student : this.students) {
				allStudents = allStudents + student.getUserID() + " : " + student.getName() + "\n";
			}
		}else{
			allStudents += "No Students Yet";
		}
		return allStudents;
	}

	private String getExamsString(){
    	String allExams = "Exams\n----------\n";

		if(this.exams.size() > 0){
			for (Exam exam: this.exams.values()) {
                allExams += exam.getDetailsString() + "\n-----------------\n";
            }
		} else allExams += "No exams yet";

		return allExams;
	}

    public void onDelete() {
        for (Student student: students) student.removeSemesterCourse(this);
        for (Exam exam: exams.values()) exam.onDelete();
        File file = new File("src/data/semester_courses/" + this.semester + this.getCourseID() + ".txt");
        file.delete();
    }

    @Override
    public int compareTo(SemesterCourse o) {//Sort by semester, then by course
        return (this.semester.equals(getSemester()))? this.getCourse().compareTo(o.getCourse()): this.semester.compareTo(o.getSemester());
    }

    @Override
	public String toString(){
		return this.getStudentsString() + this.getExamsString();
	}

    @Override
    public boolean equals(Object obj) {
        return obj instanceof SemesterCourse && this.compareTo((SemesterCourse) obj) == 0;
    }

}