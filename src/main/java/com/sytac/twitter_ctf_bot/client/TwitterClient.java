package com.sytac.twitter_ctf_bot.client;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.sytac.twitter_ctf_bot.conf.Prop;

import twitter4j.DirectMessage;
import twitter4j.IDs;
import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterClient {

	private Twitter twitter4jClient;
	private static final Logger LOGGER = Logger.getLogger(TwitterClient.class);
	
	private final static byte OK = 0;
	private final static byte SEMI_SUCCESS = 1;
	private final static byte FAILURE = -1;

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

	public boolean followParticipant(String idParticipant){
		try {
			twitter4jClient.createFriendship(Long.valueOf((idParticipant)));
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
	  * @return true if DM sent succeeded, false otherwise
	  */
	 public boolean dm(String userName, String userId, String message){
		 try {
			 final DirectMessage msg = twitter4jClient.sendDirectMessage(Long.valueOf(userId), message);
			 LOGGER.info("Sent: " + msg.getText() + " to @" + msg.getRecipientScreenName());
			 return true;
		 } catch (TwitterException e) {
			 LOGGER.error("Error during the DM to the partecipant @" + userName + ": TWITTER4J exception, ", e);
			return false;
		 }	 
	 }
	 
	 
	 /**
	  * Send a mention (namely update a status)
	  * @param mentionMessage
	  * @return true if mention succeeded, false otherwise
	  */
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
	public byte dmOrMention(String userName, String userId, String message, String mentionMessage) {
		return (byte) (dm(userName, userId, message) ? OK : (mention(String.format(mentionMessage, userName)) ? SEMI_SUCCESS : FAILURE));
	}
	

	
	public void printFriendsInfos(OutputStream oS){
	   try {
		   printMatrix(splitsBy100AndMatrixify(getListIDS()), oS);
	    } catch (Exception e) {
	    	LOGGER.error("Error during printing the users informations ", e);
	    }	   
	}
	/**
	 * Due to twitter REST 1.1 API limitation I have to paginate the call by 100 users, 
	 * so after getting all the friends IDs I split them in packs of 100 and put it 
	 * an array of longs so composed:
	 * [0][100 elements]
	 * [1][100 elements]
	 * [...][100 elements]
	 * [n][100 elements]
	 * [n+1][eventual spare elements if not a perfect 100 multiple]
	 * 
	 * @param usersIDs
	 * @return
	 */
	private long[][] splitsBy100AndMatrixify(ArrayList<Long> usersIDs){
        final int nrArrays = usersIDs.size() / 100;
        final int sizeSpare = usersIDs.size() % 100;   
        final long[][] idsMatrix = (sizeSpare > 0) ? new long[nrArrays+1][] : new long[nrArrays][];
        for(int i=0; i < nrArrays; i++){
        	idsMatrix[i] = new long[100];
        	for(int j = 0; j < 100; j++){
        		idsMatrix[i][j] = usersIDs.get(j + (i*100)).longValue();
        	}
        }
        if(sizeSpare > 0){
        	idsMatrix[nrArrays] = new long[sizeSpare];
        	for(int j = 0; j < sizeSpare; j++){
        		idsMatrix[nrArrays][j] = usersIDs.get(j + ((nrArrays) * 100)).longValue();	
        	}
        }
        return idsMatrix;
	}
	
	private void printMatrix(long[][] matrix, OutputStream oS) throws TwitterException{
		for(int i=0; i< matrix.length; i++){
			printUsers(twitter4jClient.lookupUsers(matrix[i]), oS);
		}
	}
		
	
	private ArrayList<Long> getListIDS(){
        final IDs ids;
		try {
			ids = twitter4jClient.getFriendsIDs(-1);
		} catch (TwitterException e) {
			LOGGER.error("Error during printing the users informations ", e);
			return null;
		}
        final ArrayList<Long> usersIDs = new ArrayList<>();
        for(long id : ids.getIDs()){
        	usersIDs.add(id);
        }
        return usersIDs;
	}
	
	
	/**
	 * Print on the OutputStream {@code oS} the informations of the users in the List {@code users}
	 * @param users
	 */
	private void printUsers(ResponseList<User> users, OutputStream oS){
		final PrintWriter out = new PrintWriter(oS);
		for (User x : users){
			out.println(String.format("%s\t%s\t%s\t%s\t%s\t%s\t%s", x.getName(), x.getScreenName(),
					x.getLocation(), x.getDescription(), x.getURL(), x.getOriginalProfileImageURL(),
					String.valueOf(x.getFollowersCount())));
		}
		out.println();
		out.flush();
	}
	
}
