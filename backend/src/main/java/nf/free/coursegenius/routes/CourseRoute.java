package nf.free.coursegenius.routes;

import nf.free.coursegenius.dto.ResponseObject;
import nf.free.coursegenius.exceptions.RouteException;
import nf.free.coursegenius.dto.RequestContext;
import nf.free.coursegenius.dto.Course;
import nf.free.coursegenius.dto.User;
import nf.free.coursegenius.util.CourseUtil;
import nf.free.coursegenius.util.TokenUtil;
import nf.free.coursegenius.util.UserUtil;

import java.util.List;

// import org.eclipse.jetty.http.MetaData.Request;

public class CourseRoute extends Route {
    public CourseRoute() {
        super();
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void registerRoutes() {
        registerHandler("/add-course", "POST", this::addCourse);
        registerHandler("/delete-course", "POST", this::deleteCourse);
        registerHandler("/get-all-courses", "GET", this::getCoursesByUser);
        registerHandler("/get-course", "GET", this::getCourseById);
    }

    public ResponseObject addCourse(RequestContext ctx){
        ResponseObject response = new ResponseObject();
        String userAccessToken = ctx.getCookie("accessToken");

        String userOid = UserUtil.getUserOidByAccessToken(userAccessToken);
        if (userOid == null) {
            throw new RouteException("Invalid access token", 401);
        }

        try {
            System.out.println("Body: " + ctx.getBody());
            Object courseNameObj = ctx.getBody().get("courseName");
            if (courseNameObj == null) {
                throw new RouteException("Missing courseName parameter", 400);
            }
            String courseName = courseNameObj.toString();

            if (courseName == null || courseName.isEmpty()) {
                throw new RouteException("Missing courseName parameter", 400);
            }
            CourseUtil.addCourse(courseName, userOid);
            response.setStatusCode(200);
            response.addBody("message", "Course added successfully");
        } catch (Exception e) {
            throw new RuntimeException("Internal server error: " + e.getMessage());
        }

        return response;
    }

    public ResponseObject deleteCourse(RequestContext ctx) {
        ResponseObject response = new ResponseObject();
        String userAccessToken = ctx.getCookie("accessToken");
        if (userAccessToken == null) {
            throw new RouteException("No access token given, not logged in", 401);
        }

        String userOid = TokenUtil.getUserDataFromToken(userAccessToken).get("oid").toString();
        if (userOid == null) {
            throw new RouteException("Invalid access token", 401);
        }

        try {
            Object courseIdObj = ctx.getBody().get("courseId");
            if (courseIdObj == null) {
                throw new RouteException("Missing courseId parameter", 400);
            }
            String courseIdStr = courseIdObj.toString();
            if (courseIdStr == null || courseIdStr.isEmpty()) {
                throw new RouteException("Course Id is broken?", 400);
            }
            
            int courseId = Integer.parseInt(courseIdStr);
            CourseUtil.deleteCourseById(courseId, userOid);
            response.setStatusCode(200);
            response.addBody("message", "Course deleted successfully");
        } catch (NumberFormatException e) {
            throw new RouteException("Invalid courseId parameter", 400);
        } catch (Exception e) {
            throw new RuntimeException("Internal server error: " + e.getMessage());
        }

        return response;
    }

    public ResponseObject getCoursesByUser(RequestContext ctx) {
        ResponseObject response = new ResponseObject();

        String userAccessToken = ctx.getCookie("accessToken");
        if (userAccessToken == null) {
            throw new RouteException("No access token given, not logged in", 401);
        }

        String userOid = TokenUtil.getUserDataFromToken(userAccessToken).get("oid").toString();

        User user = UserUtil.getUserByOid(userOid);

        try {
            List<Course> courses = CourseUtil.getCoursesByUserId(user.getId());
            response.setStatusCode(200);
            response.addBody("courses", courses);
        } catch (NumberFormatException e) {
            throw new RouteException("Invalid userId parameter", 400);
        } catch (Exception e) {
            throw new RuntimeException("Internal server error: " + e.getMessage());
        }

        return response;
    }

    public ResponseObject getCourseById(RequestContext ctx) {// FIXME add authorization
        ResponseObject response = new ResponseObject();
        String courseIdStr = ctx.getQueryStringParameters().get("courseId");

        if (courseIdStr == null) {
            throw new RouteException("Missing courseId parameter", 400);
        }

        try {
            int courseId = Integer.parseInt(courseIdStr);
            Course course = CourseUtil.getCourseById(courseId);
            if (course != null) {
                response.setStatusCode(200);
                response.addBody("course", course);
            } else {
                response.setStatusCode(404);
                response.addBody("message", "Course not found");
            }
        } catch (NumberFormatException e) {
            throw new RouteException("Invalid courseId parameter", 400);
        } catch (Exception e) {
            throw new RuntimeException("Internal server error: " + e.getMessage());
        }

        return response;
    }
}