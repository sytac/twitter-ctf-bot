package com.sytac.twitter_ctf_bot;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

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

	private static final Logger LOGGER = Logger.getLogger(JsonParser.class);

    private final Prop config;

    public JsonParser(Prop config) {
        this.config = config;
    }
    
    public ParsedJson parse(String toParse) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = null;
		try {
			node = mapper.readTree(toParse);
		} catch (IOException e1) {
			 LOGGER.error("I/O Error during the parsing: ", e1);
			 return null;
		}
        ParsedJson parsed = new Unknown(node, MSG_TYPE.UNKNOWN);
        /** OTHER NODES**/

        if (isFromSelf(node)){
        	LOGGER.info("The JSON received is an outgoing message, skip it. ");
        }else if (isMention(node)) {
            parsed = getMention(node);
        } else if (isDirectMessage(node)) {
            parsed = getDM(node);
        } else if (isEvent(node)) {
            parsed = getEvent(node);
        } else {
            LOGGER.info("The JSON received isn't a ctf-related message: " + node.toString());
        }
        return parsed;
    }

    private DM getDM(JsonNode tweet) {
    	final DM json = new DM(tweet, MSG_TYPE.DM);
        json.setUser_screenName(tweet.path("direct_message").path("sender").path("screen_name").getTextValue());
        json.setUser_name(tweet.path("direct_message").path("sender").path("name").getTextValue());
        json.setUser_Id(String.valueOf(tweet.path("direct_message").path("sender").path("id").getLongValue()));
        json.setUser_url(tweet.path("direct_message").path("sender").path("url").getTextValue());
        json.setDm_string(tweet.path("direct_message").path("text").getTextValue());
        json.setUser_location(tweet.path("direct_message").path("sender").path("location").getTextValue());
        json.setUser_description(tweet.path("direct_message").path("sender").path("description").getTextValue());
        json.setUser_followerCount(tweet.path("direct_message").path("sender").path("followers_count").getLongValue());
        json.setUser_img(tweet.path("direct_message").path("sender").path("profile_image_url").getTextValue());
        return json;
    }

    private Mention getMention(JsonNode node) {
        final JsonNode participant = node.path("user"); //present in case of a mention
        final JsonNode participant_name = node.path("user").path("screen_name"); //present in case of a mention

        final Mention json = new Mention(node, MSG_TYPE.MENTION);
        json.setUser_Id(String.valueOf(participant.path("id").getLongValue()));
        json.setUser_name(participant_name.getTextValue());
        json.setUser_description(participant.path("description").getTextValue());
        json.setUser_screenName(participant.path("screen_name").getTextValue());
        json.setUser_location(participant.path("location").getTextValue());
        json.setUser_url(participant.path("url").getTextValue());
        json.setUser_followerCount(participant.path("followers_count").getLongValue());
        json.setUser_img(participant.path("profile_image_url").getTextValue());
        json.setMentionText(node.path("text").getTextValue());
        return json;
    }

    private Event getEvent(JsonNode node) {
        final JsonNode user = node.path("source");
        final Event json = new Event(node, MSG_TYPE.EVENT);
        
        json.setEventName(node.path("event").getTextValue());
        json.setUser_Id(String.valueOf(user.path("id").getLongValue()));
        json.setUser_name(user.getTextValue());
        json.setUser_description(user.path("description").getTextValue());
        json.setUser_screenName(user.path("screen_name").getTextValue());
        json.setUser_location(user.path("location").getTextValue());
        json.setUser_url(user.path("url").getTextValue());
        json.setUser_followerCount(user.path("followers_count").getLongValue());
        json.setUser_img(user.path("profile_image_url").getTextValue());
        return json;
    }
    
    
    private boolean isEvent(JsonNode node) {
        final JsonNode event_node = node.path("event"); // present in case of a event message
        final JsonNode event_source_id = node.path("source").path("id"); //id of the new follower
        
        return !event_node.isMissingNode() &&
                !event_source_id.isMissingNode() &&
                event_source_id.getLongValue() != config.SYTAC_USER_ID;
    }

    private boolean isDirectMessage(JsonNode tweet) {
        /** DM NODES**/
        final JsonNode direct_msg = tweet.path("direct_message").path("text"); //present in case of a DM
        final JsonNode direct_msg_senderId = tweet.path("direct_message").path("sender").path("id"); //present in case of a DM
        final String direct_msgStr = direct_msg.getTextValue();//present in case of a DM


        return direct_msg.isValueNode() &&
                direct_msgStr.toLowerCase().contains(config.FLAG_KEYWORD) && //if direct_msg contains the CTF flag
                !direct_msg_senderId.isMissingNode() &&
                direct_msg_senderId.getLongValue() != config.SYTAC_USER_ID;
    }

    private boolean isMention(JsonNode tweet) {

        return isWithoutEvent(tweet) &&
                isWithoutDelete(tweet) &&
                containsText(tweet) &&
                containsCTFHashTag(tweet) && //if mention text contains the "#ctf" flag
                isNotFromSelf(tweet) &&
                isNotRetweet(tweet);
    }

    private boolean isNotFromSelf(JsonNode tweet) {
        return (config.SYTAC_USER_ID != tweet.path("user").path("id").getLongValue() &&
        		 config.SYTAC_USER_ID != tweet.path("sender").path("id").getLongValue() &&
        		 config.SYTAC_USER_ID != tweet.path("source").path("id").getLongValue());
    }
    
    private boolean isFromSelf(JsonNode tweet) {
        return (config.SYTAC_USER_ID == tweet.path("id").getLongValue() ||
        		 config.SYTAC_USER_ID == tweet.path("direct_message").path("sender").path("id").getLongValue() ||
        		 config.SYTAC_USER_ID == tweet.path("user").path("source").path("id").getLongValue());
    }

    private boolean containsCTFHashTag(JsonNode tweet) {
        if(tweet.has("text")) {
            JsonNode message = tweet.path("text");
            return message.getTextValue().toLowerCase().contains(config.FLAG_KEYWORD);
        }

        return false;
    }

    private boolean isWithoutDelete(JsonNode tweet) {
        return tweet.path("delete").isMissingNode();
    }

    private boolean containsText(JsonNode tweet) {
        return tweet.has("text");
    }

    private boolean isWithoutEvent(JsonNode tweet) {
        return tweet.path("event").isMissingNode();
    }
    
    private boolean isNotRetweet(JsonNode tweet){
    	return tweet.path("retweeted_status").isMissingNode();
    }
}
