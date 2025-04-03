package nf.free.coursegenius.routes;

import nf.free.coursegenius.dto.ResponseObject;
import nf.free.coursegenius.dto.RequestContext;
import nf.free.coursegenius.config.AppConfig;
import nf.free.coursegenius.exceptions.ApiException;

import javax.servlet.http.Cookie;

public class TestRoute extends Route{
    public TestRoute() {
        super();
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void registerRoutes() {
        registerHandler("/config", "GET", this::config);
        registerHandler("/cookie",  "GET", this::cookie);
        registerHandler("/redirect",  "GET", this::redirect);
        registerHandler("/",  "GET", this::get);
        registerHandler("/",  "POST", this::post);
        registerHandler("/query",  "GET", this::getQuery);
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
        if(ctx.getBody() == null) {
            throw new ApiException("Missing body", 400);
        }
        ResponseObject response = new ResponseObject();
        response.setStatusCode(200);
        response.addBody("message", "Hello, test from POST");
        response.addBody("POSTED", ctx.getBody());
        return response;
    }

    public ResponseObject getQuery(RequestContext ctx) {
        if(ctx.getQueryStringParameters() == null) {
            throw new ApiException("Missing query string parameters", 400);
        }
        ResponseObject response = new ResponseObject();
        response.setStatusCode(200);
        response.addBody("message", "Hello, from test GET with query");
        response.addBody("query", ctx.getQueryStringParameters());
        return response;
    }
}
