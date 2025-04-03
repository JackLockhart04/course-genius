package nf.free.coursegenius.util;

import nf.free.coursegenius.config.AppConfig;
import nf.free.coursegenius.dto.Assignment;
import nf.free.coursegenius.exceptions.ApiException;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AssignmentUtil {

    static {
        try {
            Class.forName(AppConfig.dbDriverClassName);
        } catch (ClassNotFoundException e) {
            throw new ApiException("Failed to load database driver", 500);
        }
    }

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(AppConfig.dbUrl, AppConfig.dbUsername, AppConfig.dbPassword);
        } catch (SQLException e) {
            throw new ApiException("Failed to connect to database", 500);
        }
    }

    public static List<Assignment> getAssignmentsByCourseId(int courseId) {
        String sql = "SELECT * FROM assignment WHERE course_id = ?";
        List<Assignment> assignments = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, courseId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    assignments.add(mapResultSetToAssignment(rs));
                }
            }
        } catch (SQLException e) {
            throw new ApiException("Error fetching assignments: " + e.getMessage(), 500);
        }
        return assignments;
    }

    public static Assignment getAssignmentById(int assignmentId) {
        String sql = "SELECT * FROM assignment WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, assignmentId);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAssignment(rs);
                }
            }
        } catch (SQLException e) {
            throw new ApiException("Error fetching assignment: " + e.getMessage(), 500);
        }
        throw new ApiException("Assignment not found", 404);
    }

    private static Assignment mapResultSetToAssignment(ResultSet rs) {
        try {
            int id = rs.getInt("id");
            int courseId = rs.getInt("course_id");
            String name = rs.getString("name");
            BigDecimal weight = rs.getBigDecimal("weight");
            BigDecimal grade = rs.getBigDecimal("grade");

            return new Assignment(id, courseId, name, weight, grade);
        } catch (SQLException e) {
            throw new ApiException("Error mapping result set to assignment: " + e.getMessage(), 500);
        }
    }
}