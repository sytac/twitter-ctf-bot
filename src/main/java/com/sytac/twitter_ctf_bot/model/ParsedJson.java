package com.sytac.twitter_ctf_bot.model;

import org.codehaus.jackson.JsonNode;

import com.sytac.twitter_ctf_bot.model.enumeration.MessageType;

public interface ParsedJson {

	public JsonNode getRoot();
	public MessageType getType();

}
