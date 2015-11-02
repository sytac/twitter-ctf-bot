package com.sytac.twitter_ctf_bot.model;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.JsonMappingException;

import com.sytac.twitter_ctf_bot.client.MongoDBClient;
import com.sytac.twitter_ctf_bot.client.TwitterClient;
import com.sytac.twitter_ctf_bot.conf.Prop;

public class Participant extends Raw{

	final boolean[] foundFlags = new boolean[6];
	
	public Participant(JsonNode rt) {
		super(rt);
	}

	@Override
	public byte handleMe(Prop p, TwitterClient twitter, MongoDBClient mongo)
			throws JsonGenerationException, JsonMappingException, IOException {

		return 0;
	}
	
	

}
