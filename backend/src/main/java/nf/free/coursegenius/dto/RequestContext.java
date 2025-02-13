package nf.free.coursegenius.dto;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.json.JSONObject;

public class RequestContext{

    private String path;
    private String[] pathParts;
    private String method;
    private Map<String, String> cookies;
    private Map<String, String> headers;
    private Map<String, String> queryStringParameters;
    private Map<String, Object> body;

    // Copy constructor with deep copy
    public RequestContext(RequestContext old) {
        this.path = old.path;
        this.pathParts = old.pathParts != null ? old.pathParts.clone() : null;
        this.method = old.method;
        this.cookies = old.cookies != null ? new HashMap<>(old.cookies) : null;
        this.headers = old.headers != null ? new HashMap<>(old.headers) : null;
        this.queryStringParameters = old.queryStringParameters != null ? new HashMap<>(old.queryStringParameters) : null;
        this.body = old.body != null ? new HashMap<>(old.body) : null;
    }

    public RequestContext(Map<String, Object> input) {
        // Initialize path and method
        this.path = input.get("path") instanceof String ? (String) input.get("path") : null;
        this.method = input.get("method") instanceof String ? (String) input.get("method") : null;
        if(this.path == null || this.method == null) {
            throw new RuntimeException("path or method not found");
        }

        // Split path into parts
        this.pathParts = Stream.of(this.path.split("/"))
                                .filter(part -> !part.isEmpty())
                                .map(part -> "/" + part)
                                .toArray(String[]::new);
        if(this.pathParts.length == 0) {
            this.pathParts = new String[]{"/"};
        }

        // Cookies
        this.cookies = new HashMap<>();
        Object cookiesObject = input.get("cookies");
        if(cookiesObject instanceof Map){
            for (Map.Entry<?, ?> entry : ((Map<?, ?>) cookiesObject).entrySet()) {
                if (entry.getKey() instanceof String && entry.getValue() instanceof String) {
                    this.cookies.put((String) entry.getKey(), (String) entry.getValue());
                }
            }
        }

        // Headers
        this.headers = new HashMap<>();
        Object headersObject = input.get("headers");
        if (headersObject instanceof Map) {
            for (Map.Entry<?, ?> entry : ((Map<?, ?>) headersObject).entrySet()) {
                if (entry.getKey() instanceof String && entry.getValue() instanceof String) {
                    this.headers.put((String) entry.getKey(), (String) entry.getValue());
                }
            }
        }

        // Query string parameters
        this.queryStringParameters = new HashMap<>();
        Object queryStringParametersObject = input.get("queryStringParameters");
        if (queryStringParametersObject instanceof Map) {
            for (Map.Entry<?, ?> entry : ((Map<?, ?>) queryStringParametersObject).entrySet()) {
                if (entry.getKey() instanceof String && entry.getValue() instanceof String) {
                    this.queryStringParameters.put((String) entry.getKey(), (String) entry.getValue());
                }
            }
        }

        Object bodyObject = input.get("body");
        // If string, convert to map
        this.body = new HashMap<>();
        if (bodyObject instanceof String) {
            try {
                JSONObject jsonObject = new JSONObject((String) bodyObject);
                for (String key : jsonObject.keySet()) {
                    this.body.put(key, jsonObject.get(key));
                }
            } catch (Exception e) {
                this.body.put("message", bodyObject);
            }
        } else if (bodyObject instanceof Map) {
            for (Map.Entry<?, ?> entry : ((Map<?, ?>) bodyObject).entrySet()) {
                if (entry.getKey() instanceof String) {
                    this.body.put((String) entry.getKey(), entry.getValue());
                }
            }
        }
    }

    public String getPath() {
        return path;
    }

    public void setPathParts(String[] pathParts) {
        this.pathParts = pathParts;
    }

    public String[] getPathParts() {
        return pathParts;
    }

    public String getMethod() {
        return method;
    }

    public Map<String, String> getCookies() {
        return cookies;
    }
    public String getCookie(String key) {
        return cookies.get(key);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
    public String getHeader(String key) {
        return headers.get(key);
    }

    public Map<String, String> getQueryStringParameters() {
        return queryStringParameters;
    }
    public String getQueryParam(String key) {
        return queryStringParameters.get(key);
    }

    public Map<String, Object> getBody() {
        return body;
    }
}