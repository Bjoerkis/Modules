import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;

public class Client {
    static String type = "";
    private static void Request(String link, String linkType) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest getRequest = HttpRequest.newBuilder()
                .GET()
                .header("accept", type)
                .uri(URI.create("http://localhost/" + link))
                .build();
    }
}
