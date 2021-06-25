package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.StringTokenizer;

public class MyIOUtils {
    public static String readData(BufferedReader br, int contentLength) throws IOException {
        char[] body = new char[contentLength];
        br.read(body, 0, contentLength);
        return String.copyValueOf(body);
    }

    public static String extract_url(BufferedReader br, int contentLength) throws IOException {
        StringTokenizer st = new StringTokenizer(br.readLine(), " ");
        st.nextToken();
        return st.nextToken();
    }
}
