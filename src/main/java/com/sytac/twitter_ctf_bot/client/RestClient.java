package com.sytac.twitter_ctf_bot.client;

public class RestClient {

	
	/**
	  * Send the received answer for the quiz {@value quizNr} to the Sytac webapp for checking
	  * @param flag
	  * @param partecipantName
	  * @param partecipantId
	  * @return
	  */
	 public static boolean processAnswerToRemote(String flag, String partecipantName, long partecipantId ){	 
		 /*
		  try {
			 
			HttpResponse<String> a = Unirest.post(SYTAC_REST_ENDPOINT)
			  //.queryString("quizNr", quizNr)
			  .field(FLAG_KEY, flag)
			  .field(PARTIC_ID_KEY, partecipantId)
			  .field(PARTIC_NAME_KEY, partecipantName)
			  .asString();
			  //.asJson();
			if(a.getStatus() == 200) 
				return Boolean.valueOf(a.getBody());
			return false;		
		} catch (UnirestException e) {
			_LOGGER.error("Error during the HTTP-POST to the Sytac-webapp", e);
			
			return false;
			
		}*/
		 return false;
	 }
}
