package com.sytac.twitter_ctf_bot;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Loads the configuration from a properties file and exposes it to the application
 *
 * @author Carlo Sciolla
 * @since 1.0
 */
public class Configuration {

    // Known configuration entries:
    private static final String CONSUMER_KEY    = "consumerKey";
    private static final String CONSUMER_SECRET = "consumerSecret";
    private static final String TOKEN           = "token";
    private static final String SECRET          = "secret";
    private static final String OWN_USER_ID     = "ownUserId";
    private static final String BOT_NAME        = "botName";

    private final Properties properties;

    /**
     * Constructs a new configuration from the backing Properties object
     *
     * @param properties The properties from which to retrieve the configuration
     */
    public Configuration(final Properties properties) {
        this.properties = properties;
    }

    /**
     * Constructs a new configuration from a file located at the given path
     *
     * @param path The physical file system path of the properties file containing the configuration
     * @throws IOException If something goes wrong when reading the configuration file
     */
    public Configuration(String path) throws IOException {
        this(asProperties(path));
    }

    /**
     * Reads the properties file at the given path into a {@link Properties} object
     *
     * @param path The path in which to find the configuration file
     * @return The configuration as a {@link Properties} file
     * @throws IOException If something goes wrong when reading the configuration file
     */
    private static Properties asProperties(String path) throws IOException {
        InputStream asStream = Files.newInputStream(Paths.get(path));
        Properties properties = new Properties();
        properties.load(asStream);

        return properties;
    }

    /**
     * Gets the consumer secret for the OAuth Twitter authentication
     *
     * @return The consumer secret
     */
    public String getConsumerSecret(){
        return properties.getProperty(CONSUMER_SECRET);
    }

    /**
     * Gets the token for the OAuth Twitter authentication
     *
     * @return The token
     */
    public String getToken() {
        return properties.getProperty(TOKEN);
    }

    /**
     * Gets the consumer key for the OAuth Twitter authentication
     *
     * @return The consumer key
     */
    public String getConsumerKey() {
        return properties.getProperty(CONSUMER_KEY);
    }

    /**
     * Gets the secret for the OAuth Twitter authentication
     *
     * @return The secret
     */
    public String getSecret() {
        return properties.getProperty(SECRET);
    }

    /**
     * Gets the own twitter user ID
     *
     * @return The own user id
     */
    public String getOwnUserId(){
        return properties.getProperty(OWN_USER_ID);
    }

    public String getBotName() {
        return properties.getProperty(BOT_NAME);
    }
}
