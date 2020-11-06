package network;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;

public class serverResponse implements Runnable {
    static final String TEST1_HTML = "/test1.html";
    static final String TEST2_HTML = "/test2.html";
    static final String VISITS_HTML = "/visits.html";
    static final String img = "/home/cxz416/csds325_p1/src/static/img/amazon.jpg";
    static final String FAVICON = "/favicon.ico";
    static final String ID = "/cxz416";
    static final String MY_COOKIE_HEADER = "325_p1_visit_cnt";

    static final String VALID = "HostNameValid";
    static final String INVALID = "HostNameInvalid";

    static final String ERROR_PATH = "/home/cxz416/csds325_p1/src/static/misc/error.html";
    static final File ERROR_FILE = new File(ERROR_PATH);
    BufferedReader ERROR_READER = new BufferedReader(new FileReader(ERROR_FILE));

    static final String OK = "HTTP/1.1 200";
    static final String htmlType = "Content-Type: text/html";
    static final String imgType = "Content-Type: image/jpg";
    static final String closed = "Connection: Closed";
    static final String NO = "404 Not Found";
    static final String ENDLINE = "\r\n";

    static final String VISITED_TEMPLATE = "    <p>Your browser visited various URLs on this site X times</p>";


    Socket connectionSocket;
    PrintWriter out;
    BufferedReader in;
    netProtocol protocol;

    public serverResponse(Socket connetionSocket) throws FileNotFoundException {
        this.connectionSocket = connetionSocket;
    }

    @Override
    public void run() {
        String inMessage;
        String GET = null;
        String cookie = null;
        try {
            boolean coockieCheck = false;
            boolean validURL = false;
            boolean getRequest =  false;
            this.in = new BufferedReader(new InputStreamReader(this.connectionSocket.getInputStream()));
            this.out = new PrintWriter(connectionSocket.getOutputStream());
            protocol = new netProtocol();
            while (true) {
                inMessage = in.readLine();
                //System.out.println(inMessage);
                if(inMessage == null){
                    break;
                }

                String response = protocol.parseHeaderInfo(inMessage);
                //System.out.println("READ..." + inMessage + "        RESPONSE..." + response);

                if(response.equals(VALID)){
                    validURL = true;
                } else if(response.equals(INVALID)){
                    postErrorPage();
                    break;
                }

                if(response.equals("GET")){
                    getRequest = true;
                    GET = inMessage;
                }

                if(response.equals("CookieFound")){
                    cookie = inMessage;
                }

                if(inMessage.length() == 0){
                    if(validURL && getRequest){
                        processRequest(GET, cookie);
                    } else{
                        postErrorPage();
                    }
                    out.close();
                    in.close();
                    connectionSocket.close();
                    break;
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Client closed.");
        }
    }



    private void processRequest(String GET, String cookie) throws IOException {
        String rq = processGetRequestHeader(GET);
        System.out.println(rq);
        //check if the get is for our stored image
        if(rq.equals(img)){
            File imgData = new File("/home/cxz416/csds325_p1/src/static/img/amazon.jpg");
            postImg(imgData);
        }else if(!rq.substring(0,ID.length()).equals(ID)){ //other wise, only serve the request if it is one of the three cases described in instructions
            postErrorPage();
        } else {
            rq = rq.substring(ID.length());
            File index;
            if (!rq.equals(FAVICON)) {
                if (rq.equals(TEST1_HTML)) {
                    index = new File("/home/cxz416/csds325_p1/src/static/misc/test1.html");
                    postHTML(index, cookie, false);
                } else if (rq.equals(TEST2_HTML)) {
                    index = new File("/home/cxz416/csds325_p1/src/static/misc/test2.html");
                    postHTML(index, cookie, false);
                } else if (rq.equals(VISITS_HTML)) {
                    index = new File("/home/cxz416/csds325_p1/src/static/misc/visits.html");
                    postHTML(index, cookie, true);
                } else {
                    postErrorPage();
                }
            }
        }
    }
    private void postImg(File index) {
        FileInputStream imgIn = null;
        byte[] imgData = new byte[(int)index.length()];
        try {
            imgIn = new FileInputStream(index);
            imgIn.read(imgData);

            BufferedOutputStream imgOut = new BufferedOutputStream(connectionSocket.getOutputStream());


            out.println(OK);
            out.println(htmlType);
            out.println(closed);
            out.println("Content-Length: " + index.length());
            out.write(ENDLINE);
            out.flush();
            imgOut.write(imgData, 0, (int)index.length());
            imgOut.flush();

        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private void postHTML(File index, String cookie, boolean display) throws IOException {
        String[] cookies = cookie.split(" ");
        String my_cookie = null;
        for(String st : cookies){
            if(st.length() > MY_COOKIE_HEADER.length()) {
                if (st.substring(0, MY_COOKIE_HEADER.length()).equals(MY_COOKIE_HEADER)) {
                    my_cookie = st;
                    break;
                }
            }
        }
        BufferedReader reader = new BufferedReader(new FileReader(index));
        out.println(OK);
        out.println(htmlType);
        out.println(closed);
        out.println("Content-Length: " + index.length());
        int cookie_cnt;
        if(my_cookie == null){
            out.println("Set-Cookie: 325_p1_visit_cnt=1");
            cookie_cnt = 1;
        } else{
            int indx = my_cookie.indexOf('=');
            String cnt = my_cookie.substring(indx+1, my_cookie.length()-1);
            cookie_cnt = (Integer.parseInt(cnt) + 1);
            String incr_cnt = Integer.toString(cookie_cnt);
            String incr_cookie = "Set-Cookie: 325_p1_visit_cnt=" + incr_cnt;
            out.println(incr_cookie);
        }
        out.println(ENDLINE);
        if(display){
            postHTMLDynamic(reader, cookie_cnt);
        } else {
            postHTMLStatic(reader);
        }
    }

    private void postErrorPage() throws IOException {
        out.println(OK);
        System.out.println(NO);
        out.println(htmlType);
        out.println(closed);
        out.println("Content-Length: " + ERROR_FILE.length());
        out.println(ENDLINE);
        String htmlLine = ERROR_READER.readLine();
        while (htmlLine != null) {
            out.println(htmlLine);
            htmlLine = ERROR_READER.readLine();
        }
        ERROR_READER.close();
    }

    private void postHTMLStatic(BufferedReader reader) throws IOException {
        String htmlLine = reader.readLine();
        while (htmlLine != null) {
            out.println(htmlLine);
            htmlLine = reader.readLine();
        }
        reader.close();
    }

    //hard coded for displaying visted cookie count on visited.html
    private void postHTMLDynamic(BufferedReader reader, int cnt) throws IOException {
        String htmlLine = reader.readLine();
        while (htmlLine != null) {
            if(htmlLine.equals(VISITED_TEMPLATE)){
                int indx = htmlLine.indexOf('X');
                String tmp = htmlLine.substring(0, indx) + cnt + htmlLine.substring(indx+1);
                htmlLine = tmp;
            }
            out.println(htmlLine);
            htmlLine = reader.readLine();
        }
        reader.close();
    }

    //parse get request header
    private String processGetRequestHeader(String rq){
        String ret = "";
        int i = 4;
        if(rq.length() < i){
            return null;
        }
        while(rq.charAt(i) != ' '){
            ret += rq.charAt(i);
            i++;
        }
        return ret;
    }
}
