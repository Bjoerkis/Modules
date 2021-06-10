import com.google.gson.Gson;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    // initially used synchronizedList in order to make the server thread safe. with the static list used bellow
    // public static List<String> billboard = Collections.synchronizedList(new ArrayList<>());
    // ended up opting for a normal ArrayList and only synchronizing more specific parts in order to optimize performance

    public static List<String> billboard = new ArrayList<>();

    public static void main(String[] args) {

        ExecutorService executorService = Executors.newCachedThreadPool();

        try (ServerSocket serverSocket = new ServerSocket(5050)) {
            while (true) {
                Socket client = serverSocket.accept();
                //start thread
                // Thread thread = new Thread(() -> handleConnection(client));
                // thread.start();
                executorService.submit(() -> handleConnection(client));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void handleConnection(Socket client) {
        try {
            var inputFromClient = new BufferedReader(new InputStreamReader((client.getInputStream())));
            var url = readRequest(inputFromClient);

            var outputToClient = (client.getOutputStream());
            //routing
            if (url.equals("/doggo.png"))
                sendImageResponse(outputToClient);

                // Due to iteration of list, the list cannot apply synchronizedList used above.
                // By locking the list within the "synchronized"-statement,
                // we apply it(the same function as synchronizedList) more precisely to the foreach-loop in the sendResponse method.
            else
                sendJsonResponse(outputToClient);

            inputFromClient.close();
            outputToClient.close();
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void sendImageResponse(OutputStream outputToClient) throws IOException {
        String header = "";
        byte[] bytes = new byte[0];
        File f = new File("doggo.png");
        if (!(f.exists() && !f.isDirectory())) {
            header = "HTTP/1.1 404 Not Found\r\nContent length: 0\r\n";

        } else {
            try (FileInputStream fileInputStream = new FileInputStream(f)) {
                bytes = new byte[(int) f.length()];
                fileInputStream.read(bytes);

                header = "HTTP/1.1 200 OK\r\nContent-Type: image/png\r\nContent-Length: " + bytes.length + "\r\n\r\n";

            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        outputToClient.write(header.getBytes());
        outputToClient.write(bytes);

        outputToClient.flush();
    }

    private static void sendJsonResponse(OutputStream outputToClient) throws IOException {
        //Return Json information
        var people = List.of(new Person("Martin", 31, true),
                new Person("Abel", 49, false),
                new Person("Jenny", 19, true));


        Gson gson = new Gson();

        String json = gson.toJson(people);
        System.out.println(json);

        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
//        synchronized (billboard) {
//            for (String line : billboard) {
//                outputToClient.println(line + "\r\n");
//            }
//        }
//        outputToClient.print("\r\n");
        String header = "HTTP/1.1 200 OK\r\nContent-Type: application/json\r\nContent-Length: " + bytes.length + "\r\n\r\n";
        outputToClient.write(header.getBytes());
        outputToClient.write(bytes);

        outputToClient.flush();
    }

    private static String readRequest(BufferedReader inputFromClient) throws IOException {
        // List<String> temporaryList = new ArrayList<>();
        var url = "";
        while (true) {
            var oneLineAtTheTime = inputFromClient.readLine();
            if (oneLineAtTheTime.startsWith("GET"))
                url = oneLineAtTheTime.split(" ")[1];
            if (oneLineAtTheTime == null || oneLineAtTheTime.isEmpty()) {
                break;
            }
            //    temporaryList.add(oneLineAtTheTime);
            System.out.println(oneLineAtTheTime);
        }
        return url;
        // by applying synchronization to this part only, we only "lock" billboard when
        // adding from the temporary list
//        synchronized (billboard) {
//            billboard.addAll(temporaryList);
//        }
    }
}
