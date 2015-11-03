package com.sytac.twitter_ctf_bot.model;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.JsonMappingException;

import com.sytac.twitter_ctf_bot.client.MongoDBClient;
import com.sytac.twitter_ctf_bot.client.TwitterClient;
import com.sytac.twitter_ctf_bot.conf.Prop;
import com.sytac.twitter_ctf_bot.model.enumeration.MSG_TYPE;
/**
 * Event model class
 * @author Tonino Catapano - tonino.catapano@sytac.io
 *
 */
public class Event extends Raw implements ParsedJson{

	private String eventName;
	
	public String getEventName() {
		return eventName;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	public Event(JsonNode rt) {
		super(rt);
	}
	
	public Event(JsonNode rt, MSG_TYPE msg_type){
		super(rt);
		super.type = msg_type;
	}
	
	public Event(String user_Id, String user_name, String eventName){
		super();
		setUser_Id(user_Id);
		setUser_name(user_name);
		setEventName(eventName);
	}

	@Override
	public byte handleMe(Prop p, TwitterClient twitter, MongoDBClient mongo)
			throws JsonGenerationException, JsonMappingException, IOException {
		LOGGER.info("New event message received: \n" + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this));
		
		return 0;
	}

}
