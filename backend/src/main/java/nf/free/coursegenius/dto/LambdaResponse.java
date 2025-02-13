package nf.free.coursegenius.dto;

import java.util.Map;
import java.util.List;

public class LambdaResponse {
    // FIXME implement LambdaResponse
    public Map<String, Object> body;
    public int statusCode;
    public Map<String, String> headers;
    public List<String> cookies;

    public LambdaResponse() {
        this.body = null;
        this.statusCode = 200;
        this.headers = null;
        this.cookies = null;
    }
}
