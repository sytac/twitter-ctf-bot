package com.sytac.twitter_ctf_bot.model;

import java.util.Date;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.mongojack.ObjectId;

import com.sytac.twitter_ctf_bot.model.enumeration.MSG_TYPE;

@JsonIgnoreProperties({"LOGGER", "mapper", "root", "type" })
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public abstract class Raw implements ParsedJson{

	protected static final Logger LOGGER = Logger.getLogger(Raw.class);
	
	protected ObjectMapper mapper;
		
	private JsonNode root;
	
	

	protected MSG_TYPE type;
	
	@ObjectId
	public String _id;
	
	@JsonProperty
	private String user_Id;

	@JsonProperty
	private String user_name;
	@JsonProperty
	private String user_screenName;
	@JsonProperty
	private String user_location;
	@JsonProperty
	private String user_url;
	@JsonProperty
	private String user_description;
	@JsonProperty
	private long user_followerCount;
	@JsonProperty
	private String user_img;
	
	@JsonProperty
	private Date lastUpdate = new Date();
	
	
	public Raw(){}

	public Raw(JsonNode rt){
		this.root = rt;
		this.mapper = new ObjectMapper();
	}
	
	
	public Raw(String user_Id, String user_name, String user_description, 
			String user_screenName, String user_location, String user_url, 
			Long user_followerCount, String user_img){
			
		this.setUser_Id(user_Id);
		this.setUser_name(user_name);
		this.setUser_description(user_description);
		this.setUser_screenName(user_screenName);
		this.setUser_location(user_location);
		this.setUser_url(user_url);
		this.setUser_followerCount(user_followerCount);
		this.setUser_img(user_img);
		this.lastUpdate = new Date();
	}
	
	


	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
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

	public String getUser_Id() {
		return user_Id;
	}

	public void setUser_Id(String user_Id) {
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
