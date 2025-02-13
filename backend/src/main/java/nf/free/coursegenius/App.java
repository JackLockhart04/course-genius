package nf.free.coursegenius;

import nf.free.coursegenius.routes.*;
import nf.free.coursegenius.dto.ResponseObject;
import nf.free.coursegenius.dto.RequestContext;
import nf.free.coursegenius.exceptions.RouteException;

import java.util.HashMap;
import java.util.Map;

public class App {
    Map<String, Route> routes = new HashMap<>();

    public App (){
        registerRoute("/", new BaseRoute());
        registerRoute("/test", new TestRoute());
        registerRoute("/auth", new AuthRoute());
        registerRoute("/user", new UserRoute());
    }

    public void registerRoute(String path, Route routeHandler) {
        routeHandler.setPath(path);
        routes.put(path, routeHandler);
    }

    public ResponseObject handleRequest(RequestContext ctx) {
        try {
            String[] path = ctx.getPathParts();

            // Call route handler if path is registered
            if (path.length > 0 && routes.containsKey(path[0])) {
                Route route = routes.get(path[0]);
                return route.handle(ctx);
            }
            throw new RouteException("Route not found", 404);
        } catch (RouteException e) {
            // Log any errors
            System.out.println(e.getMessage());
            ResponseObject response = new ResponseObject();
            response.setStatusCode(e.getErrorCode());
            response.setError(e.getMessage());
            return response;
        } catch (Exception e) {
            // Log any errors
            System.out.println(e.getMessage());
            ResponseObject response = new ResponseObject();
            response.setStatusCode(500);
            response.setError("Internal server error");
            return response;
        }
    }

}
