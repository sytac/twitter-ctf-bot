package com.sytac.twitter_ctf_bot.model;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.JsonMappingException;

import com.sytac.twitter_ctf_bot.client.TwitterClient;
import com.sytac.twitter_ctf_bot.conf.Prop;
import com.sytac.twitter_ctf_bot.model.enumeration.MSG_TYPE;

public interface ParsedJson {

	public JsonNode getRoot();
	public MSG_TYPE getType();
	
	public byte handleMe(Prop p, TwitterClient twitter) throws JsonGenerationException, JsonMappingException, IOException;
	
}
