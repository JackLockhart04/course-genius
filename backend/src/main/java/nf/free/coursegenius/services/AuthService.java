package nf.free.coursegenius.services;

import nf.free.coursegenius.dto.ResponseObject;
import nf.free.coursegenius.dto.RequestContext;
import nf.free.coursegenius.exceptions.ApiException;

public interface AuthService {
    /**
     * Initiates the authentication flow
     */
    ResponseObject initiateAuth(RequestContext ctx) throws ApiException;

    /**
     * Handles the authentication callback
     */
    ResponseObject handleCallback(RequestContext ctx) throws ApiException;

    /**
     * Handles user logout
     */
    ResponseObject logout(RequestContext ctx) throws ApiException;

    /**
     * Validates the authentication token
     */
    boolean validateToken(String token) throws ApiException;
} 