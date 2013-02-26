package com.michaelfitzmaurice.clocktwerk;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

public class TweetDatabaseTest {
    
    private File tweetFile;
    
    @Before
    public void setup() {
        File tempDir = new File( System.getProperty("java.io.tmpdir") );
        tweetFile = new File(tempDir, "tweet-file.txt");
    } 
    
    @Test (expected = IOException.class)
    public void rejectsNonExistentTweetFile() 
    throws IOException {
        
        File nonExistant = new File("/tmp/does/not/exist");
        assertFalse( nonExistant.exists() );
        
        new TweetDatabase(nonExistant);
    }
    
    // helper methods
//    private 

}
