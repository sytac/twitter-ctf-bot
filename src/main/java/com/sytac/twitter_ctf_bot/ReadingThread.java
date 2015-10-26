package com.sytac.twitter_ctf_bot;

import com.twitter.hbc.core.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.Twitter;

import java.util.concurrent.BlockingQueue;

public class ReadingThread implements Runnable {

	final static Logger LOGGER = LoggerFactory.getLogger(ReadingThread.class);
	
	/** Instances variables initialized in the constructor **/
	private final BlockingQueue<String> _msgQueue;
	private final Client _hosebirdClient;

	//private final Integer _partecipantsCount;
	
	public ReadingThread(BlockingQueue<String> msgQueue, Client hosebirdClient,
			Twitter twitter4jClient, Integer partecipantsNumber) {

		_hosebirdClient = hosebirdClient;
		_msgQueue = msgQueue;
		
		//_partecipantsCount = partecipantsNumber;
	}
	

	
	/**
	 * The loop that will intercept and handle the incoming messages for Sytac
	 */
	public void run(){
		try {
			while (!_hosebirdClient.isDone()) {
				  String msg = _msgQueue.take();
				  new Processor().processMessage(msg);
			}
			_hosebirdClient.stop();
		}catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	 }
}
