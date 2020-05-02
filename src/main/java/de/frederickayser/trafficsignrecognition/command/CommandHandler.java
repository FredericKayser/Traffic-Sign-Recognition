package de.frederickayser.trafficsignrecognition.command;

import de.frederickayser.trafficsignrecognition.TrafficSignRecognition;
import de.frederickayser.trafficsignrecognition.console.MessageBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * by Frederic on 01.04.20(14:53)
 */
public class CommandHandler extends Thread {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandHandler.class);

    private static CommandHandler commandHandler;
    private final Map<String, Command> commandMap;

    private CommandHandler() {
        commandMap = new HashMap<String, Command>();
    }

    public static CommandHandler getInstance() {
        if(commandHandler == null)
            commandHandler = new CommandHandler();
        return commandHandler;
    }

    public void registerCommand(String key, Command command) {
        commandMap.put(key, command);
    }

    public void run() {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        String line = null;
        while (true) {
            try {
                if (!((line = bufferedReader.readLine()) != null)) break;

                String key;
                if(line.contains(" ")) {
                    key = line.split(" ")[0];
                } else {
                    key = line;
                }
                System.out.println("> " + line);
                if(commandMap.containsKey(key)) {
                    String[] arguments;
                    if(line.contains(" ")) {
                        arguments = line.split(" ");
                    } else {
                        arguments = new String[0];
                    }
                    commandMap.get(key).execute(arguments);
                } else {
                    MessageBuilder.send(LOGGER, MessageBuilder.MessageType.ERROR, "Could not find command '" + key + "'.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
