package com.sytac.twitter_ctf_bot;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.Hosts;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.UserstreamEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;





public class Bot {
	private static Client hosebirdClient;
	private final static Logger LOGGER = LoggerFactory.getLogger(Bot.class);
	private static final Properties CONF_FILE = new Properties();
	
	public Bot(){}
	
	private void run(String path){
		Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run(){
                LOGGER.info("Shutdown hook catched, closing the hosebirdClient");
                hosebirdClient.stop();
            }
        });
		
		try{
			InputStream in = Files.newInputStream(Paths.get(path));
			if (path == null || path.isEmpty() || in == null){
				LOGGER.error("Please specificate a valid path for the properties file in the first argument");
				return;
			}
			CONF_FILE.load(in);
			in.close();
			String consumerKey = CONF_FILE.getProperty("consumerKey");
			String consumerSecret = CONF_FILE.getProperty("consumerSecret");
			String token = CONF_FILE.getProperty("token");
			String secret = CONF_FILE.getProperty("secret");
			
			
			/** Set up your blocking queues: Be sure to size these properly based on expected TPS of your stream */
			BlockingQueue<String> msgQueue = new LinkedBlockingQueue<String>(100000);
			/** Declare the host you want to connect to, the endpoint, and authentication (basic auth or oauth) */
			Hosts hosebirdHosts = new HttpHosts(Constants.USERSTREAM_HOST);
			UserstreamEndpoint userEndpoint = new UserstreamEndpoint();
			userEndpoint.withUser(true); //fetch only the user-related messages
			
			// These secrets should be read from a config file
			Authentication hosebirdAuth = new OAuth1(consumerKey, consumerSecret, token, secret);
			ClientBuilder builder = new ClientBuilder()
					  .name("Hosebird-Client-01")                              // optional: mainly for the logs
					  .hosts(hosebirdHosts)
					  .authentication(hosebirdAuth)
					  .endpoint(userEndpoint)
					  .processor(new StringDelimitedProcessor(msgQueue));
					 // .eventMessageQueue(eventQueue);                          // optional: use this if you want to process client events
	
			hosebirdClient = builder.build();
			// Attempts to establish a connection.
			hosebirdClient.connect();
			//Run the Thread that will consume the User-stream
			new ReadingThread(hosebirdHosts, msgQueue, hosebirdClient).start();
		}catch(Exception e){
			LOGGER.error(e.getMessage(),e);
			LOGGER.info("Closing the connection...");
			hosebirdClient.stop();
		}
	}
	
	public static void main(String[] args) {
		new Bot().run(args[0]);
	}		
}
