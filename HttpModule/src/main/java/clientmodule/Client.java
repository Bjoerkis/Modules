package clientmodule;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

public class Client {

    static Scanner sc = new Scanner(System.in);
    static String link = "";
    static String linkType = "";
    static boolean loop = true;
    static String Extension = "";

    public static void main(String[] args) throws IOException, InterruptedException {

        while (loop) {
            System.out.println("What kind of request would you like to perform?");
            System.out.println("1. GET request");
            System.out.println("2. HEAD request");
            System.out.println("3. POST request");
            int Type = sc.nextInt();
            sc.nextLine();

            System.out.println("Enter url: ");
            link = sc.nextLine();

            if (link.contains(".")) {
                String[] linkArray = link.split("\\.");
                int lastInArray = linkArray.length - 1;
                linkType = linkArray[lastInArray];
                Extension = linkType;
            }

            switch (linkType) {
                case "html" -> linkType = "application/html";
                case "css" -> linkType = "text/css";
                case "js" -> linkType = "text/javascript";
                case "png" -> linkType = "image/png";
                case "jpg" -> linkType = "image/jpg";
                case "pdf" -> linkType = "application/pdf";
                default -> linkType = "application/json";
            }

            Map<String, String> bodyText = new HashMap<String, String>();

            String parameter = "";
            if (link.contains("&")) {
                parameter = link.split("&")[1];
            }
            if (parameter.startsWith("changename")) {
                String userNameInput = parameter.split("=")[1];
                bodyText.put("changename", userNameInput);
            }
            String urlType;
            switch (Type) {
                case 1 -> {
                    urlType = "GET";
                    GETAndHEAD(link, urlType);
                }
                case 2 -> {
                    urlType = "HEAD";
                    GETAndHEAD(link, urlType);
                }
                case 3 -> POST(bodyText);
            }
        }

    }


    private static void GETAndHEAD(String link, String Type) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest getRequest = HttpRequest.newBuilder()
                .GET()
                .header("accept", linkType)
                .uri(URI.create("http://localhost/" + link))
                .build();


        if (link.equals("storage")) {
            HttpResponse<String> response;
            response = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
            if (Type.equals("HEAD")) {
                System.out.println(response.headers());
            } else {
                ObjectMapper mapper = new ObjectMapper();
                List<Person> posts = mapper.readValue(response.body(), new TypeReference<>() {
                });
                posts.forEach(System.out::println);
            }
        } else if (link.contains("?")) {
            HttpResponse<String> response;
            response = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
            if (Type.equals("HEAD")) {
                System.out.println(response.headers());
            } else {
                ObjectMapper mapper = new ObjectMapper();
                Person post = mapper.readValue(response.body(), new TypeReference<>() {
                });
                System.out.println(post);
            }

        } else if (link.contains(".")) {
            HttpResponse<byte[]> response = client.send(getRequest, HttpResponse.BodyHandlers.ofByteArray());
            if (Type.equals("HEAD")) {
                System.out.println(response.headers());
            } else {
                if (200 == response.statusCode()) {
                    byte[] bytes = response.body();
                    try (OutputStream out = new FileOutputStream(link)) {
                        out.write(bytes);
                    }
                }
            }
        }

    }

    public static CompletableFuture<Void> POST(Map<String, String> bodyText) throws IOException {
        ObjectMapper Mapper = new ObjectMapper();
        String requestBody = Mapper
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(bodyText);

        HttpRequest request = HttpRequest.newBuilder()
                .header("Content-Type", linkType)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .uri(URI.create("http://localhost/" + link))
                .build();

        return HttpClient.newHttpClient()
                .sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::statusCode)
                .thenAccept(System.out::println);
    }
}
