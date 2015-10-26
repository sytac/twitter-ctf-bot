package com.sytac.twitter_ctf_bot;

import java.io.File;
import java.net.URISyntaxException;

/**
 * Utility class to make testing easier
 */
public abstract class Utils {

    /**
     * Given the classpath location of a file, returns the concrete path on the file system
     *
     * @param path The classpath location to find
     * @return The file system location of the provided classpath
     */
    public static  String findClassPathLocation(String path) throws URISyntaxException {

        return new File(Utils.class.getClassLoader().getResource(path).toURI()).getAbsolutePath();
    }

}
