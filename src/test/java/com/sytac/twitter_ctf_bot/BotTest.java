package com.sytac.twitter_ctf_bot;

import com.sytac.twitter_ctf_bot.client.HosebirdClient;
import com.sytac.twitter_ctf_bot.conf.Prop;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

/**
 * Tests the correct execution of the Bot
 */
public class BotTest {

    private Bot bot;
    private HosebirdClient stream = mock(HosebirdClient.class);
    private Prop config = mock(Prop.class);

    @Before
    public void setup(){
        bot = new Bot(config, stream, null);
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
