package com.sytac.twitter_ctf_bot.conf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
/**
 * Class for the properties file
 * @author Tonino Catapano - tonino.catapano@sytac.io
 *
 */
public class Prop {
	
	private static final Logger LOGGER = Logger.getLogger(Prop.class);

	private Properties properties;

	//get the keys and tokens from the properties
	public String consumerKey;
	public String consumerSecret;
	public String token;
	public String secret;

	
	private List<String> answers;
	public String PLEASE_FOLLOW;
	/** User related parameters **/
	public String WELCOME_PARTICIPANT;
	public String COULDNOT_FOLLOW;
	public String RIGHT_ANSWER;
	public String WRONG_ANSWER;
	public String WINNER;
	public String BAD;
	public String WELCOME_NO_FOLLOW;

	public String FLAG_KEYWORD;


	/** Endpoint-related parameters **/
	public String TWITTER_DM_ENDPOINT;
	public String SYTAC_REST_ENDPOINT;
	public String FLAG_KEY ;
	public String PARTIC_ID_KEY ;
	public String PARTIC_NAME_KEY ;

	public long SYTAC_USER_ID;
	public int QUEUE_BUFFER_SIZE;
	
	public  Prop(String path) {
		InputStream in = null;
		try {
			in = Files.newInputStream(Paths.get(path));
			if (path == null || path.isEmpty() || in == null){
				LOGGER.error("Please specificate a valid path for the properties file in the first argument");
				return;
			}
			properties = new Properties();
			properties.load(in);

		} catch(IOException e) {
			LOGGER.error("Error while reading the properties file: "+ path, e);
			throw new IllegalStateException(e);
		} finally {
			if(in != null) {
				try {
					in.close();
				} catch (IOException e) {
					LOGGER.error("Error while closing the properties file: "+ path, e);
				}
			}
		}

		consumerKey = properties.getProperty("consumerKey");
		consumerSecret = properties.getProperty("consumerSecret");
		token = properties.getProperty("token");
		secret = properties.getProperty("secret");
		WELCOME_PARTICIPANT  = properties.getProperty("WELCOME_PARTICIPANT");
		COULDNOT_FOLLOW   = properties.getProperty("COULDNOT_FOLLOW");
		RIGHT_ANSWER  = properties.getProperty("RIGHT_ANSWER");
		WRONG_ANSWER  = properties.getProperty("WRONG_ANSWER");
		WINNER  = properties.getProperty("WINNER");
		BAD  = properties.getProperty("BAD");
		WELCOME_NO_FOLLOW = properties.getProperty("WELCOME_NO_FOLLOW");
		FLAG_KEYWORD = properties.getProperty("FLAG_KEYWORD");
		TWITTER_DM_ENDPOINT = properties.getProperty("TWITTER_DM_ENDPOINT");
		SYTAC_REST_ENDPOINT = properties.getProperty("SYTAC_REST_ENDPOINT");
		FLAG_KEY = properties.getProperty("FLAG_KEY");
		PARTIC_ID_KEY = properties.getProperty("PARTIC_ID_KEY");
		PARTIC_NAME_KEY = properties.getProperty("PARTIC_NAME_KEY");
		SYTAC_USER_ID = Long.valueOf(properties.getProperty("SYTAC_USER_ID"));
		FLAG_KEYWORD = properties.getProperty("FLAG_KEYWORD");
		QUEUE_BUFFER_SIZE = Integer.valueOf(properties.getProperty("QUEUE_BUFFER_SIZE"));
		answers = Arrays.asList(splitAnswers(properties.getProperty("ANSWERS")));
		PLEASE_FOLLOW = properties.getProperty("PLEASE_FOLLOW");
		
	}

	public List<String> getAnswers() {
		return answers;
	}
	
	private String[] splitAnswers(String answers){
		return answers.split(";");
	}
}
