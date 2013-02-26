package com.michaelfitzmaurice.clocktwerk.prevayler;

import static com.michaelfitzmaurice.clocktwerk.prevayler.PrevaylentTweetIndex.NUMBER_OF_TWEETS_KEY;
import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;

public class SetNumberOfTweetsTransactionTest {
    
    private int randomNumberOfTweets;
    
    @Before
    public void setup() {
        randomNumberOfTweets = new Random().nextInt(100000);
    }
    
    @Test (expected = ClassCastException.class)
    public void rejectsPrevaylentSystemIfNotAHashMap() 
    throws Exception {
         
        SetNumberOfTweetsTransaction transaction = 
            new SetNumberOfTweetsTransaction(randomNumberOfTweets);
        
        try {
            transaction.executeOn(new HashSet<String>(), null);
        } catch (ClassCastException e) {
            String expectedMsg = 
                "java.util.HashSet cannot be cast to java.util.HashMap";
            assertEquals( expectedMsg, e.getMessage() );
            throw e;
        }
    }
    
    @Test
    public void writesNumberOfTweetsToPrevayledHashMap() 
    throws Exception {
        
        HashMap<String, Integer> prevayledMap = new HashMap<String, Integer>();
        
        SetNumberOfTweetsTransaction transaction = 
            new SetNumberOfTweetsTransaction(randomNumberOfTweets);
        transaction.executeOn(prevayledMap, null);
        
        Integer valueWrittenToMap = prevayledMap.get(NUMBER_OF_TWEETS_KEY);
        assertEquals( randomNumberOfTweets, valueWrittenToMap.intValue() );
    }

}
