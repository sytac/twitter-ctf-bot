package com.sytac.twitter_ctf_bot;

import org.junit.Test;

import java.io.File;
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
    public void main_happyFlow() throws URISyntaxException {
        String path = findClassPathLocation("test-configuration.properties");
        BotApp.main(new String[]{path});
        // no exceptions -> we're good!
    }

    /**
     * Given the classpath location of a file, returns the concrete path on the file system
     *
     * @param s The classpath location to find
     * @return The file system location of the provided classpath
     */
    private String findClassPathLocation(String s) throws URISyntaxException {

        return new File(this.getClass().getClassLoader().getResource(s).toURI()).getAbsolutePath();
    }
}
