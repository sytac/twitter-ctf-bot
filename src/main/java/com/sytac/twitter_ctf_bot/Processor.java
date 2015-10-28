package com.sytac.twitter_ctf_bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sytac.twitter_ctf_bot.client.RestClient;
import com.sytac.twitter_ctf_bot.client.TwitterClient;
import com.sytac.twitter_ctf_bot.conf.Prop;
import com.sytac.twitter_ctf_bot.model.DM;
import com.sytac.twitter_ctf_bot.model.Event;
import com.sytac.twitter_ctf_bot.model.Mention;
import com.sytac.twitter_ctf_bot.model.ParsedJson;
/**
 * Singleton class responsible of message's handling 
 * @author Tonino Catapano - tonino.catapano@sytac.io
 * @since 1.0
 */
public class Processor {
	
	private final static Logger _LOGGER = LoggerFactory.getLogger(Processor.class);

	private final Prop _prop;
	private final TwitterClient twitter;
	
	public Processor(Prop config, TwitterClient twitter) {
		this._prop = config;
        this.twitter = twitter;
    }

	/**
	  * The processing routine: handle mentions and DM receiving and answers
	  */
	 public void processMessage(ParsedJson raw){
		switch (raw.getType()){
			case DM: 
				DM dm = (DM) raw;
				String answer[] = dm.getDm_string().toLowerCase().split(_prop.FLAG_KEYWORD);
				if(answer.length < 2){
					_LOGGER.warn("The JSON received isn't a #ctf well formed message: " + dm.getRoot().toString());
					twitter.dm(dm.getUser_name(), dm.getUser_Id(), _prop.BAD_MESSAGE, _prop.WELCOME_NO_FOLLOW_MESSAGE);
					return;
				}
				boolean ok = RestClient.processAnswerToRemote(answer[1], dm.getUser_name(), dm.getUser_Id());
				twitter.dm(dm.getUser_name(), dm.getUser_Id(), ok ? _prop.RIGHT_ANSWER_MESSAGE : _prop.WRONG_ANSWER_MESSAGE, _prop.WELCOME_NO_FOLLOW_MESSAGE); 
				_LOGGER.info("New answer from participant: " + dm.getUser_name() + " ID: " + dm.getUser_Id());
			break;	
			
			case EVENT: 
				Event event = (Event) raw;
				System.out.println(event.getRoot().toString()); //TODO
			break;
			
			case MENTION: 
				Mention mention = (Mention) raw;
				_LOGGER.info("Received mention: " + mention.getMentionText());		
				boolean followSuccess = twitter.followParticipant(mention.getUser_Id());	
				twitter.dm(mention.getUser_name(), mention.getUser_Id(), followSuccess ? _prop.WELCOME_PARTICIPANT_MESSAGE : _prop.COULDNOT_FOLLOW_MESSAGE, _prop.WELCOME_NO_FOLLOW_MESSAGE); 
				_LOGGER.info("New Participant: " + mention.getUser_name());	
			break;
			
			default: 
				_LOGGER.warn("The JSON received isn't a ctf-related message: " + raw.getRoot().toString());
			break;	
		}    
	 }

	 
}
