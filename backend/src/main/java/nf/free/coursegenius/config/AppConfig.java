package nf.free.coursegenius.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppConfig {

    public static String webDomain;
    public static String apiDomain;
    public static String clientId;
    public static String clientSecret;
    public static String tenantId;
    public static String redirectUri;
    // SQL stuff
    public static String dbUrl;
    public static String dbUsername;
    public static String dbPassword;
    public static String dbDriverClassName;

    public static void loadProperties(String fileName) {
        Properties properties = new Properties();
        try (InputStream input = AppConfig.class.getClassLoader().getResourceAsStream(fileName)) {
            if (input == null) {
                throw new RuntimeException("Sorry, unable to find " + fileName);
            }
            properties.load(input);
        } catch (IOException ex) {
            throw new RuntimeException("Error loading properties file", ex);
        }
        webDomain = properties.getProperty("domain.web");
        apiDomain = properties.getProperty("domain.api");
        clientId = properties.getProperty("azure.client-id");
        clientSecret = properties.getProperty("azure.client-secret");
        tenantId = properties.getProperty("azure.tenant-id");
        redirectUri = properties.getProperty("azure.redirect-uri");
        // SQL stuff
        dbUrl = properties.getProperty("database.url");
        dbUsername = properties.getProperty("database.username");
        dbPassword = properties.getProperty("database.password");
        dbDriverClassName = properties.getProperty("database.driver-class-name");
    }
}