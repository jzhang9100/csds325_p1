package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;


public class HTTPServer implements Runnable {
    static final String connectionAccepted = "\nConnection accepted from: %s, port: %d.";

    ServerSocket listeningSocket;

    public HTTPServer() throws IOException {
        //parse config
        listeningSocket = new ServerSocket(5555);
    }

    @Override
    public void run() {
        try {
            while(true) {
                Socket connectionSocket = listeningSocket.accept();
                System.out.println(String.format(connectionAccepted,
                        connectionSocket.getLocalAddress(), connectionSocket.getPort()));

                (new Thread(new serverResponse(connectionSocket))).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

/*
try {
            in = new BufferedReader(new InputStreamReader(connect.getInputStream()));  // read characters from client via input stream on socket
            out = new PrintWriter(connect.getOutputStream());  // get character output stream to client (for headers)
            dataOut = new BufferedOutputStream(connect.getOutputStream());  // get binary output stream to client (for requested data)


            String input = in.readLine();  // get first line of the request from client
            StringTokenizer parse = new StringTokenizer(input);  // parse the request with string tokenizer
            String method = parse.nextToken().toUpperCase(); // get the HTTP method of client
            fileRequested = parse.nextToken().toLowerCase();  // get file requested


            File file = new File(WEB_ROOT, fileRequested);
            int fileLength = (int) file.length();
            String content = getContentType(fileRequested);

            if (method.equals("GET")) { // GET method so send content

*/
