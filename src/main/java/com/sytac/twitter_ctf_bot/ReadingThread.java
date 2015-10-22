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
	final static String WELCOME_MESSAGE_PARTICIPANT = "Welcome to the Capture the flag competition from the Sytac team, have fun!";
	final static String RIGHT_ANSWER_MESSAGE = "Great!";
	final static String WRONG_ANSWER_MESSAGE = "Too Bad :/";
	
	
	Hosts _hosebirdHosts;
	BlockingQueue<String> _msgQueue;
	Client _hosebirdClient;
	Integer _partecipantsCount;
	
	public ReadingThread(Hosts hosebirdHosts, BlockingQueue<String> msgQueue, Client hosebirdClient, Integer partecipantsNumber){
		_hosebirdClient = hosebirdClient;
		_msgQueue = msgQueue;
		_hosebirdClient = hosebirdClient;
		_partecipantsCount = partecipantsNumber;
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
			JsonNode mention_text = node.path("text");
			JsonNode direct_message = node.path("direct_message").path("text");
			if(mention.isValueNode() && mention_text.isValueNode()){ //case of mention
				_partecipantsCount++;
				LOGGER.info("Received message: "+ mention_text.getTextValue());
				JsonNode participant = node.path("user");
				followParticipant(participant);
				DM(participant.path("id").getTextValue(), WELCOME_MESSAGE_PARTICIPANT);
			}else if(direct_message.isValueNode()){
				//HANDLE DM
			}else{
				LOGGER.warn("The JSON received isn't a well formed DM");
				return;
			}

			//DO SOMETHING WITH THE RECEIVED MESSAGE
	        System.out.println(direct_message.getTextValue());
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}    
	 }
	 
	 
	 private void followParticipant(JsonNode participant){
		String idParticipant = participant.path("id").getTextValue();
		
	 }
	 
	 private boolean DM(String idUser, String message){
		 return false;
	 }
	 
	 private boolean processAnswerToQuiz(String quizNr, String answer){	 
		 //HTTP CALL TO A REMOTE REST SERVICE
		 return false;
	 }
	 
	 
}
