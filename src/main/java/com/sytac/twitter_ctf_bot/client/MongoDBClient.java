package com.sytac.twitter_ctf_bot.client;

import java.net.UnknownHostException;
import java.util.Date;

import org.mongojack.JacksonDBCollection;
import org.mongojack.WriteResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.sytac.twitter_ctf_bot.model.DM;
import com.sytac.twitter_ctf_bot.model.Event;
import com.sytac.twitter_ctf_bot.model.Mention;
import com.sytac.twitter_ctf_bot.model.Participant;

public class MongoDBClient {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MongoDBClient.class);
	
	private final static String DB_NAME = "bot";
	private final static String DM_COLL_NAME = "dm";
	private final static String MENTION_COLL_NAME = "mention";
	private final static String EVENT_COLL_NAME = "event";
	private final static String PARTICIP_COLL_NAME = "participant";
	
	private MongoClient client;
	
	public MongoDBClient(String url, int port){
		try {
			setClient(new MongoClient(url, port));
		} catch (UnknownHostException e) {
			LOGGER.error("error while connecting to Mongo",e);
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
	

	public boolean storeMention(Mention m){
		try{
			final DBCollection mentions = getOrCreateCollection(MENTION_COLL_NAME);
			final JacksonDBCollection<Mention, String> coll = JacksonDBCollection.wrap(mentions, Mention.class, String.class);
			final WriteResult<Mention, String> result = coll.insert(m);
			LOGGER.info("Mention correctly stored into the database with id: " + result.getSavedId());
		}catch(Exception e){
			LOGGER.error("error",e);
			return false;
		}
		return true;
	}
	
	public boolean storeNewParticipant(Participant p){
		try{
			final DBCollection participantsColl = getOrCreateCollection(PARTICIP_COLL_NAME);
			final JacksonDBCollection<Participant, String> j_participantsColl = JacksonDBCollection.wrap(participantsColl, Participant.class, String.class);
			try{
				j_participantsColl.insert(p);
			}catch(MongoException e){
				if (e.getCode() == 11000){
					LOGGER.info("Participant \'@" + p.getUser_name() + "\' already in the competition, not stored");
				}else{
					LOGGER.error("Unpredictable MongoDB error: ", e);
				}
				return false;
			}
			LOGGER.info("new Participant \'@" + p.getUser_name() + "\' stored in db and running for the competition.");
		}catch(Exception e){
			LOGGER.error("error",e);
			return false;
		}
		return true;
	}
	
	/**
	 * update the answer array of the participant {@code user_id} with
	 * @param quizNr
	 * @param user_Id
	 * @return
	 */
	public byte newGoodAnswer(byte quizNr, String user_Id){
		try{
			final DBCollection competitionColl = getOrCreateCollection(PARTICIP_COLL_NAME);
			final JacksonDBCollection<Participant, String> coll = JacksonDBCollection.wrap(competitionColl, Participant.class, String.class);	
			Participant p = coll.findOne(new BasicDBObject("user_Id", user_Id));
			if (p == null) return 2; //participant not registred, send him a reminder to register (to mention @sytac #ctf)
			if(p.checkWinner()) return 1;
			final Participant result = coll.findAndModify(
				new BasicDBObject("user_Id", user_Id), //query
				null, //the fields I want back: null specify to return ALL THE FIELDS
				null, //sort CRITERIA
				false, //remove the document after modifying it
				new BasicDBObject("$set", new BasicDBObject("foundFlags." + quizNr, true)).append("$set", new BasicDBObject("lastUpdate", new Date())), //the update query I want to execute
				true, //true indicate to return the object AFTER the UPDATE in the last row (false make it return before)
				true // UPSERT: if the document does not exist then create one.
			);
			if(result.checkWinner())
				return 1;
			return 0;
		}catch(Exception e){
			LOGGER.error("error",e);
			return -1;
		}	
	}
	
	
	public boolean storeDM(DM dm){
		try{
			final DBCollection dms = getOrCreateCollection(DM_COLL_NAME);
			final JacksonDBCollection<DM, String> coll = JacksonDBCollection.wrap(dms, DM.class, String.class);
			final DM toStore = new DM(dm.getUser_Id(), dm.getUser_name(), dm.getDm_string());
			final WriteResult<DM, String> result = coll.insert(toStore);
			LOGGER.info("DM correctly stored into the database with id: " + result.getSavedId());
		}catch(Exception e){
			LOGGER.error("error",e);
			return false;
		}
		return true;
	}
	
	public boolean storeEvent(Event e){
		try{
			final DBCollection dms = getOrCreateCollection(EVENT_COLL_NAME);
			final JacksonDBCollection<Event, String> coll = JacksonDBCollection.wrap(dms, Event.class, String.class);
			final Event toStore = new Event(e.getUser_Id(), e.getUser_name(), e.getEventName());
			final WriteResult<Event, String> result = coll.insert(toStore);
			LOGGER.info("DM correctly stored into the database with id: " + result.getSavedId());
		}catch(Exception ex){
			LOGGER.error("error", ex);
			return false;
		}
		return true;
	}
	
	
}
