package com.sytac.twitter_ctf_bot.model;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.JsonMappingException;
import org.mongojack.JacksonDBCollection;
import org.mongojack.WriteResult;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
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
		super(null);
		
		this.setUser_Id(user_Id);
		this.setUser_name(user_name);
		this.setUser_description(user_description);
		this.setUser_screenName(user_screenName);
		this.setUser_location(user_location);
		this.setUser_url(user_url);
		this.setUser_followerCount(user_followerCount);
		this.setUser_img(user_img);
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
		storeNewMentionToDB(mongo);
		//storeParticipant(mongo);
		return 0;
	}
	
	
	private boolean storeNewMentionToDB(MongoDBClient mongo){
		try{
			final DBCollection mentions = mongo.getOrCreateCollection("mention", true);
			final JacksonDBCollection<Mention, String> coll = JacksonDBCollection.wrap(mentions, Mention.class, String.class);
			final Mention toStore = new Mention(getUser_Id(), getUser_name(), getUser_description(), 
					getUser_screenName(), getUser_location(), getUser_url(),
					getUser_followerCount(), getUser_img(), getMentionText());
			
			final WriteResult<Mention, String> result = coll.insert(toStore);
			//final ObjectId oid = (ObjectId) result.getDbObject().get("_id");
			LOGGER.info("Object correctly stored into MongoDB with id: " + result.getSavedId());
			//final Mention savedObject = coll.findOneById(result.getSavedId());
		}catch(Exception e){
			LOGGER.error("error",e);
			return false;
		}
		return true;
	}
	
	
	private boolean storeParticipant(MongoDBClient mongo){
		try{
			final DBCollection mentions = mongo.getOrCreateCollection("participant", true);
			final JacksonDBCollection<Participant, String> coll = JacksonDBCollection.wrap(mentions, Participant.class, String.class);
			
			final Participant participant = new Participant(getUser_Id(), getUser_name(), getUser_description(), 
					getUser_screenName(), getUser_location(), getUser_url(),
					getUser_followerCount(), getUser_img(), getMentionText());
			
			final Participant result = coll.findAndModify(			
			new BasicDBObject("user_id", getUser_Id()), //query
			null, //the fields I want back: null specify to return ALL THE FIELDS
			null, //sort CRITERIA
			false, //remove the document after modifying it
			new BasicDBObject("$inc", new BasicDBObject("counter", "")), //the update query I want to execute
			true, //true indicate to return the object AFTER the UPDATE in the last row (false make it return before)
			true // UPSERT: if the document does not exist then create one.
			);
			
			//final ObjectId oid = (ObjectId) result.getDbObject().get("_id");
			//LOGGER.info("Object correctly stored into MongoDB with id: " + result.getSavedId());
			//final Mention savedObject = coll.findOneById(result.getSavedId());
		}catch(Exception e){
			LOGGER.error("error",e);
			return false;
		}
		return true;
	}

}
