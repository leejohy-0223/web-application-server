package webserver;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import util.HttpRequestUtils;
import util.MyIOUtils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class RequestHandlerTest {

    private MyIOUtils ioUtils;
    @Before
    public void setup() {
        ioUtils = new MyIOUtils();
    }

    @Test
    public void Request_header_print() throws IOException {
        String tmp = "get / http/1.1";
        BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(tmp.getBytes())));
        String tmp2 = ioUtils.readData(br, tmp.length());
        assertEquals(tmp, tmp2);

    }

    @Test
    public void Request_header_url추출() throws IOException {
        String tmp = "get /index.html http/1.1";
        BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(tmp.getBytes())));
        assertEquals("/index.html", ioUtils.extract_url(br, tmp.length()));
    }

    @Test
    public void url_get() throws IOException {
        String tmp = "GET /user/create?userId=leejo&password=whgud&name=%EC%9D%B4%EC%A1%B0%ED%98%95&email=leejohy%40naver.com HTTP/1.1";
        String url = HttpRequestUtils.getUrl(tmp);
        String param = HttpRequestUtils.getParam(url);
        Map<String, String> queryStringMap = HttpRequestUtils.parseQueryString(param);
        assertEquals("leejo", queryStringMap.get("userId"));

    }

    @Test
    public void getpath() {
        String tmp = "GET /user/create?userId=leejo&password=whgud&name=%EC%9D%B4%EC%A1%B0%ED%98%95&email=leejohy%40naver.com HTTP/1.1";
        String url = HttpRequestUtils.getUrl(tmp);
        String path = HttpRequestUtils.getPath(url);
        assertEquals("/user/create", path);
    }

    @Test
    public void getparam() {
        String tmp = "GET /user/create?userId=leejo&password=whgud&name=%EC%9D%B4%EC%A1%B0%ED%98%95&email=leejohy%40naver.com HTTP/1.1";
        String url = HttpRequestUtils.getUrl(tmp);
        String path = HttpRequestUtils.getParam(url);
        assertEquals("userId=leejo&password=whgud&name=%EC%9D%B4%EC%A1%B0%ED%98%95&email=leejohy%40naver.com", path);
    }
}
