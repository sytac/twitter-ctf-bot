package com.sytac.twitter_ctf_bot.model;

import org.codehaus.jackson.JsonNode;

import com.sytac.twitter_ctf_bot.model.enumeration.MessageType;
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
	
	public Mention(JsonNode rt, MessageType messageType) {
		super(rt);
		super.type = messageType;
	}

	public String getMentionText() {
		return mentionText;
	}

	public void setMentionText(String mentionText) {
		this.mentionText = mentionText;
	}

}
