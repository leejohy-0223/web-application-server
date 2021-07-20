package webserver;

import controller.Controller;
import db.DataBase;
import http.HttpRequest;
import http.HttpResponse;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Collection;
import java.util.Map;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect!! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {

            HttpRequest request = new HttpRequest(in);
            HttpResponse response = new HttpResponse(out);
            Controller controller = RequestMapping.getController(request.getPath());

            // mapping 된 컨트롤러가 없다면 html, css, js이므로 헤더랑 바디 붙여서 리턴
            if(controller == null) {
                String path = getDefaultPath(request.getPath());
                response.forward(path);
            } else {
                // 추가 로직 발생 시(로그아웃 등) 여기는 건들 필요 없이, Controller 인터페이스 새로 구현하는
                // 클래스를 추가하기만 하면 된다.
                controller.service(request, response);
            }

        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void listUser(HttpRequest request, HttpResponse response) {
        if(!request.getCookieStatus()) {
            response.sendRedirect("/user/login.html");
            return;
        }
        Collection<User> users = DataBase.findAll();
        StringBuilder sb = new StringBuilder();
        sb.append("<table border='1'>");
        for (User user : users) {
            sb.append("<tr>");
            sb.append("<td>" + user.getUserId() + "</td>");
            sb.append("<td>" + user.getName() + "</td>");
            sb.append("<td>" + user.getEmail() + "</td>");
            sb.append("</tr>");
        }
        sb.append("</table>");
        response.forwardBody(sb.toString());
    }

    private void login(HttpRequest request, HttpResponse response) {
        User user = DataBase.findUserById(
                request.getParameter("userId"));
        if (user != null) {
            if (user.login(request.getParameter("password"))) {
                response.addHeader("Set-Cookie", "logined=true");
                response.sendRedirect("/index.html");
            } else {
                response.sendRedirect("/user/logined_failed.html");
            }
        } else {
            response.sendRedirect("/user/logined_failed.html");
        }
    }

    private void createUser(HttpRequest request, HttpResponse response) {
        User user = new User(
                request.getParameter("userId"),
                request.getParameter("password"),
                request.getParameter("name"),
                request.getParameter("email"));
        log.debug("user : {}", user);
        DataBase.addUser(user);
        response.sendRedirect("/index.html");
    }

    private String getDefaultPath(String path) {
        if(path.equals("/")) {
            return "/index.html";
        }
        return path;
    }

    private boolean isLogin(String line) {
        Map<String, String> cookies = HttpRequestUtils.parseCookies(line);
        String value = cookies.get("logined");
        if (value == null) {
            return false;
        }
        return Boolean.parseBoolean(value);
    }

}
