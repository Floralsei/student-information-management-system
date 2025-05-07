package com.studentinfo.dao;

import com.studentinfo.model.Course;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseDAO {
    private final Connection connection;

    public CourseDAO(Connection connection) {
        this.connection = connection;
    }

    public void addCourse(Course course) throws SQLException {
        String sql = "INSERT INTO courses (course_code, name, credits, teacher_id, description, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, course.getCourseCode());
            stmt.setString(2, course.getName());
            stmt.setInt(3, course.getCredits());
            stmt.setInt(4, course.getTeacherId());
            stmt.setString(5, course.getDescription());
            stmt.setString(6, course.getStatus());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    course.setId(rs.getInt(1));
                }
            }
        }
    }

    public void updateCourse(Course course) throws SQLException {
        String sql = "UPDATE courses SET course_code = ?, name = ?, credits = ?, teacher_id = ?, description = ?, status = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, course.getCourseCode());
            stmt.setString(2, course.getName());
            stmt.setInt(3, course.getCredits());
            stmt.setInt(4, course.getTeacherId());
            stmt.setString(5, course.getDescription());
            stmt.setString(6, course.getStatus());
            stmt.setInt(7, course.getId());
            stmt.executeUpdate();
        }
    }

    public void deleteCourse(int id) throws SQLException {
        String sql = "DELETE FROM courses WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public Course getCourseById(int id) throws SQLException {
        String sql = "SELECT * FROM courses WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractCourseFromResultSet(rs);
                }
            }
        }
        return null;
    }

    public Course getCourseByCode(String courseCode) throws SQLException {
        String sql = "SELECT * FROM courses WHERE course_code = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, courseCode);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractCourseFromResultSet(rs);
                }
            }
        }
        return null;
    }

    public List<Course> getAllCourses() throws SQLException {
        String sql = "SELECT * FROM courses";
        List<Course> courses = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                courses.add(extractCourseFromResultSet(rs));
            }
        }
        return courses;
    }

    public List<Course> getCoursesByTeacherId(int teacherId) throws SQLException {
        String sql = "SELECT * FROM courses WHERE teacher_id = ?";
        List<Course> courses = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, teacherId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    courses.add(extractCourseFromResultSet(rs));
                }
            }
        }
        return courses;
    }

    public List<Course> getAvailableCourses() throws SQLException {
        String sql = "SELECT * FROM courses WHERE status = '开放选课'";
        List<Course> courses = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    courses.add(extractCourseFromResultSet(rs));
                }
            }
        }
        return courses;
    }

    private Course extractCourseFromResultSet(ResultSet rs) throws SQLException {
        Course course = new Course();
        course.setId(rs.getInt("id"));
        course.setCourseCode(rs.getString("course_code"));
        course.setName(rs.getString("name"));
        course.setCredits(rs.getInt("credits"));
        course.setTeacherId(rs.getInt("teacher_id"));
        course.setDescription(rs.getString("description"));
        course.setStatus(rs.getString("status"));
        return course;
    }
} 