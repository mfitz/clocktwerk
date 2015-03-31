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
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Properties;

import org.prevayler.Prevayler;
import org.prevayler.PrevaylerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import com.michaelfitzmaurice.clocktwerk.RandomTweetIndex;
import com.michaelfitzmaurice.clocktwerk.TweetDatabase;
import com.michaelfitzmaurice.clocktwerk.TweetResponder;

/**
 * A Twitter bot that harvests mentions (@username) since
 * the last time it ran and sends each one a reply chosen
 * at random from a supplied list. All the configuration 
 * required is explicit and must be passed via command line 
 * arguments.
 * 
 * @author Michael Fitzmaurice, July 2013
 */
public class RandomReplyBot {
    
    private static final transient Logger LOG = 
            LoggerFactory.getLogger(RandomReplyBot.class);

    /**
     * @param args
     * @throws Exception 
     */
    public static void main(String[] args) 
    throws Exception {
        
        if (args.length < 3) {
            String msg = 
                "Error - you must supply full path to Persistence Directory, " 
                + "full path to replies file, and full path to Twitter OAuth " 
                + "file. Cannot do anything without these values - exiting.";
            System.err.println(msg);
            LOG.error(msg);
            System.exit(1);
        }
        
        LOG.info("Setting up Prevayler for persistence...");
        String persistenceDirectory = args[0];
        Prevayler prevayler = 
            PrevaylerFactory.createPrevayler( new HashMap<String, Long>(), 
                                                persistenceDirectory );
        LOG.info("Prevayler created");
        
        LOG.info("Reading reply set in from file...");
        File repliesFile = new File(args[1]);
        TweetDatabase tweetDatabase = 
            new TweetDatabase( repliesFile, new RandomTweetIndex() );
        LOG.info("Created randomised reply set");
        
        LOG.info("Setting up Twitter client...");
        Properties oAuthProps = new Properties();
        oAuthProps.load( new FileInputStream( new File(args[2] ) ) );
        Configuration twitterAuthConf = 
            new ConfigurationBuilder()
                .setOAuthConsumerKey( 
                        oAuthProps.getProperty("oauth.consumerKey") )
                .setOAuthConsumerSecret( 
                        oAuthProps.getProperty("oauth.consumerSecret") )
                .setOAuthAccessToken( 
                        oAuthProps.getProperty("oauth.accessToken") )
                .setOAuthAccessTokenSecret( 
                        oAuthProps.getProperty("oauth.accessTokenSecret") )
                .build();
        Twitter twitterClient = 
                new TwitterFactory(twitterAuthConf).getInstance();
        if (twitterClient.getAuthorization().isEnabled() == false) {
            String msg = 
                "Fatal - not authenticated to Twitter. " 
                + "Check values in Twitter OAuth file before restarting.";
            LOG.error(msg);
            throw new IOException(msg);
        }
        LOG.info("Twitter client connected and authenticated");
        
        TweetResponder responder = 
            new TweetResponder(prevayler, tweetDatabase, twitterClient);
        Collection<Status> mentionsToRespondTo = 
            responder.getNewMentions();
        responder.respondToMentions(mentionsToRespondTo);
    }

}
