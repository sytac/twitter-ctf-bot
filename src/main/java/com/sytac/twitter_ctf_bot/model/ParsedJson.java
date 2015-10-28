package com.sytac.twitter_ctf_bot.model;

import org.codehaus.jackson.JsonNode;

import com.sytac.twitter_ctf_bot.model.enumeration.MSG_TYPE;

public interface ParsedJson {

	public JsonNode getRoot();
	public MSG_TYPE getType();
	
}
