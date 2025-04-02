package nf.free.coursegenius.util;

import nf.free.coursegenius.config.AppConfig;
import nf.free.coursegenius.dto.Assignment;
import nf.free.coursegenius.dto.Course;
import nf.free.coursegenius.dto.User;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CourseUtil {

    static {
        try {
            Class.forName(AppConfig.dbDriverClassName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to load database driver", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(AppConfig.dbUrl, AppConfig.dbUsername, AppConfig.dbPassword);
    }

    public static void addCourse(String courseName, String userOid) {
        if (courseName == null || courseName.isEmpty()) {
            throw new RuntimeException("Course name cannot be null or empty");
        }
        if (userOid == null || userOid.isEmpty()) {
            throw new RuntimeException("User ID cannot be null or empty");
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
                throw new RuntimeException("Course already exists for this user");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String sql = "INSERT INTO course (user_id, name) VALUES (?, ?)";
        try (Connection conn = getConnection();
                PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, userId);
            statement.setString(2, courseName);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteCourseById(int courseId, String userOid) {
        if (courseId <= 0) {
            throw new RuntimeException("Invalid course ID");
        }
        if (userOid == null || userOid.isEmpty()) {
            throw new RuntimeException("User ID cannot be null or empty");
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
                throw new RuntimeException("Course does not exist or does not belong to this user");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Delete the course
        String sql = "DELETE FROM course WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, courseId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Course> getCoursesByUserId(int userId) {
        String sql = "SELECT c.id AS course_id, c.user_id, c.name AS course_name, " +
                     "a.id AS assignment_id, a.course_id AS assignment_course_id, " +
                     "a.name AS assignment_name, a.weight, a.grade " +
                     "FROM course c " +
                     "LEFT JOIN assignment a ON c.id = a.course_id " +
                     "WHERE c.user_id = ?";
        Map<Integer, Course> courseMap = new HashMap<>();
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, userId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    int courseId = rs.getInt("course_id");
                    Course course = courseMap.get(courseId);
                    if (course == null) {
                        course = new Course(courseId, rs.getInt("user_id"), rs.getString("course_name"), new ArrayList<>());
                        courseMap.put(courseId, course);
                    }
                    int assignmentId = rs.getInt("assignment_id");
                    if (assignmentId != 0) {
                        Assignment assignment = new Assignment(
                                assignmentId,
                                rs.getInt("assignment_course_id"),
                                rs.getString("assignment_name"),
                                rs.getBigDecimal("weight"),
                                rs.getBigDecimal("grade")
                        );
                        course.getAssignments().add(assignment);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>(courseMap.values());
    }

    public static Course getCourseById(int courseId) {
        String sql = "SELECT c.id AS course_id, c.user_id, c.name AS course_name, " +
                     "a.id AS assignment_id, a.course_id AS assignment_course_id, " +
                     "a.name AS assignment_name, a.weight, a.grade " +
                     "FROM course c " +
                     "LEFT JOIN assignment a ON c.id = a.course_id " +
                     "WHERE c.id = ?";
        Course course = null;
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, courseId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    if (course == null) {
                        course = new Course(rs.getInt("course_id"), rs.getInt("user_id"), rs.getString("course_name"), new ArrayList<>());
                    }
                    int assignmentId = rs.getInt("assignment_id");
                    if (assignmentId != 0) {
                        Assignment assignment = new Assignment(
                                assignmentId,
                                rs.getInt("assignment_course_id"),
                                rs.getString("assignment_name"),
                                rs.getBigDecimal("weight"),
                                rs.getBigDecimal("grade")
                        );
                        course.getAssignments().add(assignment);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return course;
    }

    // private static Course mapResultSetToCourse(ResultSet rs) throws SQLException {
    //     int id = rs.getInt("id");
    //     int userId = rs.getInt("user_id");
    //     String name = rs.getString("name");

    //     return new Course(id, userId, name);
    // }
}