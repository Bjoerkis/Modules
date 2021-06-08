import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class UtilsTest {

    @Test
    void simpleGETReturnsUrl() {
        String url = Utils.parseUrl("""
                GET / HTTP/1.1\r\n \
                Host: www.example.com\r\n \
                \r\n \
                """);
        assertThat(url).isEqualTo("/");
    }

    @Test
    void filePathGETReturnsUrl() {
        String url = Utils.parseUrl("""
                GET / HTTP/1.1\r\n \
                Host: www.example.com\r\n \
                \r\n \
                """);
        assertThat(url).isEqualTo("/index.html");
    }

    @Test
    void filePathHeadReturnsHEADAndURL() {
        String url = Utils.parseHttpRequestType("""
                HEAD / HTTP/1.1\r\n \
                Host: www.example.com\r\n \
                \r\n \
                """);
        assertThat(url).isEqualTo("/index.html");
    }
}
