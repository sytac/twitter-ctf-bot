package com.sytac.twitter_ctf_bot.client;

import java.util.concurrent.BlockingQueue;

import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Hosts;
import com.twitter.hbc.core.endpoint.UserstreamEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;

public class HosebirdClient {
		
	private static HosebirdClient _instance = null;
	protected HosebirdClient() {}  // Exists only to defeat instantiation.

	public static HosebirdClient getInstance() {
		if(_instance == null) {
			_instance = new HosebirdClient();
	    }
	    return _instance;
	}

	private Client hosebirdClient;
	
	public Client getClient() {
		return hosebirdClient;
	}


	public void setClient(Client hosebirdClient) {
		this.hosebirdClient = hosebirdClient;
	}
	
	/**
	 * Initialize the HoseBird Client (STREAMING-API)
	 * @param consumerKey
	 * @param consumerSecret
	 * @param token
	 * @param secret
	 * @param msgQueue
	 * @param hosebirdHosts
	 */
	public void initializeHBC(String consumerKey,String consumerSecret,String token,String secret,BlockingQueue<String> msgQueue, Hosts hosebirdHosts){		
		UserstreamEndpoint userEndpoint = new UserstreamEndpoint();
		userEndpoint.withUser(true);
		
		Authentication hosebirdAuth = new OAuth1(consumerKey, consumerSecret, token, secret);
		ClientBuilder builder = new ClientBuilder()
				  .hosts(hosebirdHosts)
				  .authentication(hosebirdAuth)
				  .endpoint(userEndpoint)
				  .processor(new StringDelimitedProcessor(msgQueue));
		hosebirdClient = builder.build();
	}
	
	
}
