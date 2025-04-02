package nf.free.coursegenius.routes;

import nf.free.coursegenius.dto.ResponseObject;
import nf.free.coursegenius.exceptions.RouteException;
import nf.free.coursegenius.dto.RequestContext;
import nf.free.coursegenius.dto.Assignment;
import nf.free.coursegenius.util.AssignmentUtil;

public class AssignmentRoute extends Route {
    public AssignmentRoute() {
        super();
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void registerRoutes() {
        registerHandler("/get-assignment", "GET", this::getAssignmentById);
    }

    public ResponseObject getAssignmentById(RequestContext ctx) {// FIXME add authorization
        ResponseObject response = new ResponseObject();
        String assignmentIdStr = ctx.getQueryStringParameters().get("assignmentId");

        if (assignmentIdStr == null) {
            throw new RouteException("Missing assignmentId parameter", 400);
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
            throw new RouteException("Invalid assignmentId parameter", 400);
        } catch (Exception e) {
            throw new RuntimeException("Internal server error: " + e.getMessage());
        }

        return response;
    }
}