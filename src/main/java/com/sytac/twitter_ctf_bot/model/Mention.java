package com.sytac.twitter_ctf_bot.model;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.JsonMappingException;

import com.sytac.twitter_ctf_bot.client.MongoDBClient;
import com.sytac.twitter_ctf_bot.client.TwitterClient;
import com.sytac.twitter_ctf_bot.conf.Prop;
import com.sytac.twitter_ctf_bot.model.enumeration.MSG_TYPE;
/**
 * Mention model class
 * @author Tonino Catapano - tonino.catapano@sytac.io
 *
 */
public class Mention extends Raw implements ParsedJson{

	@JsonProperty
	private String mentionText;
	
	
	
	public Mention(JsonNode rt) {
		super(rt);
	}
	
	public Mention() {
		super(null);
	}
	
	public Mention(String user_Id, String user_name, String user_description, 
			String user_screenName, String user_location, String user_url, 
			Long user_followerCount, String user_img, String mentionText){
		
		super(user_Id, user_name, user_description, 
				 user_screenName,  user_location, user_url, 
				 user_followerCount,  user_img);
		
		this.setMentionText(mentionText);
		
	}
	
	public Mention(JsonNode rt, MSG_TYPE msg_type) {
		super(rt);
		super.type = msg_type;
	}

	public String getMentionText() {
		return mentionText;
	}

	public void setMentionText(String mentionText) {
		this.mentionText = mentionText;
	}

	@Override
	public byte handleMe(Prop p, TwitterClient twitter, MongoDBClient mongo)
			throws JsonGenerationException, JsonMappingException, IOException {
		
		LOGGER.info("Received mention for #ctf: \n" + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this));
		boolean followSuccess = twitter.followParticipant(getUser_Id());	
		final String mess;
		switch(twitter.dmOrMention(getUser_name(), getUser_Id(), followSuccess ? p.WELCOME_PARTICIPANT : p.COULDNOT_FOLLOW, p.WELCOME_NO_FOLLOW)){
			case 0: mess = "New participant handled correctly with a DM: ";  break;
			case 1: mess = "New participant handled with a Mention: "; break;
			default: mess = "Could not handle the new participant: "; break;
		}
		LOGGER.info(mess + getUser_name());
		final Mention toStore = new Mention(getUser_Id(), getUser_name(), getUser_description(), 
				getUser_screenName(), getUser_location(), getUser_url(),
				getUser_followerCount(), getUser_img(), getMentionText());
		final Participant partic = new Participant(getUser_Id(), getUser_name(), getUser_description(), 
				getUser_screenName(), getUser_location(), getUser_url(),
				getUser_followerCount(), getUser_img());
		
		mongo.storeMention(toStore);
		mongo.storeNewParticipant(partic);
		return 0;
	}
	

}
