package com.sytac.twitter_ctf_bot;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sytac.twitter_ctf_bot.client.HosebirdClient;
import com.sytac.twitter_ctf_bot.client.MongoDBClient;
import com.sytac.twitter_ctf_bot.client.TwitterClient;
import com.sytac.twitter_ctf_bot.conf.Prop;
import com.sytac.twitter_ctf_bot.controller.RestControllerTh;
import com.sytac.twitter_ctf_bot.model.ParsedJson;

/**
 * Bot Control class
 * @author Tonino Catapano - tonino.catapano@sytac.io
 * @since 1.0
 */
public class Bot implements Closeable {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Bot.class);

	private final Prop conf;
	private final HosebirdClient stream;
	private final BlockingQueue<String> inMessages;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private TwitterClient twitter;
    private MongoDBClient mongoDBClient;

	public Bot(Prop configuration, HosebirdClient stream, BlockingQueue<String> inMessages) {
		this.conf = configuration;
		this.stream = stream;
		this.inMessages = inMessages;
	}

	public void run(){
		try {
			BlockingQueue<ParsedJson> outMessages = new LinkedBlockingQueue<>(conf.QUEUE_BUFFER_SIZE.intValue());
			stream.connect();
			twitter = new TwitterClient(conf);
			mongoDBClient = new MongoDBClient("localhost", 27017);
			new ReadingThread(conf, stream, inMessages, outMessages).start();
			new RestControllerTh(mongoDBClient).start();
            executor.submit(() -> {
                try {
                    this.process(outMessages);
                } catch (InterruptedException e) {
                    LOGGER.error("Something iffy happened: {}", e.getMessage());
                }
            });
		} catch(Exception e){
			LOGGER.error(e.getMessage(),e);
			LOGGER.info("Unexpected error encountered, closing the connection...");
			stream.stop();
		}
	}

	private void process(BlockingQueue<ParsedJson> messages) throws InterruptedException {
		while(!stream.isDone()) {
			try {
				messages.take().handleMe(conf, twitter, mongoDBClient);
			} catch (IOException e) {
				LOGGER.error(e.getMessage(),e);
			}
		}
	}

    public void close(){
        executor.shutdownNow();
    }


}
