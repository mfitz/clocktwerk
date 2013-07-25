package com.michaelfitzmaurice.clocktwerk.prevayler;

import static com.michaelfitzmaurice.clocktwerk.TweetResponder.LAST_MENTION_HANDLED_ID_KEY;

import java.util.Date;
import java.util.HashMap;

import org.prevayler.Query;

public class QueryLastMentionHandledId implements Query {
    
    public static final Long NO_RECORD_IN_PREVAYLER = new Long(1);

    @Override
    public Object query(Object prevaylentSystem, Date ignored) 
    throws Exception {
        
        @SuppressWarnings("unchecked")
        HashMap<String, Long> map = 
            (HashMap<String, Long>)prevaylentSystem;
        
        Long lastHandledmentionId = map.get(LAST_MENTION_HANDLED_ID_KEY);
        if (lastHandledmentionId == null) {
            return NO_RECORD_IN_PREVAYLER;
        } else {
            return lastHandledmentionId;
        }
    }

}
