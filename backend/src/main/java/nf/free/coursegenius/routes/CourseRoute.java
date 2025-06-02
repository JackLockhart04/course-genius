package nf.free.coursegenius.routes;

import nf.free.coursegenius.dto.ResponseObject;
import nf.free.coursegenius.exceptions.ApiException;
import nf.free.coursegenius.dto.RequestContext;
import nf.free.coursegenius.dto.Course;
import nf.free.coursegenius.dto.User;
import nf.free.coursegenius.util.CourseUtil;
import nf.free.coursegenius.util.TokenUtil;
import nf.free.coursegenius.util.UserUtil;

import java.math.BigDecimal;
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
        registerHandler("/update-course-gpa", "POST", this::updateCourseGpa);
    }

    public ResponseObject addCourse(RequestContext ctx) {
        ResponseObject response = new ResponseObject();
        
        // Get user access token from cookies
        String userAccessToken = ctx.getCookie("accessToken");
        if (userAccessToken == null) {
            throw new ApiException("No access token given, not logged in", 401);
        }
        
        // Get userOid from access token
        String userOid = UserUtil.getUserOidByAccessToken(userAccessToken);

        // Get courseName from request body
        Object courseNameObj = ctx.getBody().get("courseName");
        if (courseNameObj == null) {
            throw new ApiException("Missing courseName parameter", 400);
        }
        String courseName = courseNameObj.toString();
        if (courseName.isEmpty()) {
            throw new ApiException("Course name cannot be empty", 400);
        }

        // Get credit hours from request body (optional)
        BigDecimal creditHours = null;
        Object creditHoursObj = ctx.getBody().get("creditHours");
        if (creditHoursObj != null) {
            try {
                creditHours = new BigDecimal(creditHoursObj.toString());
                if (creditHours.compareTo(BigDecimal.ZERO) <= 0) {
                    throw new ApiException("Credit hours must be greater than 0", 400);
                }
            } catch (NumberFormatException e) {
                throw new ApiException("Invalid credit hours format", 400);
            }
        }

        // Add course - CourseUtil handles errors
        CourseUtil.addCourse(courseName, userOid, creditHours);

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
        String userOid = UserUtil.getUserOidByAccessToken(userAccessToken);

        // Get courseID from request body
        Object courseIdObj = ctx.getBody().get("courseId");
        if (courseIdObj == null) {
            throw new ApiException("Missing courseId parameter", 400);
        }
        
        int courseId;
        try {
            courseId = Integer.parseInt(courseIdObj.toString());
            if (courseId <= 0) {
                throw new ApiException("Invalid course ID", 400);
            }
        } catch (NumberFormatException e) {
            throw new ApiException("Invalid courseId format", 400);
        }

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
        String userOid = UserUtil.getUserOidByAccessToken(userAccessToken);
        
        // Get userId from userOid
        User user = UserUtil.getUserByOid(userOid);

        // Get courses by userId
        List<Course> courses = CourseUtil.getCoursesByUserId(user.getId());

        // Return success response
        response.setStatusCode(200);
        response.addBody("courses", courses);
        return response;
    }

    public ResponseObject getCourseById(RequestContext ctx) {
        ResponseObject response = new ResponseObject();

        // Get user access token from cookies
        String userAccessToken = ctx.getCookie("accessToken");
        if (userAccessToken == null) {
            throw new ApiException("No access token given, not logged in", 401);
        }

        // Get userOid from access token
        String userOid = UserUtil.getUserOidByAccessToken(userAccessToken);

        // Get courseId from query parameters
        String courseIdStr = ctx.getQueryStringParameters().get("courseId");
        if (courseIdStr == null) {
            throw new ApiException("Missing courseId parameter", 400);
        }

        try {
            int courseId = Integer.parseInt(courseIdStr);
            if (courseId <= 0) {
                throw new ApiException("Invalid course ID", 400);
            }

            // Get course and verify ownership
            Course course = CourseUtil.getCourseById(courseId);
            if (course == null) {
                throw new ApiException("Course not found", 404);
            }

            // Verify course belongs to user
            User user = UserUtil.getUserByOid(userOid);
            if (course.getUserId() != user.getId()) {
                throw new ApiException("Unauthorized access to course", 403);
            }

            response.setStatusCode(200);
            response.addBody("course", course);
        } catch (NumberFormatException e) {
            throw new ApiException("Invalid courseId format", 400);
        }

        return response;
    }

    public ResponseObject updateCourseGpa(RequestContext ctx) {
        ResponseObject response = new ResponseObject();

        // Get user access token from cookies
        String userAccessToken = ctx.getCookie("accessToken");
        if (userAccessToken == null) {
            throw new ApiException("No access token given, not logged in", 401);
        }

        // Get userOid from access token
        String userOid = UserUtil.getUserOidByAccessToken(userAccessToken);

        // Get courseId from request body
        Object courseIdObj = ctx.getBody().get("courseId");
        if (courseIdObj == null) {
            throw new ApiException("Missing courseId parameter", 400);
        }

        int courseId;
        try {
            courseId = Integer.parseInt(courseIdObj.toString());
            if (courseId <= 0) {
                throw new ApiException("Invalid course ID", 400);
            }
        } catch (NumberFormatException e) {
            throw new ApiException("Invalid courseId format", 400);
        }

        // Get GPA from request body
        Object gpaObj = ctx.getBody().get("gpa");
        if (gpaObj == null) {
            throw new ApiException("Missing gpa parameter", 400);
        }

        BigDecimal gpa;
        try {
            gpa = new BigDecimal(gpaObj.toString());
            if (gpa.compareTo(BigDecimal.ZERO) < 0 || gpa.compareTo(new BigDecimal("4.0")) > 0) {
                throw new ApiException("GPA must be between 0 and 4.0", 400);
            }
        } catch (NumberFormatException e) {
            throw new ApiException("Invalid GPA format", 400);
        }

        // Verify course belongs to user
        Course course = CourseUtil.getCourseById(courseId);
        if (course == null) {
            throw new ApiException("Course not found", 404);
        }

        User user = UserUtil.getUserByOid(userOid);
        if (course.getUserId() != user.getId()) {
            throw new ApiException("Unauthorized access to course", 403);
        }

        // Update course GPA
        CourseUtil.updateCourseGpa(courseId, gpa);

        // Return success response
        response.setStatusCode(200);
        response.addBody("message", "Course GPA updated successfully");
        return response;
    }
}