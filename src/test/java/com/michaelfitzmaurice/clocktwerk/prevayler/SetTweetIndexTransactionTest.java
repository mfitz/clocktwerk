package com.michaelfitzmaurice.clocktwerk.prevayler;

import static com.michaelfitzmaurice.clocktwerk.prevayler.PrevaylentTweetIndex.INDEX_KEY;
import static com.michaelfitzmaurice.clocktwerk.prevayler.PrevaylentTweetIndex.NUMBER_OF_TWEETS_KEY;
import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;

public class SetTweetIndexTransactionTest {
    
    private int randomTweetIndex;
    
    @Before
    public void setup() {
        randomTweetIndex = new Random().nextInt(100000);
    }
    
    @Test (expected = ClassCastException.class)
    public void rejectsPrevaylentSystemIfNotAHashMap() 
    throws Exception {
         
        SetTweetIndexTransaction transaction = 
            new SetTweetIndexTransaction(randomTweetIndex);
        
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
    public void writesTweetIndexValueToPrevayledHashMap() 
    throws Exception {
        
        HashMap<String, Integer> prevayledMap = new HashMap<String, Integer>();
        
        SetTweetIndexTransaction transaction = 
            new SetTweetIndexTransaction(randomTweetIndex);
        transaction.executeOn(prevayledMap, null);
        
        Integer valueWrittenToMap = prevayledMap.get(INDEX_KEY);
        assertEquals( randomTweetIndex, valueWrittenToMap.intValue() );
    }

}
