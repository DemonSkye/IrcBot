//https://github.com/FasterXML/jackson-core/wiki -- Json Parser, or on MVC

import com.fasterxml.jackson.databind.ObjectMapper;
import hirondelle.date4j.DateTime;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.TimeZone;
import java.io.*;
import java.net.InetAddress;

public class globalFunctions {
    public static int channelCheck(String chatChannels[], String line) {
        for (String s : chatChannels) {
            int found = line.indexOf(s);
            if (line.contains(s)) {
                return found;
            }
        }
        return -1;
    }

    public static String timeStamp(){
        DateTime now = DateTime.now(TimeZone.getTimeZone("UTC"));
        return now.format("YYYY-MM-DD hh:mm:ss"); //Removes nanoseconds from time;
    }

    public static String getChannelName(String chatChannels[], String line){
        for(String s: chatChannels) {
            if(line.toLowerCase().contains(s.toLowerCase())){
                return s;
            }
        }
        return "";
    }

    public static String getCommandList(){
        return "!seen <username>, !wiki <topic>, !donate, !C++list, !C++ list, !forum, !forums, !Weather, " +
                "!weather, !temp, !Temp, !Tutorials, !file, !string, !ss, !debug, !function";
    }

    public static void doWeather(String line, ircBot ircBot, String channel) throws Exception {
        String userHostName = getHostByMsg(line);
        //System.out.println("UserHostNam: " + userHostName);
        String userIpAddress = null;
        try { //To get the ip address from the hostname
            InetAddress userIpAddressObj = InetAddress.getByName(userHostName);
            userIpAddress = userIpAddressObj.toString();
            System.out.println("userIpAddress: " + userIpAddress);
            if(userIpAddress.contains("/")){
                int findSlash = userIpAddress.indexOf("/");
                userIpAddress = userIpAddress.substring(findSlash+1, userIpAddress.length());
            }
            //Diagnostic ip address output
            //writeMsg(writer, channel, "IP Address: " + userIpAddress + "\r\n");
        }catch(UnknownHostException uhe){ uhe.printStackTrace(); }

        Map ipLocation = getIpInfoByIP(userIpAddress);
        //Assign values returned by function

        //System.out.println(ipLocation); //diagnostic
        String userCity;
        String userState;
        try {
            userCity = ipLocation.get("city").toString();
            userState = ipLocation.get("region").toString();
        }catch(NullPointerException npe){ npe.getStackTrace();
            writeMsg(ircBot, channel, "Could not get the weather for: IP Address: " + userIpAddress + "\r\n");
            return;
        }
        userState = Abbr.getStateAbbr(userState); //Change state to 2-letter abbreviation
        String userCountry = ipLocation.get("country").toString();

        //Debug Values
        System.out.println(userCity);
        System.out.println(userState);
        System.out.println(userCountry);


        //
        String currentConditions = "";
        if (userCity != null && userState != null) {
            currentConditions = getWeather(userCity, userState);
        }
        if (userState == null && userCity != null) {
            currentConditions = getWeather(userCity, userCountry);
        }
        if (userCity == null && userState == null) {
            writeMsg(ircBot, channel, "Could not get weather for your region");
            return;
        }

        int cw = currentConditions.indexOf("weather=");
        String currentWeather = currentConditions.substring(cw, currentConditions.length());
        cw = currentWeather.indexOf(",");
        currentWeather = currentWeather.substring(8, cw);

        cw = currentConditions.indexOf("temp_f");
        String currentTemp = currentConditions.substring(cw, currentConditions.length());
        cw = currentTemp.indexOf(",");
        currentTemp = currentTemp.substring(7, cw);

        cw = currentConditions.indexOf("temp_c");
        String currentTempC = currentConditions.substring(cw, currentConditions.length());
        cw = currentTempC.indexOf(",");
        currentTempC = currentTempC.substring(7, cw);
        if(userState == null || userState.equals("")) {
            String userForeCast = "The current conditions for: " + userCity + ", " + userCountry + " are: " + currentTemp + "F / " + currentTempC + "C, and " + currentWeather;
            writeMsg(ircBot, channel, userForeCast + "\r\n");
        }
        else{
            String userForeCast = "The current conditions for: " + userCity + ", " + userState + " are: " + currentTemp + "F / " + currentTempC + "C, and " + currentWeather;
            writeMsg(ircBot, channel, userForeCast + "\r\n");
        }
    }

    public static String getHostByMsg(String line){
        int hostFinder = line.indexOf("@");
        int hostFinder2 = line.indexOf("PRIVMSG");
        return line.substring(hostFinder+1, hostFinder2-1);
    }

    public static Map getIpInfoByIP(String userIpAddress){
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpget = new HttpGet("http://ipinfo.io/" + userIpAddress + "/json");
        CloseableHttpResponse response;
        InputStream in=null;
        try{
            response = httpclient.execute(httpget);
            in = response.getEntity().getContent();
        }catch(Exception e){ e.printStackTrace(); }

        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(in, Map.class);
        }catch(IOException ioe){ioe.printStackTrace(); }

        //Debug values
        return null;
    }

    public static String getWeather(String userCity, String userState){
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpget = new HttpGet("http://api.wunderground.com/api/" + privateStuff.getApiKey() + "/conditions/q/" + userState + "/" + userCity + ".json");
        CloseableHttpResponse response;
        InputStream in=null;
        try {
            response = httpclient.execute(httpget);
            in = response.getEntity().getContent();
        } catch (Exception e) {
            System.out.println("Test");
            e.printStackTrace();
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            Map<String, Object> userWeather = mapper.readValue(in, Map.class);

            //To fix later
            return userWeather.get("current_observation").toString();
        } catch(IOException ioe) { ioe.printStackTrace(); }
        return null;
    }


    //Logscrape will dig through the logs and search for a username as part of the !Seen function
    public static String logScrape(String userName, String channel, ircBot ircBot) {
        System.out.println("First Check In Function:" + userName);
        if (userName.length() < 6) {
            return "The seen command requires a username after it, example: !seen DemonSkye";
        }
        userName = userName.substring(5, userName.length());
        userName = userName.trim();
        String logUserName = ":" + userName + "!";
        String lastSaid="";
        String userFoundTime = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader("ircLogs.txt"));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains(logUserName)){
                    int lastChat = line.indexOf(channel);
                    if(line.contains("QUIT")!= true) {
                        lastSaid = line.substring((lastChat + channel.length()), line.length());
                    }
                    line =line.substring(0,19); //Removes all characters after the first 19, which are the timestamp
                    userFoundTime = line;
                }
            }
            br.close();
            if (lastSaid == "" && userFoundTime == "") {
                lastSaid = "User has not chatted in this channel since I started keeping logs";
                return lastSaid;
            }
            /*System.out.println("LastSaid: " + lastSaid);
            System.out.println("LastSaid Time: " + userFoundTime);*/

            writeMsg(ircBot, channel, "The last message sent from that user was: " + lastSaid + " -- \r\n");
        }catch (IOException ioe){ ioe.printStackTrace(); }

        userFoundTime = compareTime(userFoundTime);

        return userFoundTime;
    }

    public static String compareTime(String lastSeen) {
        DateTime lastOnline = new DateTime(lastSeen);
        DateTime now = DateTime.now(TimeZone.getTimeZone("UTC"));
        System.out.println("LastOnline time: " + lastOnline.getMilliseconds(TimeZone.getTimeZone("UTC")));
        System.out.println("Now time: " + now.getMilliseconds(TimeZone.getTimeZone("UTC")));

        long lastSeenInMilli = now.getMilliseconds(TimeZone.getTimeZone("UTC")) - lastOnline.getMilliseconds(TimeZone.getTimeZone("UTC"));
        long MILLIS_IN_SECOND = 1000, SECONDS_IN_MINUTE = 60, MINUTES_IN_HOUR = 60, HOURS_IN_DAY = 24, DAYS_IN_YEAR = 365,
                MILLISECONDS_IN_YEAR = MILLIS_IN_SECOND * SECONDS_IN_MINUTE * MINUTES_IN_HOUR * HOURS_IN_DAY * DAYS_IN_YEAR;

        Integer yearsSinceLast=0, daysSinceLast=0, hoursSinceLast=0, minutesSinceLast=0, secondsSinceLast =0;



        while(lastSeenInMilli >= MILLISECONDS_IN_YEAR){
            yearsSinceLast++;
            lastSeenInMilli-=MILLISECONDS_IN_YEAR;
        }
        System.out.println("Years Since Last: " + yearsSinceLast);
        while(lastSeenInMilli >= 86400000){
            daysSinceLast++;
            lastSeenInMilli-=86400000;
        }
        System.out.println("Days Since Last: " + daysSinceLast);
        while(lastSeenInMilli >= 3600000){
            hoursSinceLast++;
            lastSeenInMilli-=3600000;
        }
        System.out.println("Hours Since Last: " + hoursSinceLast);
        while(lastSeenInMilli >= 60000){
            minutesSinceLast++;
            lastSeenInMilli-=60000;
        }
        System.out.println("Minutes Since Last: " + minutesSinceLast);
        while(lastSeenInMilli >= 1000){
            secondsSinceLast++;
            lastSeenInMilli-=1000;
        }
        System.out.println("Seconds Since Last: " + secondsSinceLast);

        lastSeen ="Which was sent: ";
        if(yearsSinceLast >=1){lastSeen += yearsSinceLast.toString(); lastSeen+= " year"; lastSeen+=pluralize(yearsSinceLast); lastSeen +=", ";}
        if(daysSinceLast >=1){lastSeen += daysSinceLast.toString(); lastSeen+= " day"; lastSeen+=pluralize(daysSinceLast); lastSeen +=", ";}
        if(hoursSinceLast >=1){lastSeen += hoursSinceLast.toString(); lastSeen+= " hour"; lastSeen+=pluralize(hoursSinceLast); lastSeen +=", ";}
        if(minutesSinceLast >=1){lastSeen += minutesSinceLast.toString(); lastSeen+= " minute"; lastSeen+=pluralize(minutesSinceLast); lastSeen +=", ";}
        lastSeen += secondsSinceLast.toString();
        lastSeen += " seconds";
        lastSeen += " ago.";

        return lastSeen;
    }

    public static String pluralize(int number){
        if(number >=2){
            return "s";
        }
        return "";
    }

    public static void writeMsg(ircBot ircBot, String Channel, String Message) {
        try {
            System.out.println("Bot Command: PRIVMSG " + Channel + Message);
            ircBot.getWriter().write("PRIVMSG " + Channel + Message);
            ircBot.getWriter().flush();
        }catch(IOException ioe){ioe.printStackTrace(); }
    }
}

