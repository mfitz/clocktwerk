package com.michaelfitzmaurice.clocktwerk.prevayler;

import java.util.Date;
import java.util.HashMap;

import org.prevayler.Transaction;

public class SetNumberOfTweetsTransaction implements Transaction {

    private static final long serialVersionUID = 9038534696796501018L;

    private Integer numberOfTweets;
    
    public SetNumberOfTweetsTransaction(int numberOfTweets) {
        super();
        this.numberOfTweets = numberOfTweets;
    }

    @Override
    public void executeOn(Object prevaylentSystem, Date ignored) {
       
        @SuppressWarnings("unchecked")
        HashMap<String, Integer> map = 
            (HashMap<String, Integer>)prevaylentSystem;
        map.put(PrevaylentTweetIndex.NUMBER_OF_TWEETS_KEY, numberOfTweets);
    }

}
