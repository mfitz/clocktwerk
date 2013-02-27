package com.michaelfitzmaurice.clocktwerk;

import static com.michaelfitzmaurice.clocktwerk.TweetDaemon.DEFAULT_TWEET_INTERVAL_MILLISECONDS;
import static com.michaelfitzmaurice.clocktwerk.TweetDaemon.TWEET_INTERVAL_PROPERTY;
import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Random;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;

import twitter4j.Twitter;
import twitter4j.TwitterException;

public class TweetDaemonTest {
    
    private TweetDatabase tweetDatabase;
    private Twitter twitterClient;
    private ScheduledExecutorService scheduler;
    private String tweet = "Oranges and lemons";
    
    @Before
    public void setup() 
    throws Exception {
        
        tweetDatabase = createStrictMock(TweetDatabase.class);
        tweetDatabase.getNextTweet();
        expectLastCall().andReturn(tweet);
        replay(tweetDatabase);
        
        twitterClient = createStrictMock(Twitter.class);
        twitterClient.getScreenName();
        expectLastCall().andReturn("someone");
        twitterClient.updateStatus(tweet);
        expectLastCall().andReturn(null);
        replay(twitterClient);
    }
    
    @Test
    public void schedulesTweetsAtDefaultIntervalIfNotOverridden() {
        
        assertNull( System.getProperty(TWEET_INTERVAL_PROPERTY) );
        
        scheduler = 
            createStrictMock(ScheduledExecutorService.class);
        scheduler.scheduleAtFixedRate((Runnable)anyObject(), 
                                    eq(0l), 
                                    eq(DEFAULT_TWEET_INTERVAL_MILLISECONDS), 
                                    eq(TimeUnit.MILLISECONDS) );
        expectLastCall().andReturn(null);
        replay(scheduler);
        
        TweetDaemon tweetDaemon = 
            new TweetDaemon(tweetDatabase, scheduler, twitterClient);
        tweetDaemon.start();
        
        verify(scheduler);
    }
    
    @Test
    public void overridesDefaultTweetIntervalWithSystemProperty() {
        
        long randomIntervalMillis = new Random().nextLong();
        System.setProperty(TWEET_INTERVAL_PROPERTY, "" + randomIntervalMillis);
        
        try {
            scheduler = 
                createStrictMock(ScheduledExecutorService.class);
            scheduler.scheduleAtFixedRate((Runnable)anyObject(), 
                                        eq(0l), 
                                        eq(randomIntervalMillis), 
                                        eq(TimeUnit.MILLISECONDS) );
            expectLastCall().andReturn(null);
            replay(scheduler);
            
            TweetDaemon tweetDaemon = 
                new TweetDaemon(tweetDatabase, scheduler, twitterClient);
            tweetDaemon.start();
        } finally {
            System.clearProperty(TWEET_INTERVAL_PROPERTY);
            verify(scheduler);
        }
    }
    
    @Test
    public void sendsNextTweetWhenScheduledJobRuns() 
    throws Exception {
        
        TweetDaemon tweetDaemon = 
                new TweetDaemon(tweetDatabase, 
                                new SynchronousScheduler(1), 
                                twitterClient);
        tweetDaemon.start();
        
        verify(tweetDatabase);
        verify(twitterClient);
    }
    
    @Test
    public void updatesTweetsSentCountAfterSendingATweet()
    throws Exception {
        
        TweetDaemon tweetDaemon = 
                new TweetDaemon(tweetDatabase, 
                                new SynchronousScheduler(1), 
                                twitterClient);
        assertEquals( 0, tweetDaemon.tweetsPostedSinceStartup() );
        tweetDaemon.start();
        assertEquals( 1, tweetDaemon.tweetsPostedSinceStartup() );
    }
    
    @Test
    public void swallowsExceptionsSendingTweets() 
    throws Exception {
        
        twitterClient = createStrictMock(Twitter.class);
        twitterClient.getScreenName();
        expectLastCall().andReturn("someone");
        twitterClient.updateStatus(tweet);
        expectLastCall().andThrow( new TwitterException("BANG!") );
        replay(twitterClient);
        
        TweetDaemon tweetDaemon = 
                new TweetDaemon(tweetDatabase, 
                                new SynchronousScheduler(1), 
                                twitterClient);
        assertEquals( 0, tweetDaemon.tweetsPostedSinceStartup() );
        tweetDaemon.start();
        assertEquals( 0, tweetDaemon.tweetsPostedSinceStartup() );
        
        verify(twitterClient);
    }
    
    ///////////////////////////////////////////////////////
    // helper methods
    ///////////////////////////////////////////////////////
    
    private class SynchronousScheduler extends ScheduledThreadPoolExecutor {

        public SynchronousScheduler(int corePoolSize) {
            super(corePoolSize);
        }

        @Override
        public ScheduledFuture<?> scheduleAtFixedRate(Runnable command,
                                                    long initialDelay, 
                                                    long period, 
                                                    TimeUnit unit) {
            
            // just run the command once now in the current thread of execution
            command.run();
            
            return null;
        }
    }
    
}
