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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

public class TweetDaemon {
	
    private static final int MAX_TWEET_LENGTH = 140;
    private static final int INITIAL_TWEET_INDEX = 0;
    
	// default to 6 hours between tweets
	private static final long DEFAULT_TWEET_INTERVAL_MILLISECONDS = 
		1000 * 60 * 60 * 6; 
	private static final String TWEET_INTERVAL_PROPERTY = 
		"tweetdaemon.tweetinterval.milliseconds";
	
	private static final transient Logger LOG = 
		LoggerFactory.getLogger(TweetDaemon.class);
	
	private List<String> tweets;
	private int tweetIndex = INITIAL_TWEET_INDEX;
	private Twitter twitter;
	private int tweetsPosted;
	
	public TweetDaemon() throws IOException {
		
		tweets = getTweetList();
		twitter = authenticateToTwitter();
	}
	
	private List<String> getTweetList() throws IOException {
		
		ArrayList<String> tweetList = new ArrayList<String>();
		
		// TODO - make tweet file location a sys prop
		LOG.info("Looking for tweets file...");
		URL tweetFileUrl = 
			new Object().getClass().getResource("/tweets.txt");
		if (tweetFileUrl == null) {
		    throw new IOException("No tweets.txt file found on the classpath");
		}
		
		LOG.info("Found tweets file at {}", tweetFileUrl);
		File tweetFile = new File( tweetFileUrl.getPath() );
		BufferedReader reader = 
			new BufferedReader( new FileReader(tweetFile) );
		
		LOG.info("Parsing tweets from file at {}", tweetFileUrl);
		int lineNumber = 0;
		String line = reader.readLine();
		while (line != null) {
		    lineNumber++;
		    String tweet = line.trim();
			if (tweet.length() > MAX_TWEET_LENGTH) {
			    LOG.warn("Tweet on line {} > {} characters - ignoring...", 
			            lineNumber,
			            MAX_TWEET_LENGTH);
			} else {
			    tweetList.add(tweet);
			}
			line = reader.readLine();
		}
		
		reader.close();
		LOG.info("Parsed {} tweets from file at {}", 
					tweetList.size(), 
					tweetFileUrl);
		
		return tweetList;
	}
	
	private Twitter authenticateToTwitter() throws IOException {
		
		LOG.info("Setting up Twitter client...");
		// TODO - add support for multiple accounts
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
        
        return twitter;
	}

	public void start() {
		
		LOG.info("Starting tweet daemon...");
		// TODO - schedule a job per user account
		ScheduledExecutorService scheduler = 
			Executors.newScheduledThreadPool(1);
		scheduler.scheduleAtFixedRate( getTweetRunnable(), 
										0, 
										getTweetInterval(), 
										TimeUnit.MILLISECONDS);
		LOG.info("Daemon started; tweeting every {} ms", getTweetInterval() );
	}
	
	private Runnable getTweetRunnable() {
		
		return new Runnable() {

			@Override
			public void run() {
				if ( tweetIndex == tweets.size() ) {
					LOG.info("Reached the end of the tweet file; starting " 
								+ "from the beginning again");
					tweetIndex = INITIAL_TWEET_INDEX;
				}
				
				String tweet = tweets.get(tweetIndex);
				try {
					LOG.info("Sending tweet number {} as user {}: '{}'", 
							new Object[] {
					            tweetIndex,
    							twitter.getOAuthAccessToken().getUserId(),
    							tweet});
					twitter.updateStatus(tweet);
					tweetIndex++;
					tweetsPosted++;
					LOG.info("Posted {} tweets since startup", tweetsPosted);
				} catch (TwitterException e) {
					// swallow
					LOG.warn("Failed to send tweet", e);
				}
			}
		};
	}
	
	private long getTweetInterval() {
		 return Long.getLong(TWEET_INTERVAL_PROPERTY, 
				 			DEFAULT_TWEET_INTERVAL_MILLISECONDS);
	}


	public static void main(String[] args) throws IOException {
		
		TweetDaemon tweetDaemon = new TweetDaemon();
		tweetDaemon.start();
	}
		
}
