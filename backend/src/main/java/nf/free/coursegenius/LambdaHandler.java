package nf.free.coursegenius;

import nf.free.coursegenius.dto.*;
import nf.free.coursegenius.config.AppConfig;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LambdaHandler implements RequestHandler<Map<String, Object>, LambdaResponse>{
    @Override
    @SuppressWarnings("unchecked")
    public LambdaResponse handleRequest(Map<String, Object> input, Context context) {
        // Setup AppConfig
        AppConfig.loadProperties("application-prod.properties");
        
        // Setup input for ctx
        Map<String, Object> ctxInput = new HashMap<>();

        // Find path and method
        Map<String, Object> requestContext = (Map<String, Object>) input.get("requestContext");
        Map<String, Object> http = (Map<String, Object>) requestContext.get("http");
        String path = (String) http.get("path");
        ctxInput.put("path", path);
        String method = (String) http.get("method");
        ctxInput.put("method", method);

        // Body
        Object body = input.get("body");
        if (body instanceof String) {
            ctxInput.put("body", body);
        } else if (body instanceof Map) {
            ctxInput.put("body", body);
        } else {
            ctxInput.put("body", null);
        }

        // Cookies
        Map<String, String> cookies = new HashMap<>();
        Object cookiesObject = input.get("cookies");
        if (cookiesObject instanceof List) {
            List<String> cookieStrings = (List<String>) cookiesObject;
            for (String cookieString : cookieStrings) {
                String[] cookieParts = cookieString.split("=", 2);
                if (cookieParts.length == 2) {
                    cookies.put(cookieParts[0], cookieParts[1]);
                }
            }
        }
        ctxInput.put("cookies", cookies);

        // Headers
        ctxInput.put("headers", input.get("headers"));

        // Find query parameters
        Map<String, Object> queryStringParameters = (Map<String, Object>) input.get("queryStringParameters");
        ctxInput.put("queryStringParameters", queryStringParameters);

        RequestContext ctx = new RequestContext(ctxInput);
        System.out.println("Handling request for path: " + ctx.getPath() + ", method: " + ctx.getMethod());

        // Handle preflight OPTIONS request
        if ("OPTIONS".equalsIgnoreCase(method)) {
            LambdaResponse response = new LambdaResponse();
            response.statusCode = 200;
            // String origin = (String) ((Map<String, Object>) input.get("headers")).get("origin");
            // if (origin != null) {
            //     response.addHeader("Access-Control-Allow-Origin", origin);
            // }
            // response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            // response.addHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
            // response.addHeader("Access-Control-Allow-Credentials", "true");
            System.out.println("Returning OPTIONS response");
            return response;
        }

        App app = new App();
        LambdaResponse response = app.handleRequest(ctx).toLambdaResponse();

        // Add CORS headers
        // String origin = (String) ((Map<String, Object>) input.get("headers")).get("origin");
        // if (origin != null) {
        //     response.addHeader("Access-Control-Allow-Origin", origin);
        // }
        // response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        // response.addHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        // response.addHeader("Access-Control-Allow-Credentials", "true");
        System.out.println("Returning " + method + " response");
        return response;
    }
}