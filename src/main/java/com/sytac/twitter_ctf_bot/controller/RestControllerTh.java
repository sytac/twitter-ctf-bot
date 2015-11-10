package com.sytac.twitter_ctf_bot.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.*;
import com.sytac.twitter_ctf_bot.client.MongoDBClient;
import com.sytac.twitter_ctf_bot.client.TwitterClient;

/**
 * A simple rest controller for future use
 */
@SuppressWarnings("restriction")
public class RestControllerTh extends Thread{


	private static MongoDBClient mongo;
	private static TwitterClient twitter;
	
	public RestControllerTh(MongoDBClient m, TwitterClient twit){
		mongo = m;
		twitter = twit;
	}

	public void run(){
		HttpServer server;
		try {
			server = HttpServer.create(new InetSocketAddress(8080), 0);
	        server.createContext("/api/leaderboard.json", new LeaderBoardH());
	        server.createContext("/participants.html", new ParticipantsH());
	        server.createContext("/users.html", new FriendsListH());
	        server.setExecutor(null); // creates a default executor
	        server.start();
		} catch (IOException e){}
	}
	
	
	
	static class LeaderBoardH implements HttpHandler {
		@Override
        public void handle(HttpExchange t) throws IOException {
            String response = mongo.leaderBoard();
            Headers h = t.getResponseHeaders();
            h.set("Content-Type", "application/json");
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

	static class ParticipantsH implements HttpHandler {
		@Override
        public void handle(HttpExchange t) throws IOException {
            String response = mongo.participants();
            Headers h = t.getResponseHeaders();
            h.set("Content-Type", "text/html");
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
	
	static class FriendsListH implements HttpHandler {
		@Override
        public void handle(HttpExchange t) throws IOException {
            Headers h = t.getResponseHeaders();
            h.set("Content-Type", "text/plain");
            OutputStream os = t.getResponseBody();
            t.sendResponseHeaders(200,0);
            twitter.printFriendsInfos(os);
            os.close();
        }
    }
	
	
}