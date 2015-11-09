package com.sytac.twitter_ctf_bot.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.*;
import com.sytac.twitter_ctf_bot.client.MongoDBClient;

/**
 * A simple rest controller for future use
 */
@SuppressWarnings("restriction")
public class RestControllerTh extends Thread{


	private static MongoDBClient mongo;
	
	public RestControllerTh(MongoDBClient m){
		mongo = m;
	}

	public void run(){
		HttpServer server;
		try {
			server = HttpServer.create(new InetSocketAddress(8080), 0);
	        server.createContext("/api/leaderboard.json", new MyHandler());
	        server.createContext("/participants.html", new MyHandler2());
	        server.setExecutor(null); // creates a default executor
	        server.start();
		} catch (IOException e){}
	}
	
	
	
	static class MyHandler implements HttpHandler {
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

	static class MyHandler2 implements HttpHandler {
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
	
	
}