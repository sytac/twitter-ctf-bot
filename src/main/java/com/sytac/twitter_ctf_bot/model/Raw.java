package com.sytac.twitter_ctf_bot.model;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.ObjectMapper;

import com.sytac.twitter_ctf_bot.model.enumeration.MSG_TYPE;

@JsonIgnoreProperties({"root"})
public abstract class Raw implements ParsedJson{

	protected static final Logger LOGGER = Logger.getLogger(Raw.class);
	
	ObjectMapper mapper = new ObjectMapper();
	
	protected MSG_TYPE type;
	private JsonNode root;
	
	
	private long user_Id;
	private String user_name;
	private String user_screenName;
	private String user_location;
	private String user_url;
	private String user_description;
	private long user_followerCount;
	private String user_img;
	
	
	
	
	
	public Raw(JsonNode rt){
		root = rt;
	}
	
	public MSG_TYPE getType(){
		return type;
	}
	
	public void setType(MSG_TYPE t){
		this.type = t;
	}
	
	public JsonNode getRoot(){
		return root;
	}

	public long getUser_Id() {
		return user_Id;
	}

	public void setUser_Id(long user_Id) {
		this.user_Id = user_Id;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getUser_screenName() {
		return user_screenName;
	}

	public void setUser_screenName(String user_screenName) {
		this.user_screenName = user_screenName;
	}

	public String getUser_location() {
		return user_location;
	}

	public void setUser_location(String user_location) {
		this.user_location = user_location;
	}

	public String getUser_url() {
		return user_url;
	}

	public void setUser_url(String user_url) {
		this.user_url = user_url;
	}

	public String getUser_description() {
		return user_description;
	}

	public void setUser_description(String user_description) {
		this.user_description = user_description;
	}

	public long getUser_followerCount() {
		return user_followerCount;
	}

	public void setUser_followerCount(long user_followerCount) {
		this.user_followerCount = user_followerCount;
	}

	public String getUser_img() {
		return user_img;
	}

	public void setUser_img(String user_img) {
		this.user_img = user_img;
	}
	
}
