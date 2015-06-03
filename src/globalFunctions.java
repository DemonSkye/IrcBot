//https://github.com/FasterXML/jackson-core/wiki -- Json Parser, or on MVC

import com.fasterxml.jackson.databind.ObjectMapper;
import hirondelle.date4j.DateTime;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.TimeZone;
import java.io.*;
import java.net.InetAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class globalFunctions {
    public static int channelCheck(String channel, String line) {
        int found = line.indexOf(channel);
        if (line.contains(channel)) {
                return found;
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

    public static void doWeather(String userHostName, ircBot ircBot, String channel) {
        if (userHostName == null) {
            writeMsg(ircBot, channel, "Could not resolve hostname: ");
            return;
        }
        String userIpAddress = getIpFromHostName(userHostName);

        Map ipLocation = getIpInfoByIP(userIpAddress, ircBot);
        System.out.println(ipLocation); //diagnostic

        String latitude = "";
        String longitude = "";
        try {
            latitude = ipLocation.get("latitude").toString();
            longitude = ipLocation.get("longitude").toString();
        } catch (NullPointerException npe) {
            npe.printStackTrace();
            writeMsg(ircBot, channel, "Could not get your Lat / Long from GEOIP Provider");
            return;
        }
        String userCity = "";
        String userRegion = "";
        String userCountry = "";
        try {
            userCity = ipLocation.get("city").toString();
            userRegion = ipLocation.get("region_code").toString();
            userCountry = ipLocation.get("country_code").toString();
        } catch (NullPointerException npe) {
            System.err.println("Weahter iplocation.get NPE: ");
            npe.getStackTrace();
        } catch (Exception e) {
            System.err.println("Weahter iplocation.get: ");
            e.printStackTrace();
        }

        System.out.println(userCity);
        System.out.println(userRegion);
        System.out.println(userCountry);

        String currentConditions = "";
        try {
            currentConditions = getWeather(latitude, longitude, ircBot);
        } catch (NullPointerException npe) {
            System.err.println("Weather NullPointerException, getWeather Call: ");
            npe.printStackTrace();
        }

        System.out.println(currentConditions);
        int cw = currentConditions.indexOf("summary=");
        String currentWeather = currentConditions.substring(cw, currentConditions.length());
        cw = currentWeather.indexOf(",");
        currentWeather = currentWeather.substring(8, cw);

        cw = currentConditions.indexOf("temperature=");
        String currentTemp = currentConditions.substring(cw, currentConditions.length());
        cw = currentTemp.indexOf(",");
        currentTemp = currentTemp.substring(12, cw);

        Double tempCelcius = ((Double.parseDouble(currentTemp) - 32) * 5 / 9);
        DecimalFormat df = new DecimalFormat("#.##");

        if ((userRegion != null || !userRegion.equals("")) && userCountry.equals("US")) {
            String userForeCast = "The current conditions for: " + userCity + ", " + userRegion + " are: " + currentTemp + "F / " + (df.format(tempCelcius) + "C, and " + currentWeather);
            writeMsg(ircBot, channel, userForeCast);
            return;
        }
        else{
            String userForeCast = "The current conditions for: " + userCity + " " + userRegion + ", " + userCountry + " are: " + currentTemp + "F / " + (df.format(tempCelcius) + "C, and " + currentWeather);
            writeMsg(ircBot, channel, userForeCast);
            return;
        }
    }

    public static String getIpFromHostName(String userHostName) {
        String userIpAddress = "";
        System.out.println(userHostName);
        userHostName = userHostName.trim();
        try {
            InetAddress userIpAddressObj = InetAddress.getByName(userHostName);
            userIpAddress = userIpAddressObj.toString();
            System.out.println("userIpAddress: " + userIpAddress);
            if (userIpAddress.contains("/")) {
                int findSlash = userIpAddress.indexOf("/");
                userIpAddress = userIpAddress.substring(findSlash + 1, userIpAddress.length());
            }
            //Diagnostic ip address output
            //writeMsg(writer, channel, "IP Address: " + userIpAddress + "\r\n");
        } catch (UnknownHostException uhe) {
            uhe.printStackTrace();
        }
        return userIpAddress;
    }
    public static String getHostByMsg(String line){
        int hostFinder = line.indexOf("@");
        int hostFinder2 = line.indexOf("PRIVMSG");
        return line.substring(hostFinder+1, hostFinder2-1);
    }

    public static Map getIpInfoByIP(String userIpAddress, ircBot ircBot) {
        String ipInfo = "https://freegeoip.net/json/" + userIpAddress;
        InputStream in = ircBot.doHTTPSConnection(ipInfo);
        ObjectMapper mapper = new ObjectMapper();
        try {
            Map<String, Object> ipGeo = mapper.readValue(in, Map.class);
            System.out.println("IP GEO: " + ipGeo);
            return ipGeo;
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return null;
    }

    public static String getWeather(String userLat, String userLong, ircBot ircBot) {
        String weatherGet = "https://api.forecast.io/forecast/" + privateStuff.getApiKey() + "/" + userLat + "," + userLong;
        InputStream in = ircBot.doHTTPSConnection(weatherGet);
        ObjectMapper mapper = new ObjectMapper();
        try {
            Map<String, Object> userWeather = mapper.readValue(in, Map.class);
            return userWeather.get("currently").toString();
        } catch(IOException ioe) { ioe.printStackTrace(); }
        return null;
    }


    //Logscrape will dig through the logs and search for a username as part of the !Seen function
    public static String logScrape(String userName, String channel, ircBot ircBot) {
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
                    if (!line.contains("QUIT")) {
                        lastSaid = line.substring((lastChat + channel.length()), line.length());
                    }
                    line =line.substring(0,19); //Removes all characters after the first 19, which are the timestamp
                    userFoundTime = line;
                }
            }
            br.close();
            if (lastSaid.equals("") && userFoundTime.equals("")) {
                lastSaid = "User has not chatted in this channel since I started keeping logs, note, names are case sensitive (DemonSkye is not the same as Demonskye)";
                return lastSaid;
            }
            /*System.out.println("LastSaid: " + lastSaid);
            System.out.println("LastSaid Time: " + userFoundTime);*/

            writeMsg(ircBot, channel, "The last message sent from that user was: " + lastSaid);
        }catch (IOException ioe){ ioe.printStackTrace(); }

        userFoundTime = "Which was sent: " + compareTime(userFoundTime) + " ago.";

        return userFoundTime;
    }

    public static String compareTime(String lastSeen) {
        DateTime lastOnline = new DateTime(lastSeen);
        DateTime now = DateTime.now(TimeZone.getTimeZone("UTC"));
        //System.out.println("LastOnline time: " + lastOnline.getMilliseconds(TimeZone.getTimeZone("UTC")));
        //System.out.println("Now time: " + now.getMilliseconds(TimeZone.getTimeZone("UTC")));

        long lastSeenInMilli = now.getMilliseconds(TimeZone.getTimeZone("UTC")) - lastOnline.getMilliseconds(TimeZone.getTimeZone("UTC"));
        long MILLIS_IN_SECOND = 1000, SECONDS_IN_MINUTE = 60, MINUTES_IN_HOUR = 60, HOURS_IN_DAY = 24, DAYS_IN_YEAR = 365,
                MILLISECONDS_IN_YEAR = MILLIS_IN_SECOND * SECONDS_IN_MINUTE * MINUTES_IN_HOUR * HOURS_IN_DAY * DAYS_IN_YEAR;

        Integer yearsSinceLast=0, daysSinceLast=0, hoursSinceLast=0, minutesSinceLast=0, secondsSinceLast =0;



        while(lastSeenInMilli >= MILLISECONDS_IN_YEAR){
            yearsSinceLast++;
            lastSeenInMilli-=MILLISECONDS_IN_YEAR;
        }
        //System.out.println("Years Since Last: " + yearsSinceLast);
        while(lastSeenInMilli >= 86400000){
            daysSinceLast++;
            lastSeenInMilli-=86400000;
        }
        //System.out.println("Days Since Last: " + daysSinceLast);
        while(lastSeenInMilli >= 3600000){
            hoursSinceLast++;
            lastSeenInMilli-=3600000;
        }
        //System.out.println("Hours Since Last: " + hoursSinceLast);
        while(lastSeenInMilli >= 60000){
            minutesSinceLast++;
            lastSeenInMilli-=60000;
        }
        //System.out.println("Minutes Since Last: " + minutesSinceLast);
        while(lastSeenInMilli >= 1000){
            secondsSinceLast++;
            lastSeenInMilli-=1000;
        }
        //System.out.println("Seconds Since Last: " + secondsSinceLast);

        lastSeen = "";
        if(yearsSinceLast >=1){lastSeen += yearsSinceLast.toString(); lastSeen+= " year"; lastSeen+=pluralize(yearsSinceLast); lastSeen +=", ";}
        if(daysSinceLast >=1){lastSeen += daysSinceLast.toString(); lastSeen+= " day"; lastSeen+=pluralize(daysSinceLast); lastSeen +=", ";}
        if(hoursSinceLast >=1){lastSeen += hoursSinceLast.toString(); lastSeen+= " hour"; lastSeen+=pluralize(hoursSinceLast); lastSeen +=", ";}
        if(minutesSinceLast >=1){lastSeen += minutesSinceLast.toString(); lastSeen+= " minute"; lastSeen+=pluralize(minutesSinceLast); lastSeen +=", ";}
        lastSeen += secondsSinceLast.toString();
        lastSeen += " seconds";

        return lastSeen;
    }

    public static String pluralize(int number){
        if(number >=2){
            return "s";
        }
        return "";
    }

    public static void writeMsg(ircBot ircBot, String Channel, String Message) {
        ircBot.setNextMessage("PRIVMSG " + Channel + Message);
        System.out.println("PRIVMSG " + Channel + Message);
        ircBot.setNextServerMessage(false);

        try {
            ExecutorService threadPool = Executors.newSingleThreadExecutor();
            threadPool.execute(ircBot);
            threadPool.shutdown();
        } catch (Exception e) {
            System.err.println("Thread Exception: ");
            e.printStackTrace();
        }
    }

    public static void writeServerMsg(ircBot ircBot, String Message) {
        ircBot.setNextMessage(Message);
        System.out.println("SERVER MESSAGE:" + Message);
        ircBot.setNextServerMessage(true);
        try {
            ExecutorService threadPool = Executors.newSingleThreadExecutor();
            threadPool.execute(ircBot);
            threadPool.shutdown();
        } catch (Exception e) {
            System.err.println("Thread Exception: ");
            e.printStackTrace();
        }
    }

}

