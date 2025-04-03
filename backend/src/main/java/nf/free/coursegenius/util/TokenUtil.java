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
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
            data.putAll(claimsSet.getClaims());
            return data;
        } catch (ParseException e) {
            throw new ApiException("Error parsing token: " + e.getMessage(), 500);
        }
    }
}
