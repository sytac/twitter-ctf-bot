[![Build Status](https://semaphoreci.com/api/v1/projects/b0913859-9af4-4f2f-a727-223ce7010c4e/588384/badge.svg)](https://semaphoreci.com/skuro/twitter-ctf-bot)

# twitter-ctf-bot
A Twitter bot written in Java to manage a Capture The Flag competition.

## Features
Manages the whole competition via Twitter, namely:

- registers new competitors
- automatically sends tweets and DMs to participants to better explain the rules of the game
- allows participants to attempt at solutions via DMs
- sends feedback on flag found / wrong flag attempts

## 3rd Party libs
Automatio of twitter DM handling, re-follow, and more is done via [HBC](https://github.com/twitter/hbc) and the twitter [Streaming API](https://dev.twitter.com/streaming/overview).
The [REST-API](https://dev.twitter.com/rest/public) is accessed via [twitter4j](https://github.com/yusuke/twitter4j/).

# Status

## DONE/TBD

- [x] Fetch DM
- [x] Fetch mentions
- [x] Logging
- [ ] Proper testing
- [ ] More...

## Install Requirements

* JDK 8
* Maven 3.x
