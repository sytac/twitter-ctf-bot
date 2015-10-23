package com.sytac.twitter_ctf_bot;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Hosts;

import twitter4j.DirectMessage;
import twitter4j.Twitter;
import twitter4j.TwitterException;

public class ReadingThread extends Thread{

	final static Logger LOGGER = LoggerFactory.getLogger(Bot.class);
	final static String WELCOME_MESSAGE_PARTICIPANT = "Welcome to the Capture the flag competition from the Sytac team, have fun!";
	final static String COULDNOT_FOLLOW_MESSAGE = "Seems you were already signed to the Capture the flag competition";
	final static String RIGHT_ANSWER_MESSAGE = "Great!";
	final static String WRONG_ANSWER_MESSAGE = "Too Bad :/";
	final static String WINNER_MESSAGE = "Congratulations, you are the winner of an amazing Parrot AR Drone 2.0!";
	
	final static String TWITTER_DM_ENDPOINT = "https://api.twitter.com/1.1/direct_messages/new.json";
	final static String SYTAC_REST_ENDPOINT = "http://sytac.io/ctf.do";
	final static String FLAG_KEY = "foundFlag";
	final static String PARTIC_ID_KEY = "partecipantId";
	final static String PARTIC_NAME_KEY = "partecipantName";
	
	final static long SYTAC_USER_ID = 194054816;
	
	Hosts _hosebirdHosts;
	BlockingQueue<String> _msgQueue;
	Client _hosebirdClient;
	Twitter _twitter4jClient;
	Integer _partecipantsCount;
	
	public ReadingThread(Hosts hosebirdHosts, BlockingQueue<String> msgQueue, Client hosebirdClient, Twitter twitter4jClient, Integer partecipantsNumber){
		_hosebirdClient = hosebirdClient;
		_msgQueue = msgQueue;
		_hosebirdClient = hosebirdClient;
		_partecipantsCount = partecipantsNumber;
		_twitter4jClient = twitter4jClient;
	}
	
	
	
	/**
	 * The loop that will intercept and handle the incoming messages for Sytac
	 */
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
	 
	
	
	
	 /**The processing routine: handle mentions and DM receiving and answers
	  * 
	  * @param json
	  */
	 private void processMessage(String json){
		LOGGER.debug(json); //SET TO DEBUG!!!!
        ObjectMapper mapper = new ObjectMapper();
		try {
			//Unmarshalling of the JSON
			JsonNode node = mapper.readTree(json);
			JsonNode mention = node.path("entities").path("user_mentions");
			JsonNode mention_text = node.path("text");
			JsonNode direct_msg = node.path("direct_message").path("text");
			String direct_msgStr = direct_msg.getTextValue();
			JsonNode direct_msg_senderId = node.path("direct_message").path("sender").path("id");
			JsonNode name = node.path("direct_message").path("sender").path("screen_name");
			JsonNode event_node = node.path("event");
			JsonNode participant = node.path("user");
			
			if(event_node.isMissingNode() && //if the event node is not present in the message
				!mention.isMissingNode() && //if mention node is present in the message
				mention_text.isValueNode() && //if mention text is present in the message
				mention_text.getTextValue().toLowerCase().contains("#ctf")) //if mention text contains the CTF flag
			{ //CASE OF A MENTION: try to follow the user and add him to the participants	
				LOGGER.info("Received mention: " + mention_text.getTextValue());		
				//follow the participant
				boolean followSuccess = followParticipant(participant.path("id").getLongValue());
				// send him/her a welcome DM or a DM informing he's/she's already in the competition
				dm(participant.path("id").getLongValue(), followSuccess ? WELCOME_MESSAGE_PARTICIPANT : COULDNOT_FOLLOW_MESSAGE); 
				if(followSuccess) _partecipantsCount++;
			}else if(direct_msg.isValueNode() && //if the direct_msg node is present in the message
					direct_msgStr.toLowerCase().startsWith("#ctf") && //if direct_msg contains the CTF flag
					!direct_msg_senderId.isMissingNode() && //if direct_msg_senderId is present in the message
					direct_msg_senderId.getLongValue() != SYTAC_USER_ID) //if the received message is not an echo message (message from Sytac itself) 
			{ //CASE of a DM for the competition
				boolean ok = processAnswerToRemote((direct_msgStr.toLowerCase().split("#ctf")[1]).trim(), name.getTextValue(), direct_msg_senderId.getLongValue());
				dm(participant.path("id").getLongValue(), ok ? RIGHT_ANSWER_MESSAGE : WRONG_ANSWER_MESSAGE); 
			}else{ //OTHER MESSAGES RECEIVED: skip them
				LOGGER.warn("The JSON received isn't a ctf-related message: " + node.toString());
				return;
			}

		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}    
	 }
	 
	 
	 private boolean followParticipant(long idParticipant){
		try {
			_twitter4jClient.createFriendship(idParticipant);
		} catch (TwitterException e) {
			LOGGER.error(e.getMessage(), e);
			return false;
		}
		return true;
	 }
	 
	 /**
	  * Send a private message to the participant 
	  * @param idUser
	  * @param message
	  * @return
	  */
	 private void dm(long idUser, String message){
		 try {
			 DirectMessage msg = _twitter4jClient.sendDirectMessage(idUser, message);
			 LOGGER.info("Sent: " + msg.getText() + " to @" + msg.getRecipientScreenName());
		 } catch (TwitterException e) {
			LOGGER.error("Error during the DM to the partecipant " + idUser + ": TWITTER4J exception ", e);
		 }	 
	 }
	 

	 
	 
	 /**
	  * Send the received answer for the quiz {@value quizNr} to the Sytac webapp for checking
	  * @param flag
	  * @param partecipantName
	  * @param partecipantId
	  * @return
	  */
	 private boolean processAnswerToRemote(String flag, String partecipantName, long partecipantId ){	 
		 /*
		  try {
			 
			HttpResponse<String> a = Unirest.post(SYTAC_REST_ENDPOINT)
			  //.queryString("quizNr", quizNr)
			  .field(FLAG_KEY, flag)
			  .field(PARTIC_ID_KEY, partecipantId)
			  .field(PARTIC_NAME_KEY, partecipantName)
			  .asString();
			  //.asJson();
			if(a.getStatus() == 200) 
				return Boolean.valueOf(a.getBody());
			return false;		
		} catch (UnirestException e) {
			LOGGER.error("Error during the HTTP-POST to the Sytac-webapp", e);
			
			return false;
			
		}*/
		 return false;
	 }
	 
	 
}
