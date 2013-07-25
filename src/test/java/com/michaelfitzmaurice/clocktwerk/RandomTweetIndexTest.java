/**
 *    Copyright 2013 Michael Fitzmaurice
 * 
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0
 * 
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.michaelfitzmaurice.clocktwerk;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.junit.Test;

public class RandomTweetIndexTest {
    
    @Test
    public void defaultsToMinusOneForNumberOfTweets()
    throws Exception {
        
        RandomTweetIndex randomTweetIndex = new RandomTweetIndex();
        assertEquals("Unset value for number of tweets should be -1", 
                        -1, 
                        randomTweetIndex.getNumberOfTweets() );
    }
    
    @Test
    public void recordsNewValueForNumberOfTweets()
    throws Exception {
        
        RandomTweetIndex randomTweetIndex = new RandomTweetIndex();
        int numberOfTweets = randomInt();
        randomTweetIndex.setNumberOfTweets(numberOfTweets);
        assertEquals( "New number of tweets was not recroded", 
                        numberOfTweets, 
                        randomTweetIndex.getNumberOfTweets() );
    }
    
    @Test
    public void returnsMinusOneAsCurrentIndexWhenNumberOfTweetsUnset() {
        
        RandomTweetIndex randomTweetIndex = new RandomTweetIndex();
        assertEquals(
                "Index value should be -1 when number of tweets unknown", 
                -1, 
                randomTweetIndex.getIndex() );
    }
    
    @Test
    public void returnsMinusOneAsIncrementedIndexWhenNumberOfTweetsUnset() {
        
        RandomTweetIndex randomTweetIndex = new RandomTweetIndex();
        assertEquals(
            "Incremented index value should be -1 when number of tweets unknown", 
            -1, 
            randomTweetIndex.incrementAndGetIndex() );
    }
    
    @Test
    public void returnsRandomNumberInAppropriateRangeAsCurrentIndexWhenNumberOfTweetsIsSet() {
        
        RandomTweetIndex randomTweetIndex = new RandomTweetIndex();
        int numberOfTweets = 1000;
        randomTweetIndex.setNumberOfTweets(numberOfTweets);
        
        Set<Integer> valuesReturned = new HashSet<Integer>();
        for (int i = 0; i < numberOfTweets; i++) {
            int indexValue = randomTweetIndex.getIndex();
            valuesReturned.add(indexValue);
            assertInRange(indexValue, -1, numberOfTweets);
        }
        assertTrue("Index value is returning the same value every time ", 
                    valuesReturned.size() > 1);
    }
    
    @Test
    public void returnsRandomNumberInAppropriateRangeAsIncrementedIndexWhenNumberOfTweetsIsSet() {
        
        RandomTweetIndex randomTweetIndex = new RandomTweetIndex();
        int numberOfTweets = 1000;
        randomTweetIndex.setNumberOfTweets(numberOfTweets);
        
        Set<Integer> valuesReturned = new HashSet<Integer>();
        for (int i = 0; i < numberOfTweets; i++) {
            int indexValue = randomTweetIndex.incrementAndGetIndex();
            valuesReturned.add(indexValue);
            assertInRange(indexValue, -1, numberOfTweets);
        }
        assertTrue("Index value is returning the same value every time ", 
                    valuesReturned.size() > 1);
    }
    
    ///////////////////////////////////////////////////////
    // helper methods
    ///////////////////////////////////////////////////////
    
    private int randomInt() {
        return new Random().nextInt(1000);
    }
    
    private void assertInRange(int number, int floor, int ceiling) {
        assertTrue(format("Index value should be >= than %s", floor),
                    number >= floor);
        assertTrue(format("Index value should be less than %s", ceiling),
                    number < ceiling);
    }

}
