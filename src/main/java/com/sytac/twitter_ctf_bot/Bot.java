package com.sytac.twitter_ctf_bot;

import com.mashape.unirest.http.Unirest;
import com.sytac.twitter_ctf_bot.client.HosebirdClient;
import com.sytac.twitter_ctf_bot.client.TwitterClient;
import com.sytac.twitter_ctf_bot.conf.Prop;
import com.sytac.twitter_ctf_bot.model.ParsedJson;


import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;


/**
 * Bot Control class
 * @author Tonino Catapano - tonino.catapano@sytac.io
 * @since 1.0
 */
public class Bot {
	
	
	private static final Logger LOGGER = Logger.getLogger(Bot.class);
	
	
	private final Prop configuration;
	private final HosebirdClient stream;
	
	public Bot(Prop configuration, HosebirdClient stream) {
		this.configuration = configuration;
		this.stream = stream;
	}

	public void run(){
		Runtime.getRuntime().addShutdownHook(new Thread(){ //catch the shutdown hook
            @Override
            public void run(){
                LOGGER.info("Shutdown hook caught, closing the hosebirdClient");
                stream.stop();
                try {
					Unirest.shutdown();
				} catch (IOException e) {
					 LOGGER.error("error while shutting down Unirest Client: ", e);
				}
            }
        });		
		try{
			BlockingQueue<String> inMessages = new LinkedBlockingQueue<>(1000);
			BlockingQueue<ParsedJson> outMessages = new LinkedBlockingQueue<>(1000);
			stream.connect();
			TwitterClient twitter = new TwitterClient(configuration);
			new ReadingThread(configuration, stream, inMessages, outMessages).start();
			Processor processor = new Processor(configuration, twitter);
			process(outMessages, processor);
		}catch(Exception e){
			LOGGER.error(e.getMessage(),e);
			LOGGER.info("Unexpected error encountered, closing the connection...");
			stream.stop();
		}
	}

	private void process(BlockingQueue<ParsedJson> messages, Processor processor) throws InterruptedException {
		while(!stream.isDone()) {
			ParsedJson message = messages.take();
			processor.processMessage(message);
		}
	}


}
