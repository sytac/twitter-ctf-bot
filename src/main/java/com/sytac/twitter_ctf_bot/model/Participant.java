package com.sytac.twitter_ctf_bot.model;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.JsonMappingException;

import com.sytac.twitter_ctf_bot.client.MongoDBClient;
import com.sytac.twitter_ctf_bot.client.TwitterClient;
import com.sytac.twitter_ctf_bot.conf.Prop;

public class Participant extends Raw{

	@JsonProperty
	private boolean[] foundFlags = new boolean[6];
	
	public boolean[] getFoundFlags() {
		return foundFlags;
	}

	public void setFoundFlags(boolean[] foundFlags) {
		this.foundFlags = foundFlags;
	}

	public Participant(JsonNode rt) {
		super(rt);
	}
	
	public Participant() {
		super(null);
	}

	public Participant(String user_Id, String user_name, String user_description, 
			String user_screenName, String user_location, String user_url, 
			Long user_followerCount, String user_img){
		super(null);	
		this.setUser_Id(user_Id);
		this.setUser_name(user_name);
		this.setUser_description(user_description);
		this.setUser_screenName(user_screenName);
		this.setUser_location(user_location);
		this.setUser_url(user_url);
		this.setUser_followerCount(user_followerCount);
		this.setUser_img(user_img);
	}
	
	@Override
	public byte handleMe(Prop p, TwitterClient twitter, MongoDBClient mongo)
			throws JsonGenerationException, JsonMappingException, IOException {

		return 0;
	}
	
	public boolean checkWinner(){
		boolean winner = true;
		for(boolean x : foundFlags)
			winner &= x; 
		return winner;
	}

}
