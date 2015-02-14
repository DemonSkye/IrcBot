import java.io.*;
import java.lang.Thread;
public class Connect {
    public static void Connection(ircBot ircBot, BufferedWriter writer, String channels[]) {
        // The server to connect to and our details.
        String nick = "Prog-Bot";
        String nick2 = "Prog-Bot2";
        String login = "Damien-Lol";

        ircBot.setUserName(nick);
        // Log on to the server.
        try {
            writer.write("NICK " + nick + "\r\n");
            writer.write("USER " + login + " 8 * : Test\r\n");
            writer.flush();

            // Read lines from the server until it tells us we have connected.
            String line;
            while ((line = ircBot.getReader().readLine()) != null) {
                System.out.println(line);
                if (line.toLowerCase().contains("ping")) {
                    // We must respond to PINGs to avoid being disconnected.
                    writer.write("PONG " + line.substring(5) + "\r\n");
                    writer.flush();
                }
                //Set the host
                if (line.contains("001")) {
                    int hostName = line.indexOf(":");
                    int hostName2 = line.indexOf("001");
                    System.out.println("IRCBOT SERVERHOST: " + line.substring(hostName, hostName2 - 1));
                    ircBot.setServerHost(line.substring(hostName, hostName2 - 1));
                }
                if (line.contains("004")) {
                    // We are now logged in.
                    break;
                } else if (line.contains("433")) {
                    System.out.println("Nickname is already in use, trying Nickname 2");
                    writer.write("NICK " + nick2 + "\r\n");
                    writer.write("USER " + login + " 8 * : Test\r\n");

                    return;
                }
            }
            // Join the channel.
            for (String ss : channels) {
                writer.write("JOIN " + ss + "\r\n");
                writer.flush();
                Thread.sleep(300);
                //writer.write("PRIVMSG " + channels[i] + " :<Java IRC BOT V.0.0.3 --Online> \r\n");
                writer.flush();
            }

            writer.write("AUTH Prog-Bot " + privateStuff.getPassword() + "\r\n");
        } catch (IOException ioe) {
            System.err.println("Connect IOE: ");
            ioe.printStackTrace();
        } catch (Exception e) {
            System.err.println("Connect E: ");
            e.printStackTrace();
        }

    }
}
