package nf.free.coursegenius.services;

import java.util.Map;
import nf.free.coursegenius.exceptions.ApiException;
import nf.free.coursegenius.util.TokenUtil;

public class TokenService {
    public static Map<String, Object> validateAndGetUserData(String token) {
        if (token == null || token.isEmpty()) {
            throw new ApiException("Token is null or empty", 401);
        }

        try {
            Map<String, Object> userData = TokenUtil.getUserDataFromToken(token);
            
            // Validate required fields
            if (!userData.containsKey("oid") || !userData.containsKey("email")) {
                throw new ApiException("Invalid token: missing required fields", 401);
            }

            // Check token expiration
            if (userData.containsKey("exp")) {
                long expirationTime = Long.parseLong(userData.get("exp").toString());
                if (System.currentTimeMillis() / 1000 > expirationTime) {
                    throw new ApiException("Token has expired", 401);
                }
            }

            return userData;
        } catch (Exception e) {
            throw new ApiException("Error validating token: " + e.getMessage(), 401);
        }
    }
} 