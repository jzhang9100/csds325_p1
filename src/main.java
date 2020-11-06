import network.HTTPServer;
import network.HTTPClient;

import java.io.FileInputStream;
import java.util.Properties;

public class main {
    public static void main(String args[]) throws Exception {
        System.out.println("Running...");
        System.out.println("Sever initilized...");

        Properties prop = new Properties();
        FileInputStream ip = new FileInputStream("/home/cxz416/csds325_p1/src/static/config.properties");

        prop.load(ip);
        int port = (int) prop.get("port");
        (new Thread(new HTTPServer(port))).start();
    }
}
