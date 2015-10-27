package com.sytac.twitter_ctf_bot;

import com.twitter.hbc.core.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.Twitter;

import java.util.concurrent.BlockingQueue;

public class ReadingThread implements Runnable {

	final static Logger LOGGER = LoggerFactory.getLogger(ReadingThread.class);
	
	/** Instances variables initialized in the constructor **/
	private final BlockingQueue<String> queue;
	private final Client stream;
	private final Processor processor;

	public ReadingThread(BlockingQueue<String> msgQueue, Client hosebirdClient,
			Twitter twitter4jClient, Configuration configuration) {
		stream = hosebirdClient;
		queue = msgQueue;
		processor = new Processor(twitter4jClient, configuration);
	}

	/**
	 * The loop that will intercept and handle the incoming messages for Sytac
	 */
	public void run(){
		try {
			while (!stream.isDone()) {
				  String msg = queue.take();
				  processor.processMessage(msg);
			}
			stream.stop();
		}catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	 }
}
