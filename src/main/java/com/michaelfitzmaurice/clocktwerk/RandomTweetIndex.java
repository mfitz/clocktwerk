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

import java.util.Random;

/**
 * A TweetIndex implementation that gives a random value when
 * asked for the current or incremented index, rather than 
 * actually keeping track of the index. The random value will 
 * always be between 0 (inclusive) and the number of Tweets 
 * being managed (exclusive).
 * 
 * @author Michael Fitzmaurice, July 2013
 */
public class RandomTweetIndex implements TweetIndex {
    
    private int numberOfTweets = -1;
    private Random random = new Random();

    @Override
    public int incrementAndGetIndex() {
        return randomIndexValue();
    }

    @Override
    public int getIndex() {
        return randomIndexValue();
    }
    
    private int randomIndexValue() {
        
        if (numberOfTweets == -1) {
            return -1;
        } else {
            return random.nextInt(numberOfTweets);
        }
    }

    @Override
    public int getNumberOfTweets() {
        return numberOfTweets;
    }
    
    @Override
    public void setNumberOfTweets(int numberOfTweets) {
        this.numberOfTweets = numberOfTweets;
    }

}
