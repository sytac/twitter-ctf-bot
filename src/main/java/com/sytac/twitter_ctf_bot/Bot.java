package com.sytac.twitter_ctf_bot;

import com.mashape.unirest.http.Unirest;
import com.twitter.hbc.core.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.Twitter;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

/**
 * A twitter Bot which manages the communication with the participants of the Sytac Capture The Flag competition. Reacts
 * to mentions and direct messages to manage:
 *
 * - subscriptions
 * - answers checks
 *
 * @author Tonino Catapano
 * @author Carlo Sciolla
 * @since 1.0
 */
public class Bot {

    private final static Logger LOGGER = LoggerFactory.getLogger(Bot.class);

    private final Configuration config;
    private final BlockingQueue<String> queue;

    private final Client hosebirdClient;
    private Integer participantNumber = 0;
    private final Twitter twitter;

    public Bot(Configuration configuration, Twitter twitterClient, Client streamClient, BlockingQueue<String> queue) {
        this.config = configuration;
        this.hosebirdClient = streamClient;
        this.twitter = twitterClient;
        this.queue = queue;

        installShutdownHook();
    }

    public void run() {
        try {
            hosebirdClient.connect(); // Attempts to establish a connection to the Sytac's user stream.
            ReadingThread reader = new ReadingThread(queue, hosebirdClient, twitter, participantNumber, config);
            new Thread(reader).start(); //Run the Thread that will consume the User-stream

            // TODO: process messages in an infinite loop here..
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            LOGGER.info("Unexpected error encoutered, closing the connection...");
            if (hosebirdClient != null) {
                hosebirdClient.stop();
            }
        } finally {
            // TODO: kill the runnable if still running
        }
    }

    /**
     * Register a shutdown hook to make sure resources are properly freed
     */
    private void installShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                LOGGER.info("Shutdown hook caught, closing the hosebirdClient");
                LOGGER.info("Number of participants registered for this session: " + participantNumber);
                hosebirdClient.stop();
                try {
                    Unirest.shutdown();
                } catch (IOException e) {
                    LOGGER.error("error while shutting down Unirest Client: ", e);
                }
            }
        });
    }


}
