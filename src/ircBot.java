/**
 * Created by Damien on 1/14/2015.
 */

import java.io.*;
import java.net.*;

public class ircBot {
    public static void main(String[] args) {
        try {
            // The server to connect to and our details.
            String server = "irc.quakenet.org";
            String channels[] = {"#BeginnersProgramming", "#testing1234"};
            String chatChannels[] = new String[channels.length];
            for (int i = 0; i < channels.length; i++) {
                chatChannels[i] = channels[i].concat(" :");
            }


            // Connect directly to the IRC server.
            Socket socket = new Socket(server, 6667);
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));

            String line;
            boolean isAdmin = false;
            Connect.Connect(reader, writer, channels);

            try {
                // Keep reading lines from the server.
                while ((line = reader.readLine()) != null) {
                    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("ircLogs.txt", true)));
                    if(line.toLowerCase().startsWith("ping") != true){
                        out.println(globalFunctions.timeStamp() + "--" + line);
                    }

                    if (line.toLowerCase().startsWith("ping")) {
                        // We must respond to PINGs to avoid being disconnected.
                        writer.write("PONG " + line.substring(5) + "\r\n");
                        writer.flush();
                    }

                    if (line.startsWith(":DemonSkye!") || line.startsWith(":thearrowflies!")) {
                        isAdmin = true;
                    }
                    int found = globalFunctions.channelCheck(chatChannels, line);
                    if (found >= 0) {
                        String command = line;
                        String channelName = globalFunctions.getChannelName(chatChannels, line);
                        if(((line.contains("!Weather") || line.contains("!weather") || line.contains("!temp") || line.contains("!Temp")))!=true) {
                            command = line.substring(found, line.length());
                            command = command.substring(channelName.length(), command.length());
                        }
                        Commands.handleCommands(command, writer, channelName, isAdmin);
                    }
                    out.close();
                }
            } catch (IOException e) {
                System.out.println("Error: " + e.getMessage());
                e.printStackTrace();
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
