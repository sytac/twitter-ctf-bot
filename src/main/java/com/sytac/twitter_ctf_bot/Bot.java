package com.sytac.twitter_ctf_bot;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mashape.unirest.http.Unirest;
import com.sytac.twitter_ctf_bot.client.HosebirdClient;
import com.sytac.twitter_ctf_bot.client.TwitterClient;
import com.sytac.twitter_ctf_bot.conf.Prop;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.Hosts;
import com.twitter.hbc.core.HttpHosts;


/**
 * Bot Control class
 * @author Tonino Catapano - tonino.catapano@sytac.io
 * @since 1.0
 */
public class Bot {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(Bot.class);


	
	public void run(String path){
		Runtime.getRuntime().addShutdownHook(new Thread(){ //catch the shutdown hook
            @Override
            public void run(){
                LOGGER.info("Shutdown hook catched, closing the hosebirdClient");
                HosebirdClient.getInstance().getClient().stop();
                try {
					Unirest.shutdown();
				} catch (IOException e) {
					 LOGGER.error("error while shutting down Unirest Client: ", e);
				}
            }
        });		
		try{
			Prop p = Prop.getInstance();
			p.initPropFile(path);			
			BlockingQueue<String> msgQueue = new LinkedBlockingQueue<String>(1000);
			Hosts hosebirdHosts = new HttpHosts(Constants.USERSTREAM_HOST);
			HosebirdClient.getInstance().initializeHBC(p.consumerKey, p.consumerSecret, p.token, p.secret, msgQueue, hosebirdHosts);
			HosebirdClient.getInstance().getClient().connect();
			TwitterClient.getInstance().initializeTwit4j(p.consumerKey, p.consumerSecret, p.token, p.secret);
			new ReadingThread(msgQueue).start();
		}catch(Exception e){
			LOGGER.error(e.getMessage(),e);
			LOGGER.info("Unexpected error encoutered, closing the connection...");
			HosebirdClient.getInstance().getClient().stop();
		}
	}
	
	
}
