package com.sytac.twitter_ctf_bot;

import org.junit.Test;

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

}
