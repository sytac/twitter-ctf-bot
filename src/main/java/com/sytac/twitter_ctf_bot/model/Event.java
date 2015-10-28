package com.sytac.twitter_ctf_bot.model;

import org.codehaus.jackson.JsonNode;

import com.sytac.twitter_ctf_bot.model.enumeration.MSG_TYPE;
/**
 * Event model class
 * @author Tonino Catapano - tonino.catapano@sytac.io
 *
 */
public class Event extends Raw implements ParsedJson{

	public Event(JsonNode rt) {
		super(rt);
	}
	
	public Event(JsonNode rt, MSG_TYPE msg_type) {
		super(rt);
		super.type = msg_type;
	}

}
