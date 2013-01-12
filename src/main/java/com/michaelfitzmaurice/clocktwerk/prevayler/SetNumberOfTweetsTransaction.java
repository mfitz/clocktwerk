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

import java.util.Date;
import java.util.HashMap;

import org.prevayler.Transaction;

public class SetNumberOfTweetsTransaction implements Transaction {

    private static final long serialVersionUID = 9038534696796501018L;

    private Integer numberOfTweets;
    
    public SetNumberOfTweetsTransaction(int numberOfTweets) {
        super();
        this.numberOfTweets = numberOfTweets;
    }

    @Override
    public void executeOn(Object prevaylentSystem, Date ignored) {
       
        @SuppressWarnings("unchecked")
        HashMap<String, Integer> map = 
            (HashMap<String, Integer>)prevaylentSystem;
        map.put(PrevaylentTweetIndex.NUMBER_OF_TWEETS_KEY, numberOfTweets);
    }

}
