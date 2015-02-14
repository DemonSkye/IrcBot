import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class ircBot implements Runnable {
    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private String server;
    private String userName;
    private String serverHost;
    private Deque<String> nextMessage = new ArrayDeque<String>();
    private HashMap<String, String> userHostName = new HashMap<String, String>();
    public final ReentrantLock lock = new ReentrantLock();
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

    public String getNextMessage() {
        String myString = nextMessage.getLast();
        System.out.println("MyString: " + myString);
        nextMessage.removeLast();
        return myString;
    }

    public void setNextMessage(String nextMessageText) {
        this.nextMessage.addFirst(nextMessageText);
    }

    //Non-blocking message sending.
    public void run() {
        boolean messageSent = false;
        try {
            while (!messageSent) {
                if (!lock.isLocked()) {
                    lock.lock();
                    String myMessage = getNextMessage();
                    this.getWriter().write(myMessage + "\r\n");
                    this.getWriter().flush();
                    messageSent = true;
                    int timer = 1000 + (myMessage.length() * 4) * 6;
                    //System.out.println(globalFunctions.timeStamp() + "--Waiting " + timer + "ms before next message. ");
                    Thread.sleep(timer);
                    lock.unlock();
                    //System.out.println(globalFunctions.timeStamp() + "--Next Message available to be sent");
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (InterruptedException interrupted) {
            interrupted.printStackTrace();
        }
    }
}

