package nf.free.coursegenius.routes;

import nf.free.coursegenius.dto.ResponseObject;
import nf.free.coursegenius.exceptions.ApiException;
import nf.free.coursegenius.dto.RequestContext;
import nf.free.coursegenius.dto.Assignment;
import nf.free.coursegenius.dto.AssignmentGroup;
import nf.free.coursegenius.util.AssignmentUtil;
// import nf.free.coursegenius.util.TokenUtil;
import nf.free.coursegenius.util.UserUtil;

import java.math.BigDecimal;
import java.util.List;

public class AssignmentRoute extends Route {
    public AssignmentRoute() {
        super();
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void registerRoutes() {
        registerHandler("/add-assignment-group", "POST", this::addAssignmentGroup);
        registerHandler("/add-assignment", "POST", this::addAssignment);
        registerHandler("/get-assignment", "GET", this::getAssignmentById);
        registerHandler("/get-assignment-groups", "GET", this::getAssignmentGroupsByCourseId);
        registerHandler("/get-assignment-group", "GET", this::getAssignmentGroupById);
        registerHandler("/update-assignment", "POST", this::updateAssignment);
        registerHandler("/update-assignment-name", "POST", this::updateAssignmentName);
        registerHandler("/update-assignment-group", "POST", this::updateAssignmentGroup);
        registerHandler("/delete-assignment", "POST", this::deleteAssignment);
        registerHandler("/delete-assignment-group", "POST", this::deleteAssignmentGroup);
    }

    public ResponseObject addAssignmentGroup(RequestContext ctx) {
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

        // Check courseID exists and belongs to user
        if (!UserUtil.checkCourseIdExists(userOid, courseId)) {
            throw new ApiException("Course ID does not exist or does not belong to user", 400);
        }

        // Get group name from request body
        Object groupNameObj = ctx.getBody().get("groupName");
        if (groupNameObj == null) {
            throw new ApiException("Missing groupName parameter", 400);
        }
        String groupName = groupNameObj.toString();
        if (groupName.isEmpty()) {
            throw new ApiException("Group name cannot be empty", 400);
        }

        // Get weight from request body
        Object weightObj = ctx.getBody().get("weight");
        if (weightObj == null) {
            throw new ApiException("Missing weight parameter", 400);
        }
        BigDecimal weight;
        try {
            weight = new BigDecimal(weightObj.toString());
            if (weight.compareTo(BigDecimal.ZERO) <= 0 || weight.compareTo(BigDecimal.ONE) > 0) {
                throw new ApiException("Weight must be between 0 and 1", 400);
            }
        } catch (NumberFormatException e) {
            throw new ApiException("Invalid weight format", 400);
        }

        // Add assignment group
        AssignmentUtil.addAssignmentGroup(courseId, groupName, weight);

        // Return success response
        response.setStatusCode(200);
        response.addBody("message", "Assignment group added successfully");
        return response;
    }

    public ResponseObject addAssignment(RequestContext ctx) {
        ResponseObject response = new ResponseObject();
        
        // Get user access token from cookies
        String userAccessToken = ctx.getCookie("accessToken");
        if (userAccessToken == null) {
            throw new ApiException("No access token given, not logged in", 401);
        }

        // Get userOid from access token
        String userOid = UserUtil.getUserOidByAccessToken(userAccessToken);

        // Get assignmentGroupId from request body
        Object groupIdObj = ctx.getBody().get("assignmentGroupId");
        if (groupIdObj == null) {
            throw new ApiException("Missing assignmentGroupId parameter", 400);
        }
        int groupId;
        try {
            groupId = Integer.parseInt(groupIdObj.toString());
            if (groupId <= 0) {
                throw new ApiException("Invalid assignment group ID", 400);
            }
        } catch (NumberFormatException e) {
            throw new ApiException("Invalid assignmentGroupId format", 400);
        }

        // Get assignment name from request body
        Object assignmentNameObj = ctx.getBody().get("assignmentName");
        if (assignmentNameObj == null) {
            throw new ApiException("Missing assignmentName parameter", 400);
        }
        String assignmentName = assignmentNameObj.toString();
        if (assignmentName.isEmpty()) {
            throw new ApiException("Assignment name cannot be empty", 400);
        }

        // Get points earned from request body
        Object pointsEarnedObj = ctx.getBody().get("pointsEarned");
        if (pointsEarnedObj == null) {
            throw new ApiException("Missing pointsEarned parameter", 400);
        }
        BigDecimal pointsEarned;
        try {
            pointsEarned = new BigDecimal(pointsEarnedObj.toString());
            if (pointsEarned.compareTo(BigDecimal.ZERO) < 0) {
                throw new ApiException("Points earned cannot be negative", 400);
            }
        } catch (NumberFormatException e) {
            throw new ApiException("Invalid pointsEarned format", 400);
        }

        // Get points possible from request body
        Object pointsPossibleObj = ctx.getBody().get("pointsPossible");
        if (pointsPossibleObj == null) {
            throw new ApiException("Missing pointsPossible parameter", 400);
        }
        BigDecimal pointsPossible;
        try {
            pointsPossible = new BigDecimal(pointsPossibleObj.toString());
            if (pointsPossible.compareTo(BigDecimal.ZERO) <= 0) {
                throw new ApiException("Points possible must be greater than 0", 400);
            }
        } catch (NumberFormatException e) {
            throw new ApiException("Invalid pointsPossible format", 400);
        }

        if (pointsEarned.compareTo(pointsPossible) > 0) {
            throw new ApiException("Points earned cannot be greater than points possible", 400);
        }

        // Add assignment
        AssignmentUtil.addAssignment(groupId, assignmentName, pointsEarned, pointsPossible);

        // Return success response
        response.setStatusCode(200);
        response.addBody("message", "Assignment added successfully");
        return response;
    }

    public ResponseObject getAssignmentById(RequestContext ctx) {
        ResponseObject response = new ResponseObject();
        
        // Get user access token from cookies
        String userAccessToken = ctx.getCookie("accessToken");
        if (userAccessToken == null) {
            throw new ApiException("No access token given, not logged in", 401);
        }

        // Get userOid from access token
        String userOid = UserUtil.getUserOidByAccessToken(userAccessToken);

        String assignmentIdStr = ctx.getQueryStringParameters().get("assignmentId");
        if (assignmentIdStr == null) {
            throw new ApiException("Missing assignmentId parameter", 400);
        }

        try {
            int assignmentId = Integer.parseInt(assignmentIdStr);
            if (assignmentId <= 0) {
                throw new ApiException("Invalid assignment ID", 400);
            }

            Assignment assignment = AssignmentUtil.getAssignmentById(assignmentId);
            if (assignment != null) {
                response.setStatusCode(200);
                response.addBody("assignment", assignment);
            } else {
                response.setStatusCode(404);
                response.addBody("message", "Assignment not found");
            }
        } catch (NumberFormatException e) {
            throw new ApiException("Invalid assignmentId format", 400);
        }

        return response;
    }

    public ResponseObject getAssignmentGroupsByCourseId(RequestContext ctx) {
        ResponseObject response = new ResponseObject();
        
        // Get user access token from cookies
        String userAccessToken = ctx.getCookie("accessToken");
        if (userAccessToken == null) {
            throw new ApiException("No access token given, not logged in", 401);
        }

        // Get userOid from access token
        String userOid = UserUtil.getUserOidByAccessToken(userAccessToken);

        String courseIdStr = ctx.getQueryStringParameters().get("courseId");
        if (courseIdStr == null) {
            throw new ApiException("Missing courseId parameter", 400);
        }

        try {
            int courseId = Integer.parseInt(courseIdStr);
            if (courseId <= 0) {
                throw new ApiException("Invalid course ID", 400);
            }

            // Check courseID exists and belongs to user
            if (!UserUtil.checkCourseIdExists(userOid, courseId)) {
                throw new ApiException("Course ID does not exist or does not belong to user", 400);
            }

            List<AssignmentGroup> groups = AssignmentUtil.getAssignmentGroupsByCourseId(courseId);
            response.setStatusCode(200);
            response.addBody("assignmentGroups", groups);
        } catch (NumberFormatException e) {
            throw new ApiException("Invalid courseId format", 400);
        }

        return response;
    }

    public ResponseObject getAssignmentGroupById(RequestContext ctx) {
        ResponseObject response = new ResponseObject();
        
        // Get user access token from cookies
        String userAccessToken = ctx.getCookie("accessToken");
        if (userAccessToken == null) {
            throw new ApiException("No access token given, not logged in", 401);
        }

        // Get userOid from access token
        String userOid = UserUtil.getUserOidByAccessToken(userAccessToken);

        String groupIdStr = ctx.getQueryStringParameters().get("groupId");
        if (groupIdStr == null) {
            throw new ApiException("Missing groupId parameter", 400);
        }

        try {
            int groupId = Integer.parseInt(groupIdStr);
            if (groupId <= 0) {
                throw new ApiException("Invalid group ID", 400);
            }

            // Get assignment group and verify ownership
            AssignmentGroup group = AssignmentUtil.getAssignmentGroupById(groupId);
            if (group == null) {
                throw new ApiException("Assignment group not found", 404);
            }

            // Verify group belongs to user's course
            if (!UserUtil.checkCourseIdExists(userOid, group.getCourseId())) {
                throw new ApiException("Unauthorized access to assignment group", 403);
            }

            response.setStatusCode(200);
            response.addBody("assignmentGroup", group);
        } catch (NumberFormatException e) {
            throw new ApiException("Invalid groupId format", 400);
        }

        return response;
    }

    public ResponseObject updateAssignment(RequestContext ctx) {
        ResponseObject response = new ResponseObject();
        
        // Get user access token from cookies
        String userAccessToken = ctx.getCookie("accessToken");
        if (userAccessToken == null) {
            throw new ApiException("No access token given, not logged in", 401);
        }

        // Get userOid from access token
        String userOid = UserUtil.getUserOidByAccessToken(userAccessToken);

        // Get assignmentId from request body
        Object assignmentIdObj = ctx.getBody().get("assignmentId");
        if (assignmentIdObj == null) {
            throw new ApiException("Missing assignmentId parameter", 400);
        }
        int assignmentId;
        try {
            assignmentId = Integer.parseInt(assignmentIdObj.toString());
            if (assignmentId <= 0) {
                throw new ApiException("Invalid assignment ID", 400);
            }
        } catch (NumberFormatException e) {
            throw new ApiException("Invalid assignmentId format", 400);
        }

        // Get points earned from request body
        Object pointsEarnedObj = ctx.getBody().get("pointsEarned");
        if (pointsEarnedObj == null) {
            throw new ApiException("Missing pointsEarned parameter", 400);
        }
        BigDecimal pointsEarned;
        try {
            pointsEarned = new BigDecimal(pointsEarnedObj.toString());
            if (pointsEarned.compareTo(BigDecimal.ZERO) < 0) {
                throw new ApiException("Points earned cannot be negative", 400);
            }
        } catch (NumberFormatException e) {
            throw new ApiException("Invalid pointsEarned format", 400);
        }

        // Get points possible from request body
        Object pointsPossibleObj = ctx.getBody().get("pointsPossible");
        if (pointsPossibleObj == null) {
            throw new ApiException("Missing pointsPossible parameter", 400);
        }
        BigDecimal pointsPossible;
        try {
            pointsPossible = new BigDecimal(pointsPossibleObj.toString());
            if (pointsPossible.compareTo(BigDecimal.ZERO) <= 0) {
                throw new ApiException("Points possible must be greater than 0", 400);
            }
        } catch (NumberFormatException e) {
            throw new ApiException("Invalid pointsPossible format", 400);
        }

        // Update assignment
        AssignmentUtil.updateAssignment(assignmentId, pointsEarned, pointsPossible);

        // Return success response
        response.setStatusCode(200);
        response.addBody("message", "Assignment updated successfully");
        return response;
    }

    public ResponseObject updateAssignmentName(RequestContext ctx) {
        ResponseObject response = new ResponseObject();
        
        // Get user access token from cookies
        String userAccessToken = ctx.getCookie("accessToken");
        if (userAccessToken == null) {
            throw new ApiException("No access token given, not logged in", 401);
        }

        // Get userOid from access token
        String userOid = UserUtil.getUserOidByAccessToken(userAccessToken);

        // Get assignmentId from request body
        Object assignmentIdObj = ctx.getBody().get("assignmentId");
        if (assignmentIdObj == null) {
            throw new ApiException("Missing assignmentId parameter", 400);
        }
        int assignmentId;
        try {
            assignmentId = Integer.parseInt(assignmentIdObj.toString());
            if (assignmentId <= 0) {
                throw new ApiException("Invalid assignment ID", 400);
            }
        } catch (NumberFormatException e) {
            throw new ApiException("Invalid assignmentId format", 400);
        }

        // Get name from request body
        Object nameObj = ctx.getBody().get("name");
        if (nameObj == null) {
            throw new ApiException("Missing name parameter", 400);
        }
        String name = nameObj.toString();
        if (name.isEmpty()) {
            throw new ApiException("Assignment name cannot be empty", 400);
        }

        // Update assignment name
        AssignmentUtil.updateAssignmentName(assignmentId, name);

        // Return success response
        response.setStatusCode(200);
        response.addBody("message", "Assignment name updated successfully");
        return response;
    }

    public ResponseObject updateAssignmentGroup(RequestContext ctx) {
        ResponseObject response = new ResponseObject();
        
        // Get user access token from cookies
        String userAccessToken = ctx.getCookie("accessToken");
        if (userAccessToken == null) {
            throw new ApiException("No access token given, not logged in", 401);
        }

        // Get userOid from access token
        String userOid = UserUtil.getUserOidByAccessToken(userAccessToken);

        // Get groupId from request body
        Object groupIdObj = ctx.getBody().get("groupId");
        if (groupIdObj == null) {
            throw new ApiException("Missing groupId parameter", 400);
        }
        int groupId;
        try {
            groupId = Integer.parseInt(groupIdObj.toString());
            if (groupId <= 0) {
                throw new ApiException("Invalid group ID", 400);
            }
        } catch (NumberFormatException e) {
            throw new ApiException("Invalid groupId format", 400);
        }

        // Get name from request body
        Object nameObj = ctx.getBody().get("name");
        if (nameObj == null) {
            throw new ApiException("Missing name parameter", 400);
        }
        String name = nameObj.toString();
        if (name.isEmpty()) {
            throw new ApiException("Group name cannot be empty", 400);
        }

        // Update assignment group name
        AssignmentUtil.updateAssignmentGroupName(groupId, name);

        // Return success response
        response.setStatusCode(200);
        response.addBody("message", "Assignment group name updated successfully");
        return response;
    }

    public ResponseObject deleteAssignment(RequestContext ctx) {
        ResponseObject response = new ResponseObject();
        
        // Get user access token from cookies
        String userAccessToken = ctx.getCookie("accessToken");
        if (userAccessToken == null) {
            throw new ApiException("No access token given, not logged in", 401);
        }

        // Get userOid from access token
        String userOid = UserUtil.getUserOidByAccessToken(userAccessToken);

        // Get assignmentId from request body
        Object assignmentIdObj = ctx.getBody().get("assignmentId");
        if (assignmentIdObj == null) {
            throw new ApiException("Missing assignmentId parameter", 400);
        }
        int assignmentId;
        try {
            assignmentId = Integer.parseInt(assignmentIdObj.toString());
            if (assignmentId <= 0) {
                throw new ApiException("Invalid assignment ID", 400);
            }
        } catch (NumberFormatException e) {
            throw new ApiException("Invalid assignmentId format", 400);
        }

        // Delete assignment
        AssignmentUtil.deleteAssignment(assignmentId);

        // Return success response
        response.setStatusCode(200);
        response.addBody("message", "Assignment deleted successfully");
        return response;
    }

    public ResponseObject deleteAssignmentGroup(RequestContext ctx) {
        ResponseObject response = new ResponseObject();
        
        // Get user access token from cookies
        String userAccessToken = ctx.getCookie("accessToken");
        if (userAccessToken == null) {
            throw new ApiException("No access token given, not logged in", 401);
        }

        // Get userOid from access token
        String userOid = UserUtil.getUserOidByAccessToken(userAccessToken);

        // Get groupId from request body
        Object groupIdObj = ctx.getBody().get("groupId");
        if (groupIdObj == null) {
            throw new ApiException("Missing groupId parameter", 400);
        }
        int groupId;
        try {
            groupId = Integer.parseInt(groupIdObj.toString());
            if (groupId <= 0) {
                throw new ApiException("Invalid group ID", 400);
            }
        } catch (NumberFormatException e) {
            throw new ApiException("Invalid groupId format", 400);
        }

        // Delete assignment group
        AssignmentUtil.deleteAssignmentGroup(groupId);

        // Return success response
        response.setStatusCode(200);
        response.addBody("message", "Assignment group deleted successfully");
        return response;
    }
}