package com.sytac.twitter_ctf_bot.model;

import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.JsonMappingException;

import com.sytac.twitter_ctf_bot.client.MongoDBClient;
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
	
	
	public DM(String user_Id, String user_name, String dm_string){
		super();
		this.setUser_Id(user_Id);
		this.setUser_name(user_name);
		this.setDm_string(dm_string);
	}
	
	
	
	@Override
	public byte handleMe(Prop p, TwitterClient twitter, MongoDBClient mongo) throws JsonGenerationException, JsonMappingException, IOException {
		mongo.storeDM(this);
		final String answer[] = dm_string.toLowerCase().split(p.FLAG_KEYWORD);
		if(answer.length < 2){
			LOGGER.warn("The JSON received isn't a #ctf well formed message: \n" + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this));
			twitter.dmOrMention(getUser_name(), getUser_Id(), p.BAD, p.PLEASE_FOLLOW);
			return -1;
		}
		LOGGER.info("New answer from participant: \n" + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this));
		final byte foundAnswer = processAnswer(p.getAnswers(), answer); // foundAnswer == -1 no , foundAnswer != 1 = found answer for the quiz
		if(foundAnswer != -1){
			byte result = mongo.newGoodAnswer(foundAnswer, getUser_Id());
			switch (result){
				case -1: break; //error db
				case 0:  return twitter.dmOrMention(getUser_name(), getUser_Id(), p.RIGHT_ANSWER, p.PLEASE_FOLLOW); //normal
				case 1:  return twitter.dmOrMention(getUser_name(), getUser_Id(), p.WINNER , p.PLEASE_FOLLOW);  //winner
				case 2:  return twitter.dmOrMention(getUser_name(), getUser_Id(), p.COULDNOT_FOLLOW , p.PLEASE_FOLLOW);  // not a participant yet
			}
		}
		return twitter.dmOrMention(getUser_name(), getUser_Id(), p.WRONG_ANSWER, p.PLEASE_FOLLOW);
	}

	private byte processAnswer(List<String> correct, String[] answer){
		 for(byte i=0; i < correct.size(); i++){
			 for(String attempt : answer){
				 if(attempt.toLowerCase().contains(correct.get(i))) return i;
			 }
		 }
		 return -1;
	 }
	 
}
