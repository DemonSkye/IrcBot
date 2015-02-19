import java.io.IOException;

public class Commands {
    public static void handleCommands(String command, ircBot ircBot, String channel, boolean isAdmin) {
        if(command.startsWith("!") || command.toLowerCase().contains(":!weather") || command.toLowerCase().contains(":!temp")){
            command = command.substring(1,command.length());
            bangCommands(command, ircBot, channel, isAdmin);
        }
        //else

    }

    public static void bangCommands(String command, ircBot ircBot, String channel, boolean isAdmin) {
        if (command.toLowerCase().startsWith("wiki")) {
            if (command.length() < 6) {
                globalFunctions.writeMsg(ircBot, channel, "The correct usage for this command is !wiki <article>, example: !wiki China");
                return;
            }
            command = command.substring(5, command.length());
            command = command.trim();
            command = command.replace(" ", "_");
            globalFunctions.writeMsg(ircBot, channel, "http://en.wikipedia.org/wiki/" + command);
        }

        if (command.toLowerCase().startsWith("swwiki")) {
            if (command.length() < 6) {
                globalFunctions.writeMsg(ircBot, channel, "The correct usage for this command is !swwiki <article>, example: !swwiki Magic Archer");
                return;
            }
            command = command.substring(6, command.length());
            command = command.trim();
            command = command.replace(" ", "-");

            globalFunctions.writeMsg(ircBot, channel, "http://summonerswarskyarena.info/" + command);
        }


        if (command.toLowerCase().startsWith("seen")) {
            String lastSeen = globalFunctions.logScrape(command, channel, ircBot);
            globalFunctions.writeMsg(ircBot, channel, lastSeen);
        }

        //Forum Link
        if (command.equalsIgnoreCase("forums") || command.equalsIgnoreCase("forum")) {
            globalFunctions.writeMsg(ircBot, channel, "C++ Forums:  http://beginnerscpp.com/forums/");
        }

        //getinfo
        if (command.toLowerCase().startsWith("getinfo")) {
            if (command.length() > 8) {
                command = command.substring(8, command.length());
                command = command.trim();
                System.out.println("Getinfo: " + command);
                globalFunctions.writeServerMsg(ircBot, "whois " + ircBot.getServerHost().substring(1) + " " + command);
            } else {
                globalFunctions.writeMsg(ircBot, channel, "The getInfo command is meant to take a username (Ex: !getinfo DemonSkye)");
            }
        }

        //showInfo
        if (command.toLowerCase().startsWith("showhost") || command.toLowerCase().startsWith("showinfo")) {
            if (isAdmin) {
                if (command.length() > 9) {
                    String userName = command.substring(9, command.length());
                    userName = userName.trim();
                    System.out.println("UserName: " + userName);
                    String userHost = ircBot.getUserHostName(userName);
                    System.out.println("userHost: " + userHost);
                    System.out.println("Bot Command: PRIVMSG" + channel + "Information for user: " + userName + userHost);
                    globalFunctions.writeMsg(ircBot, channel, "Information for user: " + userName + "-- Hostname: " + userHost + " IP Address: "
                            + globalFunctions.getIpFromHostName(userHost));
                } else {
                    globalFunctions.writeMsg(ircBot, channel, "The showInfo command is meant to take a username (Ex: !showInfo DemonSkye)");
                }
            } else {
                globalFunctions.writeMsg(ircBot, channel, "You do not have adequate permissions to use this command.");
            }
        }

        //Weather Command
        if (command.toLowerCase().contains("weather") || command.toLowerCase().contains("temp")) {
            int findTemp = command.lastIndexOf("temp");
            String command2 = command.substring(findTemp, command.length());
            if (command2.length() > 5) {
                command2 = command2.substring(5, command2.length());
                command2 = command2.trim();
                String userHostName = ircBot.getUserHostName(command2);
                globalFunctions.doWeather(userHostName, ircBot, channel);
            } else {
                String userHostName = globalFunctions.getHostByMsg(command);
                globalFunctions.doWeather(userHostName, ircBot, channel);
            }
        }

        //C++ List Implementation
        if (command.equalsIgnoreCase("c++list") || command.equalsIgnoreCase("c++ list")) {
            globalFunctions.writeMsg(ircBot, channel, "C++ Linked List Example implementation:  http://beginnerscpp.com/forums/index.php/topic,47.0.html/");
        }

        //New Tutorials
        if (command.equalsIgnoreCase("tutorials")) {
            globalFunctions.writeMsg(ircBot, channel, "BeginnersC++ New Tutorials: http://beginnerscpp.com/category/new-tutorials/");
        }

        //Donate
        if (command.equalsIgnoreCase("plug")) {
            globalFunctions.writeMsg(ircBot, channel, "Hopefully this was helpful to you, consider unblocking us from adblock, telling your friends and if you have " +
                    "the means, you could consider donating: http://beginnerscpp.com/donate/");
        }

        //File handling
        if (command.toLowerCase().equalsIgnoreCase("file")) {
            globalFunctions.writeMsg(ircBot, channel, "Lesson 13: File I/O --  http://beginnerscpp.com/placeholder-lesson-13-file-input-output/");
        }

        //String
        if (command.toLowerCase().equalsIgnoreCase("string")) {
            globalFunctions.writeMsg(ircBot, channel, "Lesson 12: Working With Strings  -- http://beginnerscpp.com/lesson-12-working-with-strings/");
        }

        //Stringstream
        if (command.toLowerCase().equalsIgnoreCase("ss")) {
            globalFunctions.writeMsg(ircBot, channel, "Lesson 17: Data Validation with StringStream -- http://beginnerscpp.com/placeholder-lesson-17-data-validation-type-checking-with-stringstream/");
        }

        //Debug
        if (command.toLowerCase().equalsIgnoreCase("debug")) {
            globalFunctions.writeMsg(ircBot, channel, "Lesson 8: Hard Debugging--  http://beginnerscpp.com/lesson-8-hard-debugging/");

        }
        if (command.toLowerCase().equalsIgnoreCase("array") || command.toLowerCase().equalsIgnoreCase("arrays")) {
            globalFunctions.writeMsg(ircBot, channel, "http://beginnerscpp.com/placeholder-lesson-14-introduction-to-arrays/");

        }
        //Functions
        if (command.toLowerCase().equalsIgnoreCase("function") || command.toLowerCase().equalsIgnoreCase("functions")) {
            globalFunctions.writeMsg(ircBot, channel, "Lesson 18: Intro To Functions -- http://beginnerscpp.com/placeholder-lesson-18-introduction-to-functions/");
        }

        //Help Command
        if (command.toLowerCase().startsWith("help")) {
            globalFunctions.writeMsg(ircBot, channel, "Help:  Available commands are: " + globalFunctions.getCommandList());
        }

        //Help Command
        if (command.toLowerCase().startsWith("rules")) {
            globalFunctions.writeMsg(ircBot, channel, "Rules:  The rules are:  1. No code in the channel.  All code gets posted to ideone.com or pastebin.com.   All images go to" +
                    "imgur.com or another image host.  2. No swearing at helpers.  3. No being impatient.  FIFO rules in here.");
        }

        //BotProject
        if (command.equalsIgnoreCase("botproj") || command.equalsIgnoreCase("BotProject")) {
            globalFunctions.writeMsg(ircBot, channel, "Bot Project is available here: https://github.com/DemonSkye/IrcBot/tree/master/src");
        }


        //Admin Commands
        if (command.toLowerCase().equalsIgnoreCase("auth")) {
            command = command.replace(" ", "_");
            System.out.println(globalFunctions.timeStamp() + "--AUTH Prog-Bot " + privateStuff.getPassword());
            if (isAdmin) {
                try {
                    ircBot.getWriter().write("AUTH Prog-Bot " + privateStuff.getPassword() + "\r\n");
                    ircBot.getWriter().flush();
                } catch (IOException ioe) {
                    System.err.println("Auth: ");
                    ioe.printStackTrace();
                }
        }
        }

        if (command.toLowerCase().startsWith("kb ")) {
            System.out.println(globalFunctions.timeStamp() + "--KickBan: user");
            if (isAdmin) {
                try {
                    ircBot.getWriter().write("/msg Q TEMPBAN #BeginnersProgramming");
                } catch (IOException ioe) {
                    System.err.println("Auth: ");
                    ioe.printStackTrace();
                }
            }
        }

    }
}
