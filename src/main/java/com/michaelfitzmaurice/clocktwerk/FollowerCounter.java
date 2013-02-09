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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.User;

/**
 * Records number of followers for a supplied list of twitter users.
 * The number of followers for each user is appended to a CSV file 
 * named <screenName>-followers.csv in a specified parent directory.
 * 
 * @author Michael Fitzmaurice
 */
public class FollowerCounter {
    
    private static final transient Logger LOG = 
            LoggerFactory.getLogger(FollowerCounter.class);

    public static void main(String[] args) 
    throws Exception {

        File outputDir = new File(args[0]);
        String[] users = new String[args.length - 1];
        System.arraycopy(args, 1, users, 0, users.length);
        
        LOG.info("Setting up Twitter client...");
        Twitter twitter = new TwitterFactory().getInstance();
        LOG.info("Twitter client connected");
        
        LOG.info("Getting followers for users " + Arrays.asList(users) );
        ResponseList<User> foundUsers = twitter.lookupUsers(users);
        for (User user : foundUsers) {
            LOG.info("\t'{}' has {} followers", 
                        user.getName(), 
                        user.getFollowersCount() );
            recordFollowersToCsv(outputDir, 
                                user.getScreenName(), 
                                user.getFollowersCount() );
        }
    }
    
    private static void recordFollowersToCsv(File parentDir, 
                                            String username, 
                                            int followers) 
    throws IOException {
        
        SimpleDateFormat dateFormat = 
                new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String timeString = dateFormat.format( new Date() );
        
        File outputFile = new File(parentDir, username + "-followers.csv");
        FileWriter fileWiter = new FileWriter(outputFile, true);
        fileWiter.write("\n" + timeString + ", " + followers);
        fileWiter.close();
        
        LOG.info("Recorded number of followers at {} to file at {}", 
                    timeString, 
                    outputFile);
    }
    
}
