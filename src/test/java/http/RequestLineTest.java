package http;

import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;

public class RequestLineTest {
    @Test
    public void create_method() {
        RequestLine line = new RequestLine("GET /user/create?userId=leejohy&password=pswd&name=johyeong HTTP/1.1");
        assertEquals(HttpMethod.GET, line.getMethod());
        assertEquals("/user/create", line.getPath());

        line = new RequestLine("POST /index.html HTTP/1.1");
        assertEquals("/index.html", line.getPath());
    }

    @Test
    public void create_path_and_params() {
        RequestLine line = new RequestLine("GET /user/create?userId=leejohy&password=pswd&name=johyeong HTTP/1.1");
        assertEquals(HttpMethod.GET, line.getMethod());
        Map<String, String> params = line.getParams();
        assertEquals(3, params.size());
    }

}
