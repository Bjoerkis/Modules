package corepackage;

import com.google.gson.Gson;
import dbpackage.ConnectionDB;
import dbpackage.Person;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    static String link = "";
    static String linkType = "";


    public static void main(String[] args) {

        ExecutorService executorService = Executors.newCachedThreadPool();

        try (ServerSocket serverSocket = new ServerSocket(80)) {
            while (true) {
                Socket client = serverSocket.accept();
                executorService.submit(() -> handleConnection(client));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private static void handleConnection(Socket client) {

        try {
            BufferedReader inputFromClient = new BufferedReader(new InputStreamReader((client.getInputStream())));
            String inputHeader = RequestReader(inputFromClient);
            linkType = inputHeader.split(" ")[0];
            link = inputHeader.split(" ")[1];

            OutputStream outputToClient = client.getOutputStream();


            if (linkType.equals("GET") || linkType.equals("HEAD")) {
                if (link.contains("storage")) {
                    if (link.startsWith("/storage?id=")) {
                        findById(link, outputToClient);
                    } else if (link.equals("/storage")) {
                        sendJsonResponse(outputToClient, linkType);
                    }
                }
                sendGETResponse(outputToClient, linkType);
            } else if (linkType.equals("POST")) {
                link = Decoder.decode(link);
                if (link.startsWith("/storage?id=")) {
                    String nameValue = RequestBodyCreator(inputFromClient);
                    WithPOSTChangeNameById(nameValue, link, outputToClient);
                }
            } else {
                NotFoundResponse(outputToClient);
            }

            inputFromClient.close();
            outputToClient.close();
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static void WithPOSTChangeNameById(String nameValue, String url, OutputStream outputToClient) throws IOException {
        String id = url.replaceAll("[^0-9.]", "");
        int dbId = Integer.parseInt(id);

        Person person = ConnectionDB.sendIdResponse(dbId);


        person = ConnectionDB.NameUpdateSender(dbId, nameValue);

        GsonObjectiveCreator200(outputToClient, person);
    }


    private static String RequestBodyCreator(BufferedReader inputFromClient) throws IOException {
        StringBuffer buffer = new StringBuffer();
        String string = null;
        int LengthOfBody = 0;
        while (!(string = inputFromClient.readLine()).equals("")) {
            buffer.append(string + "");
            if (string.startsWith("Content-Length:")) {
                LengthOfBody = Integer.parseInt(string.substring(string.indexOf(' ') + 1));
            }
        }
        char[] body = new char[LengthOfBody];
        inputFromClient.read(body, 0, LengthOfBody);
        String requestBody = new String(body);
        String name = requestBody.split(":")[1];
        name = name.split("\"")[1];
        return name;
    }

    private static void sendGETResponse(OutputStream outputToClient, String urlType) throws IOException {
        String header = "";
        byte[] bytes = new byte[0];

        File f = Path.of(link.substring(1)).toFile();
        if (!f.exists() && !f.isDirectory()) {
            System.out.println(f.toPath());
            header = "HTTP/1.1 404 Not Found\r\nContent-length: 0\r\n\r\n";
        } else {
            try (FileInputStream fileInputStream = new FileInputStream(f)) {
                bytes = new byte[(int) f.length()];
                fileInputStream.read(bytes);
                String contentType = Files.probeContentType(f.toPath());
                header = "HTTP/1.1 200 OK\r\nContent-type: " + contentType + "\r\nContent-length: " + bytes.length + "\r\n\r\n";
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        outputToClient.write(header.getBytes());
        if (urlType.equals("GET")) {
            outputToClient.write(bytes);
            outputToClient.flush();
        }
    }

//  private static void sendImageResponse(OutputStream outputToClient) throws IOException {
//        String header = "";
//        byte[] bytes = new byte[0];
//        File f = new File("doggo.png");
//        if (!(f.exists() && !f.isDirectory())) {
//            header = "HTTP/1.1 404 Not Found\r\nContent length: 0\r\n";
//
//        } else {
//            try (FileInputStream fileInputStream = new FileInputStream(f)) {
//                bytes = new byte[(int) f.length()];
//                fileInputStream.read(bytes);
//
//                header = "HTTP/1.1 200 OK\r\nContent-Type: image/png\r\nContent-Length: " + bytes.length + "\r\n\r\n";
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//
//        outputToClient.write(header.getBytes());
//        outputToClient.write(bytes);
//
//        outputToClient.flush();
//    }

    private static void sendJsonResponse(OutputStream outputToClient, String linkType) throws IOException {
        //Return Json information
        List<Person> people = ConnectionDB.getAllPersonsFromDb();

        Gson gson = new Gson();

        String json = gson.toJson(people);
        System.out.println(json);

        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);

        String header = "HTTP/1.1 200 OK\r\nContent-Type: application/json\r\nContent-Length: " + bytes.length + "\r\n\r\n";

        outputToClient.write(header.getBytes());
        if (Main.linkType.equals("GET") || Main.linkType.equals("POST")) {
            outputToClient.write(bytes);
            outputToClient.flush();
        }
    }

    private static void findById(String url, OutputStream outputToClient) throws IOException {
        String id = url.replaceAll("[^0-9.]", "");
        int dbId = Integer.parseInt(id);

        Person person = ConnectionDB.sendIdResponse(dbId);

        GsonObjectiveCreator200(outputToClient, person);
    }

    private static String RequestReader(BufferedReader inputFromClient) throws IOException {
        String linkType = "";
        String link = "";

        while (true) {
            String line = inputFromClient.readLine();
            if (line.startsWith("GET")) {
                linkType = "GET";
                link = line.split(" ")[1];
            } else if (line.startsWith("HEAD")) {
                linkType = "HEAD";
                link = line.split(" ")[1];
            } else if (line.startsWith("POST")) {
                linkType = "POST";
                link = line.split(" ")[1];
            }
            if (link.endsWith("/")) {
                StringBuffer stringbuffer = new StringBuffer(link);
                stringbuffer.deleteCharAt(stringbuffer.length() - 1);
                link = stringbuffer.toString();
            }
            return linkType + " " + link;
        }
    }
    private static void GsonObjectiveCreator200(OutputStream outputToClient, Person person) throws IOException {
        Gson gson = new Gson();
        String json = gson.toJson(person);
        System.out.println(json);

        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        String header = "HTTP/1.1 200 OK\r\nContent-Type: application/json\r\nContent-length: " + bytes.length + "\r\n\r\n";

        outputToClient.write(header.getBytes());
        outputToClient.write(bytes);
        outputToClient.flush();
    }
    // in case of 404 error
    private static void NotFoundResponse(OutputStream outputToClient) throws IOException {
        String header = "HTTP/1.1 404 Not Found\r\nContent-length: 0\r\n\r\n";
        outputToClient.write(header.getBytes());
        outputToClient.flush();
    }

}
