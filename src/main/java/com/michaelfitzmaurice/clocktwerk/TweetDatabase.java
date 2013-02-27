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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TweetDatabase {
    
    static final int MAX_TWEET_LENGTH = 140;
    
    private static final transient Logger LOG = 
            LoggerFactory.getLogger(TweetDatabase.class);
    
    private ArrayList<String> tweetList = new ArrayList<String>();
    private TweetIndex tweetIndex;
    
    public TweetDatabase(File tweetFile, TweetIndex tweetIndex) 
    throws IOException {
        
        BufferedReader reader = 
            new BufferedReader( new FileReader(tweetFile) );
            
        int lineNumber = 0;
        String line = reader.readLine();
        while (line != null) {
            lineNumber++;
            String tweet = line.trim();
            if (tweet.length() > MAX_TWEET_LENGTH) {
                LOG.warn("Tweet on line {} > {} characters - ignoring...", 
                        lineNumber,
                        MAX_TWEET_LENGTH);
            } else {
                tweetList.add(tweet);
            }
            line = reader.readLine();
        }
        reader.close();
        
        this.tweetIndex = tweetIndex;
        updateNumberOfTweets();
    }

    private void updateNumberOfTweets() {
        
        int previousNumberOfTweets = tweetIndex.getNumberOfTweets();
        if ( previousNumberOfTweets != tweetList.size() ) {
            LOG.warn("No. lines in tweets.txt changed; previously {}, now {}", 
                      previousNumberOfTweets, 
                      tweetList.size() );
            tweetIndex.setNumberOfTweets( tweetList.size() );
        }
    }
    
    public String getNextTweet() {
        return tweetList.get( tweetIndex.incrementAndGetIndex() );
    }
    
    String[] getAllTweets() {
        return tweetList.toArray( new String[ tweetList.size() ] );
    }

}
