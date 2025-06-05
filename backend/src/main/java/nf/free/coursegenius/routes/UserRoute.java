package nf.free.coursegenius.routes;

import nf.free.coursegenius.dto.ResponseObject;
import nf.free.coursegenius.dto.RequestContext;
import nf.free.coursegenius.util.TokenUtil;
import nf.free.coursegenius.util.UserUtil;
import nf.free.coursegenius.dto.User;
import nf.free.coursegenius.exceptions.ApiException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

public class UserRoute extends Route {
    public UserRoute() {
        super();
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void registerRoutes() {
        registerHandler("/get-user-data", "GET", this::getUserData);
        registerHandler("/test-get", "GET", this::testGet);
    }

    public ResponseObject getUserData(RequestContext ctx) {
        ResponseObject response = new ResponseObject();
        String accessToken = ctx.getCookie("accessToken");

        if (accessToken == null) {
            response.setStatusCode(200);
            response.addBody("message", "Not logged in: No access token found");
            return response;
        }

        Map<String, Object> userData = TokenUtil.getUserDataFromToken(accessToken);
        String oid = userData.get("oid").toString();
        if (oid == null || oid.isEmpty()) {
            throw new ApiException("OID not found in token data", 400);
        }

        // Find or create user
        User user = UserUtil.getUserByOid(oid);
        
        // Update user data if needed
        String email = userData.get("email").toString();
        String username = email.split("@")[0];
        if (!email.equals(user.getEmail()) || !username.equals(user.getUsername())) {
            String sql = "UPDATE user SET email = ?, username = ? WHERE oid = ?";
            try (Connection conn = UserUtil.getConnection();
                 PreparedStatement statement = conn.prepareStatement(sql)) {
                statement.setString(1, email);
                statement.setString(2, username);
                statement.setString(3, oid);
                statement.executeUpdate();
                
                // Update user object
                user = new User(user.getId(), oid, username, email);
            } catch (SQLException e) {
                throw new ApiException("Error updating user data", 500);
            }
        }

        // Return data
        response.setStatusCode(200);
        response.addBody("id", user.getId());
        response.addBody("oid", user.getOid());
        response.addBody("username", user.getUsername());
        response.addBody("email", user.getEmail());

        return response;
    }

    public ResponseObject testGet(RequestContext ctx) {
        ResponseObject response = new ResponseObject();
        response.setStatusCode(200);
        response.addBody("message", "GET request successful");
        return response;
    }
}
