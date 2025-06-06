package nf.free.coursegenius.services;

import nf.free.coursegenius.config.AppConfig;
import nf.free.coursegenius.dto.ResponseObject;
import nf.free.coursegenius.dto.RequestContext;
import nf.free.coursegenius.exceptions.ApiException;

import com.microsoft.aad.msal4j.*;
import javax.servlet.http.Cookie;
import java.net.URI;
import java.util.concurrent.CompletableFuture;

public class MicrosoftAuthService implements AuthService {
    @Override
    public ResponseObject initiateAuth(RequestContext ctx) throws ApiException {
        String authorizationUrl = String.format(
            "https://login.microsoftonline.com/%s/oauth2/v2.0/authorize?client_id=%s&response_type=code&redirect_uri=%s&response_mode=query&scope=openid profile email User.Read&state=12345&prompt=select_account",
            AppConfig.tenantId, AppConfig.clientId, AppConfig.redirectUri
        );
        ResponseObject response = new ResponseObject();
        return response.redirect(authorizationUrl);
    }

    @Override
    public ResponseObject handleCallback(RequestContext ctx) throws ApiException {
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

            // Validate the token before setting it
            try {
                TokenService.validateAndGetUserData(result.accessToken());
            } catch (ApiException e) {
                throw new ApiException("Invalid token received from Microsoft: " + e.getMessage(), 500);
            }

            // Set the access token as a cookie
            Cookie cookie = new Cookie("accessToken", result.accessToken());
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            cookie.setMaxAge(24 * 60 * 60); // 24 hours
            response.addCookie(cookie);

            // Redirect to the frontend application
            return response.redirect(AppConfig.webDomain);
        } catch (Exception e) {
            throw new ApiException(e.getMessage(), 500);
        }
    }

    @Override
    public ResponseObject logout(RequestContext ctx) throws ApiException {
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

    @Override
    public boolean validateToken(String token) throws ApiException {
        try {
            TokenService.validateAndGetUserData(token);
            return true;
        } catch (ApiException e) {
            return false;
        }
    }

    private ConfidentialClientApplication buildClientApp() throws ApiException {
        try {
            return ConfidentialClientApplication.builder(
                    AppConfig.clientId,
                    ClientCredentialFactory.createFromSecret(AppConfig.clientSecret))
                .authority("https://login.microsoftonline.com/" + AppConfig.tenantId)
                .build();
        } catch (Exception e) {
            throw new ApiException("Failed to create client application: " + e.getMessage(), 500);
        }
    }
} 