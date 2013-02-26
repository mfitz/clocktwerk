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
package com.michaelfitzmaurice.clocktwerk.prevayler;

import org.prevayler.Prevayler;

import com.michaelfitzmaurice.clocktwerk.TweetIndex;

public class PrevaylentTweetIndex implements TweetIndex {
    
    public static final String INDEX_KEY = "tweet_index";
    public static final String NUMBER_OF_TWEETS_KEY = "number_of_tweets";
    
    Prevayler prevayler;

    public PrevaylentTweetIndex(Prevayler prevayler) {
        this.prevayler = prevayler;
    }

    @Override
    public int incrementAndGetIndex() {
        
        try {
            return (Integer)prevayler.execute( 
                    new IncrementAndGetTweetIndexTransaction() );
        } catch (Exception e) {
            throw new RuntimeException(
                "Error incrementing & getting tweet index", e);            
        }
    }

    @Override
    public int getIndex() {
        
        try {
            return (Integer)prevayler.execute( new QueryTweetIndex() );
        } catch (Exception e) {
            throw new RuntimeException("Error getting tweet index", e);            
        }
    }

    @Override
    public void setIndex(int index) {
        
        prevayler.execute( new SetTweetIndexTransaction(
                                    new Integer(index) ) );
    }

    @Override
    public int getNumberOfTweets() {
        
        try {
            Integer persistedNumberOfTweets = 
                (Integer)prevayler.execute( new QueryNumberOfTweets() );
            
            if (persistedNumberOfTweets == null) {
                return -1;
            } else {
                return persistedNumberOfTweets.intValue();
            }
        } catch (Exception e) {
            throw new RuntimeException(
                "Error getting number of tweets", e);            
        }
    }

    @Override
    public void setNumberOfTweets(int numberOfTweets) {
        
        prevayler.execute( new SetNumberOfTweetsTransaction(
                                    new Integer(numberOfTweets) ) );
    }

}
