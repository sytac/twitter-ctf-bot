package com.sytac.twitter_ctf_bot.model;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.JsonMappingException;

import com.sytac.twitter_ctf_bot.client.TwitterClient;
import com.sytac.twitter_ctf_bot.conf.Prop;
import com.sytac.twitter_ctf_bot.model.enumeration.MSG_TYPE;

public class Unknown extends Raw implements ParsedJson{

	public Unknown(JsonNode rt) {
		super(rt);
	}
	
	public Unknown(JsonNode rt, MSG_TYPE msg_type) {
		super(rt);
		super.type = msg_type;
	}

	@Override
	public byte handleMe(Prop p, TwitterClient twitter)
			throws JsonGenerationException, JsonMappingException, IOException {
		LOGGER.info("A #ctf non-related message received, skipping it.");
		return -1;
	}

}
