package com.michaelfitzmaurice.clocktwerk.prevayler;

import static com.michaelfitzmaurice.clocktwerk.prevayler.PrevaylentTweetIndex.NUMBER_OF_TWEETS_KEY;
import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import org.junit.Test;

public class QueryNumberOfTweetsTest {
    
    @Test (expected = ClassCastException.class)
    public void rejectsPrevaylentSystemIfNotAHashMap() 
    throws Exception {
         
        try {
            new QueryNumberOfTweets().query(new HashSet<String>(), null);
        } catch (ClassCastException e) {
            String expectedMsg = 
                "java.util.HashSet cannot be cast to java.util.HashMap";
            assertEquals( expectedMsg, e.getMessage() );
            throw e;
        }
    }
    
    @Test
    public void readsQueryResultFromHashMap() 
    throws Exception {
        
        Integer expectedResult = new Random().nextInt(100000);
        HashMap<String, Integer> prevayledMap = new HashMap<String, Integer>();
        prevayledMap.put(NUMBER_OF_TWEETS_KEY, expectedResult);
        
        Integer actualResult = 
            (Integer)new QueryNumberOfTweets().query(prevayledMap, null);
        assertEquals(expectedResult, actualResult);
    }
}
