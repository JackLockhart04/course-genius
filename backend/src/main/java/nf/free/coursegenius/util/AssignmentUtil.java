package nf.free.coursegenius.util;

import nf.free.coursegenius.config.AppConfig;
import nf.free.coursegenius.dto.Assignment;
import nf.free.coursegenius.dto.AssignmentGroup;
import nf.free.coursegenius.exceptions.ApiException;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public static void addAssignmentGroup(int courseId, String name, BigDecimal weight) {
        if (name == null || name.isEmpty()) {
            throw new ApiException("Assignment group name cannot be empty", 400);
        }
        if (weight == null || weight.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ApiException("Assignment group weight must be greater than 0", 400);
        }

        String sql = "INSERT INTO assignment_group (course_id, name, weight) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, courseId);
            statement.setString(2, name);
            statement.setBigDecimal(3, weight);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new ApiException("Error adding assignment group: " + e.getMessage(), 500);
        }
    }

    public static void addAssignment(int assignmentGroupId, String name, BigDecimal pointsEarned, BigDecimal pointsPossible) {
        if (name == null || name.isEmpty()) {
            throw new ApiException("Assignment name cannot be empty", 400);
        }
        if (pointsEarned == null || pointsEarned.compareTo(BigDecimal.ZERO) < 0) {
            throw new ApiException("Points earned cannot be negative", 400);
        }
        if (pointsPossible == null || pointsPossible.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ApiException("Points possible must be greater than 0", 400);
        }
        if (pointsEarned.compareTo(pointsPossible) > 0) {
            throw new ApiException("Points earned cannot be greater than points possible", 400);
        }

        // Calculate percentage grade
        BigDecimal percentageGrade = pointsEarned.divide(pointsPossible, 4, BigDecimal.ROUND_HALF_UP)
                                               .multiply(new BigDecimal("100"));

        String sql = "INSERT INTO assignment (assignment_group_id, name, points_earned, points_possible, percentage_grade) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, assignmentGroupId);
            statement.setString(2, name);
            statement.setBigDecimal(3, pointsEarned);
            statement.setBigDecimal(4, pointsPossible);
            statement.setBigDecimal(5, percentageGrade);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new ApiException("Error adding assignment: " + e.getMessage(), 500);
        }
    }

    public static List<AssignmentGroup> getAssignmentGroupsByCourseId(int courseId) {
        String sql = "SELECT ag.id AS group_id, ag.name AS group_name, ag.weight AS group_weight, " +
                     "a.id AS assignment_id, a.name AS assignment_name, " +
                     "a.points_earned, a.points_possible, a.percentage_grade " +
                     "FROM assignment_group ag " +
                     "LEFT JOIN assignment a ON ag.id = a.assignment_group_id " +
                     "WHERE ag.course_id = ?";
        
        Map<Integer, AssignmentGroup> groupMap = new HashMap<>();
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, courseId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    int groupId = rs.getInt("group_id");
                    AssignmentGroup group = groupMap.get(groupId);
                    if (group == null) {
                        group = new AssignmentGroup(
                            groupId,
                            rs.getString("group_name"),
                            rs.getBigDecimal("group_weight"),
                            courseId,
                            new ArrayList<>()
                        );
                        groupMap.put(groupId, group);
                    }

                    int assignmentId = rs.getInt("assignment_id");
                    if (assignmentId != 0) {
                        Assignment assignment = new Assignment(
                            assignmentId,
                            groupId,
                            rs.getString("assignment_name"),
                            rs.getBigDecimal("points_earned"),
                            rs.getBigDecimal("points_possible"),
                            rs.getBigDecimal("percentage_grade")
                        );
                        group.addAssignment(assignment);
                    }
                }
            }
        } catch (SQLException e) {
            throw new ApiException("Error fetching assignment groups: " + e.getMessage(), 500);
        }
        return new ArrayList<>(groupMap.values());
    }

    public static Assignment getAssignmentById(int assignmentId) {
        String sql = "SELECT a.*, ag.course_id " +
                     "FROM assignment a " +
                     "JOIN assignment_group ag ON a.assignment_group_id = ag.id " +
                     "WHERE a.id = ?";
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

    public static void updateAssignment(int assignmentId, BigDecimal pointsEarned, BigDecimal pointsPossible) {
        if (pointsEarned == null || pointsEarned.compareTo(BigDecimal.ZERO) < 0) {
            throw new ApiException("Points earned cannot be negative", 400);
        }
        if (pointsPossible == null || pointsPossible.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ApiException("Points possible must be greater than 0", 400);
        }

        // Calculate percentage grade
        BigDecimal percentageGrade = pointsEarned.divide(pointsPossible, 4, BigDecimal.ROUND_HALF_UP)
                                               .multiply(new BigDecimal("100"));

        String sql = "UPDATE assignment SET points_earned = ?, points_possible = ?, percentage_grade = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setBigDecimal(1, pointsEarned);
            statement.setBigDecimal(2, pointsPossible);
            statement.setBigDecimal(3, percentageGrade);
            statement.setInt(4, assignmentId);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                throw new ApiException("Assignment not found", 404);
            }
        } catch (SQLException e) {
            throw new ApiException("Error updating assignment: " + e.getMessage(), 500);
        }
    }

    public static void deleteAssignment(int assignmentId) {
        String sql = "DELETE FROM assignment WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, assignmentId);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                throw new ApiException("Assignment not found", 404);
            }
        } catch (SQLException e) {
            throw new ApiException("Error deleting assignment: " + e.getMessage(), 500);
        }
    }

    public static AssignmentGroup getAssignmentGroupById(int groupId) {
        String sql = "SELECT ag.*, a.id AS assignment_id, a.name AS assignment_name, " +
                     "a.points_earned, a.points_possible, a.percentage_grade " +
                     "FROM assignment_group ag " +
                     "LEFT JOIN assignment a ON ag.id = a.assignment_group_id " +
                     "WHERE ag.id = ?";
        
        AssignmentGroup group = null;
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, groupId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    if (group == null) {
                        group = new AssignmentGroup(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getBigDecimal("weight"),
                            rs.getInt("course_id"),
                            new ArrayList<>()
                        );
                    }

                    int assignmentId = rs.getInt("assignment_id");
                    if (assignmentId != 0) {
                        Assignment assignment = new Assignment(
                            assignmentId,
                            groupId,
                            rs.getString("assignment_name"),
                            rs.getBigDecimal("points_earned"),
                            rs.getBigDecimal("points_possible"),
                            rs.getBigDecimal("percentage_grade")
                        );
                        group.addAssignment(assignment);
                    }
                }
            }
        } catch (SQLException e) {
            throw new ApiException("Error fetching assignment group: " + e.getMessage(), 500);
        }
        return group;
    }

    private static Assignment mapResultSetToAssignment(ResultSet rs) {
        try {
            int id = rs.getInt("id");
            int assignmentGroupId = rs.getInt("assignment_group_id");
            String name = rs.getString("name");
            BigDecimal pointsEarned = rs.getBigDecimal("points_earned");
            BigDecimal pointsPossible = rs.getBigDecimal("points_possible");
            BigDecimal percentageGrade = rs.getBigDecimal("percentage_grade");

            return new Assignment(id, assignmentGroupId, name, pointsEarned, pointsPossible, percentageGrade);
        } catch (SQLException e) {
            throw new ApiException("Error mapping result set to assignment: " + e.getMessage(), 500);
        }
    }

    public static void updateAssignmentName(int assignmentId, String name) {
        String sql = "UPDATE assignment SET name = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setInt(2, assignmentId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new ApiException("Failed to update assignment name: " + e.getMessage(), 500);
        }
    }

    public static void updateAssignmentGroupName(int groupId, String name) {
        String sql = "UPDATE assignment_group SET name = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setInt(2, groupId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new ApiException("Failed to update assignment group name: " + e.getMessage(), 500);
        }
    }

    public static void deleteAssignmentGroup(int groupId) {
        // First delete all assignments in the group
        String deleteAssignmentsSql = "DELETE FROM assignment WHERE assignment_group_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(deleteAssignmentsSql)) {
            stmt.setInt(1, groupId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new ApiException("Failed to delete assignments in group: " + e.getMessage(), 500);
        }

        // Then delete the group itself
        String deleteGroupSql = "DELETE FROM assignment_group WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(deleteGroupSql)) {
            stmt.setInt(1, groupId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new ApiException("Failed to delete assignment group: " + e.getMessage(), 500);
        }
    }

    public static void updateAssignmentGroup(int groupId, String name, BigDecimal weight) {
        String sql = "UPDATE assignment_group SET name = ?, weight = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setBigDecimal(2, weight);
            stmt.setInt(3, groupId);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new ApiException("Assignment group not found", 404);
            }
        } catch (SQLException e) {
            throw new ApiException("Failed to update assignment group: " + e.getMessage(), 500);
        }
    }
}