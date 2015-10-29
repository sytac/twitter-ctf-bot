package com.sytac.twitter_ctf_bot;

import java.util.concurrent.BlockingQueue;

import org.apache.log4j.Logger;

import com.sytac.twitter_ctf_bot.client.HosebirdClient;
import com.sytac.twitter_ctf_bot.conf.Prop;
import com.sytac.twitter_ctf_bot.model.ParsedJson;

/**
 * The reading thread which consume the twitter user input stream
 * @since 1.0
 * @author Tonino Catapano - tonino.catapano@sytac.io
 */
public class ReadingThread extends Thread {

	private static final Logger LOGGER = Logger.getLogger(ReadingThread.class);

    private final BlockingQueue<String> _inMessages;
    private final BlockingQueue<ParsedJson> _outMessages;
    private final HosebirdClient _hosebirdClient;
    private final JsonParser _parser;

    public ReadingThread(Prop props, HosebirdClient stream, BlockingQueue<String> incoming, BlockingQueue<ParsedJson> outgoing) {
        _hosebirdClient = stream;
        _inMessages = incoming;
        _outMessages = outgoing;
        _parser = new JsonParser(props);
    }


    /**
     * The loop that will intercept and handle the incoming messages for Sytac
     */
    public void run() {
        try {
            while (!_hosebirdClient.isDone()) {
                String msg = _inMessages.take();
				ParsedJson parsed = _parser.parse(msg);
				_outMessages.add(parsed);
            }
            _hosebirdClient.stop();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

}
