package nf.free.coursegenius.routes;

import nf.free.coursegenius.dto.ResponseObject;
import nf.free.coursegenius.exceptions.ApiException;
import nf.free.coursegenius.dto.RequestContext;
import nf.free.coursegenius.dto.Assignment;
import nf.free.coursegenius.util.AssignmentUtil;
// import nf.free.coursegenius.util.TokenUtil;
import nf.free.coursegenius.util.UserUtil;

public class AssignmentRoute extends Route {
    public AssignmentRoute() {
        super();
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void registerRoutes() {
        registerHandler("/add-assignment", "POST", this::addAssignment);
        registerHandler("/get-assignment", "GET", this::getAssignmentById);
    }

    public ResponseObject addAssignment(RequestContext ctx){
        ResponseObject response = new ResponseObject();
        // Get user access token from cookies
        String userAccessToken = ctx.getCookie("accessToken");

        // Get userOid from access token
        String userOid = UserUtil.getUserOidByAccessToken(userAccessToken);

        // Get courseId from request body
        Object courseIdObj = ctx.getBody().get("courseId");
        if (courseIdObj == null) {
            throw new ApiException("Missing courseId parameter", 400);
        }
        String courseId = courseIdObj.toString();
        if (courseId == null || courseId.isEmpty()) {
            throw new ApiException("Something went wrong with courseId parameter", 400);
        }
        int courseIdInt;
        try {
            courseIdInt = Integer.parseInt(courseId);
        } catch (NumberFormatException e) {
            throw new ApiException("Invalid courseId parameter", 400);
        }
        // Check courseID exists and belongs to user
        if (!UserUtil.checkCourseIdExists(userOid, courseIdInt)) {
            throw new ApiException("Course ID does not exist or does not belong to user", 400);
        }

        // Get assignmentName from request body
        Object assignmentNameObj = ctx.getBody().get("assignmentName");
        if (assignmentNameObj == null) {
            throw new ApiException("Missing assignmentName parameter", 400);
        }
        String assignmentName = assignmentNameObj.toString();
        if (assignmentName == null || assignmentName.isEmpty()) {
            throw new ApiException("Something went wrong with assignmentName parameter", 400);
        }

        // Get grade from request body
        double grade = 0;
        Object gradeObj = ctx.getBody().get("assignmentGrade");
        if (gradeObj != null) {
            String gradeStr = gradeObj.toString();
            if (gradeStr == null || gradeStr.isEmpty()) {
                throw new ApiException("Something went wrong with grade parameter", 400);
            }
            try {
                grade = Double.parseDouble(gradeStr);
            } catch (NumberFormatException e) {
                throw new ApiException("Invalid grade parameter", 400);
            }
            if (grade < 0 || grade > 100) {
                throw new ApiException("Grade must be between 0 and 100", 400);
            }
        }

        // Get weight from request body
        double weight = 0;
        Object weightObj = ctx.getBody().get("assignmentWeight");
        if (weightObj != null) {
            String weightStr = weightObj.toString();
            if (weightStr == null || weightStr.isEmpty()) {
                throw new ApiException("Something went wrong with weight parameter", 400);
            }
            try {
                weight = Double.parseDouble(weightStr);
            } catch (NumberFormatException e) {
                throw new ApiException("Invalid weight parameter", 400);
            }
            if (weight < 0 || weight > 1) {
                throw new ApiException("Weight must be between 0 and 1", 400);
            }
        }


        // Add assignment - AssignmentUtil handles errors
        AssignmentUtil.addAssignment(courseIdInt, assignmentName, grade, weight);

        // Return success response
        response.setStatusCode(200);
        response.addBody("message", "Assignment added successfully");
        return response;
    }

    public ResponseObject getAssignmentById(RequestContext ctx) { // FIXME add authorization
        ResponseObject response = new ResponseObject();
        String assignmentIdStr = ctx.getQueryStringParameters().get("assignmentId");

        if (assignmentIdStr == null) {
            throw new ApiException("Missing assignmentId parameter", 400);
        }

        try {
            int assignmentId = Integer.parseInt(assignmentIdStr);
            Assignment assignment = AssignmentUtil.getAssignmentById(assignmentId);
            if (assignment != null) {
                response.setStatusCode(200);
                response.addBody("assignment", assignment);
            } else {
                response.setStatusCode(404);
                response.addBody("message", "Assignment not found");
            }
        } catch (NumberFormatException e) {
            throw new ApiException("Invalid assignmentId parameter", 400);
        } catch (Exception e) {
            throw new ApiException("Internal server error: " + e.getMessage(), 500);
        }

        return response;
    }
}