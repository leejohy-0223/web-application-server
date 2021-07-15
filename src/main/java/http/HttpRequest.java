package http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);

    private Map<String, String> header = new HashMap<>();
    private Map<String, String> params = new HashMap<>();
    private RequestLine requestLine;
    private boolean cookie_value;

    public HttpRequest(InputStream in) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String line = br.readLine();
            if (line == null) {
                return;
            }

            // 첫 번째 line에서 정보를 얻기 위한 메서드 구현
            requestLine = new RequestLine(line);

            line = br.readLine();
            while (!line.equals("")) {
                log.debug("header : {}", line);
                String[] tokens = line.split(":");
                header.put(tokens[0].trim(), tokens[1].trim());
                line = br.readLine();
            }


            if (getMethod().isPost()) {
                String body = IOUtils.readData(br, Integer.parseInt(header.get("Content-Length")));
                params = HttpRequestUtils.parseQueryString(body);
            } else {
                params = requestLine.getParams();
            }

            if(header.get("Cookie") != null) {
                Map<String, String> cookies = HttpRequestUtils.parseCookies(header.get("Cookie"));
                String value = cookies.get("logined");
                if(value == null) {
                    cookie_value = false;
                }
                cookie_value = Boolean.parseBoolean(value);
            }



        } catch (IOException io) {
            log.error(io.getMessage());
        }
    }

    public HttpMethod getMethod() {
        return requestLine.getMethod();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getHeader(String name) {
        return header.get(name);
    }

    public String getParameter(String name) {
        return params.get(name);
    }

    public Boolean getCookieStatus() {
        return cookie_value;
    }
}
