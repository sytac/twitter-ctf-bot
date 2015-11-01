package com.sytac.twitter_ctf_bot;

import org.junit.Test;

import java.net.URISyntaxException;

public class BotAppTest {

	@Test(expected = IllegalArgumentException.class)
	public void main_configurationIsNotFound() {
		String notExistent = "/path/to/nowhere";
		BotApp.main(new String[]{notExistent});
	}

	@Test(expected = IllegalArgumentException.class)
	public void main_configurationNotProvided() {
		BotApp.main(new String[]{});
	}

    @Test
    public void main_happyFlow() throws URISyntaxException, InterruptedException {
        String path = Utils.findClassPathLocation("test-configuration.properties");
        BotApp.main(new String[]{path});
		Thread.sleep(1000l); // no exceptions -> we're good!
		BotApp.stop();
    }
}
