package com.studentinfo.dao;

import com.studentinfo.database.DatabaseConnection;
import com.studentinfo.model.Grade;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class GradeDAO {
    private final Connection connection;

    public GradeDAO(Connection connection) {
        this.connection = connection;
    }

    public void addGrade(Grade grade) throws SQLException {
        String sql = "INSERT INTO grades (student_id, course_id, score, comment) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, grade.getStudentId());
            stmt.setInt(2, grade.getCourseId());
            stmt.setInt(3, grade.getScore());
            stmt.setString(4, grade.getComment());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    grade.setId(rs.getInt(1));
                }
            }
        }
    }

    public void updateGrade(Grade grade) throws SQLException {
        String sql = "UPDATE grades SET score = ?, comment = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, grade.getScore());
            stmt.setString(2, grade.getComment());
            stmt.setInt(3, grade.getId());
            stmt.executeUpdate();
        }
    }

    public void deleteGrade(int id) throws SQLException {
        String sql = "DELETE FROM grades WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public Grade getGradeById(int id) throws SQLException {
        String sql = "SELECT * FROM grades WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractGradeFromResultSet(rs);
                }
            }
        }
        return null;
    }

    public Grade getGradeByStudentAndCourse(int studentId, int courseId) throws SQLException {
        String sql = "SELECT * FROM grades WHERE student_id = ? AND course_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, courseId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractGradeFromResultSet(rs);
                }
            }
        }
        return null;
    }

    public List<Grade> getGradesByStudentId(int studentId) throws SQLException {
        String sql = "SELECT * FROM grades WHERE student_id = ?";
        List<Grade> grades = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    grades.add(extractGradeFromResultSet(rs));
                }
            }
        }
        return grades;
    }

    public List<Grade> getGradesByCourseId(int courseId) throws SQLException {
        String sql = "SELECT * FROM grades WHERE course_id = ?";
        List<Grade> grades = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, courseId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    grades.add(extractGradeFromResultSet(rs));
                }
            }
        }
        return grades;
    }

    private Grade extractGradeFromResultSet(ResultSet rs) throws SQLException {
        Grade grade = new Grade();
        grade.setId(rs.getInt("id"));
        grade.setStudentId(rs.getInt("student_id"));
        grade.setCourseId(rs.getInt("course_id"));
        grade.setScore(rs.getInt("score"));
        grade.setComment(rs.getString("comment"));
        return grade;
    }
} 