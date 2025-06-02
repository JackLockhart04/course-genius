package nf.free.coursegenius.util;

import nf.free.coursegenius.config.AppConfig;
import nf.free.coursegenius.dto.Assignment;
import nf.free.coursegenius.dto.AssignmentGroup;
import nf.free.coursegenius.dto.Course;
import nf.free.coursegenius.dto.User;
import nf.free.coursegenius.exceptions.ApiException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.math.BigDecimal;

public class CourseUtil {

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
            throw new ApiException("Failed to connect to database: " + e.getMessage(), 500);
        }
    }

    public static void addCourse(String courseName, String userOid, BigDecimal creditHours) {
        if (courseName == null || courseName.isEmpty()) {
            throw new ApiException("Course name cannot be null or empty", 400);
        }
        if (userOid == null || userOid.isEmpty()) {
            throw new ApiException("User ID cannot be null or empty", 400);
        }
        if (creditHours == null) {
            creditHours = new BigDecimal("3.0"); // Default credit hours
        }

        // User stuff
        User user = UserUtil.getUserByOid(userOid);
        int userId = user.getId();

        // Check if the course already exists for the user
        String checkSql = "SELECT COUNT(*) FROM course WHERE user_id = ? AND name = ?";
        try (Connection conn = getConnection();
                PreparedStatement checkStatement = conn.prepareStatement(checkSql)) {
            checkStatement.setInt(1, userId);
            checkStatement.setString(2, courseName);
            ResultSet rs = checkStatement.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                throw new ApiException("Course already exists for this user", 409);
            }
        } catch (SQLException e) {
            throw new ApiException("Error checking course existence: " + e.getMessage(), 500);
        }

        String sql = "INSERT INTO course (user_id, name, credit_hours) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
                PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, userId);
            statement.setString(2, courseName);
            statement.setBigDecimal(3, creditHours);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new ApiException("Error adding course: " + e.getMessage(), 500);
        }
    }

    public static void deleteCourseById(int courseId, String userOid) {
        if (courseId <= 0) {
            throw new ApiException("Invalid course ID", 400);
        }
        if (userOid == null || userOid.isEmpty()) {
            throw new ApiException("User ID cannot be null or empty", 400);
        }
        
        // Check if the course exists and belongs to user
        User user = UserUtil.getUserByOid(userOid);
        int userId = user.getId();
        String checkSql = "SELECT COUNT(*) FROM course WHERE id = ? AND user_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement checkStatement = conn.prepareStatement(checkSql)) {
            checkStatement.setInt(1, courseId);
            checkStatement.setInt(2, userId);
            ResultSet rs = checkStatement.executeQuery();
            if (rs.next() && rs.getInt(1) == 0) {
                throw new ApiException("Course does not exist or does not belong to this user", 404);
            }
        } catch (SQLException e) {
            throw new ApiException("Error checking course existence: " + e.getMessage(), 500);
        }

        // Delete the course
        String sql = "DELETE FROM course WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, courseId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new ApiException("Error deleting course: " + e.getMessage(), 500);
        }
    }

    public static List<Course> getCoursesByUserId(int userId) {
        String sql = "SELECT c.id AS course_id, c.user_id, c.name AS course_name, " +
                     "c.credit_hours, c.gpa, " +
                     "ag.id AS group_id, ag.name AS group_name, ag.weight AS group_weight, " +
                     "a.id AS assignment_id, a.name AS assignment_name, " +
                     "a.points_earned, a.points_possible, a.percentage_grade " +
                     "FROM course c " +
                     "LEFT JOIN assignment_group ag ON c.id = ag.course_id " +
                     "LEFT JOIN assignment a ON ag.id = a.assignment_group_id " +
                     "WHERE c.user_id = ?";
        Map<Integer, Course> courseMap = new HashMap<>();
        Map<Integer, AssignmentGroup> groupMap = new HashMap<>();
        
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, userId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    int courseId = rs.getInt("course_id");
                    Course course = courseMap.get(courseId);
                    if (course == null) {
                        course = new Course(
                            courseId,
                            rs.getInt("user_id"),
                            rs.getString("course_name"),
                            rs.getBigDecimal("credit_hours"),
                            rs.getBigDecimal("gpa"),
                            new ArrayList<>()
                        );
                        courseMap.put(courseId, course);
                    }
                    
                    // Handle assignment group
                    int groupId = rs.getInt("group_id");
                    if (groupId != 0) {
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
                            course.addAssignmentGroup(group);
                        }
                        
                        // Add assignment if it exists
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
            }
        } catch (SQLException e) {
            throw new ApiException("Error retrieving courses: " + e.getMessage(), 500);
        }
        return new ArrayList<>(courseMap.values());
    }

    public static Course getCourseById(int courseId) {
        String sql = "SELECT c.id AS course_id, c.user_id, c.name AS course_name, " +
                     "c.credit_hours, c.gpa, " +
                     "ag.id AS group_id, ag.name AS group_name, ag.weight AS group_weight, " +
                     "a.id AS assignment_id, a.name AS assignment_name, " +
                     "a.points_earned, a.points_possible, a.percentage_grade " +
                     "FROM course c " +
                     "LEFT JOIN assignment_group ag ON c.id = ag.course_id " +
                     "LEFT JOIN assignment a ON ag.id = a.assignment_group_id " +
                     "WHERE c.id = ?";
        Course course = null;
        Map<Integer, AssignmentGroup> groupMap = new HashMap<>();
        
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, courseId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    if (course == null) {
                        course = new Course(
                            rs.getInt("course_id"),
                            rs.getInt("user_id"),
                            rs.getString("course_name"),
                            rs.getBigDecimal("credit_hours"),
                            rs.getBigDecimal("gpa"),
                            new ArrayList<>()
                        );
                    }
                    
                    // Handle assignment group
                    int groupId = rs.getInt("group_id");
                    if (groupId != 0) {
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
                            course.addAssignmentGroup(group);
                        }
                        
                        // Add assignment if it exists
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
            }
        } catch (SQLException e) {
            throw new ApiException("Error retrieving course: " + e.getMessage(), 500);
        }
        return course;
    }

    public static void updateCourseGpa(int courseId, BigDecimal gpa) {
        String sql = "UPDATE course SET gpa = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setBigDecimal(1, gpa);
            statement.setInt(2, courseId);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                throw new ApiException("Course not found", 404);
            }
        } catch (SQLException e) {
            throw new ApiException("Error updating course GPA: " + e.getMessage(), 500);
        }
    }
}