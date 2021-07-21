package webserver;

import http.HttpRequest;
import http.HttpResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import controller.Controller;
import util.HttpRequestUtils;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            HttpRequest request = new HttpRequest(in);
            HttpResponse response = new HttpResponse(out);

            //세션 아이디가 존재하지 않을 경우, 새로운 세션 아이디를 생성하여 addHeader한다.
//            if(getSessionId(request.getHeader("Cookie")) == null) {
//                response.addHeader("Set-Cookie", "JSEESIONID=" + UUID.randomUUID());
//            }

            if(request.getCookies().getCookie("JSESSIONID") == null) {
                response.addHeader("Set-Cookie", "JSEESIONID=" + UUID.randomUUID());
            }

            Controller controller = RequestMapping.getController(request.getPath());
            if (controller == null) {
                String path = getDefaultPath(request.getPath());
                response.forward(path);
            } else {
                controller.service(request, response);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

//    private String getSessionId(String cookieValue) {
//        Map<String, String> cookies = HttpRequestUtils.parseCookies(cookieValue);
//        return cookies.get("JSEESSIONID");
//    }

    private String getDefaultPath(String path) {
        if (path.equals("/")) {
            return "/index.html";
        }
        return path;
    }
}
