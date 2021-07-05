package util;

import java.io.BufferedReader;
import java.io.StringReader;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IOUtilsTest {
    private static final Logger logger = LoggerFactory.getLogger(IOUtilsTest.class);

    @Test
    public void readData() throws Exception {
        String data = "abcd123";
        StringReader sr = new StringReader(data);
        BufferedReader br = new BufferedReader(sr);

        logger.debug("parse body : {}", IOUtils.readData(br, data.length()));
    }

    @Test
    public void geturl() {
        String data = "GET /index.html HTTP/1.1";
        String url = HttpRequestUtils.getUrl(data);
        Assert.assertEquals("/index.html", url);
        logger.debug("url : {}", url);
    }
}
