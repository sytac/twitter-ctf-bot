package com.sytac.twitter_ctf_bot;

import com.twitter.hbc.core.Client;
import org.junit.Before;
import org.junit.Test;
import twitter4j.Twitter;

import java.util.concurrent.BlockingQueue;

import static org.mockito.Mockito.*;

/**
 * Tests the correct execution of the Bot
 */
@SuppressWarnings("unchecked")
public class BotTest {

    private Bot bot;
    private Twitter twitter = mock(Twitter.class);
    private Client stream = mock(Client.class);
    private Configuration config = mock(Configuration.class);
    private BlockingQueue<String> queue = mock(BlockingQueue.class);

    @Before
    public void setup(){
        bot = new Bot(config, twitter, stream, queue);
    }

    @Test
    public void canRun() {
        bot.run();
        // no exceptions -> we're good!
    }

    @Test
    public void clientIsStoppedUponException(){
        doThrow(new IllegalStateException("foobar!")).when(stream).connect();

        try {
            bot.run();
        } catch (IllegalStateException e) {
            // NOP
        }
        verify(stream, times(1)).stop();
    }

}
