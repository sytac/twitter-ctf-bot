package com.sytac.twitter_ctf_bot;

import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import twitter4j.DirectMessage;
import twitter4j.Twitter;
import twitter4j.TwitterException;
/**
 * Parses Twitter stream messages
 *
 * @author Tonino
 */
public class Processor {
	
	static final Logger LOGGER = LoggerFactory.getLogger(Processor.class);
	private final Twitter twitter;
	private final Configuration config;
	
	public Processor(Twitter twitter4jClient, Configuration configuration) {
		this.twitter = twitter4jClient;
        this.config = configuration;
    }
	
	/**
	  * The processing routine: handle mentions and DM receiving and answers
	  * @param json
	  */
	 public void processMessage(String json){
		LOGGER.debug(json);
        ObjectMapper mapper = new ObjectMapper();
		try {
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
				mention_text.getTextValue().toLowerCase().contains(config.getFlagKeyword()) && //if mention text contains the "#ctf" flag
				config.getOwnUserId() != participant_id.getLongValue()) //if I am not the mention maker
			{
				LOGGER.info("Received mention: " + mention_text.getTextValue());		
				//follow the participant
				boolean followSuccess = followParticipant(participant.path("id").getLongValue());
				// send him/her a welcome DM or a DM informing he's/she's already in the competition
				dm(participant_name.getTextValue(), participant.path("id").getLongValue(), followSuccess ? config.getWelcomeMessage() : config.getCannotFollowMessage());
				//if(followSuccess) _partecipantsCount++;
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
					direct_msgStr.toLowerCase().contains(config.getFlagKeyword()) && //if direct_msg contains the CTF flag
					!direct_msg_senderId.isMissingNode() && //if direct_msg_senderId is present in the message
					direct_msg_senderId.getLongValue() != config.getOwnUserId()) //if the received message is not an echo message (message from Sytac itself)
			{
				String answer[] = direct_msgStr.toLowerCase().split(config.getFlagKeyword());
				if(answer.length < 2){
					LOGGER.warn("The JSON received isn't a #ctf well formed message: " + node.toString());
					dm(direct_msg_name.getTextValue(), direct_msg_senderId.getLongValue(), config.getBadMessage());
					return;
				}
				//if (answer!= null) answer = answer[1].trim();
				boolean ok = processAnswerToRemote(answer[1], direct_msg_name.getTextValue(), direct_msg_senderId.getLongValue());
				dm(direct_msg_name.getTextValue(), direct_msg_senderId.getLongValue(), ok ? config.getRightAnswerMessage() : config.getWrongAnswerMessage());
				LOGGER.info("New answer from participant: " + direct_msg_name.getTextValue() + " ID: " + direct_msg_senderId.getLongValue());
			}
			/**
			 * CASE OF A NEW FOLLOWER: Send his infos to the webapp
			 */
			else if(!event_node.isMissingNode() && 
					!event_source_id.isMissingNode() && 
					event_source_id.getLongValue() != config.getOwnUserId()) {
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
			twitter.createFriendship(idParticipant);
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
	  * @return 0 if the pm is sent, 1 if cant send pm but succeded send a mention, -1 if nor the pm and the mention are sent
	  */
	 private byte dm(String userName, long userId, String message){
		 try {
			 DirectMessage msg = twitter.sendDirectMessage(userId, message);
			 LOGGER.info("Sent: " + msg.getText() + " to @" + msg.getRecipientScreenName());
			 return 0;
		 } catch (TwitterException e) {
			LOGGER.error("Error during the DM to the partecipant " + userId + ": TWITTER4J exception, try to mention him/her ");
			try {
				twitter.updateStatus(String.format(config.getWelcomeNoFollowMessage(), userName));
				return 1;
			} catch (TwitterException e1) {
				LOGGER.error("Error during the mention of the new partecipant " + userName + ", id: "+userId +" - TWITTER4J exception ", e);
				return -1;
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
