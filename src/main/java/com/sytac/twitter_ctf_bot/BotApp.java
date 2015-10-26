package com.sytac.twitter_ctf_bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Application that bootstraps the Capture The Flag Twitter bot
 *
 * @author Carlo Sciolla
 * @since 1.0
 */
public class BotApp {

    private final static Logger LOGGER = LoggerFactory.getLogger(BotApp.class);

    /**
     * Reads the configuration file location from the program arguments and starts the process
     *
     * @param args Should only contain one entry, the configuration file path
     */
    public static void main(String[] args) {
        String configFile = parseArguments(args);
        if(fileExists(configFile)) {
            new Bot().run(configFile);
        } else {
            LOGGER.error("No configuration file found at location: {}", configFile);
            throw new IllegalArgumentException();
        }
    }

    /**
     * Reads the input parameters from the command line
     *
     * @param args The input parameters from the command line
     * @return The provided configuration file path, or a default value if a wrong command line is used
     */
    private static String parseArguments(String[] args) {
        if(args.length == 1) {
            return args[0];
        } else {
            usage();
        }

        return "<invalid command line>";
    }

    /**
     * Prints usage information
     */
    private static void usage() {
        LOGGER.error("Usage:");
        LOGGER.error("\tjava -jar ctf-bot.jar <path-to-config>");
    }

    /**
     * Validates whether a file exists at the given
     *
     * @param path The path to validate
     * @return True if the path exists
     */
    private static boolean fileExists(String path) {
        return new File(path).isFile();
    }

}
