package com.sytac.twitter_ctf_bot;

import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sytac.twitter_ctf_bot.conf.Prop;
import com.sytac.twitter_ctf_bot.model.DM;
import com.sytac.twitter_ctf_bot.model.Event;
import com.sytac.twitter_ctf_bot.model.Mention;
import com.sytac.twitter_ctf_bot.model.ParsedJson;
import com.sytac.twitter_ctf_bot.model.Unknown;
import com.sytac.twitter_ctf_bot.model.enumeration.MSG_TYPE;

/**
 * The JsonParser class
 * 
 * @author Tonino Catapano - tonino.catapano@sytac.io
 * @since 1.0
 */
public class JsonParser {
	
	final static Logger LOGGER = LoggerFactory.getLogger(JsonParser.class);
	
	
	public JsonParser(){}
	
	public ParsedJson parse(String toParse){
		ObjectMapper mapper = new ObjectMapper();
		try {
			final JsonNode node = mapper.readTree(toParse);	
		/** MENTION NODES**/
			final JsonNode mention = node.path("entities").path("user_mentions"); //present in case of a mention
			final JsonNode mention_text = node.path("text"); //present in case of a mention
			final JsonNode participant = node.path("user"); //present in case of a mention
			final JsonNode participant_name = node.path("user").path("screen_name"); //present in case of a mention
			final JsonNode participant_id = node.path("user").path("id"); //present in case of a mention
			
			/** DM NODES**/
			final JsonNode direct_msg = node.path("direct_message").path("text"); //present in case of a DM
			final JsonNode direct_msg_senderId = node.path("direct_message").path("sender").path("id"); //present in case of a DM
			final JsonNode direct_msg_name = node.path("direct_message").path("sender").path("screen_name"); //present in case of a DM
			final String direct_msgStr = direct_msg.getTextValue();//present in case of a DM
			
			/** OTHER NODES**/
			final JsonNode event_node = node.path("event"); // present in case of a event message
			final JsonNode event_source_id = node.path("source").path("id"); //id of the new follower
			
			final JsonNode delete_node = node.path("delete"); // present in case of a delete message (aka unfollow) (skipped)
		
			

			if(event_node.isMissingNode() &&
				delete_node.isMissingNode() && 
				!mention.isMissingNode() && 
				mention_text.isValueNode() && 
				mention_text.getTextValue().toLowerCase().contains(Prop.getInstance().FLAG_KEYWORD) && //if mention text contains the "#ctf" flag
				Prop.getInstance().SYTAC_USER_ID != participant_id.getLongValue()) //if I am not the mention maker
			{
				Mention json = new Mention(node, MSG_TYPE.MENTION);
				json.setUser_Id(participant.path("id").getLongValue());
				json.setUser_name(participant_name.getTextValue());
				json.setUser_description(participant.path("description").getTextValue());
				json.setUser_screenName(participant.path("screen_name").getTextValue());
				json.setUser_location(participant.path("location").getTextValue());
				json.setUser_url(participant.path("url").getTextValue());
				json.setUser_followerCount(participant.path("followers_count").getLongValue());
				json.setUser_img(participant.path("profile_background_image_url").getTextValue());
				return json;
			}
			else if(direct_msg.isValueNode() && 
					direct_msgStr.toLowerCase().contains(Prop.getInstance().FLAG_KEYWORD) && //if direct_msg contains the CTF flag
					!direct_msg_senderId.isMissingNode() &&
					direct_msg_senderId.getLongValue() != Prop.getInstance().SYTAC_USER_ID) //if the received message is not an echo message (message from Sytac itself) 
			{
				DM json = new DM(node, MSG_TYPE.DM);		
				json.setUser_Id(direct_msg_senderId.getLongValue());
				json.setUser_name(direct_msg_name.getTextValue());
				json.setDm_string(direct_msgStr);
				return json;
			}else if(!event_node.isMissingNode() && 
					!event_source_id.isMissingNode() && 
					event_source_id.getLongValue() != Prop.getInstance().SYTAC_USER_ID) {
				
				Event json = new Event(node, MSG_TYPE.EVENT);
				return json;
			}else{
				Unknown json = new Unknown(node, MSG_TYPE.UNKNOWN);
				LOGGER.warn("The JSON received isn't a ctf-related message: " + node.toString());
				return json;
			}
		} catch (IOException e) {
			LOGGER.error("I/O Error during the parsing: ", e);
			return null;
		}
	}
}
