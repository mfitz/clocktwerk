package com.michaelfitzmaurice.clocktwerk.prevayler;

import static com.michaelfitzmaurice.clocktwerk.TweetResponder.LAST_MENTION_HANDLED_ID_KEY;
import static com.michaelfitzmaurice.clocktwerk.prevayler.QueryLastMentionHandledId.NO_RECORD_IN_PREVAYLER;
import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import org.junit.Test;

public class QueryLastMentionHandledIdTest {
    
    @Test (expected = ClassCastException.class)
    public void rejectsPrevaylentSystemIfNotAHashMap() 
    throws Exception {
         
        try {
            new QueryLastMentionHandledId().query(new HashSet<String>(), null);
        } catch (ClassCastException e) {
            String expectedMsg = 
                "java.util.HashSet cannot be cast to java.util.HashMap";
            assertEquals( expectedMsg, e.getMessage() );
            throw e;
        }
    }
    
    @Test
    public void returnsSpecialValueWhenNoValueStoredInPrevayler()
    throws Exception {
        
        HashMap<String, Long> prevayledMap = new HashMap<String, Long>();
        Long lastMentionHandledId = 
            (Long)new QueryLastMentionHandledId().query(prevayledMap, null);
        String failMsg =
            "When no value exists in Prevayler, last handled mention id should be -1";
        assertEquals(failMsg, NO_RECORD_IN_PREVAYLER, lastMentionHandledId);
    }
    
    @Test
    public void readsQueryResultFromHashMap() 
    throws Exception {
        
        Long expectedResult = new Long( new Random().nextInt(100000) );
        HashMap<String, Long> prevayledMap = new HashMap<String, Long>();
        prevayledMap.put(LAST_MENTION_HANDLED_ID_KEY, expectedResult);
        
        Long actualResult = 
            (Long)new QueryLastMentionHandledId().query(prevayledMap, null);
        assertEquals(expectedResult, actualResult);
    }

}
