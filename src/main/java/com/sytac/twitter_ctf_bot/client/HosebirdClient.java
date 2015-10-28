package com.sytac.twitter_ctf_bot.client;

import java.util.concurrent.BlockingQueue;

import com.sytac.twitter_ctf_bot.conf.Prop;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.Hosts;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.UserstreamEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;

public class HosebirdClient {

	private Client hosebirdClient;

	public HosebirdClient(Prop prop, BlockingQueue<String> msgQueue) {
		UserstreamEndpoint userEndpoint = new UserstreamEndpoint();
		userEndpoint.withUser(true);
		Hosts hosebirdHosts = new HttpHosts(Constants.USERSTREAM_HOST);

		Authentication hosebirdAuth = new OAuth1(prop.consumerKey, prop.consumerSecret, prop.token, prop.secret);
		ClientBuilder builder = new ClientBuilder()
				.hosts(hosebirdHosts)
				.authentication(hosebirdAuth)
				.endpoint(userEndpoint)
				.processor(new StringDelimitedProcessor(msgQueue));
		hosebirdClient = builder.build();

		installShutdownHook();
	}

	private void installShutdownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread(){ //catch the shutdown hook
			@Override
			public void run(){
				hosebirdClient.stop();
			}
		});
	}

	public void connect() {
		hosebirdClient.connect();
	}

	public void stop() {
		hosebirdClient.stop();
	}

	public boolean isDone() {
		return hosebirdClient.isDone();
	}
	
}
