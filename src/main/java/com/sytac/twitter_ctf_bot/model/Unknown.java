package com.sytac.twitter_ctf_bot.model;

import org.codehaus.jackson.JsonNode;

import com.sytac.twitter_ctf_bot.model.enumeration.MSG_TYPE;

public class Unknown extends Raw implements ParsedJson{

	public Unknown(JsonNode rt) {
		super(rt);
	}
	
	public Unknown(JsonNode rt, MSG_TYPE msg_type) {
		super(rt);
		super.type = msg_type;
	}

}
