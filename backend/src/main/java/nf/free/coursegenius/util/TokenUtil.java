package nf.free.coursegenius.util;

import java.util.HashMap;
import java.util.Map;
import nf.free.coursegenius.exceptions.ApiException;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.text.ParseException;

public class TokenUtil {
    public static Map<String, Object> getUserDataFromToken(String token) {
        // Ensure token passed in
        if(token == null) {
            throw new ApiException("Token is null", 400);
        }
        // Get the data from the token
        try {
            Map<String, Object> data = new HashMap<>();
            // Split the token into parts (header.payload.signature)
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                throw new ApiException("Invalid token format", 400);
            }
            // Decode the payload (second part)
            String payload = new String(java.util.Base64.getUrlDecoder().decode(parts[1]));
            // Parse the JSON payload
            org.json.JSONObject json = new org.json.JSONObject(payload);
            // Convert JSON to Map
            for (String key : json.keySet()) {
                data.put(key, json.get(key));
            }
            return data;
        } catch (Exception e) {
            throw new ApiException("Error parsing token: " + e.getMessage(), 500);
        }
    }
}
