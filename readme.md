[![Build Status](https://semaphoreci.com/api/v1/projects/b0913859-9af4-4f2f-a727-223ce7010c4e/588384/badge.svg)](https://semaphoreci.com/skuro/twitter-ctf-bot)

# twitter-ctf-bot
A Twitter bot written in Java+MongoDB to manage the Sytac Capture The Flag competition at the #JFall2015.

## Features
Manages the whole competition via Twitter, namely:

- registers new competitors and re-follow them
- automatically sends tweets or DMs to participants to better explain the rules of the game
- allows participants to attempt at solutions via DMs
- sends feedback on flag found / wrong flag attempts
- save all DMs, Tweet and Events (e.g. follow), to a MongoDB database, skip all the ctf non-related messages.

## 3rd Party libs
Fetching of all the user-related messages via [HBC](https://github.com/twitter/hbc) and the twitter [Streaming API](https://dev.twitter.com/streaming/overview).
The [REST-API](https://dev.twitter.com/rest/public) is accessed via [twitter4j](https://github.com/yusuke/twitter4j/).
(DMs, follows and tweets)

## Install Requirements
* JDK 8
* Maven 3.x

## Usage
1. Create a config file like the example [here](https://github.com/sytac/twitter-ctf-bot/raw/master/src/examples/config.properties), specifying your application tokens.
2. launch `MVN package` to create the uber jar
3. from the terminal:    
    `java -jar twitter-ctf-bot-1.0.0-SNAPSHOT.jar path/to/properties.file`

## License
All the code and documentation in this repository are distributed under [MIT license](https://opensource.org/licenses/MIT).
