package nf.free.coursegenius.routes;

import nf.free.coursegenius.dto.*;
import nf.free.coursegenius.exceptions.RouteException;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public abstract class Route {
    protected String path;
    protected Map<String, Function<RequestContext, ResponseObject>> getHandlers = new HashMap<>();
    protected Map<String, Function<RequestContext, ResponseObject>> postHandlers = new HashMap<>();

    public Route() {
        registerRoutes();
    }

    public void setPath(String path) {
        this.path = path;
    }

    protected void registerHandler(String path, String method, Function<RequestContext, ResponseObject> handler) {
        if(method.equals("GET")) {
            getHandlers.put(path, handler);
        }
        else if (method.equals("POST")) {
            postHandlers.put(path, handler);
        } else {
            throw new RouteException("Invalid method registered", 500);
        }
    }

    public ResponseObject handle(RequestContext ctx) {
        // Ensure its the correct path
        if (!ctx.getPathParts()[0].equals(path)) {
            throw new RouteException("Route path does not match", 500);
        }
        // FIXME for now only allow 1 level deep paths
        if(ctx.getPathParts().length > 2) {
            throw new RouteException("Route path not found", 404);
        }
        // Remove the base path from the request path
        String[] pathParts = ctx.getPathParts();
        String pathPart = pathParts.length > 1 ? pathParts[1] : "/";
        // Call the handler for the path
        String method = ctx.getMethod();
        if (method.equals("GET")) {
            if (getHandlers.containsKey(pathPart)) {
                return getHandlers.get(pathPart).apply(ctx);
            }
        } else if (method.equals("POST")) {
            if (postHandlers.containsKey(pathPart)) {
                return postHandlers.get(pathPart).apply(ctx);
            }
        }
        throw new RouteException("Route handler not found", 404);
    }

    // Subclasses must implement this method
    public abstract void registerRoutes();
}
