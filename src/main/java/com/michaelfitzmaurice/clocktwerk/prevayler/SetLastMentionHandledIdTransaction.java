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

import java.util.Date;
import java.util.HashMap;

import org.prevayler.Transaction;

public class SetLastMentionHandledIdTransaction implements Transaction {

    private static final long serialVersionUID = -6338691596915597951L;
    
    private Long lastMentionHandledId;
    
    public SetLastMentionHandledIdTransaction(Long lastMentionHandledId) {
        this.lastMentionHandledId = lastMentionHandledId;
    }

    public Long getLastMentionHandledId() {
        return lastMentionHandledId;
    }

    @Override
    public void executeOn(Object prevaylentSystem, Date ignored) {

        @SuppressWarnings("unchecked")
        HashMap<String, Long> map = (HashMap<String, Long>)prevaylentSystem;
        map.put(LAST_MENTION_HANDLED_ID_KEY, lastMentionHandledId);
    }

}
