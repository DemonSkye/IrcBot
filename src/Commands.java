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
            System.out.println(globalFunctions.timeStamp() + "--PRIVMSG " + channel + "http://en.wikipedia.org/wiki/" + command);
            writer.write("PRIVMSG " + channel + "http://en.wikipedia.org/wiki/" + command + "\r\n");
            writer.flush();
        }



        if (command.toLowerCase().startsWith("seen")) {
            String lastSeen = globalFunctions.logScrape(command);
            System.out.println("PRIVMSG " + channel + " Last seen in channel: " + lastSeen +"\r\n");
            writer.write("PRIVMSG " + channel + lastSeen +"\r\n");
            writer.flush();
        }

        //Forum Link
        if (command.equalsIgnoreCase("forums") || command.equalsIgnoreCase("forum")) {
            System.out.println(globalFunctions.timeStamp()  + "--PRIVMSG " + channel + "http://beginnerscpp.com/forums/");
            writer.write("PRIVMSG " + channel + "C++ Forums:  http://beginnerscpp.com/forums/ \r\n");
            writer.flush();
        }
        if (command.toLowerCase().contains("weather") || command.toLowerCase().contains("temp")) {
            globalFunctions.doWeather(command, writer, channel);
            writer.flush();
        }

        //C++ List Implementation
        if (command.equalsIgnoreCase("c++list") || command.equalsIgnoreCase("c++ list")) {
            System.out.println(globalFunctions.timeStamp()  + "--PRIVMSG " + channel + "http://beginnerscpp.com/forums/index.php/topic,47.0.html/");
            writer.write("PRIVMSG " + channel + "C++ Linked List Example implementation:  http://beginnerscpp.com/forums/index.php/topic,47.0.html/ \r\n");
            writer.flush();
        }

        //New Tutorials
        if (command.equalsIgnoreCase("tutorials")) {
            System.out.println(globalFunctions.timeStamp() + "--PRIVMSG " + channel + "http://beginnerscpp.com/category/new-tutorials/");
            writer.write("PRIVMSG " + channel + "BeginnersC++ New Tutorials: http://beginnerscpp.com/category/new-tutorials/\r\n");
            writer.flush();
        }

        //Donate
        if (command.equalsIgnoreCase("donate")) {
            System.out.println(globalFunctions.timeStamp() + "--PRIVMSG " + channel + "http://beginnerscpp.com/donate/");
            writer.write("PRIVMSG " + channel + "Make your donations here: http://beginnerscpp.com/donate/\r\n");
            writer.flush();
        }

        //File handling
        if (command.toLowerCase().equalsIgnoreCase("file")) {
            System.out.println(globalFunctions.timeStamp() + "--PRIVMSG " + channel + "http://beginnerscpp.com/placeholder-lesson-13-file-input-output/");
            writer.write("PRIVMSG " + channel + "Lesson 13: File I/O --  http://beginnerscpp.com/placeholder-lesson-13-file-input-output/\r\n");
            writer.flush();
        }

        //String
        if (command.toLowerCase().equalsIgnoreCase("string")) {
            System.out.println(globalFunctions.timeStamp() + "--PRIVMSG " + channel + "http://beginnerscpp.com/lesson-12-working-with-strings/");
            writer.write("PRIVMSG " + channel + "Lesson 12: Working With Strings  -- http://beginnerscpp.com/lesson-12-working-with-strings/\r\n");
            writer.flush();
        }

        //Stringstream
        if (command.toLowerCase().equalsIgnoreCase("ss")) {
            System.out.println(globalFunctions.timeStamp() + "--PRIVMSG " + channel + "http://beginnerscpp.com/placeholder-lesson-17-data-validation-type-checking-with-stringstream/");
            writer.write("PRIVMSG " + channel + "Lesson 17: Data Validation with StringStream -- http://beginnerscpp.com/placeholder-lesson-17-data-validation-type-checking-with-stringstream/\r\n");
            writer.flush();
        }
        //Functions
        if (command.toLowerCase().equalsIgnoreCase("function")) {
            System.out.println(globalFunctions.timeStamp() + "--PRIVMSG " + channel + "http://beginnerscpp.com/placeholder-lesson-18-introduction-to-functions/");
            writer.write("PRIVMSG " + channel + "Lesson 18: Intro To Functions -- http://beginnerscpp.com/placeholder-lesson-18-introduction-to-functions/\r\n");
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
