package com.studentinfo.model;

import java.time.LocalDate;

public class Student {
    private int id;
    private String studentNumber;
    private String name;
    private String gender;
    private LocalDate birthDate;
    private String address;
    private String phone;
    private String email;
    private LocalDate enrollmentDate;
    private String status;
    private String politicalStatus;
    private String dormitory;

    public Student() {
    }

    public Student(int id, String studentNumber, String name, String gender, LocalDate birthDate,
                  String address, String phone, String email, LocalDate enrollmentDate,
                  String status, String politicalStatus, String dormitory) {
        this.id = id;
        this.studentNumber = studentNumber;
        this.name = name;
        this.gender = gender;
        this.birthDate = birthDate;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.enrollmentDate = enrollmentDate;
        this.status = status;
        this.politicalStatus = politicalStatus;
        this.dormitory = dormitory;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getPoliticalStatus() {
        return politicalStatus;
    }

    public void setPoliticalStatus(String politicalStatus) {
        this.politicalStatus = politicalStatus;
    }

    public String getDormitory() {
        return dormitory;
    }

    public void setDormitory(String dormitory) {
        this.dormitory = dormitory;
    }
} 