package com.sytac.twitter_ctf_bot;

import java.io.File;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

import com.sytac.twitter_ctf_bot.client.HosebirdClient;
import com.sytac.twitter_ctf_bot.conf.Prop;

/**
 * Application that bootstraps the Capture The Flag Twitter bot
 *
 * @author Carlo Sciolla
 * @since 1.0
 */
public class BotApp {

	private static final Logger LOGGER = Logger.getLogger(BotApp.class);

    /**
     * Reads the configuration file location from the program arguments and starts the process
     *
     * @param args Should only contain one entry, the configuration file path
     */
    public static void main(String[] args) {
        String configFile = parseArguments(args);
        if (fileExists(configFile)) {
            runBot(configFile);
        } else {
            LOGGER.error("No configuration file found at location: {}");
            throw new IllegalArgumentException();
        }
    }

    private static void runBot(String configFile) {
        Prop configuration = new Prop(configFile);
        /** Set up the blocking queue for hbc: size based on expected TPS of your stream */
        BlockingQueue<String> queue = new LinkedBlockingQueue<>(configuration.QUEUE_BUFFER_SIZE);
        HosebirdClient client = new HosebirdClient(configuration, queue);
        //Twitter twitter = initializeTwit4j(configuration);
        //new RestControllerTh().start();
        new Bot(configuration, client, queue).run();
        

    }

    /**
     * Reads the input parameters from the command line
     *
     * @param args The input parameters from the command line
     * @return The provided configuration file path, or a default value if a wrong command line is used
     */
    private static String parseArguments(String[] args) {
        if (args.length == 1) {
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
