import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.concurrent.Callable;

public class threadHandler {
    public static Callable inputThreadHandler(ircBot ircBot, String channels[]) {
        Callable<String> callable = new Callable<String>() {
            public String call() {
                String str = " ";
                Scanner input = new Scanner(System.in);
                while (true) {
                    str = input.nextLine();
                    if (!str.equals(" ")) {
                        globalFunctions.writeMsg(ircBot, channels[0], " :" + str);
                    }
                    if (str.equals("exit at once!")) {
                        break;
                    }
                }
                return str;
            }
        };
        return callable;
    }

    public static Callable connectionThreadHandler(ircBot ircBot) {
        Callable<String> callable = new Callable<String>() {
            public String call() {
                String line = " ";
                boolean isAdmin = false;
                try {
                    while ((line = ircBot.getReader().readLine()) != null) {
                        String channels[] = {"#BeginnersProgramming"};
                        String channel = globalFunctions.getChannelName(channels, line);
                        channel += " :";
                        //System.out.println("Channel: " + channel );
                        if ((!line.startsWith("PING") && !line.startsWith(":Q!") && !line.contains("NOTICE") && !line.contains("372") &&
                                !line.contains("376") && !line.contains("366")) && !line.contains("312") &&
                                !line.contains("313") && !line.contains("318") && !line.contains("317") && !line.contains("319") &&
                                !line.contains("005") && !line.contains("252") && !line.contains("254") && !line.contains("255") &&
                                !line.contains("375") && !line.contains("221") ||
                                line.contains("PRIVMSG"))
                            System.out.println(line);
                        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("ircLogs.txt", true)));
                        if (!line.toLowerCase().startsWith("ping") && line.contains("PRIVMSG ") && line.contains("!")) {
                            out.println(globalFunctions.timeStamp() + "--" + line);
                            ircBot.setLastSaidTime(globalFunctions.timeStamp());
                        }

                        if (line.toLowerCase().startsWith("ping")) {
                            // We must respond to PINGs to avoid being disconnected.
                            ircBot.getWriter().write("PONG " + line.substring(5) + "\r\n");
                            ircBot.getWriter().flush();
                        }

                        if (line.contains("JOIN") && !line.contains("PRIVMSG")) {
                            String userName = line.substring(1, line.indexOf("!"));
                            String userHostname = line.substring(line.indexOf("@") + 1, line.indexOf(" "));
                            //System.out.println("Username on join: " +userName + "/UserHost on join:" +userHostname);
                            ircBot.setUserHostName(userName.toLowerCase(), userHostname.toLowerCase());
                            if (ircBot.getLastSaidTime() != null) {
                                String idleTime = globalFunctions.compareTime(ircBot.getLastSaidTime());
                                if (idleTime.contains("hours")) {
                                    globalFunctions.writeServerMsg(ircBot, "NOTICE " + userName + " :The channel has been inactive for: " + idleTime
                                            + " You can check the away message by typing !seen DemonSkye");
                                } else {
                                    globalFunctions.writeServerMsg(ircBot, "NOTICE " + userName + " :Welcome to Beginners Programming!");
                                }
                            }
                        }
                        //Returned HostInfo
                        if (line.contains(ircBot.getServerHost() + " 311") && !(line.startsWith("PING"))) {
                            String userName = line.substring(line.indexOf(ircBot.getUserName()), line.length());
                            userName = userName.substring(userName.indexOf(" ") + 1, userName.length());
                            userName = userName.substring(0, userName.indexOf(" "));

                            //System.out.println(line);
                            String hostName = line.substring(line.indexOf(ircBot.getUserName()) + 1, line.length());
                            hostName = hostName.substring(hostName.indexOf(" "), hostName.indexOf("*") - 1);
                            hostName = hostName.substring(hostName.lastIndexOf(" "), hostName.length());
                            ircBot.setUserHostName(userName.toLowerCase(), hostName);
                        }

                        if (line.startsWith(ircBot.getServerHost() + " 353") && !(line.startsWith("PING"))) {
                            line = line.substring(1);
                            line = line.substring(line.indexOf(":"), line.length());
                            String userNames[] = line.split(" ");
                            for (String s : userNames) {
                                //System.out.println("BOT WHOIS:  whois " + ircBot.getServerHost().substring(1, ircBot.getServerHost().length()) + " " + s.substring(1, s.length()));
                                if (s.startsWith("@")) {
                                    globalFunctions.writeServerMsg(ircBot, "whois " + ircBot.getServerHost().substring(1) + " " + s.substring(1, s.length()));
                                } else {
                                    globalFunctions.writeServerMsg(ircBot, "whois " + ircBot.getServerHost().substring(1) + " " + s.substring(0, s.length()));
                                }
                            }
                        }

                        isAdmin = line.startsWith(":DemonSkye!") || line.startsWith(":thearrowflies!") || line.startsWith(":Damien__");

                        int found = globalFunctions.channelCheck(channel, line);
                        if (found >= 0) {
                            String command = line;
                            if (!((line.contains("!Weather") || line.contains("!weather") || line.contains("!temp") || line.contains("!Temp")))) {
                                command = line.substring(found, line.length());
                                command = command.substring(channel.length(), command.length());
                            }
                            Commands.handleCommands(command, ircBot, channel, isAdmin);
                        }
                        out.close();
                    }

                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
                return line;
            }
        };
        return callable;
    }
}
