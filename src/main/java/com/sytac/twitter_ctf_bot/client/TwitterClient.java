package com.sytac.twitter_ctf_bot.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import twitter4j.DirectMessage;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterClient {

	private static Twitter twitter4jClient;
	private final static Logger LOGGER = LoggerFactory.getLogger(TwitterClient.class);
	
	private static TwitterClient _instance = null;
	
	protected TwitterClient() {}

	public static TwitterClient getInstance() {
		if(_instance == null) {
			_instance = new TwitterClient();
	    }
	    return _instance;
	}
	
	
	public Twitter getClient() {
		return twitter4jClient;
	}
	
	public boolean followParticipant(long idParticipant){
		try {
			twitter4jClient.createFriendship(idParticipant);
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
	 public byte dm(String userName, long userId, String message, String mentionMessage){
		 try {
			 DirectMessage msg = twitter4jClient.sendDirectMessage(userId, message);
			 LOGGER.info("Sent: " + msg.getText() + " to @" + msg.getRecipientScreenName());
			 return 0;
		 } catch (TwitterException e) {
			LOGGER.error("Error during the DM to the partecipant " + userId + ": TWITTER4J exception, try to mention him/her ");
			try {
				twitter4jClient.updateStatus(String.format(mentionMessage, userName));
				return 1;
			} catch (TwitterException e1) {
				LOGGER.error("Error during the mention of the new partecipant " + userName + ", id: "+userId +" - TWITTER4J exception ", e);
				return -1;
			}
		 }	 
	 }
	 
	 
		/**
		 * Initialize the Twitter4j Client instance (REST-API calls)
		 * @param consumerKey
		 * @param consumerSecret
		 * @param token
		 * @param secret
		 */
		public void initializeTwit4j(String consumerKey, String consumerSecret,String token, String secret){
			ConfigurationBuilder cb = new ConfigurationBuilder();
			cb.setDebugEnabled(true)
			  .setOAuthConsumerKey(consumerKey)
			  .setOAuthConsumerSecret(consumerSecret)
			  .setOAuthAccessToken(token)
			  .setOAuthAccessTokenSecret(secret);
			TwitterFactory tf = new TwitterFactory(cb.build());		
			twitter4jClient = tf.getInstance();
		}
	
}
