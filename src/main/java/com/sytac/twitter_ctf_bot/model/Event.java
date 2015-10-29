package com.sytac.twitter_ctf_bot.model;

import org.codehaus.jackson.JsonNode;

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
	
	public Event(JsonNode rt, MSG_TYPE msg_type) {
		super(rt);
		super.type = msg_type;
	}

}
