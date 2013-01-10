package com.michaelfitzmaurice.clocktwerk;


/**
 * Manages the index into the tweets database
 * 
 * @author Michael Fitzmaurice
 */
public interface TweetIndex {

    public int incrementAndGetIndex();
    public int getIndex();
    public void setIndex(int index);
    
    public int getNumberOfTweets();
    public void setNumberOfTweets(int numberOfTweets);
}
