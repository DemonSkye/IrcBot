import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.net.Socket;
import java.util.Collection;
import java.util.HashMap;

/**
 * Created by Damien on 1/22/2015.
 */
public class ircBot {
    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private String server;
    private String userName;
    private String serverHost;
    private HashMap<String, String> userHostName = new HashMap<String, String>();
    ircBot() {

    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public BufferedReader getReader() {
        return reader;
    }

    public void setReader(BufferedReader reader) {
        this.reader = reader;
    }

    public BufferedWriter getWriter() {
        return writer;
    }

    public void setWriter(BufferedWriter writer) {
        this.writer = writer;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getServerHost() {
        return serverHost;
    }

    public void setServerHost(String serverHost) {
        this.serverHost = serverHost;
    }

    public String getUserHostName(String userName) {
        return this.userHostName.get(userName);
    }

    public void setUserHostName(String userName, String userHost) {
        this.userHostName.put(userName, userHost);
    }


}

