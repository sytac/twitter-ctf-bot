package com.sytac.twitter_ctf_bot.model;

import org.codehaus.jackson.JsonNode;

import com.sytac.twitter_ctf_bot.model.enumeration.MessageType;

public class Unknown extends Raw implements ParsedJson{

	public Unknown(JsonNode rt) {
		super(rt);
	}
	
	public Unknown(JsonNode rt, MessageType messageType) {
		super(rt);
		super.type = messageType;
	}

}
