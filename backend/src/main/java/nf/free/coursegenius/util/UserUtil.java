package nf.free.coursegenius.util;

import nf.free.coursegenius.config.AppConfig;
import nf.free.coursegenius.dto.User;

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
            throw new RuntimeException("Failed to load database driver", e);
        }
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(AppConfig.dbUrl, AppConfig.dbUsername, AppConfig.dbPassword);
    }

    public User getUserByOid(String oid) {
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
            throw new RuntimeException("Error getting user by oid", e);
        }
        return null;
    }

    public static User mapToToUser(Map<String, String> userMap) {
        int id = Integer.parseInt(userMap.get("id"));
        String oid = userMap.get("oid");
        String username = userMap.get("username");
        String email = userMap.get("email");

        return new User(id, oid, username, email);
    }

    private User resultSetToUser(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String oid = rs.getString("oid");
        String username = rs.getString("username");
        String email = rs.getString("email");

        return new User(id, oid, username, email);
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