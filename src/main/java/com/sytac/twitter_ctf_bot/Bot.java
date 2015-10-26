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
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Bot {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(Bot.class);
	private static final Properties CONF_FILE = new Properties();
	
	private Client hosebirdClient;
	private Integer participantNumber = 0;
	private Twitter twitter4jClient;
	
	public void run(String path){
		//ShutDownHook to catch the SIGING when terminating the process.
		Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run(){
                LOGGER.info("Shutdown hook catched, closing the hosebirdClient");
                LOGGER.info("Number of participants registered for this session: " + participantNumber);
                hosebirdClient.stop();
                try {
					Unirest.shutdown();
				} catch (IOException e) {
					 LOGGER.error("error while shutting down Unirest Client: ", e);
				}
            }
        });	
		
		loadPropFile(path);	//load the properties file
		try{
			//get the keys and tokens from the CONF_FILE
			String consumerKey = CONF_FILE.getProperty("consumerKey");
			String consumerSecret = CONF_FILE.getProperty("consumerSecret");
			String token = CONF_FILE.getProperty("token");
			String secret = CONF_FILE.getProperty("secret");
			
			/** Set up the blocking queue for hbc: size based on expected TPS of your stream */
			BlockingQueue<String> msgQueue = new LinkedBlockingQueue<String>(1000);
			/** Declare the host you want to connect to, the endpoint, and authentication (basic auth or oauth) */
			Hosts hosebirdHosts = new HttpHosts(Constants.USERSTREAM_HOST);

			initializeHBC(consumerKey, consumerSecret, token, secret, msgQueue, hosebirdHosts);
			initializeTwit4j(consumerKey, consumerSecret, token, secret);
			
			hosebirdClient.connect();// Attempts to establish a connection to the Sytac's user stream.
			new ReadingThread(msgQueue, hosebirdClient, twitter4jClient, participantNumber, CONF_FILE).start(); //Run the Thread that will consume the User-stream
		}catch(Exception e){
			LOGGER.error(e.getMessage(),e);
			LOGGER.info("Unexpected error encoutered, closing the connection...");
			hosebirdClient.stop();
		}
	}
	
	/**
	 * Initialize the HoseBird Client (STREAMING-API part)
	 * @param consumerKey
	 * @param consumerSecret
	 * @param token
	 * @param secret
	 * @param msgQueue
	 * @param hosebirdHosts
	 */
	private void initializeHBC(String consumerKey,String consumerSecret,String token,String secret,BlockingQueue<String> msgQueue, Hosts hosebirdHosts){		
		UserstreamEndpoint userEndpoint = new UserstreamEndpoint();
		userEndpoint.withUser(true); //fetch only the user-related messages	
		
		Authentication hosebirdAuth = new OAuth1(consumerKey, consumerSecret, token, secret);
		ClientBuilder builder = new ClientBuilder()
				  .name("Hosebird-Client-01")                              // optional: mainly for the logs
				  .hosts(hosebirdHosts)
				  .authentication(hosebirdAuth)
				  .endpoint(userEndpoint)
				  .processor(new StringDelimitedProcessor(msgQueue));
				 // .eventMessageQueue(eventQueue);                          // optional: use this if you want to process client events

		hosebirdClient = builder.build();
	}
	
	/**
	 * Initialize the Twitter4j Client instance (REST-API part)
	 * @param consumerKey
	 * @param consumerSecret
	 * @param token
	 * @param secret
	 */
	private void initializeTwit4j(String consumerKey, String consumerSecret,String token, String secret){
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
		  .setOAuthConsumerKey(consumerKey)
		  .setOAuthConsumerSecret(consumerSecret)
		  .setOAuthAccessToken(token)
		  .setOAuthAccessTokenSecret(secret);
		TwitterFactory tf = new TwitterFactory(cb.build());		
		twitter4jClient = tf.getInstance();
	}
	
	
	/**
	 * Load the properties file
	 * @param path
	 */
	private void loadPropFile(String path){
		try {
			InputStream in = Files.newInputStream(Paths.get(path));
			if (path == null || path.isEmpty() || in == null){
				LOGGER.error("Please specificate a valid path for the properties file in the first argument");
				return;
			}
			CONF_FILE.load(in);
			in.close();
		} catch(IOException e) {
			LOGGER.error("Error while reading the properties file: "+ path, e);
			return;
		}
	}
}
