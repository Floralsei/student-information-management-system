package com.studentinfo.dao;

import com.studentinfo.database.DatabaseConnection;
import com.studentinfo.model.Course;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseDAO {
    private final DatabaseConnection dbConnection;

    public CourseDAO(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    public void addCourse(Course course) throws SQLException {
        String sql = "INSERT INTO courses (course_id, name, teacher, credits, description, max_students) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, course.getCourseId());
            pstmt.setString(2, course.getName());
            pstmt.setString(3, course.getTeacher());
            pstmt.setInt(4, course.getCredits());
            pstmt.setString(5, course.getDescription());
            pstmt.setInt(6, course.getMaxStudents());
            pstmt.executeUpdate();
        }
    }

    public List<Course> getAllCourses() throws SQLException {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM courses";

        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Course course = new Course(
                    rs.getInt("id"),
                    rs.getString("course_id"),
                    rs.getString("name"),
                    rs.getString("teacher"),
                    rs.getInt("credits"),
                    rs.getString("description"),
                    rs.getInt("max_students")
                );
                courses.add(course);
            }
        }
        return courses;
    }

    public Course getCourseById(int id) throws SQLException {
        String sql = "SELECT * FROM courses WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Course(
                        rs.getInt("id"),
                        rs.getString("course_id"),
                        rs.getString("name"),
                        rs.getString("teacher"),
                        rs.getInt("credits"),
                        rs.getString("description"),
                        rs.getInt("max_students")
                    );
                }
            }
        }
        return null;
    }

    public void updateCourse(Course course) throws SQLException {
        String sql = "UPDATE courses SET course_id = ?, name = ?, teacher = ?, " +
                    "credits = ?, description = ?, max_students = ? WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, course.getCourseId());
            pstmt.setString(2, course.getName());
            pstmt.setString(3, course.getTeacher());
            pstmt.setInt(4, course.getCredits());
            pstmt.setString(5, course.getDescription());
            pstmt.setInt(6, course.getMaxStudents());
            pstmt.setInt(7, course.getId());
            pstmt.executeUpdate();
        }
    }

    public void deleteCourse(int id) throws SQLException {
        String sql = "DELETE FROM courses WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }
} 