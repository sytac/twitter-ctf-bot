package com.sytac.twitter_ctf_bot.model;

import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.JsonMappingException;
import org.mongojack.JacksonDBCollection;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
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

	@Override
	public byte handleMe(Prop p, TwitterClient twitter, MongoDBClient mongo) throws JsonGenerationException, JsonMappingException, IOException {
		String answer[] = dm_string.toLowerCase().split(p.FLAG_KEYWORD);
		if(answer.length < 2){
			LOGGER.warn("The JSON received isn't a #ctf well formed message: \n" + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this));
			twitter.dmOrMention(getUser_name(), getUser_Id(), p.BAD, p.PLEASE_FOLLOW);
			return -1;
		}
		byte foundAnswer = processAnswer(p.getAnswers(), answer);
		LOGGER.info("New answer from participant: \n" + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this));
		if(foundAnswer != -1){
			updateParticipantAnswer(foundAnswer, getUser_Id(), mongo);
		}
		return twitter.dmOrMention(getUser_name(), getUser_Id(), foundAnswer != -1  ? p.RIGHT_ANSWER : p.WRONG_ANSWER, p.PLEASE_FOLLOW);
	}

	
	 private byte processAnswer(List<String> correct, String[] answer){
		 for(byte i=0; i < correct.size(); i++){
			 for(String attempt : answer){
				 if(attempt.toLowerCase().contains(correct.get(i))) return i;
			 }
		 }
		 return -1;
	 }
	 
	 
	 
		private boolean updateParticipantAnswer(byte quizNr, String user_Id, MongoDBClient mongo){
			try{
				final DBCollection competitionColl = mongo.getOrCreateCollection("participant");
				final JacksonDBCollection<Participant, String> coll = JacksonDBCollection.wrap(competitionColl, Participant.class, String.class);		
				final Participant result = coll.findAndModify(			
					new BasicDBObject("user_Id", user_Id), //query
					null, //the fields I want back: null specify to return ALL THE FIELDS
					null, //sort CRITERIA
					false, //remove the document after modifying it
					new BasicDBObject("$set", new BasicDBObject("foundFlags." + quizNr, true)), //the update query I want to execute
					true, //true indicate to return the object AFTER the UPDATE in the last row (false make it return before)
					true // UPSERT: if the document does not exist then create one.
				);
				System.out.println();
			}catch(Exception e){
				LOGGER.error("error",e);
				return false;
			}
			return true;
		}
	
}
