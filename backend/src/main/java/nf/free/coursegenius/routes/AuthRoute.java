package nf.free.coursegenius.routes;

import nf.free.coursegenius.config.AppConfig;
import nf.free.coursegenius.dto.ResponseObject;
import nf.free.coursegenius.dto.RequestContext;

import com.microsoft.aad.msal4j.*;

import java.net.URI;
import java.util.concurrent.CompletableFuture;

import javax.servlet.http.Cookie;

public class AuthRoute extends Route {
    public AuthRoute() {
        super();
    }

    public void registerRoutes() {
        registerHandler("/login", "GET", this::login);
        registerHandler("/loginCallback", "GET", this::loginCallback);
        registerHandler("/logout", "GET", this::logout);
    }

    // Build a ConfidentialClientApplication instance from the configuration
    private ConfidentialClientApplication buildClientApp() throws Exception {
        return ConfidentialClientApplication.builder(
                AppConfig.clientId,
                ClientCredentialFactory.createFromSecret(AppConfig.clientSecret))
            .authority("https://login.microsoftonline.com/" + AppConfig.tenantId)
            .build();
    }

    public ResponseObject login(RequestContext ctx) {
        System.out.println("Login route called");
        String authorizationUrl = String.format(
            "https://login.microsoftonline.com/%s/oauth2/v2.0/authorize?client_id=%s&response_type=code&redirect_uri=%s&response_mode=query&scope=openid profile email User.Read&state=12345&prompt=select_account",
            AppConfig.tenantId, AppConfig.clientId, AppConfig.redirectUri
        );
        ResponseObject response = new ResponseObject();
        return response.redirect(authorizationUrl);
    }

    public ResponseObject loginCallback(RequestContext ctx) {
        System.out.println("LoginCallback route called");
        String authorizationCode = ctx.getQueryParam("code");
        ResponseObject response = new ResponseObject();
        try {
            // Exchange the authorization code for an access token
            ConfidentialClientApplication app = buildClientApp();
            AuthorizationCodeParameters parameters = AuthorizationCodeParameters.builder(
                    authorizationCode,
                    new URI(AppConfig.redirectUri))
                .build();
            CompletableFuture<IAuthenticationResult> future = app.acquireToken(parameters);
            IAuthenticationResult result = future.get();

            // Set the access token as a cookie
            Cookie cookie = new Cookie("accessToken", result.accessToken());
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            cookie.setMaxAge(3 * 60 * 60); // 3 hours
            response.addCookie(cookie);

            // Redirect to the frontend application
            return response.redirect(AppConfig.webDomain);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ResponseObject logout(RequestContext ctx) {
        System.out.println("Logout route called");
        ResponseObject response = new ResponseObject();

        // Clear the access token cookie
        Cookie cookie = new Cookie("accessToken", "");
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        // Redirect to the frontend application
        return response.redirect(AppConfig.webDomain);
    }
}