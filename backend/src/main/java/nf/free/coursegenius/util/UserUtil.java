package nf.free.coursegenius.util;

import nf.free.coursegenius.config.AppConfig;
import nf.free.coursegenius.dto.User;
import nf.free.coursegenius.exceptions.ApiException;
import nf.free.coursegenius.services.TokenService;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Map;

public class UserUtil {

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

    public static String getUserOidByAccessToken(String accessToken){
        if (accessToken == null || accessToken.isEmpty()) {
            throw new ApiException("Access token cannot be null or empty", 400);
        }
        // Get user data from token using TokenService
        Map<String, Object> userData = TokenService.validateAndGetUserData(accessToken);
        
        // Get user ID from token data
        Object userOidObj = userData.get("oid");
        if (userOidObj == null) {
            throw new ApiException("User ID not found in token data", 400);
        }
        String userOid = userOidObj.toString();
        if (userOid == null || userOid.isEmpty()) {
            throw new ApiException("User ID cannot be null or empty", 400);
        }
        
        return userOid;
    }

    public static User getUserByOid(String oid) {
        if (oid == null || oid.isEmpty()) {
            throw new ApiException("User ID cannot be null or empty", 400);
        }
        // Get user
        String sql = "SELECT * FROM user WHERE oid = ?";
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, oid);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return resultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            throw new ApiException("Error getting user by oid", 500);
        }
        
        // User not found - create new user
        String insertSql = "INSERT INTO user (oid, email, username) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(insertSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            // For now, use oid as username and email since we don't have the token data here
            statement.setString(1, oid);
            statement.setString(2, oid + "@placeholder.com"); // Temporary email
            statement.setString(3, oid); // Use oid as username temporarily
            statement.executeUpdate();

            // Get the generated ID
            try (ResultSet rs = statement.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    return new User(id, oid, oid, oid + "@placeholder.com");
                }
            }
        } catch (SQLException e) {
            throw new ApiException("Error creating new user", 500);
        }
        
        throw new ApiException("Failed to create new user", 500);
    }

    public static User updateUserFromTokenData(String oid, Map<String, Object> tokenData) {
        if (oid == null || oid.isEmpty()) {
            throw new ApiException("User ID cannot be null or empty", 400);
        }
        if (tokenData == null || tokenData.isEmpty()) {
            throw new ApiException("Token data cannot be null or empty", 400);
        }

        String email = tokenData.get("email").toString();
        String username = email.split("@")[0]; // Use email prefix as username

        String sql = "UPDATE user SET email = ?, username = ? WHERE oid = ?";
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, email);
            statement.setString(2, username);
            statement.setString(3, oid);
            statement.executeUpdate();

            // Get updated user
            return getUserByOid(oid);
        } catch (SQLException e) {
            throw new ApiException("Error updating user data", 500);
        }
    }

    public static boolean checkCourseIdExists(String userOid, int courseId) {
        if (userOid == null || userOid.isEmpty()) {
            throw new ApiException("User ID cannot be null or empty", 400);
        }
        if(courseId < 0) {
            throw new ApiException("Invalid course ID", 400);
        }
        // Check if course ID exists and belongs to user
        String sql = "SELECT * FROM course WHERE id = ? AND user_id = (SELECT id FROM user WHERE oid = ?)";
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, courseId);
            statement.setString(2, userOid);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new ApiException("Error checking course ID existence", 500);
        }
    }

    public static boolean checkAssignmentIdExists(String userOid, int assignmentId){
        if (userOid == null || userOid.isEmpty()) {
            throw new ApiException("User ID cannot be null or empty", 400);
        }
        if(assignmentId < 0) {
            throw new ApiException("Invalid assignment ID", 400);
        }
        // Check if assignment ID exists and belongs to user
        String sql = "SELECT * FROM assignment a " +
                    "JOIN assignment_group ag ON a.assignment_group_id = ag.id " +
                    "JOIN course c ON ag.course_id = c.id " +
                    "WHERE a.id = ? AND c.user_id = (SELECT id FROM user WHERE oid = ?)";
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, assignmentId);
            statement.setString(2, userOid);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new ApiException("Error checking assignment ID existence", 500);
        }
    }

    public static User mapToToUser(Map<String, String> userMap) {
        int id = Integer.parseInt(userMap.get("id"));
        String oid = userMap.get("oid");
        String username = userMap.get("username");
        String email = userMap.get("email");

        return new User(id, oid, username, email);
    }

    private static User resultSetToUser(ResultSet rs) {
        try {
            int id = rs.getInt("id");
            String oid = rs.getString("oid");
            String username = rs.getString("username");
            String email = rs.getString("email");

            return new User(id, oid, username, email);
        } catch (SQLException e) {
            throw new ApiException("Error mapping ResultSet to User", 500);
        }
    }

    // private Map<String, Object> resultSetToMap(ResultSet rs) throws SQLException {
    //     Map<String, Object> resultMap = new HashMap<>();
    //     int columnCount = rs.getMetaData().getColumnCount();
    //     for (int i = 1; i <= columnCount; i++) {
    //         String columnName = rs.getMetaData().getColumnName(i);
    //         Object columnValue = rs.getObject(i);
    //         resultMap.put(columnName, columnValue);
    //     }
    //     return resultMap;
    // }
}