package com.michaelfitzmaurice.clocktwerk.prevayler;

import static com.michaelfitzmaurice.clocktwerk.prevayler.PrevaylentTweetIndex.INDEX_KEY;
import static com.michaelfitzmaurice.clocktwerk.prevayler.PrevaylentTweetIndex.NUMBER_OF_TWEETS_KEY;
import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import org.junit.Test;

public class IncrementAndGetTweetIndexTransactionTest {
    
    private static final Date NULL_DATE = null;
    
    @Test (expected = ClassCastException.class)
    public void rejectsPrevaylentSystemIfNotAHashMap() 
    throws Exception {
         
        IncrementAndGetTweetIndexTransaction transaction = 
                new IncrementAndGetTweetIndexTransaction();
        try {
            transaction.executeAndQuery(new HashSet<String>(), NULL_DATE);
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
            (Integer)transaction.executeAndQuery(prevaylentSystem, NULL_DATE);
        
        Integer zero = new Integer(0);
        assertEquals(zero, queryResult);
        assertEquals( zero, prevaylentSystem.get(INDEX_KEY) );
    }
    
    @Test
    public void incrementsExistingIndexValueWhenNumberOfTweetsIsUnknown()
    throws Exception {
        
        Integer oldIndexValue = randomPositiveInteger();
        Integer incrementedIndex = new Integer(oldIndexValue.intValue() + 1);
        Integer unknownNumberOfTweets = null;
        HashMap<String, Integer> prevaylentSystem = 
                prevalentSystem(oldIndexValue, unknownNumberOfTweets);
        
        assertIncrementedIndexEquals(incrementedIndex, prevaylentSystem);
    }
    
    @Test
    public void incrementsExistingIndexValueWhenWrapAroundNotReached()
    throws Exception {
        
        Integer oldIndexValue = randomPositiveInteger();
        Integer incrementedIndex = new Integer(oldIndexValue.intValue() + 1);
        Integer numberOfTweets = new Integer(incrementedIndex.intValue() + 1);
        HashMap<String, Integer> prevaylentSystem = 
                prevalentSystem(oldIndexValue, numberOfTweets);
        
        assertIncrementedIndexEquals(incrementedIndex, prevaylentSystem);
    }
    
    @Test
    public void wrapsIndexValueAroundWhenWhenItEqualsNumberOfTweets()
    throws Exception {
        
        Integer oldIndexValue = randomPositiveInteger();
        Integer wrappedAroundIndex = new Integer(0);
        Integer numberOfTweets = new Integer(oldIndexValue.intValue() + 1);
        HashMap<String, Integer> prevaylentSystem = 
                prevalentSystem(oldIndexValue, numberOfTweets);
        
        assertIncrementedIndexEquals(wrappedAroundIndex, prevaylentSystem);
    }

    @Test
    public void wrapsIndexValueAroundWhenWhenItExceedsNumberOfTweets()
    throws Exception {
        
        Integer oldIndexValue = randomPositiveInteger();
        Integer wrappedAroundIndexValue = new Integer(0);
        Integer numberOfTweets = new Integer(oldIndexValue.intValue() - 1);
        HashMap<String, Integer> prevaylentSystem = 
                prevalentSystem(oldIndexValue, numberOfTweets);
        
        assertIncrementedIndexEquals(wrappedAroundIndexValue, prevaylentSystem);
    }
    
    ///////////////////////////////////////////////////////
    // helper methods
    ///////////////////////////////////////////////////////
    
    private Integer randomPositiveInteger() {
        return new Integer( new Random().nextInt(100000) );
    }
    
    private HashMap<String, Integer> prevalentSystem(Integer indexValue, 
                                                    Integer numberOfTweets) {
        
        HashMap<String, Integer> prevaylentSystem = 
                new HashMap<String, Integer>();
        prevaylentSystem.put(INDEX_KEY, indexValue);
        prevaylentSystem.put(NUMBER_OF_TWEETS_KEY, numberOfTweets);
        
        return prevaylentSystem;
    }
    
    private void assertIncrementedIndexEquals(Integer expectedIncrementedIndex,
                                      HashMap<String, Integer> prevaylentSystem) 
    throws Exception {
        
        IncrementAndGetTweetIndexTransaction transaction = 
                new IncrementAndGetTweetIndexTransaction();
        Integer queryResult = 
            (Integer)transaction.executeAndQuery(prevaylentSystem, NULL_DATE);
        assertEquals(expectedIncrementedIndex, queryResult);
        assertEquals( expectedIncrementedIndex, prevaylentSystem.get(INDEX_KEY) );
    }

}
