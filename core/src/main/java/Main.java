import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

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
            System.out.println(client.getInetAddress());
            System.out.println(Thread.currentThread().getName());
            var inputFromClient = new BufferedReader(new InputStreamReader((client.getInputStream())));

            while (true) {
                var oneLineAtTheTime = inputFromClient.readLine();
                if (oneLineAtTheTime == null || oneLineAtTheTime.isEmpty()) {
                    break;
                }
                System.out.println(oneLineAtTheTime);

            }
            var outputToClient = new PrintWriter(client.getOutputStream());
            outputToClient.println("HTTP/1.1 404 Not Found\r\nContent-Length: 0\r\n\r\n");
            outputToClient.flush();
            inputFromClient.close();
            outputToClient.close();
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
