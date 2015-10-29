package com.sytac.twitter_ctf_bot.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.*;

/**
 * A simple rest controller for future use
 */
@SuppressWarnings("restriction")
public class RestControllerTh extends Thread{
	
	public RestControllerTh(){}

	public void start(){
		HttpServer server;
		try {
			server = HttpServer.create(new InetSocketAddress(8000), 0);
	        server.createContext("/test", new MyHandler());
	        server.setExecutor(null); // creates a default executor
	        server.start();
		} catch (IOException e){}
	}
	
	
	
	static class MyHandler implements HttpHandler {
		@Override
        public void handle(HttpExchange t) throws IOException {
            String response = "Ok";
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

}