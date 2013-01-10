package com.michaelfitzmaurice.clocktwerk.prevayler;

import java.util.Date;
import java.util.HashMap;

import org.prevayler.Query;

public class QueryNumberOfTweets implements Query {

    @Override
    public Object query(Object prevaylentSystem, Date ignored) 
    throws Exception {
        
        @SuppressWarnings("unchecked")
        HashMap<String, Integer> map = 
            (HashMap<String, Integer>)prevaylentSystem;
        
        return map.get(PrevaylentTweetIndex.NUMBER_OF_TWEETS_KEY);
    }

}
