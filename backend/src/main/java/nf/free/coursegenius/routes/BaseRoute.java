package nf.free.coursegenius.routes;

import nf.free.coursegenius.dto.ResponseObject;
import nf.free.coursegenius.dto.RequestContext;

public class BaseRoute extends Route{
    public BaseRoute() {
        super();
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void registerRoutes() {
        registerHandler("/", "GET", this::get);
        registerHandler("/", "POST", this::post);
    }

    public ResponseObject get(RequestContext ctx) {
        ResponseObject response = new ResponseObject();
        response.setStatusCode(200);
        response.addBody("message", "Hello, from GET");
        return response;
    }

    public ResponseObject post(RequestContext ctx) {
        ResponseObject response = new ResponseObject();
        response.setStatusCode(200);
        response.addBody("message", "Hello, from POST");
        response.addBody("POSTED", ctx.getBody());
        return response;
    }
}
