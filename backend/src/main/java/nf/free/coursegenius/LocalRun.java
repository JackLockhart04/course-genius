package nf.free.coursegenius;

import nf.free.coursegenius.config.AppConfig;
import nf.free.coursegenius.dto.*;

import spark.Request;
import spark.Response;
import spark.Spark;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

import org.json.JSONObject;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.Cookie;

public class LocalRun {

    public static void main(String[] args) {
        // Setup config
        AppConfig.loadProperties("application-local.properties");

        // Set the port for the server
        Integer port = 4567;
        Spark.port(port);

        Spark.options("/*", (request, response) -> {
            String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }

            String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }

            return "OK";
        });

        // Set the logging level to WARN to reduce verbosity
        Logger rootLogger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        rootLogger.setLevel(Level.WARN);

        // Create an instance of your App class
        App app = new App();

        // Define a route for GET requests
        Spark.get("/*", (request, response) -> handleRequest(app, request, response));

        // Define a route for POST requests
        Spark.post("/*", (request, response) -> handleRequest(app, request, response));

        System.out.println("Server started on port " + port);
    }

    private static String handleRequest(App app, Request request, Response response) {
        try {
            // Convert Spark request to your RequestContext
            // Path and method shit show
            Map<String, Object> input = new HashMap<>();
            input.put("path", request.pathInfo());
            input.put("method", request.requestMethod());

            // Body
            // System.out.println("Raw body: " + request.body());s
            input.put("body", request.body());

            // Headers
            Set<String> headerKeys = request.headers();
            Map<String, String> headers = new HashMap<>();
            for(String key : headerKeys){
                headers.put(key, request.headers(key));
            }
            input.put("headers", headers);

            // Cookies
            Map<String, String> cookies = request.cookies();
            input.put("cookies", cookies);

            // Params
            Map<String, String> queryParams = new HashMap<>();
            Set<String> paramKeys = request.queryParams();
            for(String key : paramKeys){
                String[] value = request.queryParamsValues(key);
                if(value.length == 1){
                    queryParams.put(key, value[0]);
                } else {
                    queryParams.put(key, String.join(",", value));
                }
            }
            input.put("queryStringParameters", queryParams);
            
            RequestContext ctx = new RequestContext(input);

            // Call your app's handleRequest method
            ResponseObject responseObject = app.handleRequest(ctx);

            // Convert to Spark response
            // Convert body to JSON string
            JSONObject jsonBody = new JSONObject(responseObject.getBody());
            response.body(jsonBody.toString());
            // Status code
            response.status(responseObject.getStatusCode());

            // Cookies
            for(Map.Entry<String, Cookie> entry : responseObject.getCookieMap().entrySet()){
                Cookie cookie = entry.getValue();
                String name = cookie.getName();
                String value = cookie.getValue();
                int maxAge = cookie.getMaxAge();
                boolean httpOnly = cookie.isHttpOnly();
                response.cookie("/", name, value, maxAge, false, httpOnly);
            }
            // Headers
            for(Map.Entry<String, String> entry : responseObject.getHeaders().entrySet()){
                response.header(entry.getKey(), entry.getValue());
            }

            // Add CORS headers to the response
            addCorsHeaders(request, response);

            System.out.println("Response: " + response.body());
            return response.body();
        } catch (Exception e) {
            e.printStackTrace();
            response.status(500);
            response.body("Internal server error");

            // Add CORS headers to the error response
            addCorsHeaders(request, response);

            return response.body();
        }
    }

    private static void addCorsHeaders(Request request, Response response) {
        String origin = request.headers("Origin");
        if (origin != null) {
            response.header("Access-Control-Allow-Origin", origin);
        }
        response.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.header("Access-Control-Allow-Headers", "Content-Type, Authorization");
        response.header("Access-Control-Allow-Credentials", "true");
    }
}