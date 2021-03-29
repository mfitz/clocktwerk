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
import static com.michaelfitzmaurice.clocktwerk.prevayler.QueryLastMentionHandledId.NO_RECORD_IN_PREVAYLER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import org.junit.Test;

public class QueryLastMentionHandledIdTest {
    
    @Test (expected = ClassCastException.class)
    public void rejectsPrevaylentSystemIfNotAHashMap() 
    throws Exception {
         
        try {
            new QueryLastMentionHandledId().query(new HashSet<String>(), null);
        } catch (ClassCastException e) {
            String expectedMsg = "java.util.HashSet cannot be cast to";
            assertTrue( e.getMessage().contains(expectedMsg) );
            throw e;
        }
    }
    
    @Test
    public void returnsSpecialValueWhenNoValueStoredInPrevayler()
    throws Exception {
        
        HashMap<String, Long> prevayledMap = new HashMap<String, Long>();
        Long lastMentionHandledId = 
            (Long)new QueryLastMentionHandledId().query(prevayledMap, null);
        String failMsg =
            "When no value exists in Prevayler, last handled mention id should be -1";
        assertEquals(failMsg, NO_RECORD_IN_PREVAYLER, lastMentionHandledId);
    }
    
    @Test
    public void readsQueryResultFromHashMap() 
    throws Exception {
        
        Long expectedResult = new Long( new Random().nextInt(100000) );
        HashMap<String, Long> prevayledMap = new HashMap<String, Long>();
        prevayledMap.put(LAST_MENTION_HANDLED_ID_KEY, expectedResult);
        
        Long actualResult = 
            (Long)new QueryLastMentionHandledId().query(prevayledMap, null);
        assertEquals(expectedResult, actualResult);
    }

}
