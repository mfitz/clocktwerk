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

import static com.michaelfitzmaurice.clocktwerk.TweetDatabase.MAX_TWEET_LENGTH;
import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

public class TweetDatabaseTest {
    
    private File tweetFile;
    private TweetIndex tweetIndex;
    private String[] tweets = {"tweet one", "tweet two", "tweet three"};
    
    @Before
    public void setup() throws Exception {
        tweetFile = createTweetsFile("tweet-file.txt", tweets);
        
        tweetIndex = createStrictMock(TweetIndex.class);
        tweetIndex.getNumberOfTweets();
        expectLastCall().andReturn(tweets.length);
        replay(tweetIndex);
    }

    @Test (expected = IOException.class)
    public void rejectsNonExistentTweetFile() 
    throws IOException {
        
        File nonExistant = new File("/tmp/does/not/exist");
        assertFalse(nonExistant + " should not exist, but does", 
                    nonExistant.exists() );
        
        new TweetDatabase(nonExistant, tweetIndex);
    }
    
    @Test
    public void ignoresTweetsLongerThanMaximumTweetLength() 
    throws IOException{
        
        StringBuffer tooLongTweet = new StringBuffer();
        for (int i = 0; i <= MAX_TWEET_LENGTH; i++) {
            tooLongTweet.append('X');
        }
        
        String legalTweet = "blah";
        String[] corruptedTweets = 
                new String[] {tooLongTweet.toString(), legalTweet};
        File corruptedFile = 
                createTweetsFile("corrupted-tweets", corruptedTweets);
        
        tweetIndex = createStrictMock(TweetIndex.class);
        tweetIndex.getNumberOfTweets();
        expectLastCall().andReturn(corruptedTweets.length - 1);
        replay(tweetIndex);
        
        TweetDatabase tweetDatabase = 
                new TweetDatabase(corruptedFile, tweetIndex);
        
        String[] tweetsAddedToDb = tweetDatabase.getAllTweets();
        assertEquals(1, tweetsAddedToDb.length);
        assertEquals(legalTweet, tweetsAddedToDb[0]);
    }
    
    @Test
    public void usesTweetIndexToFindNextTweet() 
    throws IOException {
        
        int index = 0;
        TweetDatabase tweetDatabase;
        
        for (int i = 0; i < tweets.length; i++) {
            tweetIndex = createStrictMock(TweetIndex.class);
            tweetIndex.getNumberOfTweets();
            expectLastCall().andReturn(tweets.length);
            tweetIndex.incrementAndGetIndex();
            expectLastCall().andReturn(index);
            replay(tweetIndex);
            
            tweetDatabase = new TweetDatabase(tweetFile, tweetIndex);
            assertEquals( tweets[index], tweetDatabase.getNextTweet() );            
            verify(tweetIndex); 
            
            index++;
        }
    }
    
    @Test
    public void resetsNumberOfTweetsOnIndexWhenTweetCountDiffersInTweetFile() 
    throws IOException {
        
        int oldNumberOfTweets = tweets.length - 1;
        tweetIndex = createStrictMock(TweetIndex.class);
        tweetIndex.getNumberOfTweets();
        expectLastCall().andReturn(oldNumberOfTweets);
        tweetIndex.setNumberOfTweets(tweets.length);
        replay(tweetIndex);
        
        new TweetDatabase(tweetFile, tweetIndex);
        verify(tweetIndex); 
    }
    
    ///////////////////////////////////////////////////////
    // helper methods
    ///////////////////////////////////////////////////////
    private File createTweetsFile(String tweetFileName, String[] tweets)
    throws IOException {
        
        File tempDir = new File( System.getProperty("java.io.tmpdir") );
        File tweetsFile = new File(tempDir, tweetFileName);
        tweetsFile.deleteOnExit();
        
        FileWriter writer = new FileWriter(tweetsFile);
        for (int i = 0; i < tweets.length; i++) {
            writer.write(tweets[i]);
            writer.write( System.getProperty("line.separator") );
        }
        writer.close();
        
        assertTrue( "Failed to create tweets file " + tweetsFile 
                        + " for test fixture", 
                    tweetsFile.exists() );
        
        return tweetsFile;
    }
}
