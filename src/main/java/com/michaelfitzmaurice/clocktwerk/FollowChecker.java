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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import twitter4j.Relationship;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;

/**
 * Reports on whether or not two specified users
 * follow each other.
 * 
 * @author Michael Fitzmaurice
 */
public class FollowChecker {
    
    private static final transient Logger LOG = 
            LoggerFactory.getLogger(FollowChecker.class);

    public static void main(String[] args) 
    throws Exception {

        String source = args[0];
        String target = args[1];
        
        LOG.info("Setting up Twitter client...");
        Twitter twitter = new TwitterFactory().getInstance();
        LOG.info("Twitter client connected");
        
        LOG.info("Inspecting Twitter relationship between {} and {} ", 
                    source, target);
        Relationship relationship = 
                twitter.showFriendship(source, target);
        boolean targetFollowsSource = relationship.isSourceFollowedByTarget();
        boolean sourceFollowsTarget = relationship.isSourceFollowingTarget();
        
        LOG.info("{} follows {}: {}", 
                new Object[] {target, source, targetFollowsSource} );
        LOG.info("{} follows {}: {}", 
                new Object[] {source, target, sourceFollowsTarget} );
    }
}
