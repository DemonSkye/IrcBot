/**
 * Created by Damien on 1/15/2015.
 */

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

    public static String getPassword(){
        return "unreal1234";
    }

    public static String getChannelName(String chatChannels[], String line){
        for(String s: chatChannels) {
            if(line.toLowerCase().contains(s.toLowerCase())){
                return s;
            }
        }
        return "";
    }

    public static void doWeather(String line, BufferedWriter writer, String channel) throws Exception{
        System.out.println(line);
        String userHostName = getHostByMsg(line);
        //System.out.println(userHostName);
        String userIpAddress = null;
        try { //To get the ip address from the hostname
            InetAddress userIpAddressObj = InetAddress.getByName(userHostName);
            userIpAddress = userIpAddressObj.toString();
            if(userIpAddress.contains("/")){
                int findSlash = userIpAddress.indexOf("/");
                userIpAddress = userIpAddress.substring(findSlash+1, userIpAddress.length());
            }
            writer.write("PRIVMSG " + channel + "IP Address: " + userIpAddress + "\r\n");
        }catch(UnknownHostException uhe){ uhe.printStackTrace(); }
         catch(IOException ioe){ioe.printStackTrace(); }
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpget = new HttpGet("http://ipinfo.io/" + userIpAddress + "/json");
        CloseableHttpResponse response;
        InputStream in=null;
        try{
            response = httpclient.execute(httpget);
            in = response.getEntity().getContent();
        }catch(Exception e){ e.printStackTrace(); }

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> ipLocation = mapper.readValue(in, Map.class);
        System.out.println(ipLocation);

    }

    public static String getHostByMsg(String line){
        int hostFinder = line.indexOf("@");
        int hostFinder2 = line.indexOf("PRIVMSG");
        return line.substring(hostFinder+1, hostFinder2-1);
    }


    /*public static JSONObject inToJSON(InputStream in){
        try {
            BufferedReader streamReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            StringBuilder responseStrBuilder = new StringBuilder();
            String inputStr;
            while ((inputStr = streamReader.readLine()) != null)
                responseStrBuilder.append(inputStr);
           return new JSONObject(responseStrBuilder.toString());
        }catch(UnsupportedEncodingException uee){uee.printStackTrace();}
        catch(IOException ioe){ioe.printStackTrace();}
        catch(JSONException je){je.printStackTrace();}

        return new JSONObject();
    }*/


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
    public static String compareTime(String lastSeen){
        String seen = "-- " + lastSeen.toString()+ " ";
        DateTime now = DateTime.now(TimeZone.getTimeZone("UTC"));
        //System.out.println("Horrible Line of Code: " + now.minus(lastSeen.getYear(), lastSeen.getMonth(), lastSeen.getDay(),
        //lastSeen.getHour(), lastSeen.getMinute(), lastSeen.getSecond(), 0, DateTime.DayOverflow.FirstDay).toString());
        return seen;
    }



}

