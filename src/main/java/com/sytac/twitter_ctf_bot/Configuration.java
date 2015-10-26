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
    private static final String FLAG_KEY        = "FLAG_KEY";

    // Twit template messages
    // TODO: move to message bundle
    private static final String WELCOME_PARTICIPANT_MESSAGE = "WELCOME_PARTICIPANT_MESSAGE";
    private static final String COULDNOT_FOLLOW_MESSAGE = "COULDNOT_FOLLOW_MESSAGE";
    private static final String BAD_MESSAGE = "BAD_MESSAGE";
    private static final String RIGHT_ANSWER_MESSAGE = "RIGHT_ANSWER_MESSAGE";
    private static final String WRONG_ANSWER_MESSAGE = "WRONG_ANSWER_MESSAGE";
    private static final String WELCOME_NO_FOLLOW_MESSAGE = "WELCOME_NO_FOLLOW_MESSAGE";

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
    public Long getOwnUserId(){
        return Long.parseLong(properties.getProperty(OWN_USER_ID));
    }

    /**
     * Return the name of the CTF twitter bot
     *
     * @return The CTF twitter bot name
     */
    public String getBotName() {
        return properties.getProperty(BOT_NAME);
    }

    /**
     * Returns the solution to the puzzle
     *
     * @return The solution to the puzzle
     */
    public String getFlagKeyword() {
        return properties.getProperty(FLAG_KEY);
    }

    /**
     * Return the welcome message template
     *
     * @return The welcome message template
     */
    public String getWelcomeMessage() {
        return properties.getProperty(WELCOME_PARTICIPANT_MESSAGE);
    }

    public String getCannotFollowMessage() {
        return properties.getProperty(COULDNOT_FOLLOW_MESSAGE);
    }

    public String getBadMessage() {
        return properties.getProperty(BAD_MESSAGE);
    }

    public String getRightAnswerMessage() {
        return properties.getProperty(RIGHT_ANSWER_MESSAGE);
    }

    public String getWrongAnswerMessage() {
        return properties.getProperty(WRONG_ANSWER_MESSAGE);
    }

    public String getWelcomeNoFollowMessage() {
        return properties.getProperty(WELCOME_NO_FOLLOW_MESSAGE);
    }
}
