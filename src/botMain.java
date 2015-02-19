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
/*                    if ((!line.startsWith("PING") && !line.startsWith(":Q!") && !line.contains("NOTICE") && !line.contains("372") &&
                            !line.contains("376") && !line.contains("366")) && !line.contains("312") &&
                            !line.contains("313") && !line.contains("318") && !line.contains("317") && !line.contains("319") ||
                            line.contains("PRIVMSG")) {*/
                        System.out.println(line);
                    // }
                    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("ircLogs.txt", true)));
                    if (!line.toLowerCase().startsWith("ping") && line.contains("PRIVMSG ") && line.contains("!"))
                        out.println(globalFunctions.timeStamp() + "--" + line);

                    if (line.toLowerCase().startsWith("ping")) {
                        // We must respond to PINGs to avoid being disconnected.
                        ircBot.getWriter().write("PONG " + line.substring(5) + "\r\n");
                        ircBot.getWriter().flush();
                    }

                    if (line.contains("JOIN") && !line.contains("PRIVMSG")) {
                        line = line.substring(1, line.length());
                        String userName = line.substring(0, line.indexOf("!"));
                        String userHostname = line.substring(line.indexOf("@") + 1, line.indexOf(" "));
                        ircBot.setUserHostName(userName, userHostname);
                    }

                    //Returned HostInfo
                    if (line.contains(ircBot.getServerHost() + " 311") && !(line.startsWith("PING"))) {
                        String userName = line.substring(line.indexOf(ircBot.getUserName()), line.length());
                        userName = userName.substring(userName.indexOf(" ") + 1, userName.length());
                        userName = userName.substring(0, userName.indexOf(" "));

                        System.out.println(line);
                        String hostName = line.substring(line.indexOf(ircBot.getUserName()) + 1, line.length());
                        hostName = hostName.substring(hostName.indexOf(" "), hostName.indexOf("*") - 1);
                        hostName = hostName.substring(hostName.lastIndexOf(" "), hostName.length());
                        ircBot.setUserHostName(userName.toLowerCase(), hostName);
                    }

                    if (line.contains("353") && line.startsWith(ircBot.getServerHost()) && !(line.startsWith("PING"))) {
                        line = line.substring(1);
                        line=line.substring(line.indexOf(":"), line.length());
                        String userNames[] = line.split(" ");
                        for (String s : userNames) {
                            System.out.println("BOT WHOIS:  whois " + ircBot.getServerHost().substring(1, ircBot.getServerHost().length()) + " " + s.substring(1, s.length()));
                            globalFunctions.writeServerMsg(ircBot, "whois " + ircBot.getServerHost().substring(1) + " " + s.substring(1, s.length()));
                        }
                    }

                    if (line.startsWith(":DemonSkye!") || line.startsWith(":thearrowflies!")) {
                        isAdmin = true;
                    } else {
                        isAdmin = false;
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
                System.out.println("IOE - Botmain: " + e.getMessage());
                e.printStackTrace();
            }
        } catch (Exception e) {
            System.out.println("Exception E botmain - : " + e.getMessage());
            e.printStackTrace();
        }


    }
}
