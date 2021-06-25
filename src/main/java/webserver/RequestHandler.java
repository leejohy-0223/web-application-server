package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
            String url = " ";
            url = extracted(in, url);
            System.out.println("url : " +  url);
//            byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());
            byte[] body = Files.readAllBytes(Paths.get("./webapp" + url));

            //아래는 기존 코드
//            byte[] body = "Hello World!! it is not in html".getBytes();
            DataOutputStream dos = new DataOutputStream(out);
            response200Header(dos, body.length);
            responseBody(dos, body);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private String extracted(InputStream in, String url) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
        String tmp = br.readLine();
        String tmp2 = extract_url(tmp, tmp.length());
        if (tmp == null) {
            return "false";
        }
        while (!"".equals(tmp)) {
            System.out.println(tmp);
            tmp = br.readLine();
        }
        return tmp2;
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

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private String extract_url(String tmp, int contentLength) throws IOException {

        StringTokenizer st = new StringTokenizer(tmp, " ");
        st.nextToken();
        return st.nextToken();
    }
}
