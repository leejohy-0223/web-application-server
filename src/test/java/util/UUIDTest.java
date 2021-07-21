package util;

import org.junit.Assert;
import org.junit.Test;

import java.util.Map;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class UUIDTest {
    @Test
    public void uuid() {
        System.out.println(UUID.randomUUID());
    }

    @Test
    public void parseQueryString() {
        String queryString = "userId=leejohy";
        Map<String, String> parameters = HttpRequestUtils.parseQueryString(queryString);
        assertThat(parameters.get("userId"), is("leejohy"));
        assertThat(parameters.get("password"), is(nullValue()));
    }


    @Test
    public void reqUtils() {
        Map<String, String> cookies = HttpRequestUtils.parseCookies("n1=v1; n2=v2; n5=v5");
        assertThat(cookies.get("n1"), is("v1"));
        assertThat(cookies.get("n2"), is("v2"));
        assertThat(cookies.get("n5"), is("v5"));


    }


}


