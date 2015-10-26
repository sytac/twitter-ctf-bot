package com.sytac.twitter_ctf_bot;

import com.mashape.unirest.http.Unirest;
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

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * A twitter Bot which manages the communication with the participants of the Sytac Capture The Flag competition
 *
 * @author Tonino Catapano
 * @author Carlo Sciolla
 * @since 1.0
 */
public class Bot {

    private final static Logger LOGGER = LoggerFactory.getLogger(Bot.class);

    private final Configuration config;

    private Client hosebirdClient;
    private Integer participantNumber = 0;
    private Twitter twitter;
    private ReadingThread reader;

    public Bot(Configuration configuration) {
        this.config = configuration;
    }

    public void run() {
        /** Set up the blocking queue for hbc: size based on expected TPS of your stream */
        BlockingQueue<String> msgQueue = new LinkedBlockingQueue<>(1000);

        /** Declare the host you want to connect to, the endpoint, and authentication (basic auth or oauth) */
        Hosts hosebirdHosts = new HttpHosts(Constants.USERSTREAM_HOST);

        try {
            hosebirdClient = initializeHBC(msgQueue, hosebirdHosts);
            twitter = initializeTwit4j();

            gracefulShutdown();

            hosebirdClient.connect(); // Attempts to establish a connection to the Sytac's user stream.
            reader = new ReadingThread(msgQueue, hosebirdClient, twitter, participantNumber, config);
            new Thread(reader).start(); //Run the Thread that will consume the User-stream

            // TODO: process messages in an infinite loop here..
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            LOGGER.info("Unexpected error encoutered, closing the connection...");
            if (hosebirdClient != null) {
                hosebirdClient.stop();
            }
        } finally {
            // TODO: kill the runnable if still runnning
        }
    }

    /**
     * Register a shutdown hook to make sure resources are properly freed
     */
    private void gracefulShutdown() {
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

    /**
     * Initialize the HoseBird Client (STREAMING-API part)
     *
     * @param msgQueue
     * @param hosebirdHosts
     */
    private Client initializeHBC(BlockingQueue<String> msgQueue, Hosts hosebirdHosts) {
        UserstreamEndpoint userEndpoint = new UserstreamEndpoint();
        userEndpoint.withUser(true); //fetch only the user-related messages

        Authentication hosebirdAuth = new OAuth1(config.getConsumerKey(),
                                                 config.getConsumerSecret(),
                                                 config.getToken(),
                                                 config.getSecret());

        ClientBuilder builder = new ClientBuilder()
                .name(config.getBotName())
                .hosts(hosebirdHosts)
                .authentication(hosebirdAuth)
                .endpoint(userEndpoint)
                .processor(new StringDelimitedProcessor(msgQueue));

        return builder.build();
    }

    /**
     * Initialize the Twitter4j Client instance (REST-API part)
     */
    private Twitter initializeTwit4j() {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(config.getConsumerKey())
                .setOAuthConsumerSecret(config.getConsumerSecret())
                .setOAuthAccessToken(config.getToken())
                .setOAuthAccessTokenSecret(config.getSecret());
        TwitterFactory tf = new TwitterFactory(cb.build());

        return tf.getInstance();
    }
}
