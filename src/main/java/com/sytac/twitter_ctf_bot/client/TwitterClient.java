package com.sytac.twitter_ctf_bot.client;

import org.apache.log4j.Logger;

import com.sytac.twitter_ctf_bot.conf.Prop;

import twitter4j.DirectMessage;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterClient {

	private Twitter twitter4jClient;
	private static final Logger LOGGER = Logger.getLogger(TwitterClient.class);

	public  TwitterClient(Prop prop) {
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
				.setOAuthConsumerKey(prop.consumerKey)
				.setOAuthConsumerSecret(prop.consumerSecret)
				.setOAuthAccessToken(prop.token)
				.setOAuthAccessTokenSecret(prop.secret);
		TwitterFactory tf = new TwitterFactory(cb.build());
		twitter4jClient = tf.getInstance();
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
	  * 
	  */
	 public boolean dm(String userName, long userId, String message){
		 try {
			 DirectMessage msg = twitter4jClient.sendDirectMessage(userId, message);
			 LOGGER.info("Sent: " + msg.getText() + " to @" + msg.getRecipientScreenName());
			 return true;
		 } catch (TwitterException e) {
			 LOGGER.error("Error during the DM to the partecipant " + userId + ": TWITTER4J exception, ", e);
			return false;
		 }	 
	 }
	 
	 public boolean mention(String mentionMessage){
		 try {
			twitter4jClient.updateStatus(mentionMessage);
			return true;
		} catch (TwitterException e) {
			LOGGER.error("Error during a mention - TWITTER4J exception ", e);
			return false;
		}
	 }
	 
	 
	 /**	 
	  * try to send a DM to the user {@code userName}, if he's not a follower (error) then try to mention him/her
	  * @param userName
	  * @param userId
	  * @param message
	  * @param mentionMessage
	  * @return 0 if the pm is sent, 1 if could not send pm but succeeded to send a mention, -1 if nor the pm and the mention are sent
	  */
	public byte dmOrMention(String userName, long userId, String message, String mentionMessage) {
		return (byte) (dm(userName, userId, message) ? 0 : (mention(String.format(mentionMessage, userName)) ? 1 : -1));
	}
	
}
