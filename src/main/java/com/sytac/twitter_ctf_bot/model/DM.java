package com.sytac.twitter_ctf_bot.model;

import org.codehaus.jackson.JsonNode;

import com.sytac.twitter_ctf_bot.model.enumeration.MessageType;

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
	
	public DM(JsonNode rt, MessageType messageType) {
		super(rt);
		super.type = messageType;
	}

	public String getDm_string() {
		return dm_string;
	}

	public void setDm_string(String dm_string) {
		this.dm_string = dm_string;
	}

}
