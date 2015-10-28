package com.sytac.twitter_ctf_bot.conf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Class for the properties file
 * @author Tonino Catapano - tonino.catapano@sytac.io
 *
 */
public class Prop {
	
	private static Properties CONF_FILE;
	private final static Logger LOGGER = LoggerFactory.getLogger(Prop.class);

	
	private static Prop instance = null;
	protected Prop() {}  // Exists only to defeat instantiation.

	
	public static Prop getInstance() {
		if(instance == null) {		
			instance = new Prop();	
	    }
	    return instance;
	}

	/**
	 * Load the properties file
	 * @param path
	 */
	public void initPropFile(String path){	
		try {
			InputStream in = Files.newInputStream(Paths.get(path));
			if (path == null || path.isEmpty() || in == null){
				LOGGER.error("Please specificate a valid path for the properties file in the first argument");
				return;
			}
			CONF_FILE = new Properties();
			CONF_FILE.load(in);
			in.close();

			consumerKey = CONF_FILE.getProperty("consumerKey");
			consumerSecret = CONF_FILE.getProperty("consumerSecret");
			token = CONF_FILE.getProperty("token");
			secret = CONF_FILE.getProperty("secret");			
			WELCOME_PARTICIPANT_MESSAGE  = CONF_FILE.getProperty("WELCOME_PARTICIPANT_MESSAGE");
			COULDNOT_FOLLOW_MESSAGE   = CONF_FILE.getProperty("COULDNOT_FOLLOW_MESSAGE");
			RIGHT_ANSWER_MESSAGE  = CONF_FILE.getProperty("RIGHT_ANSWER_MESSAGE");
			WRONG_ANSWER_MESSAGE  = CONF_FILE.getProperty("WRONG_ANSWER_MESSAGE");
			WINNER_MESSAGE  = CONF_FILE.getProperty("WINNER_MESSAGE");
			BAD_MESSAGE  = CONF_FILE.getProperty("BAD_MESSAGE");
			WELCOME_NO_FOLLOW_MESSAGE = CONF_FILE.getProperty("WELCOME_NO_FOLLOW_MESSAGE");			
			FLAG_KEYWORD = CONF_FILE.getProperty("FLAG_KEYWORD");
			TWITTER_DM_ENDPOINT = CONF_FILE.getProperty("TWITTER_DM_ENDPOINT");
			SYTAC_REST_ENDPOINT = CONF_FILE.getProperty("SYTAC_REST_ENDPOINT");
			FLAG_KEY = CONF_FILE.getProperty("FLAG_KEY");
			PARTIC_ID_KEY = CONF_FILE.getProperty("PARTIC_ID_KEY");
			PARTIC_NAME_KEY = CONF_FILE.getProperty("PARTIC_NAME_KEY");		
			SYTAC_USER_ID = Long.valueOf(CONF_FILE.getProperty("SYTAC_USER_ID"));
			FLAG_KEYWORD = CONF_FILE.getProperty("FLAG_KEYWORD");
		} catch(IOException e) {
			LOGGER.error("Error while reading the properties file: "+ path, e);
			return;
		}
	}
	
	
	
	//get the keys and tokens from the CONF_FILE
	public String consumerKey;
	public String consumerSecret;
	public String token;
	public String secret;
	
	
	/** User related parameters **/
	public String WELCOME_PARTICIPANT_MESSAGE;
	public String COULDNOT_FOLLOW_MESSAGE;
	public String RIGHT_ANSWER_MESSAGE;
	public String WRONG_ANSWER_MESSAGE;
	public String WINNER_MESSAGE;
	public String BAD_MESSAGE;
	public String WELCOME_NO_FOLLOW_MESSAGE;
	
	public String FLAG_KEYWORD;
	
	
	/** Endpoint-related parameters **/
	public String TWITTER_DM_ENDPOINT;
	public String SYTAC_REST_ENDPOINT;
	public String FLAG_KEY ;
	public String PARTIC_ID_KEY ;
	public String PARTIC_NAME_KEY ;
	
	public long SYTAC_USER_ID;
}
