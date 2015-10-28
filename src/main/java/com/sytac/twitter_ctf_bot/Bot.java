package com.sytac.twitter_ctf_bot;

import com.sytac.twitter_ctf_bot.client.HosebirdClient;
import com.sytac.twitter_ctf_bot.client.TwitterClient;
import com.sytac.twitter_ctf_bot.conf.Prop;
import com.sytac.twitter_ctf_bot.model.ParsedJson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * Bot Control class
 * @author Tonino Catapano - tonino.catapano@sytac.io
 * @since 1.0
 */
public class Bot {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(Bot.class);

	private final Prop configuration;
	private final HosebirdClient stream;
	
	public Bot(Prop configuration, HosebirdClient stream) {
		this.configuration = configuration;
		this.stream = stream;
	}

	public void run(){
		try{
			BlockingQueue<String> incoming = new LinkedBlockingQueue<>(1000);
			BlockingQueue<ParsedJson> messages = new LinkedBlockingQueue<>(1000);
			stream.connect();
			TwitterClient twitter = new TwitterClient(configuration);
			new ReadingThread(configuration, stream, incoming, messages).start();
			Processor processor = new Processor(configuration, twitter);
			process(messages, processor);
		}catch(Exception e){
			LOGGER.error(e.getMessage(),e);
			LOGGER.info("Unexpected error encountered, closing the connection...");
			stream.stop();
		}
	}

	private void process(BlockingQueue<ParsedJson> messages, Processor processor) throws InterruptedException {
		while(true) {
			ParsedJson message = messages.take();
			processor.processMessage(message);
		}
	}


}
