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
	
	//default to an hour between tweets
	private static final long DEFAULT_TWEET_INTERVAL_MILLISECONDS = 
		1000 * 60 * 60; 
	private static final String TWEET_INTERVAL_PROPERTY = 
		"tweetdaemon.tweetinterval.milliseconds";
	
	private static final transient Logger LOG = 
		LoggerFactory.getLogger(TweetDaemon.class);
	
	private List<String> tweets;
	private int tweetIndex;
	private Twitter twitter;
	
	public TweetDaemon() throws IOException {
		
		tweets = getTweetList();
		twitter = authenticateToTwitter();
	}
	
	private List<String> getTweetList() throws IOException {
		
		ArrayList<String> tweetList = new ArrayList<String>();
		
		LOG.debug("Looking for tweets file...");
		URL tweetFileUrl = 
			new Object().getClass().getResource("/tweets.txt");
		LOG.debug("Found for tweets file at {}", tweetFileUrl);
		File tweetFile = new File( tweetFileUrl.getPath() );
		BufferedReader reader = 
			new BufferedReader( new FileReader(tweetFile) );
		
		LOG.debug("Parsing tweets from file at {}", tweetFileUrl);
		String line = reader.readLine();
		while (line != null) {
			tweetList.add( line.trim() );
			line = reader.readLine();
		}
		reader.close();
		LOG.debug("Parsed {} tweets from file at {}", 
					tweetList.size(), 
					tweetFileUrl);
		
		return tweetList;
	}
	
	private Twitter authenticateToTwitter() throws IOException {
		
		LOG.debug("Setting up Twitter client...");
		Twitter twitter = new TwitterFactory().getInstance();
		if (twitter.getAuthorization().isEnabled() == false) {
			String msg = 
				"Not authenticated to twitter - check OAuth consumer " 
				+ "key/secret intwitter4j.properties";
			LOG.error(msg);
			throw new IOException(msg);
		}
		LOG.debug("Twitter client connected and authenticated");
        
        return twitter;
	}

	public void start() {
		
		LOG.debug("Starting tweet daemon...");
		ScheduledExecutorService scheduler = 
			Executors.newScheduledThreadPool(1);
		scheduler.scheduleAtFixedRate( getTweetRunnable(), 
										getTweetInterval(), 
										getTweetInterval(), 
										TimeUnit.MILLISECONDS);
		LOG.debug( "Daemon started to tweet every {} ms", getTweetInterval() );
	}
	
	private Runnable getTweetRunnable() {
		
		return new Runnable() {

			@Override
			public void run() {
				String tweet = tweets.get(tweetIndex);
				try {
					LOG.debug("Sending tweet number {}: '{}'", 
								tweetIndex++, 
								tweet);
					twitter.updateStatus(tweet);
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
