package network;

public class netProtocol {
    static final String HOSTNAMEPREFIX = "localhost";
    static final String VALID = "HostNameValid";
    static final String INVALID = "HostNameInvalid";
    static final String IGNORE = "IGNORE";

    public String parseHeaderInfo(String payload){
        char type = getHeaderType(payload);
        String ret;
        switch(type){
            //Process Host Name
            case 'H':
                ret = checkHostName(payload.substring(6)) ? VALID : INVALID;
                break;
            case 'G':
                ret = "GET";
                break;
            case 'C':
                ret = "CookieFound";
                break;
            default:
                ret = IGNORE;
                break;
        }
        return ret;
    }

    private char getHeaderType(String st){
        //request type header
        if(st.length() >= 3  && st.substring(0,3).equals("GET")){
            return 'G';
        }
        //host header
        if(st.length() >= 4  && st.substring(0,4).equals("Host")){
            return 'H';
        }
        if(st.length() >= 6  && st.substring(0, 6).equals("Cookie")){
            return 'C';
        }
        return 'F';
    }

    private boolean checkHostName(String name){
        String tmp = name.substring(0, HOSTNAMEPREFIX.length());
        return tmp.equals(HOSTNAMEPREFIX);
    }
}
