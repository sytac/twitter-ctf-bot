package com.sytac.twitter_ctf_bot.client;

import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.WriteResult;

public class MongoDBClient {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MongoDBClient.class);
	
	private MongoClient client;
	
	private final static String DB_NAME = "bot";
	
	private final static String RAW_MESSAGES_COLL_NAME = "raw";
	
	private final static String DM_COLL_NAME = "dm";
	
	private final static String MENTION_COLL_NAME = "mention";
	
	private final static String EVENT_COLL_NAME = "event";
	
	
	
	public MongoDBClient(String url, int port){
		try {
			setClient(new MongoClient(url, port));
		} catch (UnknownHostException e) {
			LOGGER.error("error connecting mongodb",e);
		}
	}

	public MongoClient getClient() {
		return client;
	}

	public void setClient(MongoClient client) {
		this.client = client;
	}

	
	public DB getDB(){
		return client.getDB(DB_NAME);
	}
	
	
	public DBCollection getOrCreateCollection(String collName){
		return getDB().getCollection(collName);
	}
	
	public void printAllCollection(DBCollection collection){
    	DBCursor cur = collection.find();
    	try{
    		while(cur.hasNext()){
    			DBObject curr = cur.next();
    			System.out.println(curr);
    		}
    	}finally{
    		cur.close();
    	}
    }
	
	public boolean dropCollection(String collName){
		try{
			getDB().getCollection(collName).drop();
			return true;
		}catch(Exception e){
			return false;
		}

	}

	public boolean insertObjIntoCollection(DBCollection coll, DBObject o){
		WriteResult a = coll.insert(o);
		return true;
	}
	
}
