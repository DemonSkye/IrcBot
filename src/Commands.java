/**
 * Created by Damien on 1/15/2015.
 */
import java.io.*;
public class Commands {
    public static void handleCommands(String command, BufferedWriter writer, String channel, boolean isAdmin)throws Exception{
        if(command.startsWith("!") || command.toLowerCase().contains(":!weather") || command.toLowerCase().contains(":!temp")){
            command = command.substring(1,command.length());
            bangCommands(command, writer, channel, isAdmin);
        }
        else{

        }

    }

    public static void bangCommands(String command, BufferedWriter writer, String channel, boolean isAdmin)throws Exception{
        if (command.toLowerCase().startsWith("wiki")) {
            command = command.substring(5, command.length());
            command = command.trim();
            command = command.replace(" ", "_");
            globalFunctions.writeMsg(writer, channel, "http://en.wikipedia.org/wiki/" + command + "\r\n");
            writer.flush();
        }



        if (command.toLowerCase().startsWith("seen")) {
            String lastSeen = globalFunctions.logScrape(command, channel, writer);
            globalFunctions.writeMsg(writer, channel, lastSeen + "\r\n");
        }

        //Forum Link
        if (command.equalsIgnoreCase("forums") || command.equalsIgnoreCase("forum")) {
            globalFunctions.writeMsg(writer, channel, "C++ Forums:  http://beginnerscpp.com/forums/ \r\n");
        }

        //Weather Command
        if (command.toLowerCase().contains("weather") || command.toLowerCase().contains("temp")) {
            globalFunctions.doWeather(command, writer, channel);
        }

        //C++ List Implementation
        if (command.equalsIgnoreCase("c++list") || command.equalsIgnoreCase("c++ list")) {
            globalFunctions.writeMsg(writer, channel, "C++ Linked List Example implementation:  http://beginnerscpp.com/forums/index.php/topic,47.0.html/ \r\n");
        }

        //New Tutorials
        if (command.equalsIgnoreCase("tutorials")) {
            globalFunctions.writeMsg(writer, channel, "BeginnersC++ New Tutorials: http://beginnerscpp.com/category/new-tutorials/ \r\n");
        }

        //Donate
        if (command.equalsIgnoreCase("donate")) {
            globalFunctions.writeMsg(writer, channel, "Make your donations here: http://beginnerscpp.com/donate/\r\n");
        }

        //File handling
        if (command.toLowerCase().equalsIgnoreCase("file")) {
            globalFunctions.writeMsg(writer, channel, "Lesson 13: File I/O --  http://beginnerscpp.com/placeholder-lesson-13-file-input-output/\r\n");
        }

        //String
        if (command.toLowerCase().equalsIgnoreCase("string")) {
            globalFunctions.writeMsg(writer, channel, "Lesson 12: Working With Strings  -- http://beginnerscpp.com/lesson-12-working-with-strings/\r\n");
            writer.flush();
        }

        //Stringstream
        if (command.toLowerCase().equalsIgnoreCase("ss")) {
            globalFunctions.writeMsg(writer, channel, "Lesson 17: Data Validation with StringStream -- http://beginnerscpp.com/placeholder-lesson-17-data-validation-type-checking-with-stringstream/\r\n");
            writer.flush();
        }

        //Debug
        if (command.toLowerCase().equalsIgnoreCase("debug")) {
            globalFunctions.writeMsg(writer, channel, "Lesson 13: File I/O --  http://beginnerscpp.com/lesson-8-hard-debugging/ \r\n");
            writer.flush();
        }
        //Functions
        if (command.toLowerCase().equalsIgnoreCase("function")) {
            System.out.println(globalFunctions.timeStamp() + "--PRIVMSG " + channel + "http://beginnerscpp.com/placeholder-lesson-18-introduction-to-functions/");
            writer.write("PRIVMSG " + channel + "Lesson 18: Intro To Functions -- http://beginnerscpp.com/placeholder-lesson-18-introduction-to-functions/\r\n");
            writer.flush();
        }
        //Help Command

        if (command.toLowerCase().startsWith("help")) {
            command.trim();
            writer.write("PRIVMSG " + channel + "Help:  Available commands are: " + globalFunctions.getCommandList() + "\r\n");
            System.out.println(globalFunctions.timeStamp() + "--PRIVMSG " + channel + "Help:  Available commands are: " + globalFunctions.getCommandList() + "\r\n");
            writer.flush();
        }



        //Admin Commands
        if (command.toLowerCase().equalsIgnoreCase("auth")) {
            command = command.replace(" ", "_");
            System.out.println(globalFunctions.timeStamp() + "--AUTH Prog-Bot " + privateStuff.getPassword());
            if(isAdmin) {
                writer.write("AUTH Prog-Bot " + privateStuff.getPassword() + "\r\n");
            }
            writer.flush();
        }

        if (command.toLowerCase().startsWith("kb ")) {
            System.out.println(globalFunctions.timeStamp() +"--KickBan: user");
            if(isAdmin) {
                writer.write("/msg Q TEMPBAN #BeginnersProgramming \r\n");
            }
            writer.flush();
        }
    }
}
