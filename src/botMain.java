import java.io.*;
import java.net.*;

public class botMain {
    public static void main(String[] args) {
        try {
            // The server to connect to and our details.
            String server = "irc.quakenet.org";
            String channels[] = {"#BeginnersProgramming"};
            String chatChannels[] = new String[channels.length];
            for (int i = 0; i < channels.length; i++) {
                chatChannels[i] = channels[i].concat(" :");
            }


            ircBot ircBot = new ircBot();
            ircBot.setSocket((new Socket(server, 6667)));
            ircBot.setWriter((new BufferedWriter(new OutputStreamWriter(ircBot.getSocket().getOutputStream()))));
            ircBot.setReader((new BufferedReader(new InputStreamReader(ircBot.getSocket().getInputStream()))));
            ircBot.setServer(server);


            String line;
            boolean isAdmin = false;
            Connect.Connection(ircBot, ircBot.getWriter(), channels);

            try {
                // Keep reading lines from the server.
                while ((line = ircBot.getReader().readLine()) != null) {
                    /*if (!line.startsWith("PING") || !line.startsWith(":Q!")) {
                        System.out.println(line);
                    }*/
                    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("ircLogs.txt", true)));
                    if (!line.toLowerCase().startsWith("ping") && line.contains("PRIVMSG ") && line.contains("!")) {
                        out.println(globalFunctions.timeStamp() + "--" + line);
                    }

                    if (line.toLowerCase().startsWith("ping")) {
                        // We must respond to PINGs to avoid being disconnected.
                        ircBot.getWriter().write("PONG " + line.substring(5) + "\r\n");
                        ircBot.getWriter().flush();
                    }

                    //Returned HostInfo
                    if (line.contains("311") && line.startsWith(ircBot.getServerHost()) && !(line.startsWith("PING"))) {
                        String userName = line.substring(line.indexOf(ircBot.getUserName()), line.length());
                        userName = userName.substring(userName.indexOf(" ") + 1, userName.length());
                        userName = userName.substring(0, userName.indexOf(" "));


                        String hostName = line.substring(line.indexOf(ircBot.getUserName()) + 1, line.length());
                        hostName = hostName.substring(hostName.indexOf(" "), hostName.indexOf("*") - 1);
                        hostName = hostName.substring(hostName.lastIndexOf(" "), hostName.length());
                        ircBot.setUserHostName(userName, hostName);
                    }

                    if (line.contains("353") && line.startsWith(ircBot.getServerHost()) && !(line.startsWith("PING"))) {
                        line=line.substring(1,line.length());
                        line=line.substring(line.indexOf(":"), line.length());
                        String userNames[] = line.split(" ");
                        String userNameList = "";
                        for(String s:userNames){
                            s = s.substring(1, s.length());
                            if (!s.equals("Q") && s.equals(ircBot.getUserName())) {
                                userNameList += s + ",";
                            }
                        }
                        userNameList = userNameList.substring(0, userNameList.length() - 1); //Removes extra comma from above

                        globalFunctions.writeServerMsg(ircBot, "whois " + ircBot.getServerHost() + " " + userNameList);
                    }

                    if (line.startsWith(":DemonSkye!") || line.startsWith(":thearrowflies!")) {
                        isAdmin = true;
                    }

                    int found = globalFunctions.channelCheck(chatChannels, line);
                    if (found >= 0) {
                        String command = line;
                        String channelName = globalFunctions.getChannelName(chatChannels, line);
                        if (!((line.contains("!Weather") || line.contains("!weather") || line.contains("!temp") || line.contains("!Temp")))) {
                            command = line.substring(found, line.length());
                            command = command.substring(channelName.length(), command.length());
                        }
                        Commands.handleCommands(command, ircBot, channelName, isAdmin);
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
