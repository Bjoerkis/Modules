import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

    public static void main(String[] args) {
        //localhost 127.0.0.1
        //178.174.162.51//Blombergs computer


        try (ServerSocket serverSocket = new ServerSocket(5050)) {

            while(true) {
                Socket client = serverSocket.accept();
                System.out.println(client.getInetAddress());
                var inputFromClient = new BufferedReader(new InputStreamReader((client.getInputStream())));

                while(true){
                   var oneLineAtTheTime = inputFromClient.readLine();
                   if(oneLineAtTheTime == null || oneLineAtTheTime.isEmpty()) {
                       break;
                   }
                    System.out.println(oneLineAtTheTime);

                }
                var outputToClient = new PrintWriter(client.getOutputStream());
                outputToClient.println("HTTP/1.1 404 Not Found\r\nContent-Length: 0\r\n\r\n");
                outputToClient.flush();
                //inputFromClient.lines().forEach(System.out::println);
                inputFromClient.close();
                outputToClient.close();
                client.close();
            }
            // String input = "";
            // while (input != null) {
            // input = inputFromClient.readLine();
            // System.out.println(input);
            // }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
