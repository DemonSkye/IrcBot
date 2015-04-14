import javax.net.ssl.*;
import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class ircBot implements Runnable {
    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private String server;
    private String userName;
    private String serverHost;
    private String lastSaidTime;
    private Deque<String> nextMessage = new ArrayDeque<String>();
    private Deque<Boolean> nextServerMessage = new ArrayDeque<Boolean>();
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

    public String getLastSaidTime() {
        return lastSaidTime;
    }

    public void setLastSaidTime(String lastSaidTime) {
        this.lastSaidTime = lastSaidTime;
    }

    public String getUserHostName(String userName) {
        return this.userHostName.get(userName);
    }

    public void setUserHostName(String userName, String userHost) {
        this.userHostName.put(userName, userHost);
    }

    public String getNextMessage() {
        String myString = nextMessage.getLast();
        nextMessage.removeLast();
        return myString;
    }

    public void setNextMessage(String nextMessageText) {
        this.nextMessage.addFirst(nextMessageText);
    }

    public void setNextServerMessage(Boolean nextMessageServer) {
        this.nextServerMessage.addFirst(nextMessageServer);
    }

    public Boolean getNextServerMessage() {
        Boolean isServerMessage = nextServerMessage.getLast();
        nextServerMessage.removeLast();
        return isServerMessage;
    }

    public InputStream doHTTPSConnection(String weatherURL) {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }
                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        }
                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        }
                    }
            };
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            // Create all-trusting host name verifier
            HostnameVerifier allHostsValid = new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

            URL url = new URL(weatherURL);
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            con.setRequestProperty("User-Agent", "Mozilla/5.0 ( compatible ) ");
            con.setRequestProperty("Accept", "*/*");
            con.setDoOutput(true);
            con.setDoInput(true);
            return con.getInputStream();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //Non-blocking message sending.  So much coding for this
    public void run() {
        boolean messageSent = false;
        try {
            while (!messageSent) {
                if (!lock.isLocked()) {
                    lock.lock();
                    String myMessage = getNextMessage();
                    if (!getNextServerMessage()) {
                        myMessage += " - Queued messages: " + nextMessage.size();
                    }
                    //System.out.println(globalFunctions.timeStamp() + " -- " + myMessage); //
                    this.getWriter().write(myMessage + "\r\n");
                    this.getWriter().flush();
                    messageSent = true;
                    int timer = 1000 + (myMessage.length() * 4) * 6;
                    Thread.sleep(timer);
                    lock.unlock();
                    //System.out.println(globalFunctions.timeStamp() + "--Next Message available to be sent"); // Debugs line timer
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (InterruptedException interrupted) {
            interrupted.printStackTrace();
        }
    }
}

