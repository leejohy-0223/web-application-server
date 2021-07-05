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
import java.util.StringTokenizer;

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
        Assert.assertEquals(tmp, tmp2);

    }

    @Test
    public void Request_header_url추출() throws IOException {
        String tmp = "get /index.html http/1.1";
        BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(tmp.getBytes())));
        Assert.assertEquals("/index.html", ioUtils.extract_url(br, tmp.length()));
    }

    @Test
    public void url_get() throws IOException {
        String tmp = "/user/create?userId=leejo&password=whgud&name=%EC%9D%B4%EC%A1%B0%ED%98%95&email=leejohy%40naver.com";
        StringTokenizer st = new StringTokenizer(tmp, "?");
        st.nextToken();
        Map<String, String> queryStringMap = HttpRequestUtils.parseQueryString(st.nextToken());
        Assert.assertEquals("leejo", queryStringMap.get("userId"));

    }

    @Test
    public void getmethod() throws IOException {
        String tmp = "get /index.html http/1.1";
        Assert.assertEquals("get", ioUtils.getMethod(tmp));
    }
}
