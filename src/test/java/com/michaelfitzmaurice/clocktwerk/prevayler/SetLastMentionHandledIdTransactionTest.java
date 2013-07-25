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

import static com.michaelfitzmaurice.clocktwerk.TweetResponder.LAST_MENTION_HANDLED_ID_KEY;
import static java.lang.String.format;
import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;

public class SetLastMentionHandledIdTransactionTest {
    
    private Long randomId;
    private Date ignoreDate = null;
    
    @Before
    public void setup() {
        randomId = new Long( new Random().nextInt(100000) ); 
    }
    
    @Test (expected = ClassCastException.class)
    public void rejectsPrevaylentSystemIfNotAHashMap() 
    throws Exception {
         
        SetLastMentionHandledIdTransaction transaction = 
            new SetLastMentionHandledIdTransaction(randomId);
        
        try {
            transaction.executeOn(new HashSet<String>(), ignoreDate);
        } catch (ClassCastException e) {
            String expectedMsg = 
                "java.util.HashSet cannot be cast to java.util.HashMap";
            assertEquals( expectedMsg, e.getMessage() );
            throw e;
        }
    }
    
    @Test
    public void writesIdValueToPrevayledHashMap() 
    throws Exception {
        
        HashMap<String, Long> prevayledMap = new HashMap<String, Long>();
        
        SetLastMentionHandledIdTransaction transaction = 
                new SetLastMentionHandledIdTransaction(randomId);
        transaction.executeOn(prevayledMap, ignoreDate);
        
        Long valueWrittenToMap = prevayledMap.get(LAST_MENTION_HANDLED_ID_KEY);
        String failMsg = 
            format("Prevayled map should hold the value written by transaction");
        assertEquals(failMsg, randomId, valueWrittenToMap);
    }

}
