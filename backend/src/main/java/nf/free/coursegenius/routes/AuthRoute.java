package nf.free.coursegenius.routes;

import nf.free.coursegenius.dto.ResponseObject;
import nf.free.coursegenius.dto.RequestContext;
import nf.free.coursegenius.exceptions.ApiException;
import nf.free.coursegenius.services.MicrosoftAuthService;

public class AuthRoute extends Route {
    private final MicrosoftAuthService microsoftAuthService;

    public AuthRoute() {
        super();
        this.microsoftAuthService = new MicrosoftAuthService();
    }

    public void registerRoutes() {
        registerHandler("/login", "GET", this::login);
        registerHandler("/loginCallback", "GET", this::loginCallback);
        registerHandler("/logout", "GET", this::logout);
    }

    public ResponseObject login(RequestContext ctx) {
        System.out.println("Login route called");
        return microsoftAuthService.initiateAuth(ctx);
    }

    public ResponseObject loginCallback(RequestContext ctx) {
        System.out.println("LoginCallback route called");
        return microsoftAuthService.handleCallback(ctx);
    }

    public ResponseObject logout(RequestContext ctx) {
        System.out.println("Logout route called");
        return microsoftAuthService.logout(ctx);
    }
}