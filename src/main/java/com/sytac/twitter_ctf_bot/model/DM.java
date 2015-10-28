package com.sytac.twitter_ctf_bot.model;

import org.codehaus.jackson.JsonNode;

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

}
