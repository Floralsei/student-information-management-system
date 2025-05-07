package com.studentinfo.model;

public class Course {
    private int id;
    private String courseCode;
    private String name;
    private int credits;
    private int teacherId;
    private String description;
    private String status;

    public Course() {
    }

    public Course(int id, String courseCode, String name, int credits, int teacherId, String description, String status) {
        this.id = id;
        this.courseCode = courseCode;
        this.name = name;
        this.credits = credits;
        this.teacherId = teacherId;
        this.description = description;
        this.status = status;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public int getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return courseCode + " - " + name;
    }
} 