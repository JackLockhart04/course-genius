package nf.free.coursegenius.routes;

import nf.free.coursegenius.dto.ResponseObject;
import nf.free.coursegenius.exceptions.ApiException;
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
        // Get user access token from cookies
        String userAccessToken = ctx.getCookie("accessToken");
        
        // Get userOid from access token
        String userOid = UserUtil.getUserOidByAccessToken(userAccessToken);

        // Get courseName from request body
        Object courseNameObj = ctx.getBody().get("courseName");
        if (courseNameObj == null) {
            throw new ApiException("Missing courseName parameter", 400);
        }
        String courseName = courseNameObj.toString();
        if (courseName == null || courseName.isEmpty()) {
            throw new ApiException("Something went wrong with courseName parameter", 400);
        }

        // Add course - CourseUtil handles errors
        CourseUtil.addCourse(courseName, userOid);

        // Return success response
        response.setStatusCode(200);
        response.addBody("message", "Course added successfully");
        return response;
    }

    public ResponseObject deleteCourse(RequestContext ctx) {
        ResponseObject response = new ResponseObject();

        // Get user access token from cookies
        String userAccessToken = ctx.getCookie("accessToken");
        if (userAccessToken == null) {
            throw new ApiException("No access token given, not logged in", 401);
        }

        // Get userOid from access token
        String userOid = TokenUtil.getUserDataFromToken(userAccessToken).get("oid").toString();
        if (userOid == null) {
            throw new ApiException("Invalid access token", 400);
        }

        // Get courseID from request body
        Object courseIdObj = ctx.getBody().get("courseId");
        if (courseIdObj == null) {
            throw new ApiException("Missing courseId parameter", 400);
        }
        String courseIdStr = courseIdObj.toString();
        if (courseIdStr == null || courseIdStr.isEmpty()) {
            throw new ApiException("Course Id is broken?", 400);
        }
        int courseId = Integer.parseInt(courseIdStr);

        // Delete course
        CourseUtil.deleteCourseById(courseId, userOid);

        // Return success response
        response.setStatusCode(200);
        response.addBody("message", "Course deleted successfully");

        return response;
    }

    public ResponseObject getCoursesByUser(RequestContext ctx) {
        ResponseObject response = new ResponseObject();

        // Get user access token from cookies
        String userAccessToken = ctx.getCookie("accessToken");
        if (userAccessToken == null) {
            throw new ApiException("No access token given, not logged in", 401);
        }

        // Get userOid from access token
        String userOid = TokenUtil.getUserDataFromToken(userAccessToken).get("oid").toString();
        // Get userId from userOid
        User user = UserUtil.getUserByOid(userOid);

        // Get courses by userId
        List<Course> courses = CourseUtil.getCoursesByUserId(user.getId());

        // Return success response
        response.setStatusCode(200);
        response.addBody("courses", courses);
        return response;
    }

    public ResponseObject getCourseById(RequestContext ctx) {// FIXME add authorization
        System.out.println("Route: getCourseById");
        ResponseObject response = new ResponseObject();
        String courseIdStr = ctx.getQueryStringParameters().get("courseId");

        if (courseIdStr == null) {
            throw new ApiException("Missing courseId parameter", 400);
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
            throw new ApiException("Invalid courseId parameter", 400);
        } catch (Exception e) {
            throw new ApiException("Internal server error: " + e.getMessage(), 500);
        }

        return response;
    }
}