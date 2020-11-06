package network;

import java.net.*;
import java.io.*;

public class HTTPClient {
    Socket requestSocket;
    PrintWriter out;
    BufferedReader in;

    int port;

    public HTTPClient(int port){
        this.port = port;
    }

    public void startConnection() throws Exception {
        requestSocket = new Socket(InetAddress.getLocalHost().getHostName(), port);
        out = new PrintWriter(requestSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(requestSocket.getInputStream()));
    }

    public String sendMessage(String payload) throws IOException {
        out.println(payload);
        String response = in.readLine();
        System.out.println("Message From Server Recieved: " + response);

        return response;
    }


    public void terminateConnection() throws IOException {
        in.close();
        out.close();
        requestSocket.close();
    }
}