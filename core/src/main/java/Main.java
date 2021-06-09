import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
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
            readRequest(inputFromClient);

            var outputToClient = new PrintWriter(client.getOutputStream());
            // Due to iteration of list, the list cannot apply synchronizedList used above.
            // By locking the list within the "synchronized"-statement,
            // we apply it(the same function as synchronizedList) more precisely to the foreach-loop in the sendResponse method.
            sendResponse(outputToClient);

            inputFromClient.close();
            outputToClient.close();
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void sendResponse(PrintWriter outputToClient) {
        //Return Json information
        var people =  List.of(new Person("Martin", 31, true),
                new Person("Abel", 49,false),
                new Person("Jenny",19,true));


        Gson gson = new Gson();
        String json = gson.toJson(people);
        System.out.println(json);


//        synchronized (billboard) {
//            for (String line : billboard) {
//                outputToClient.println(line + "\r\n");
//            }
//        }
//        outputToClient.print("\r\n");
        outputToClient.println("HTTP/1.1 404 Not Found\r\nContent-Length: 0\r\n\r\n");
        outputToClient.flush();
    }

    private static void readRequest(BufferedReader inputFromClient) throws IOException {
        List<String> temporaryList = new ArrayList<>();

        while (true) {
            var oneLineAtTheTime = inputFromClient.readLine();
            if (oneLineAtTheTime == null || oneLineAtTheTime.isEmpty()) {
                break;
            }
            temporaryList.add(oneLineAtTheTime);
            System.out.println(oneLineAtTheTime);
        }
        // by applying synchronization to this part only, we only "lock" billboard when
        // adding from the temporary list
        synchronized (billboard) {
            billboard.addAll(temporaryList);
        }
    }
}
