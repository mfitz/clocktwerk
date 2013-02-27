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

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.prevayler.Prevayler;
import org.prevayler.PrevaylerFactory;

import com.michaelfitzmaurice.clocktwerk.TweetDatabase;
import com.michaelfitzmaurice.clocktwerk.prevayler.PrevaylentTweetIndex;

public class TweetDatabaseTest {
    
    private File tweetFile;
    private PrevaylentTweetIndex tweetIndex;
    private String[] tweets = {"one", "two", "three"};
    
    @Before
    public void setup() throws Exception {
        File tempDir = new File( System.getProperty("java.io.tmpdir") );
        
        tweetFile = new File(tempDir, "tweet-file.txt");
        createTweetsFile(tweetFile, tweets);
        tweetFile.deleteOnExit();
        
        File prevaylerDir = 
            new File(tempDir, "prevayler-" + System.currentTimeMillis() );
        assertTrue( prevaylerDir.mkdir() );
        prevaylerDir.deleteOnExit();
        Prevayler prevayler = null;
            prevayler = 
                    PrevaylerFactory.createPrevayler( 
                            new HashMap<String, Integer>(), 
                            prevaylerDir.getAbsolutePath() );
        tweetIndex = new PrevaylentTweetIndex(prevayler);
        tweetIndex.setNumberOfTweets(tweets.length);
    } 
    
    @Test (expected = IOException.class)
    public void rejectsNonExistentTweetFile() 
    throws IOException {
        
        File nonExistant = new File("/tmp/does/not/exist");
        assertFalse( nonExistant.exists() );
        
        new TweetDatabase(nonExistant, tweetIndex);
    }
    
    @Test
    public void readsNextTweetFromNextLineOfTweetFile() 
    throws Exception {
        
        TweetDatabase tweetDatabase = 
            new TweetDatabase(tweetFile, tweetIndex);
        for (int i = 0; i < tweets.length; i++) {
            assertEquals( tweets[i], tweetDatabase.getNextTweet() );            
        }
    }
    
    @Test
    public void wrapsAroundToBeginningOfFileAfterReachingTheEnd()
    throws Exception {
        
        TweetDatabase tweetDatabase = 
            new TweetDatabase(tweetFile, tweetIndex);
        for (int i = 0; i < tweets.length; i++) {
            tweetDatabase.getNextTweet();            
        }
        
        assertEquals( tweets[0], tweetDatabase.getNextTweet() );
    }
    
    ///////////////////////////////////////////////////////
    // helper methods
    ///////////////////////////////////////////////////////
    private void createTweetsFile(File tweetFile, String[] tweets)
    throws IOException {
        
        FileWriter writer = new FileWriter(tweetFile);
        
        for (int i = 0; i < tweets.length; i++) {
            writer.write(tweets[i]);
            writer.write( System.getProperty("line.separator") );
        }
        
        writer.close();
    }

}
