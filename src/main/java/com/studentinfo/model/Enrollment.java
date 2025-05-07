package com.studentinfo.model;

import java.time.LocalDate;

public class Enrollment {
    private int id;
    private int studentId;
    private int courseId;
    private LocalDate enrollmentDate;
    private String status; // ACTIVE, DROPPED, COMPLETED
    private Student student;
    private Course course;

    public Enrollment() {
    }

    public Enrollment(int id, int studentId, int courseId, LocalDate enrollmentDate, String status) {
        this.id = id;
        this.studentId = studentId;
        this.courseId = courseId;
        this.enrollmentDate = enrollmentDate;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public LocalDate getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(LocalDate enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public String getCourseCode() {
        return course != null ? course.getCourseCode() : "";
    }

    public String getCourseName() {
        return course != null ? course.getName() : "";
    }

    public int getCredits() {
        return course != null ? course.getCredits() : 0;
    }

    @Override
    public String toString() {
        if (student != null && course != null) {
            return student.getName() + " - " + course.getName();
        }
        return "Enrollment #" + id;
    }
} 