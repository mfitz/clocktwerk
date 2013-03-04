/**
 *    Copyright 2012 Michael Fitzmaurice
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
package com.michaelfitzmaurice.clocktwerk;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 * 
 * @author Michael Fitzmaurice https://github.com/mfitz
 */
public class TweetDaemon {
	
    // default to 6 hours between tweets
    static final long DEFAULT_TWEET_INTERVAL_MILLISECONDS = 
            1000 * 60 * 60 * 6; 
    static final String TWEET_INTERVAL_PROPERTY = 
            "tweetdaemon.tweetinterval.milliseconds";
	
	private static final transient Logger LOG = 
		LoggerFactory.getLogger(TweetDaemon.class);
	
	private Twitter twitterClient;
	private TweetDatabase tweetDatabase;
	private ScheduledExecutorService scheduler;
	private int tweetsPosted;
	
    public TweetDaemon(TweetDatabase tweetDatabase, 
	                   ScheduledExecutorService scheduledExecutorService, 
	                   Twitter twitterClient) {
	    
	    this.tweetDatabase = tweetDatabase;
	    this.scheduler = scheduledExecutorService;
	    this.twitterClient = twitterClient;
	}
	
	public void start() {
		
		LOG.info("Starting tweet daemon...");
		scheduler.scheduleAtFixedRate( getTweetRunnable(), 
										0, 
										getTweetInterval(), 
										TimeUnit.MILLISECONDS);
		LOG.info("Daemon started; tweeting every {} ms", getTweetInterval() );
	}
	
    public int tweetsPostedSinceStartup() {
	    return tweetsPosted;
    }
	
	private Runnable getTweetRunnable() {
		
		return new Runnable() {

			@Override
			public void run() {
			    try {
			        sendNextTweet();
			    } catch (TweetException e) {
			        // swallow
			        LOG.error("Failed to send scheduled tweet", e);
			    }
			}
		};
	}
	
	private long getTweetInterval() {
		 return Long.getLong(TWEET_INTERVAL_PROPERTY, 
				 			DEFAULT_TWEET_INTERVAL_MILLISECONDS);
	}

    public void sendNextTweet() 
    throws TweetException {
        
        LOG.info("Fetching next tweet...");
        String tweet = tweetDatabase.getNextTweet();
        
        try {
            String screenName = twitterClient.getScreenName();
            LOG.info("Sending tweet number {} as user {}: '{}'", 
                    new Object[] {
                        tweetsPosted,
                        screenName,
                        tweet});
            twitterClient.updateStatus(tweet);
            tweetsPosted++;
            LOG.info("Posted {} tweets since startup", tweetsPosted);
        } catch (TwitterException e) {
            String msg = 
                String.format("Error sending tweet '%s'", tweet);
            LOG.error(msg, e);
            throw new TweetException(msg, e);
        }
    }
		
}
