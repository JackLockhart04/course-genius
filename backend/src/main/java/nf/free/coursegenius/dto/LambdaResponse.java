package nf.free.coursegenius.dto;

import java.util.Map;
import java.util.List;

import java.util.HashMap;

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

    public void addHeader(String key, String value) {
        if (this.headers == null) {
            this.headers = new HashMap<>();
        }
        if(key == null || value == null){
            return;
        }
        if(this.headers.containsKey(key)){
            this.headers.replace(key, value);
        }else{
            this.headers.put(key, value);
        }
    }
}
