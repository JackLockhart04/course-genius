package nf.free.coursegenius.util;

import nf.free.coursegenius.config.AppConfig;
import nf.free.coursegenius.dto.User;
import nf.free.coursegenius.exceptions.ApiException;

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
        // Get user data from token
        Map<String, Object> userData = TokenUtil.getUserDataFromToken(accessToken);
        if (userData == null || userData.isEmpty()) {
            throw new ApiException("Invalid access token", 500);
        }
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
        
        // User not found
        throw new ApiException("User not found", 404);
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