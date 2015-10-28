package com.sytac.twitter_ctf_bot;

import com.sytac.twitter_ctf_bot.client.HosebirdClient;
import com.sytac.twitter_ctf_bot.conf.Prop;
import com.sytac.twitter_ctf_bot.model.ParsedJson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;

/**
 * The reading theard which consume the twitter user stream
 *
 * @author Tonino Catapano - tonino.catapano@sytac.io
 */
public class ReadingThread extends Thread {

    final static Logger LOGGER = LoggerFactory.getLogger(ReadingThread.class);

    private final BlockingQueue<String> _incoming;
    private final BlockingQueue<ParsedJson> _parsedMessages;
    private final HosebirdClient _hosebirdClient;
    private final JsonParser _parser;

    public ReadingThread(Prop props, HosebirdClient stream, BlockingQueue<String> incoming, BlockingQueue<ParsedJson> messages) {
        _hosebirdClient = stream;
        _incoming = incoming;
        _parsedMessages = messages;
        _parser = new JsonParser(props);
    }


    /**
     * The loop that will intercept and handle the incoming messages for Sytac
     */
    public void run() {
        try {
            while (!_hosebirdClient.isDone()) {
                String msg = _incoming.take();
				ParsedJson parsed = _parser.parse(msg);
                _parsedMessages.add(parsed);
            }
            _hosebirdClient.stop();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

}
