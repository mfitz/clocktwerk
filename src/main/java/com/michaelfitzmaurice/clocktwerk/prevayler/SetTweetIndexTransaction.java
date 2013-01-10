package com.michaelfitzmaurice.clocktwerk.prevayler;

import java.util.Date;
import java.util.HashMap;

import org.prevayler.Transaction;

public class SetTweetIndexTransaction implements Transaction {

    private static final long serialVersionUID = -9094927426637877703L;
    
    private Integer tweetIndex;
    
    public SetTweetIndexTransaction(Integer tweetIndex) {
        super();
        this.tweetIndex = tweetIndex;
    }

    @Override
    public void executeOn(Object prevaylentSystem, Date ignored) {

        @SuppressWarnings("unchecked")
        HashMap<String, Integer> map = 
            (HashMap<String, Integer>)prevaylentSystem;
        map.put(PrevaylentTweetIndex.INDEX_KEY, tweetIndex);
    }

}
