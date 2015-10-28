package com.sytac.twitter_ctf_bot;

import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sytac.twitter_ctf_bot.client.HosebirdClient;
import com.twitter.hbc.core.Client;
/**
 * The reading theard which consume the twitter user stream
 * @author Tonino Catapano - tonino.catapano@sytac.io
 *
 */
public class ReadingThread extends Thread{

	final static Logger LOGGER = LoggerFactory.getLogger(ReadingThread.class);

	private final BlockingQueue<String> _msgQueue;
	private final Client _hosebirdClient;
	
	
	public ReadingThread(BlockingQueue<String> msgQueue) {
		_hosebirdClient = HosebirdClient.getInstance().getClient();
		_msgQueue = msgQueue;
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
