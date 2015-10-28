package com.sytac.twitter_ctf_bot.model;

import org.codehaus.jackson.JsonNode;

import com.sytac.twitter_ctf_bot.model.enumeration.MSG_TYPE;
/**
 * Mention model class
 * @author Tonino Catapano - tonino.catapano@sytac.io
 *
 */
public class Mention extends Raw implements ParsedJson{

	private String mentionText;
	
	
	
	public Mention(JsonNode rt) {
		super(rt);
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

}
