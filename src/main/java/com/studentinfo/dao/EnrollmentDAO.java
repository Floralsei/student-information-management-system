package com.studentinfo.dao;

import com.studentinfo.model.Enrollment;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EnrollmentDAO {
    private final Connection connection;

    public EnrollmentDAO(Connection connection) {
        this.connection = connection;
    }

    public void addEnrollment(Enrollment enrollment) throws SQLException {
        String sql = "INSERT INTO enrollments (student_id, course_id, status) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, enrollment.getStudentId());
            stmt.setInt(2, enrollment.getCourseId());
            stmt.setString(3, enrollment.getStatus());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    enrollment.setId(rs.getInt(1));
                }
            }
        }
    }

    public void updateEnrollment(Enrollment enrollment) throws SQLException {
        String sql = "UPDATE enrollments SET status = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, enrollment.getStatus());
            stmt.setInt(2, enrollment.getId());
            stmt.executeUpdate();
        }
    }

    public void deleteEnrollment(int studentId, int courseId) throws SQLException {
        String sql = "DELETE FROM enrollments WHERE student_id = ? AND course_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, courseId);
            stmt.executeUpdate();
        }
    }

    public Enrollment getEnrollmentById(int id) throws SQLException {
        String sql = "SELECT * FROM enrollments WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractEnrollmentFromResultSet(rs);
                }
            }
        }
        return null;
    }

    public List<Enrollment> getEnrollmentsByStudentId(int studentId) throws SQLException {
        String sql = "SELECT * FROM enrollments WHERE student_id = ?";
        List<Enrollment> enrollments = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    enrollments.add(extractEnrollmentFromResultSet(rs));
                }
            }
        }
        return enrollments;
    }

    public List<Enrollment> getEnrollmentsByCourseId(int courseId) throws SQLException {
        String sql = "SELECT * FROM enrollments WHERE course_id = ?";
        List<Enrollment> enrollments = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, courseId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    enrollments.add(extractEnrollmentFromResultSet(rs));
                }
            }
        }
        return enrollments;
    }

    public List<Enrollment> getAllEnrollments() throws SQLException {
        String sql = "SELECT * FROM enrollments";
        List<Enrollment> enrollments = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                enrollments.add(extractEnrollmentFromResultSet(rs));
            }
        }
        return enrollments;
    }

    public Enrollment getEnrollmentByStudentAndCourse(int studentId, int courseId) throws SQLException {
        String sql = "SELECT * FROM enrollments WHERE student_id = ? AND course_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, courseId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractEnrollmentFromResultSet(rs);
                }
            }
        }
        return null;
    }

    private Enrollment extractEnrollmentFromResultSet(ResultSet rs) throws SQLException {
        Enrollment enrollment = new Enrollment();
        enrollment.setId(rs.getInt("id"));
        enrollment.setStudentId(rs.getInt("student_id"));
        enrollment.setCourseId(rs.getInt("course_id"));
        enrollment.setStatus(rs.getString("status"));
        return enrollment;
    }
} 