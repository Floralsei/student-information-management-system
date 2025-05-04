package com.studentinfo.model;

public class Course {
    private int id;
    private String courseId;
    private String name;
    private String teacher;
    private int credits;
    private String description;
    private int maxStudents;

    public Course() {
    }

    public Course(int id, String courseId, String name, String teacher, 
                 int credits, String description, int maxStudents) {
        this.id = id;
        this.courseId = courseId;
        this.name = name;
        this.teacher = teacher;
        this.credits = credits;
        this.description = description;
        this.maxStudents = maxStudents;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getMaxStudents() {
        return maxStudents;
    }

    public void setMaxStudents(int maxStudents) {
        this.maxStudents = maxStudents;
    }
} 