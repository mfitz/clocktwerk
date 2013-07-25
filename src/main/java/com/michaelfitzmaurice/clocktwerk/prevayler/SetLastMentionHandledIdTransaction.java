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
