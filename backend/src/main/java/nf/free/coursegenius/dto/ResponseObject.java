package nf.free.coursegenius.dto;

import javax.servlet.http.Cookie;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class ResponseObject {
    private Map<String, Object> body;
    private int statusCode;
    private Map<String, String> headers;
    private Map<String, Cookie> cookies;
    private String error;
    private boolean isBase64Encoded;

    public ResponseObject() {
        this.body = new HashMap<>();
        this.statusCode = 200;
        this.headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        this.cookies = new HashMap<>();
        this.error = null;
        this.isBase64Encoded = false;
    }

    // Getters/setters
    public void addBody(String key, Object value) {
        this.body.put(key, value);
    }
    public void updatedBody(String key, Object value) {
        if(this.body.containsKey(key)){
            this.body.replace(key, value);
        } else {
            throw new RuntimeException("Body key does not exist");
        }
    }
    public Map<String, Object> getBody() {
        return body;
    }
    public Object getBodyObject(String key) {
        if(!body.containsKey(key)){
            throw new RuntimeException("Body key does not exist");
        }
        return body.get(key);
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
    public int getStatusCode() {
        return statusCode;
    }

    public void addHeader(String key, String value) {
        if(this.headers.containsKey(key)){
            this.headers.replace(key, value);
        } else{
            this.headers.put(key, value);
        }
    }
    public Map<String, String> getHeaders() {
        return headers;
    }
    public String getHeader(String key) {
        if(!this.headers.containsKey(key)){
            throw new RuntimeException("Header key does not exist");
        }
        return this.headers.get(key);
    }

    public void addCookie(Cookie cookie) {
        cookie.setHttpOnly(true);
        this.cookies.put(cookie.getName(), cookie);
    }

    public List<String> getCookieStrings() {
        List<String> cookieList = new ArrayList<>();
        for (Map.Entry<String, Cookie> entry : this.cookies.entrySet()) {
            Cookie cookieObject = entry.getValue();
            String cookieStr = cookieObject.getName() + "=" + cookieObject.getValue();
            if(cookieObject.getMaxAge() != -1){
                cookieStr += "; Max-Age=" + cookieObject.getMaxAge();
            }
            if(cookieObject.getPath() != null){
                cookieStr += "; Path=" + cookieObject.getPath();
            }
            if(cookieObject.isHttpOnly()){
                cookieStr += "; HttpOnly";
            }

            cookieList.add(cookieStr);
        }
        return cookieList;
    }
    public Map<String, Cookie> getCookieMap() {
        return cookies;
    }
    public Cookie getCookie(String key) {
        return this.cookies.get(key);
    }

    public void setError(String error) {
        this.error = error;
        if(this.body.containsKey("error")){
            this.body.replace("error", error);
        } else {
            this.body.put("error", error);
        }
    }
    public String getError() {
        return error;
    }

    public void setIsBase64Encoded(boolean isBase64Encoded) {
        this.isBase64Encoded = isBase64Encoded;
    }
    public boolean getIsBase64Encoded() {
        return isBase64Encoded;
    }

    public ResponseObject redirect(String redirectUrl) {
        this.addHeader("Location", redirectUrl);
        this.statusCode = 302; // HTTP status code for redirection
        return this;
    }

    public LambdaResponse toLambdaResponse() {
        LambdaResponse lambdaResponse = new LambdaResponse();
        lambdaResponse.body = this.body;
        lambdaResponse.statusCode = this.statusCode;
        lambdaResponse.headers = this.headers;
        lambdaResponse.cookies = this.getCookieStrings();
        return lambdaResponse;
    }
}
