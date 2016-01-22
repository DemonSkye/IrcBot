import java.util.Scanner;
import java.util.concurrent.Callable;

/**
 * Created by Damien on 1/17/2016.
 */
public class InputThread{
    public static Callable inputThreadHandler(){
        Callable<String> callable = new Callable<String>(){
            public String call(){
                Scanner input = new Scanner(System.in);
                String str = input.nextLine();
                return str;
            }
        };
        return callable;
    }
}

/*

Thread inputThread = new Thread(new Runnable() {
    public void run(){
        while(!Thread.interrupted() ){
            Scanner input = new Scanner(System.in);

        }
    }
});*/
