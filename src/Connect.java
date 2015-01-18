/**
 * Created by Damien on 1/15/2015.
 */
import java.io.*;
import java.lang.Thread;
public class Connect {
    public static void Connect(BufferedReader reader, BufferedWriter writer, String channels[]) throws Exception{
        // The server to connect to and our details.
        String nick = "Prog-Bot";
        String login = "Prog-Bot";

        // Log on to the server.
        writer.write("NICK " + nick + "\r\n");
        writer.write("USER " + login + " 8 * : Test\r\n");
        writer.flush();

        // Read lines from the server until it tells us we have connected.
        String line = null;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
            if (line.toLowerCase().contains("ping")) {
                // We must respond to PINGs to avoid being disconnected.
                writer.write("PONG " + line.substring(5) + "\r\n");
                writer.flush();
            }
            if (line.indexOf("004") >= 0) {
                // We are now logged in.
                break;
            } else if (line.indexOf("433") >= 0) {
                System.out.println("Nickname is already in use.");
                return;
            }
        }
        // Join the channel.
        for(int i=0; i<channels.length; i++) {
            writer.write("JOIN " + channels[i] + "\r\n");
            writer.flush();
            Thread.sleep(300);
            System.out.println("PRIVMSG " + channels[i] + " :<Java IRC BOT V.0.0.3 --Online>" );
            writer.write("PRIVMSG " + channels[i] + " :<Java IRC BOT V.0.0.3 --Online> \r\n");
            writer.flush();
        }

        writer.write("AUTH Prog-Bot " + privateStuff.getPassword() + "\r\n");

    }
}
