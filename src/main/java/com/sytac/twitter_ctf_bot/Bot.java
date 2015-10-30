package com.sytac.twitter_ctf_bot;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

import com.sytac.twitter_ctf_bot.client.HosebirdClient;
import com.sytac.twitter_ctf_bot.client.TwitterClient;
import com.sytac.twitter_ctf_bot.conf.Prop;
import com.sytac.twitter_ctf_bot.model.ParsedJson;


/**
 * Bot Control class
 * @author Tonino Catapano - tonino.catapano@sytac.io
 * @since 1.0
 */
public class Bot {
	
	
	private static final Logger LOGGER = Logger.getLogger(Bot.class);
	
	
	private final Prop conf;
	private final HosebirdClient stream;
	private final BlockingQueue<String> inMessages;
	private TwitterClient twitter;
	BlockingQueue<String> outMessages;
	
	public Bot(Prop configuration, HosebirdClient stream, BlockingQueue<String> inMessages) {
		this.conf = configuration;
		this.stream = stream;
		this.inMessages = inMessages;
	}

	public void run(){
		Runtime.getRuntime().addShutdownHook(new Thread(){ //catch the shutdown hook
            @Override
            public void run(){
                LOGGER.info("Shutdown hook caught, closing the hosebirdClient");
                stream.stop();
               /* try {
					Unirest.shutdown();
				} catch (IOException e) {
					 LOGGER.error("error while shutting down Unirest Client: ", e);
				}*/
            }
        });		
		try{
			BlockingQueue<ParsedJson> outMessages = new LinkedBlockingQueue<>(conf.QUEUE_BUFFER_SIZE);
			stream.connect();
			twitter = new TwitterClient(conf);
			new ReadingThread(conf, stream, inMessages, outMessages).start();
			process(outMessages);
		}catch(Exception e){
			LOGGER.error(e.getMessage(),e);
			LOGGER.info("Unexpected error encountered, closing the connection...");
			stream.stop();
		}
	}


	
	private void process(BlockingQueue<ParsedJson> messages) throws InterruptedException {
		while(!stream.isDone()) {
			try {
				messages.take().handleMe(conf, twitter);
			} catch (IOException e) {
				LOGGER.error(e.getMessage(),e);
			}
		}
	}


}
