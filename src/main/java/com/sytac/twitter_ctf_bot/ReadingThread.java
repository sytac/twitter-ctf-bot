package com.sytac.twitter_ctf_bot;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Hosts;

public class ReadingThread extends Thread{

	final static Logger LOGGER = LoggerFactory.getLogger(Bot.class);
	static int message_count = 0;
	
	
	Hosts _hosebirdHosts;
	BlockingQueue<String> _msgQueue;
	Client _hosebirdClient;

	public ReadingThread(Hosts hosebirdHosts, BlockingQueue<String> msgQueue, Client hosebirdClient){
		_hosebirdClient = hosebirdClient;
		_msgQueue = msgQueue;
		_hosebirdClient = hosebirdClient;
	}
	
	 public void run(){
		try {
			while (!_hosebirdClient.isDone()) {
				  String msg = _msgQueue.take();
				  processMessage(msg);
			}
			_hosebirdClient.stop();
		}catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	 }
	 
	 
	 private void processMessage(String json){
		//print the raw json
		LOGGER.info(json); //SET TO DEBUG!!!!
		//System.out.println(msg);
        ObjectMapper mapper = new ObjectMapper();
        // use the ObjectMapper to read the json string and create a tree
        JsonNode node;
		try {
			//Marshalling of the JSON
			node = mapper.readTree(json);
			JsonNode mention = node.path("entities").path("user_mentions");
			JsonNode text_mention = node.path("text");
			JsonNode direct_message = node.path("direct_message").path("text");
			if(!direct_message.isValueNode()){
				LOGGER.warn("The JSON received isn't a well formed DM");
				return;
			}
			//DO SOMETHING WITH THE RECEIVED MESSAGE
	        System.out.println(direct_message.getTextValue());
	        message_count++;
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}    
	 }
}
