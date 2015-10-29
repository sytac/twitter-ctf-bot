package com.sytac.twitter_ctf_bot;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import com.sytac.twitter_ctf_bot.client.TwitterClient;
import com.sytac.twitter_ctf_bot.conf.Prop;
import com.sytac.twitter_ctf_bot.model.DM;
import com.sytac.twitter_ctf_bot.model.Event;
import com.sytac.twitter_ctf_bot.model.Mention;
import com.sytac.twitter_ctf_bot.model.ParsedJson;
/**
 * Processor of the messages
 * @author Tonino Catapano - tonino.catapano@sytac.io
 * @since 1.0
 */
public class Processor {
	
	private static final Logger LOGGER = Logger.getLogger(Processor.class);

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
		 ObjectMapper mapper = new ObjectMapper();

		try{
			switch (raw.getType()){
				case DM: 
					DM dm = (DM) raw;
					String answer[] = dm.getDm_string().toLowerCase().split(_prop.FLAG_KEYWORD);
					if(answer.length < 2){
						LOGGER.warn("The JSON received isn't a #ctf well formed message: \n" + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(dm));
						twitter.dm(dm.getUser_name(), dm.getUser_Id(), _prop.BAD_MESSAGE, _prop.WELCOME_NO_FOLLOW_MESSAGE);
						return;
					}
					//boolean ok = RestClient.processAnswerToRemote(answer[1], dm.getUser_name(), dm.getUser_Id());
					//twitter.dm(dm.getUser_name(), dm.getUser_Id(), ok ? _prop.RIGHT_ANSWER_MESSAGE : _prop.WRONG_ANSWER_MESSAGE, _prop.WELCOME_NO_FOLLOW_MESSAGE); 
					LOGGER.info("New answer from participant: \n" + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(dm));
				break;	
				
				case EVENT: 
					Event event = (Event) raw;
					LOGGER.info("New event message received: \n" + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(event));
				break;
				
				case MENTION: 
					Mention mention = (Mention) raw;
					LOGGER.info("Received mention for #ctf: \n" + mapper.writerWithDefaultPrettyPrinter().writeValueAsString(mention));
					boolean followSuccess = twitter.followParticipant(mention.getUser_Id());	
					byte res = twitter.dm(mention.getUser_name(), mention.getUser_Id(), followSuccess ? _prop.WELCOME_PARTICIPANT_MESSAGE : _prop.COULDNOT_FOLLOW_MESSAGE, _prop.WELCOME_NO_FOLLOW_MESSAGE); 
					final String mess;
					switch(res){
						case 0: mess = "New participant handled correctly with a DM: ";  break;
						case 1: mess = "New participant handled with a Mention: "; break;
						default: mess = "Could not handle the new participant: "; break;
					}
					LOGGER.info(mess + mention.getUser_name());	
				break;
				
				default: 
					
				break;	
			} 
		}catch(IOException e){
			LOGGER.error("Error converting POJO to JSON: ",e);
		}
		   
	 }

	 
}
