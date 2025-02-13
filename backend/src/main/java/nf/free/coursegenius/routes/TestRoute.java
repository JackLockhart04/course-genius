package nf.free.coursegenius.routes;

import nf.free.coursegenius.dto.ResponseObject;
import nf.free.coursegenius.dto.RequestContext;
import nf.free.coursegenius.config.AppConfig;

import javax.servlet.http.Cookie;

public class TestRoute extends Route{
    public TestRoute() {
        super();
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void registerRoutes() {
        registerHandler("/", "GET", this::root);
        registerHandler("/config", "GET", this::config);
        registerHandler("/cookie",  "GET", this::cookie);
        registerHandler("/redirect",  "GET", this::redirect);
        registerHandler("/",  "GET", this::get);
        registerHandler("/",  "POST", this::post);
    }

    public ResponseObject root(RequestContext ctx) {
        ResponseObject response = new ResponseObject();
        response.setStatusCode(200);
        response.addBody("message", "Hello, from test root");
        return response;
    }

    public ResponseObject config(RequestContext ctx) {
        ResponseObject response = new ResponseObject();
        response.setStatusCode(200);
        response.addBody("message", "Hello, from test config");
        response.addBody("webDomain", AppConfig.webDomain);
        response.addBody("apiDomain", AppConfig.apiDomain);
        return response;
    }

    public ResponseObject cookie(RequestContext ctx) {
        ResponseObject response = new ResponseObject();
        response.setStatusCode(200);
        response.addBody("message", "Hello, from test cookie");
        Cookie testCookie1 = new Cookie("testCookie", "testValue");
        response.addCookie(testCookie1);
        Cookie testCookie2 = new Cookie("testCookie2", "testValue2");
        testCookie2.setMaxAge(60);
        response.addCookie(testCookie2);
        
        return response;
    }

    public ResponseObject redirect(RequestContext ctx) {
        ResponseObject response = new ResponseObject();
        response.setStatusCode(200);
        response.addBody("message", "Hello, from test redirect");
        return response.redirect(path);
    }

    public ResponseObject get(RequestContext ctx) {
        ResponseObject response = new ResponseObject();
        response.setStatusCode(200);
        response.addBody("message", "Hello, from test GET");
        return response;
    }

    public ResponseObject post(RequestContext ctx) {
        ResponseObject response = new ResponseObject();
        response.setStatusCode(200);
        response.addBody("message", "Hello, test from POST");
        response.addBody("POSTED", ctx.getBody());
        return response;
    }
}
