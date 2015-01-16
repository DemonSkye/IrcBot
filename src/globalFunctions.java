/**
 * Created by Damien on 1/15/2015.
 */

import hirondelle.date4j.DateTime;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.TimeZone;
import java.io.*;

public class globalFunctions {
    public static int channelCheck(String chatChannels[], String line) {
        for (int i = 0; i < chatChannels.length; i++) {
            int found = line.indexOf(chatChannels[i]);
            if (line.indexOf(chatChannels[i]) >= 0) {
                return found;
            }
        }
        return -1;
    }

    public static String getPassword(){
        return "<notTheRealPass>";
    }

    public static String getChannelName(String chatChannels[], String line){
        for(int i=0; i<chatChannels.length; i++){
            if(line.toLowerCase().contains(chatChannels[i].toLowerCase())){
                return chatChannels[i];
            }
        }
        return "";
    }

    public static String compareTime(String lastSeen){
        String seen = "-- " + lastSeen.toString()+ " ";
        DateTime now = DateTime.now(TimeZone.getTimeZone("UTC"));
        //System.out.println("Horrible Line of Code: " + now.minus(lastSeen.getYear(), lastSeen.getMonth(), lastSeen.getDay(),
                //lastSeen.getHour(), lastSeen.getMinute(), lastSeen.getSecond(), 0, DateTime.DayOverflow.FirstDay).toString());
        return seen;
    }

    public static String timeStamp(){
        DateTime now = DateTime.now(TimeZone.getTimeZone("UTC"));
        String currentTimeUtc = now.format("YYYY-MM-DD hh:mm:ss"); //Removes nanoseconds from time
        return currentTimeUtc;
    }


    //Logscrape will dig through the logs and search for a username as part of the !Seen function
    public static String logScrape(String userName){
        String lastSeen = "Never";
        String userFound = "";
        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("ircLogs.txt", true)));
            BufferedReader br = new BufferedReader(new FileReader("ircLogs.txt"));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains(userName)){
                    line =line.substring(0,19); //Removes all characters after the first 19, which are the timestamp
                    System.out.println("DateTime pre-function: " + line);
                    userFound = line;
                }
            }
            br.close();
        }catch (IOException ioe){ ioe.printStackTrace(); }

        lastSeen = compareTime(userFound);

        return lastSeen;
    }
}
