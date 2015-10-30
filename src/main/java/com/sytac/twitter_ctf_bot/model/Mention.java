package com.sytac.twitter_ctf_bot.model;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.JsonMappingException;

import com.sytac.twitter_ctf_bot.client.TwitterClient;
import com.sytac.twitter_ctf_bot.conf.Prop;
import com.sytac.twitter_ctf_bot.model.enumeration.MSG_TYPE;
/**
 * Mention model class
 * @author Tonino Catapano - tonino.catapano@sytac.io
 *
 */
public class Mention extends Raw implements ParsedJson{

	private String mentionText;
	
	
	
	public Mention(JsonNode rt) {
		super(rt);
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
	public byte handleMe(Prop p, TwitterClient twitter)
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
		
		return 0;
	}

}
