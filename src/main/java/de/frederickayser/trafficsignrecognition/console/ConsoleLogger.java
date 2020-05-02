package de.frederickayser.trafficsignrecognition.console;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ConsoleLogger {


    public static PrintStream out;
    public static PrintStream err;

    public static void log() {
        out = System.out;
        err = System.err;
        try {

            Date date = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss");
            String dateString = simpleDateFormat.format(date);
            File file = new File("logs/");
            file.mkdirs();
            file = new File("logs/" + dateString + ".log");
            if(!file.exists()) {
                file.createNewFile();
            }
            PrintStream printStream = new PrintStream("logs/" + dateString + ".log");
            System.setOut(printStream);
            System.setErr(printStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
