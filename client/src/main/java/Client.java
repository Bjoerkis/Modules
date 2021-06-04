import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {

    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 5050);

            var output = new PrintWriter(socket.getOutputStream());
            output.println("Hello from client");
            output.flush();
            output.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
