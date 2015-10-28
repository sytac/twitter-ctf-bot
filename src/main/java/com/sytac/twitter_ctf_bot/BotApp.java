package com.sytac.twitter_ctf_bot;

import com.mashape.unirest.http.Unirest;
import com.sytac.twitter_ctf_bot.client.HosebirdClient;
import com.sytac.twitter_ctf_bot.conf.Prop;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.Hosts;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.UserstreamEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

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
        if (fileExists(configFile)) {
            runBot(configFile);
        } else {
            LOGGER.error("No configuration file found at location: {}", configFile);
            throw new IllegalArgumentException();
        }
    }

    private static void runBot(String configFile) {
        Prop configuration = new Prop(configFile);
        /** Set up the blocking queue for hbc: size based on expected TPS of your stream */
        BlockingQueue<String> queue = new LinkedBlockingQueue<>(1000);
        HosebirdClient client = new HosebirdClient(configuration, queue);
        new Bot(configuration, client).run();

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
