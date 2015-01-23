import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.net.Socket;

/**
 * Created by Damien on 1/22/2015.
 */
public class ircBot {
    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;

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



}

