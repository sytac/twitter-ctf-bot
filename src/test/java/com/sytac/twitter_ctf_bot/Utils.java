package com.sytac.twitter_ctf_bot;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Optional;

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
    public static Optional<String> findClassPathLocation(String path) throws URISyntaxException {
        URL resource = Utils.class.getClassLoader().getResource(path);
        if(resource == null) {
            return Optional.empty();
        }
        return Optional.of(new File(resource.toURI()).getAbsolutePath());
    }

}
