package com.michaelfitzmaurice.clocktwerk.prevayler;

import org.prevayler.Prevayler;

import com.michaelfitzmaurice.clocktwerk.TweetIndex;

public class PrevaylentTweetIndex implements TweetIndex {
    
    public static final String INDEX_KEY = "tweet_index";
    public static final String NUMBER_OF_TWEETS_KEY = "number_of_tweets";
    
    Prevayler prevayler;

    public PrevaylentTweetIndex(Prevayler prevayler) {
        this.prevayler = prevayler;
    }

    @Override
    public int incrementAndGetIndex() {
        
        try {
            
            return (Integer)prevayler.execute( 
                    new IncrementAndGetTweetIndexTransaction() );
        } catch (Exception e) {
            throw new RuntimeException(
                "Error incrementing & getting tweet index", e);            
        }
    }

    @Override
    public int getIndex() {
        
        try {
            return (Integer)prevayler.execute( new QueryTweetIndex() );
        } catch (Exception e) {
            throw new RuntimeException("Error getting tweet index", e);            
        }
    }

    @Override
    public void setIndex(int index) {
        
        try {
            prevayler.execute( new SetTweetIndexTransaction(
                                    new Integer(index) ) );
        } catch (Exception e) {
            throw new RuntimeException("Error setting tweet index", e);            
        }
    }

    @Override
    public int getNumberOfTweets() {
        
        try {
            Integer persistedNumberOfTweets = 
                (Integer)prevayler.execute( new QueryNumberOfTweets() );
            
            if (persistedNumberOfTweets == null) {
                return -1;
            } else {
                return persistedNumberOfTweets.intValue();
            }
        } catch (Exception e) {
            throw new RuntimeException(
                "Error getting number of tweets", e);            
        }
    }

    @Override
    public void setNumberOfTweets(int numberOfTweets) {
        
        try {
            prevayler.execute( new SetNumberOfTweetsTransaction(
                                    new Integer(numberOfTweets) ) );
        } catch (Exception e) {
            throw new RuntimeException("Error setting no. tweets", e);            
        }
    }

}
