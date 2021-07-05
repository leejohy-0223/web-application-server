package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;

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
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.

            // 정답 코드
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String line = br.readLine();
//            log.debug("request line : {}", line);

            if(line == null) {
                return;
            }
            String url = HttpRequestUtils.getUrl(line);
            Map<String, String> headers = new HashMap<>();
            while (!line.equals("")) {
//                log.debug("header : {}", line);
                line = br.readLine();
                String[] headerTokens = line.split(": ");
                if(headerTokens.length == 2) {
                    headers.put(headerTokens[0], headerTokens[1]);
                }
            }

//            log.debug("Content-length : {} ", headers.get("Content-Length"));


            // 요구사항 4(302 status code) 구현 - start
            if(url.startsWith("/user/create")) {
                //현재 br read시, 앞에서 헤더는 다 읽었으므로 공백 다음인 body 부분을 읽게 된다.
                String requestBody = IOUtils.readData(br, Integer.valueOf(headers.get("Content-Length")));
//                log.debug("RequestBody : {} ", requestBody);
                Map<String, String> param = HttpRequestUtils.parseQueryString(requestBody);
                User user = new User(param.get("userId"), param.get("password"), param.get("name"), param.get("email"));
//                log.debug("User : {}", user);

                //회원 가입 정상 처리되면, Database에 저장
                DataBase.addUser(user);
                DataOutputStream dos = new DataOutputStream(out);
                response302Header(dos);

            } else if(url.equals("/user/login")) { // 요구사항 5(로그인하기) 구현
                String requestBody = IOUtils.readData(br, Integer.valueOf(headers.get("Content-Length")));
                Map<String, String> param = HttpRequestUtils.parseQueryString(requestBody);
                User user = DataBase.findUserById(param.get("userId"));
                DataOutputStream dos = new DataOutputStream(out);

                if(user == null) {
                    log.debug("User Not Found!");
                    response302Header(dos);
                } else if(user.getPassword().equals(param.get("password"))) {
                    log.debug("login success");
                    response302HeaderWithCookie(dos, "logined=true");
                } else {
                    log.debug("Password Mismatch");
                    response302HeaderWithCookie(dos, "logined=false");
                }

            } else {
                DataOutputStream dos = new DataOutputStream(out);
                byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());
                response200Header(dos, body.length);
                responseBody(dos, body);
            }
            // end


        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response302HeaderWithCookie(DataOutputStream dos, String cookie) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: /index.html \r\n");
            dos.writeBytes("Set-Cookie: "+ cookie + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }



    private void response302Header(DataOutputStream dos) {
        try {
            dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
            dos.writeBytes("Location: /index.html \r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
