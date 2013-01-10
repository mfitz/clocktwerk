package com.michaelfitzmaurice.clocktwerk.prevayler;

import java.util.Date;
import java.util.HashMap;

import org.prevayler.TransactionWithQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IncrementAndGetTweetIndexTransaction 
implements TransactionWithQuery {

    private static final long serialVersionUID = 2481715210452100704L;
    
    private static final transient Logger LOG = 
        LoggerFactory.getLogger(IncrementAndGetTweetIndexTransaction.class);

    @Override
    public Object executeAndQuery(Object prevaylentSystem, Date ignored) 
    throws Exception {
        
        LOG.debug("Reading tweet index from Prevayler...");
        
        @SuppressWarnings("unchecked")
        HashMap<String, Integer> map = 
            (HashMap<String, Integer>)prevaylentSystem;
        
        int currentIndex = -1;
        Integer persistedIndex = map.get(PrevaylentTweetIndex.INDEX_KEY);
        if (persistedIndex != null) {
            LOG.debug("Found index {} in Prevayler", persistedIndex);
            currentIndex = persistedIndex.intValue();
        }
        
        LOG.debug("Incrementing index...");
        currentIndex++;
        LOG.debug("Index incremented to {}", currentIndex);
        
        Integer persistedNumberOfTweets = 
                map.get(PrevaylentTweetIndex.NUMBER_OF_TWEETS_KEY);
        if (persistedNumberOfTweets != null) {
            LOG.debug("Checking if index needs to wrap around...");
            if ( currentIndex >= persistedNumberOfTweets.intValue() ) {
                LOG.info("Wrapping around to 1st tweet; index={}, no. tweets={}", 
                        currentIndex, persistedNumberOfTweets);
                currentIndex = 0;
            }
        }
        map.put(PrevaylentTweetIndex.INDEX_KEY, currentIndex);
        
        return new Integer(currentIndex);
    }

}
