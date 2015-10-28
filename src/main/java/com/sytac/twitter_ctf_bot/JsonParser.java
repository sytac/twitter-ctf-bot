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

    private final Prop config;

    public JsonParser(Prop config) {
        this.config = config;
    }

    public ParsedJson parse(String toParse) {
        ObjectMapper mapper = new ObjectMapper();
        ParsedJson parsed = new Unknown(null, MSG_TYPE.UNKNOWN);
        try {
            final JsonNode node = mapper.readTree(toParse);

            /** OTHER NODES**/
            final JsonNode event_node = node.path("event"); // present in case of a event message
            final JsonNode event_source_id = node.path("source").path("id"); //id of the new follower

            if (isMention(node)) {
                parsed = getMention(node);
            } else if (isDirectMessage(node)) {
                processDirectMessage(node);
            } else if (isEvent(event_node, event_source_id)) {
                parsed = new Event(node, MSG_TYPE.EVENT);
            } else {
                parsed = new Unknown(node, MSG_TYPE.UNKNOWN);
                LOGGER.warn("The JSON received isn't a ctf-related message: " + node.toString());
            }
        } catch (IOException e) {
            LOGGER.error("I/O Error during the parsing: ", e);
        }

        return parsed;
    }

    private void processDirectMessage(JsonNode tweet) {
        final JsonNode direct_msg = tweet.path("direct_message").path("text"); //present in case of a DM
        final JsonNode direct_msg_senderId = tweet.path("direct_message").path("sender").path("id"); //present in case of a DM
        final JsonNode direct_msg_name = tweet.path("direct_message").path("sender").path("screen_name"); //present in case of a DM
        final String direct_msgStr = direct_msg.getTextValue();//present in case of a DM

        DM json = new DM(tweet, MSG_TYPE.DM);
        json.setUser_Id(direct_msg_senderId.getLongValue());
        json.setUser_name(direct_msg_name.getTextValue());
        json.setDm_string(direct_msgStr);
    }

    private Mention getMention(JsonNode node) {
        final JsonNode participant = node.path("user"); //present in case of a mention
        final JsonNode participant_name = node.path("user").path("screen_name"); //present in case of a mention

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

    private boolean isEvent(JsonNode event_node, JsonNode event_source_id) {
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
                isNotFromSelf(tweet);
    }

    private boolean isNotFromSelf(JsonNode tweet) {
        return config.SYTAC_USER_ID != tweet.path("user").path("id").getLongValue();
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
}
