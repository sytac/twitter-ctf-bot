package com.sytac.twitter_ctf_bot;

import com.sytac.twitter_ctf_bot.conf.Prop;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;

/**
 * Tests the loading of a configuration file from the file system
 */
public class ConfigurationTest {

    @Test
    public void canLoadProperties() throws URISyntaxException, IOException {
        String path = Utils.findClassPathLocation("test-configuration.properties").get();
        Prop configuration = new Prop(path);

        assertEquals("Couldn't read the configuration properly", "foobar", configuration.secret);
    }

}
