import com.fasterxml.jackson.databind.JsonMappingException;
//Test
import java.io.*;
import java.net.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class botMain {
    public static void main(String[] args) {
        try {
            // The server to connect to and our details.
            String server = "66.225.225.66";
            String channels[] = {"#BeginnersProgramming"};


            final ircBot ircBot = new ircBot();
            ircBot.setSocket((new Socket(server, 6667)));
            ircBot.setWriter((new BufferedWriter(new OutputStreamWriter(ircBot.getSocket().getOutputStream()))));
            ircBot.setReader((new BufferedReader(new InputStreamReader(ircBot.getSocket().getInputStream()))));
            ircBot.setServer(server);
            String line;
            boolean isAdmin = false;
            Connect.Connection(ircBot, ircBot.getWriter(), channels);

            try {
                while (true) {
                    ExecutorService executor = Executors.newFixedThreadPool(2);
                    Callable inputThread = threadHandler.inputThreadHandler(ircBot, channels);
                    Callable networkThread = threadHandler.connectionThreadHandler(ircBot);
                    Future<String> inputFuture = executor.submit(inputThread);
                    Future<String> networkFuture = executor.submit(networkThread);

                    String errorString = inputFuture.get();
                    String networkErrorString = networkFuture.get();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            System.out.println("IOE - Botmain: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Exception E botmain - : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
