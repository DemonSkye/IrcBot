/**
 * Created by Damien on 1/15/2015.
 */

public class Commands {
    public static void handleCommands(String command, ircBot ircBot, String channel, boolean isAdmin) throws Exception {
        if(command.startsWith("!") || command.toLowerCase().contains(":!weather") || command.toLowerCase().contains(":!temp")){
            command = command.substring(1,command.length());
            bangCommands(command, ircBot, channel, isAdmin);
        }
        else{

        }

    }

    public static void bangCommands(String command, ircBot ircBot, String channel, boolean isAdmin) throws Exception {
        if (command.toLowerCase().startsWith("wiki")) {
            if (command.length() < 6) {
                globalFunctions.writeMsg(ircBot, channel, "The correct usage for this command is !wiki <article>, example: !wiki China\r\n");
                return;
            }
            command = command.substring(5, command.length());
            command = command.trim();
            command = command.replace(" ", "_");
            globalFunctions.writeMsg(ircBot, channel, "http://en.wikipedia.org/wiki/" + command + "\r\n");
        }



        if (command.toLowerCase().startsWith("seen")) {
            String lastSeen = globalFunctions.logScrape(command, channel, ircBot);
            globalFunctions.writeMsg(ircBot, channel, lastSeen + "\r\n");
        }

        //Forum Link
        if (command.equalsIgnoreCase("forums") || command.equalsIgnoreCase("forum")) {
            globalFunctions.writeMsg(ircBot, channel, "C++ Forums:  http://beginnerscpp.com/forums/ \r\n");
        }

        //Weather Command
        if (command.toLowerCase().contains("weather") || command.toLowerCase().contains("temp")) {
            globalFunctions.doWeather(command, ircBot, channel);
        }

        //C++ List Implementation
        if (command.equalsIgnoreCase("c++list") || command.equalsIgnoreCase("c++ list")) {
            globalFunctions.writeMsg(ircBot, channel, "C++ Linked List Example implementation:  http://beginnerscpp.com/forums/index.php/topic,47.0.html/ \r\n");
        }

        //New Tutorials
        if (command.equalsIgnoreCase("tutorials")) {
            globalFunctions.writeMsg(ircBot, channel, "BeginnersC++ New Tutorials: http://beginnerscpp.com/category/new-tutorials/ \r\n");
        }

        //Donate
        if (command.equalsIgnoreCase("donate")) {
            globalFunctions.writeMsg(ircBot, channel, "Make your donations here: http://beginnerscpp.com/donate/\r\n");
        }

        //File handling
        if (command.toLowerCase().equalsIgnoreCase("file")) {
            globalFunctions.writeMsg(ircBot, channel, "Lesson 13: File I/O --  http://beginnerscpp.com/placeholder-lesson-13-file-input-output/\r\n");
        }

        //String
        if (command.toLowerCase().equalsIgnoreCase("string")) {
            globalFunctions.writeMsg(ircBot, channel, "Lesson 12: Working With Strings  -- http://beginnerscpp.com/lesson-12-working-with-strings/\r\n");
        }

        //Stringstream
        if (command.toLowerCase().equalsIgnoreCase("ss")) {
            globalFunctions.writeMsg(ircBot, channel, "Lesson 17: Data Validation with StringStream -- http://beginnerscpp.com/placeholder-lesson-17-data-validation-type-checking-with-stringstream/\r\n");
        }

        //Debug
        if (command.toLowerCase().equalsIgnoreCase("debug")) {
            globalFunctions.writeMsg(ircBot, channel, "Lesson 8: Hard Debugging--  http://beginnerscpp.com/lesson-8-hard-debugging/ \r\n");

        }
        //Functions
        if (command.toLowerCase().equalsIgnoreCase("function")) {
            globalFunctions.writeMsg(ircBot, channel, "Lesson 18: Intro To Functions -- http://beginnerscpp.com/placeholder-lesson-18-introduction-to-functions/\r\n");
        }

        //Help Command
        if (command.toLowerCase().startsWith("help")) {
            globalFunctions.writeMsg(ircBot, channel, "Help:  Available commands are: " + globalFunctions.getCommandList() + "\r\n");
            System.out.println(globalFunctions.timeStamp() + "--PRIVMSG " + channel + "Help:  Available commands are: " + globalFunctions.getCommandList() + "\r\n");
        }



        //Admin Commands
        if (command.toLowerCase().equalsIgnoreCase("auth")) {
            command = command.replace(" ", "_");
            System.out.println(globalFunctions.timeStamp() + "--AUTH Prog-Bot " + privateStuff.getPassword());
            if(isAdmin) {
                ircBot.getWriter().write("AUTH Prog-Bot " + privateStuff.getPassword() + "\r\n");
                ircBot.getWriter().flush();
            }
        }

        if (command.toLowerCase().startsWith("kb ")) {
            System.out.println(globalFunctions.timeStamp() +"--KickBan: user");
            if(isAdmin) {
                ircBot.getWriter().write("/msg Q TEMPBAN #BeginnersProgramming \r\n");
            }
        }

    }
}
