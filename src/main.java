import network.HTTPServer;
import network.HTTPClient;

public class main {
    public static void main(String args[]) throws Exception {
        System.out.println("Running...");
        System.out.println("Sever initilized...");
        (new Thread(new HTTPServer())).start();
    }
}
