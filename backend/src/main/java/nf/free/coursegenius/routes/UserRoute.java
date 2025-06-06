package nf.free.coursegenius.routes;

import nf.free.coursegenius.dto.ResponseObject;
import nf.free.coursegenius.dto.RequestContext;
import nf.free.coursegenius.util.TokenUtil;
import nf.free.coursegenius.util.UserUtil;
import nf.free.coursegenius.dto.User;
import nf.free.coursegenius.exceptions.ApiException;
import nf.free.coursegenius.services.TokenService;

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

        try {
            // Validate token and get user data
            Map<String, Object> userData = TokenService.validateAndGetUserData(accessToken);
            String oid = userData.get("oid").toString();
            
            // Get or create user
            User user = UserUtil.getUserByOid(oid);
            
            // Update user data if needed
            String email = userData.get("email").toString();
            if (!email.equals(user.getEmail())) {
                user = UserUtil.updateUserFromTokenData(oid, userData);
            }

            // Return data
            response.setStatusCode(200);
            response.addBody("id", user.getId());
            response.addBody("oid", user.getOid());
            response.addBody("username", user.getUsername());
            response.addBody("email", user.getEmail());

            return response;
        } catch (ApiException e) {
            response.setStatusCode(e.getErrorCode());
            response.addBody("message", e.getMessage());
            return response;
        }
    }

    public ResponseObject testGet(RequestContext ctx) {
        ResponseObject response = new ResponseObject();
        response.setStatusCode(200);
        response.addBody("message", "GET request successful");
        return response;
    }
}
