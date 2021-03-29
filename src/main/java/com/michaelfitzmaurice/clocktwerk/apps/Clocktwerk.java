/**
 *    Copyright 2013 Michael Fitzmaurice
 * 
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0
 * 
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.michaelfitzmaurice.clocktwerk.apps;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.Executors;

import org.prevayler.Prevayler;
import org.prevayler.PrevaylerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.michaelfitzmaurice.clocktwerk.TweetDaemon;
import com.michaelfitzmaurice.clocktwerk.TweetDatabase;
import com.michaelfitzmaurice.clocktwerk.TweetIndex;
import com.michaelfitzmaurice.clocktwerk.prevayler.PrevaylentTweetIndex;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;

public class Clocktwerk {
    
    private static final String SINGLE_TWEET_MODE_PROPERTY = "com.michaelfitzmaurice.clocktwerk.single-tweet-mode";

    private static final transient Logger LOG = 
            LoggerFactory.getLogger(Clocktwerk.class);
    
    public static void main(String[] args) throws Exception {
        File tweetFile = new File(args[0]);
        LOG.info("Tweeting from {}", tweetFile);
        
        // TODO - make prevayler dir a sys prop/command line arg
        Prevayler prevayler = 
            PrevaylerFactory.createPrevayler( 
                            new HashMap<String, Integer>() );
        TweetIndex tweetIndex = new PrevaylentTweetIndex(prevayler);
        TweetDatabase tweetDatabase = new TweetDatabase(tweetFile, tweetIndex);
        
        LOG.info("Setting up Twitter client...");
        // TODO - make twitter4j.properties location a sys prop
        Twitter twitter = new TwitterFactory().getInstance();
        if (twitter.getAuthorization().isEnabled() == false) {
            String msg = 
                    "Not authenticated to twitter - check OAuth consumer " 
                            + "key/secret in twitter4j.properties file";
            LOG.error(msg);
            throw new IOException(msg);
        }
        LOG.info("Twitter client connected and authenticated");

        TweetDaemon tweetDaemon = null;
        if (Boolean.getBoolean(SINGLE_TWEET_MODE_PROPERTY)) {
            LOG.info("Using single tweet mode....");
            tweetDaemon = new TweetDaemon(tweetDatabase, null, twitter);
            tweetDaemon.sendNextTweet();
            LOG.info("Sent next tweet - exiting...");
        } else {
            tweetDaemon = 
                new TweetDaemon(tweetDatabase, 
                                Executors.newSingleThreadScheduledExecutor(), 
                                twitter);
            LOG.info("Starting tweet daemon....");
            tweetDaemon.start();
            LOG.info("Started tweet daemon");
        }
    }
}
