package nf.free.coursegenius.routes;

import nf.free.coursegenius.dto.ResponseObject;
import nf.free.coursegenius.dto.RequestContext;
import nf.free.coursegenius.util.TokenUtil;
import nf.free.coursegenius.util.UserUtil;
import nf.free.coursegenius.dto.User;

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
            response.setStatusCode(401);
            response.addBody("message", "Unauthorized: No access token provided");
            return response;
        }

        try {
            Map<String, Object> userData = TokenUtil.getUserDataFromToken(accessToken);
            String oid = (String) userData.get("oid");
            if (oid == null) {
                response.setStatusCode(401);
                response.addBody("message", "Unauthorized: Invalid access token");
                return response;
            }

            UserUtil userUtil = new UserUtil();
            User user = userUtil.getUserByOid(oid);
            if (user != null) {
                response.setStatusCode(200);
                
                response.addBody("id", user.getId());
                response.addBody("oid", user.getOid());
                response.addBody("username", user.getUsername());
                response.addBody("email", user.getEmail());
            } else {
                System.out.println("User not found");
                response.setStatusCode(404);
                response.addBody("message", "User not found");
            }
        } catch (Exception e) {
            response.setStatusCode(500);
            response.addBody("message", "Internal server error: " + e.getMessage());
        }

        return response;
    }

    public ResponseObject testGet(RequestContext ctx) {
        ResponseObject response = new ResponseObject();
        response.setStatusCode(200);
        response.addBody("message", "GET request successful");
        return response;
    }
}
