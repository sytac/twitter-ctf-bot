package com.sytac.twitter_ctf_bot.model;

import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.JsonMappingException;

import com.sytac.twitter_ctf_bot.client.TwitterClient;
import com.sytac.twitter_ctf_bot.conf.Prop;
import com.sytac.twitter_ctf_bot.model.enumeration.MSG_TYPE;

/**
 * Direct Message model class
 * @author Tonino Catapano - tonino.catapano@sytac.io
 *
 */
public class DM extends Raw implements ParsedJson{

	private String dm_string;
	
	public DM(JsonNode rt) {
		super(rt);
	}
	
	public DM(JsonNode rt, MSG_TYPE msg_type) {
		super(rt);
		super.type = msg_type;
	}

	public String getDm_string() {
		return dm_string;
	}

	public void setDm_string(String dm_string) {
		this.dm_string = dm_string;
	}

	@Override
	public byte handleMe(Prop p, TwitterClient twitter) throws JsonGenerationException, JsonMappingException, IOException {
		String answer[] = dm_string.toLowerCase().split(p.FLAG_KEYWORD);
		if(answer.length < 2){
			LOGGER.warn("The JSON received isn't a #ctf well formed message: \n" + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this));
			twitter.dmOrMention(getUser_name(), getUser_Id(), p.BAD, p.PLEASE_FOLLOW);
			return -1;
		}
		boolean ok = processAnswer(p.getAnswers(), answer[1].trim());
		LOGGER.info("New answer from participant: \n" + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this));
		return twitter.dmOrMention(getUser_name(), getUser_Id(), ok  ? p.RIGHT_ANSWER : p.WRONG_ANSWER, p.PLEASE_FOLLOW); 
	}

	
	 private boolean processAnswer(List<String> answers, String answer){
		 return answers.contains(answer);
	 }
	
}
