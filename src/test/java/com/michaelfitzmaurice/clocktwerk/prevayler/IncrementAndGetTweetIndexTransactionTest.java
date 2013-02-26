package com.michaelfitzmaurice.clocktwerk.prevayler;

import static com.michaelfitzmaurice.clocktwerk.prevayler.PrevaylentTweetIndex.INDEX_KEY;
import static com.michaelfitzmaurice.clocktwerk.prevayler.PrevaylentTweetIndex.NUMBER_OF_TWEETS_KEY;
import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import org.junit.Test;

public class IncrementAndGetTweetIndexTransactionTest {
    
    @Test (expected = ClassCastException.class)
    public void rejectsPrevaylentSystemIfNotAHashMap() 
    throws Exception {
         
        IncrementAndGetTweetIndexTransaction transaction = 
                new IncrementAndGetTweetIndexTransaction();
        try {
            transaction.executeAndQuery(new HashSet<String>(), null);
        } catch (ClassCastException e) {
            String expectedMsg = 
                "java.util.HashSet cannot be cast to java.util.HashMap";
            assertEquals( expectedMsg, e.getMessage() );
            throw e;
        }
    }
    
    @Test
    public void initialisesIndexToZero()
    throws Exception {
        
        IncrementAndGetTweetIndexTransaction transaction = 
                new IncrementAndGetTweetIndexTransaction();
        
        HashMap<String, Integer> prevaylentSystem = 
                new HashMap<String, Integer>();
        Integer queryResult = 
            (Integer)transaction.executeAndQuery(prevaylentSystem, null);
        assertEquals( 0, queryResult.intValue() );
        
        Integer expectedStoredIndexValue = new Integer(0);
        assertEquals(expectedStoredIndexValue, 
                    prevaylentSystem.get(INDEX_KEY) );
    }
    
    @Test
    public void incrementsExistingIndexValue()
    throws Exception {
        
        Integer oldValue = new Integer( new Random().nextInt(100000) );
        Integer expectedNewValue = new Integer(oldValue.intValue() + 1);
        HashMap<String, Integer> prevaylentSystem = 
                new HashMap<String, Integer>();
        prevaylentSystem.put(INDEX_KEY, new Integer(oldValue) );
        
        IncrementAndGetTweetIndexTransaction transaction = 
                new IncrementAndGetTweetIndexTransaction();
        Integer queryResult = 
                (Integer)transaction.executeAndQuery(prevaylentSystem, null);
        assertEquals(expectedNewValue, queryResult);
        assertEquals( expectedNewValue, prevaylentSystem.get(INDEX_KEY) );
    }
    
    @Test
    public void incrementsExistingIndexValueWhenWhenWrapAroundNotReached()
    throws Exception {
        
        Integer oldValue = new Integer( new Random().nextInt(100000) );
        Integer expectedNewValue = new Integer(oldValue.intValue() + 1);
        Integer numberOfTweets = new Integer(expectedNewValue.intValue() + 1);
        HashMap<String, Integer> prevaylentSystem = 
                new HashMap<String, Integer>();
        prevaylentSystem.put(INDEX_KEY, new Integer(oldValue) );
        prevaylentSystem.put(NUMBER_OF_TWEETS_KEY, numberOfTweets);
        
        IncrementAndGetTweetIndexTransaction transaction = 
                new IncrementAndGetTweetIndexTransaction();
        Integer queryResult = 
                (Integer)transaction.executeAndQuery(prevaylentSystem, null);
        assertEquals(expectedNewValue, queryResult);
        assertEquals( expectedNewValue, prevaylentSystem.get(INDEX_KEY) );
    }
    
    @Test
    public void wrapsIndexValueAroundWhenWhenItEqualsNumberOfTweets()
    throws Exception {
        
        Integer oldValue = new Integer( new Random().nextInt(100000) );
        Integer expectedNewValue = new Integer(0);
        Integer numberOfTweets = new Integer(oldValue.intValue() + 1);
        HashMap<String, Integer> prevaylentSystem = 
                new HashMap<String, Integer>();
        prevaylentSystem.put(INDEX_KEY, new Integer(oldValue) );
        prevaylentSystem.put(NUMBER_OF_TWEETS_KEY, numberOfTweets);
        
        IncrementAndGetTweetIndexTransaction transaction = 
                new IncrementAndGetTweetIndexTransaction();
        Integer queryResult = 
                (Integer)transaction.executeAndQuery(prevaylentSystem, null);
        assertEquals(expectedNewValue, queryResult);
        assertEquals( expectedNewValue, prevaylentSystem.get(INDEX_KEY) );
    }
    
    @Test
    public void wrapsIndexValueAroundWhenWhenItExceedsNumberOfTweets()
    throws Exception {
        
        Integer oldValue = new Integer( new Random().nextInt(100000) );
        Integer expectedNewValue = new Integer(0);
        Integer numberOfTweets = new Integer(oldValue.intValue() - 1);
        HashMap<String, Integer> prevaylentSystem = 
                new HashMap<String, Integer>();
        prevaylentSystem.put(INDEX_KEY, new Integer(oldValue) );
        prevaylentSystem.put(NUMBER_OF_TWEETS_KEY, numberOfTweets);
        
        IncrementAndGetTweetIndexTransaction transaction = 
                new IncrementAndGetTweetIndexTransaction();
        Integer queryResult = 
                (Integer)transaction.executeAndQuery(prevaylentSystem, null);
        assertEquals(expectedNewValue, queryResult);
        assertEquals( expectedNewValue, prevaylentSystem.get(INDEX_KEY) );
    }

}
