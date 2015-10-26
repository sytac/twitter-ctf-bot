package com.sytac.twitter_ctf_bot;

import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Tests the loading of a configuration file from the file system
 */
public class ConfigurationTest {

    @Test
    public void canLoadProperties() throws URISyntaxException, IOException {
        String path = Utils.findClassPathLocation("test-configuration.properties");
        new Configuration(path);
        // no exceptions -> we're good!
    }

}
