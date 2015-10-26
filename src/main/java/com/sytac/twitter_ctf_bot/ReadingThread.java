package com.sytac.twitter_ctf_bot;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Hosts;

import twitter4j.DirectMessage;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

public class ReadingThread extends Thread{

	final static Logger LOGGER = LoggerFactory.getLogger(Bot.class);
	
	/** User related parameters **/
	static String WELCOME_PARTICIPANT_MESSAGE;
	static String WELCOME_NO_FOLLOW_MESSAGE; 
	static String COULDNOT_FOLLOW_MESSAGE; 
	static String RIGHT_ANSWER_MESSAGE;
	static String WRONG_ANSWER_MESSAGE;
	static String WINNER_MESSAGE;
	static String BAD_MESSAGE;
	
	static String FLAG_KEYWORD;
	static long SYTAC_USER_ID;
	
	/** Endpoint-related parameters **/
	static String TWITTER_DM_ENDPOINT;
	static String SYTAC_REST_ENDPOINT;
	static String FLAG_KEY;
	static String PARTIC_ID_KEY;
	static String PARTIC_NAME_KEY;
	

	/** Instances variables **/
	Hosts _hosebirdHosts;
	BlockingQueue<String> _msgQueue;
	Client _hosebirdClient;
	Twitter _twitter4jClient;
	Integer _partecipantsCount;
	Properties _propFile;
	
	public ReadingThread(Hosts hosebirdHosts, BlockingQueue<String> msgQueue, Client hosebirdClient,
			Twitter twitter4jClient, Integer partecipantsNumber, Properties propFile) {
		
		_hosebirdClient = hosebirdClient;
		_msgQueue = msgQueue;
		_hosebirdClient = hosebirdClient;
		_partecipantsCount = partecipantsNumber;
		_twitter4jClient = twitter4jClient;
		_propFile = propFile;
		loadConf();
		try{
			SYTAC_USER_ID = Long.valueOf(_propFile.getProperty("ownerUserId"));
		}catch(Exception e){
			LOGGER.error("error during the extraction of the ownerUserId field from the PROP_FILE");
		}
	}
	
	private void loadConf(){
		WELCOME_PARTICIPANT_MESSAGE  = _propFile.getProperty("WELCOME_PARTICIPANT_MESSAGE");
		COULDNOT_FOLLOW_MESSAGE   = _propFile.getProperty("COULDNOT_FOLLOW_MESSAGE");
		RIGHT_ANSWER_MESSAGE  = _propFile.getProperty("RIGHT_ANSWER_MESSAGE");
		WRONG_ANSWER_MESSAGE  = _propFile.getProperty("WRONG_ANSWER_MESSAGE");
		WINNER_MESSAGE  = _propFile.getProperty("WINNER_MESSAGE");
		BAD_MESSAGE  = _propFile.getProperty("BAD_MESSAGE");
		WELCOME_NO_FOLLOW_MESSAGE = _propFile.getProperty("WELCOME_NO_FOLLOW_MESSAGE");
		
		TWITTER_DM_ENDPOINT  = _propFile.getProperty("TWITTER_DM_ENDPOINT");
		SYTAC_REST_ENDPOINT  = _propFile.getProperty("SYTAC_REST_ENDPOINT");
		FLAG_KEY  = _propFile.getProperty("FLAG_KEY");
		PARTIC_ID_KEY  = _propFile.getProperty("PARTIC_ID_KEY");
		PARTIC_NAME_KEY  = _propFile.getProperty("PARTIC_NAME_KEY");
		FLAG_KEYWORD = _propFile.getProperty("FLAG_KEYWORD");
		try{
			SYTAC_USER_ID  = Long.valueOf(_propFile.getProperty("SYTAC_USER_ID"));
		}catch(NumberFormatException e){
			LOGGER.error("unable to cast the SYTAC_USER_ID field to a long number;");
		}
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
	 
	
	
	
	 /**
	  * The processing routine: handle mentions and DM receiving and answers
	  * @param json
	  */
	 private void processMessage(String json){
		LOGGER.info(json); //SET TO DEBUG!!!!
        ObjectMapper mapper = new ObjectMapper();
		try {
			//Unmarshalling of the JSON and getting all the possible useful nodes
			JsonNode node = mapper.readTree(json);
			
			/** MENTION NODES**/
			JsonNode mention = node.path("entities").path("user_mentions"); //present in case of a mention
			JsonNode mention_text = node.path("text"); //present in case of a mention
			JsonNode participant = node.path("user"); //present in case of a mention
			JsonNode participant_name = node.path("user").path("screen_name"); //present in case of a mention
			JsonNode participant_id = node.path("user").path("id"); //present in case of a mention
			
			/** DM NODES**/
			JsonNode direct_msg = node.path("direct_message").path("text"); //present in case of a DM
			JsonNode direct_msg_senderId = node.path("direct_message").path("sender").path("id"); //present in case of a DM
			JsonNode direct_msg_name = node.path("direct_message").path("sender").path("screen_name"); //present in case of a DM
			String direct_msgStr = direct_msg.getTextValue();//present in case of a DM
			
			/** OTHER NODES**/
			JsonNode event_node = node.path("event"); // present in case of a event message
			JsonNode event_source_id = node.path("source").path("id"); //id of the new follower
			
			JsonNode delete_node = node.path("delete"); // present in case of a delete message (aka unfollow) (skipped)
			/**
			 * CASE OF A MENTION: try to 
			 * 1) follow the user 
			 * 2) add him to the participants
			 * 3) send him a welcome to the competition DM
			 */
			if(event_node.isMissingNode() && //if the event node is not present in the message
				delete_node.isMissingNode() && //if the delete node is not present in the message
				!mention.isMissingNode() && //if mention node is present in the message
				mention_text.isValueNode() && //if mention text is a value node
				mention_text.getTextValue().toLowerCase().contains(FLAG_KEYWORD) && //if mention text contains the "#ctf" flag
				SYTAC_USER_ID != participant_id.getLongValue()) //if I am not the mention maker
			{
				LOGGER.info("Received mention: " + mention_text.getTextValue());		
				//follow the participant
				boolean followSuccess = followParticipant(participant.path("id").getLongValue());
				// send him/her a welcome DM or a DM informing he's/she's already in the competition
				dm(participant_name.getTextValue(), participant.path("id").getLongValue(), followSuccess ? WELCOME_PARTICIPANT_MESSAGE : COULDNOT_FOLLOW_MESSAGE); 
				if(followSuccess) _partecipantsCount++;
				LOGGER.info("New Participant: " + participant_name.getTextValue());	
			
			}
			/**
			 * CASE of a DM for the competition: try to
			 * 1) fetch the given answer (must respect the template string)
			 * 2) send to a remote REST-service the parsed answer which will check if the answer is correct or wrong
			 * 3) send DM to the user telling him whether was a Success or a Failure
			 * In case of
			 */
			else if(direct_msg.isValueNode() && //if the direct_msg node is present in the message
					direct_msgStr.toLowerCase().contains(FLAG_KEYWORD) && //if direct_msg contains the CTF flag
					!direct_msg_senderId.isMissingNode() && //if direct_msg_senderId is present in the message
					direct_msg_senderId.getLongValue() != SYTAC_USER_ID) //if the received message is not an echo message (message from Sytac itself) 
			{
				String answer[] = direct_msgStr.toLowerCase().split(FLAG_KEYWORD);
				if(answer.length < 2){
					LOGGER.warn("The JSON received isn't a #ctf well formed message: " + node.toString());
					dm(direct_msg_name.getTextValue(), direct_msg_senderId.getLongValue(), BAD_MESSAGE);
					return;
				}
				//if (answer!= null) answer = answer[1].trim();
				boolean ok = processAnswerToRemote(answer[1], direct_msg_name.getTextValue(), direct_msg_senderId.getLongValue());
				dm(direct_msg_name.getTextValue(), direct_msg_senderId.getLongValue(), ok ? RIGHT_ANSWER_MESSAGE : WRONG_ANSWER_MESSAGE); 
				LOGGER.info("New answer from participant: " + direct_msg_name.getTextValue() + " ID: " + direct_msg_senderId.getLongValue());
			}
			/**
			 * CASE OF A NEW FOLLOWER: Send his infos to the webapp
			 */
			else if(!event_node.isMissingNode() && 
					!event_source_id.isMissingNode() && 
					event_source_id.getLongValue() != SYTAC_USER_ID) {
				JsonNode src = node.path("source");
				System.out.println(src.toString());
			}
			/**
			 * MESSAGES NON-RELATED TO CTF-COMPET. RECEIVED: skip them
			 */
			else{
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
	 private void dm(String userName, long userId, String message){
		 try {
			 DirectMessage msg = _twitter4jClient.sendDirectMessage(userId, message);
			 LOGGER.info("Sent: " + msg.getText() + " to @" + msg.getRecipientScreenName());
		 } catch (TwitterException e) {
			LOGGER.error("Error during the DM to the partecipant " + userId + ": TWITTER4J exception ", e);
			WELCOME_NO_FOLLOW_MESSAGE = String.format(WELCOME_NO_FOLLOW_MESSAGE, userName);
			try {
				_twitter4jClient.updateStatus(WELCOME_NO_FOLLOW_MESSAGE);
			} catch (TwitterException e1) {
				LOGGER.error("Error during the mention of the new partecipant " + userName + ", id: "+userId +" - TWITTER4J exception ", e);
			}
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
