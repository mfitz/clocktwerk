package com.michaelfitzmaurice.clocktwerk.prevayler;

import java.util.Date;
import java.util.HashMap;

import org.prevayler.Query;

public class QueryTweetIndex implements Query {

    @Override
    public Integer query(Object prevaylentSystem, Date ignored) 
    throws Exception {
        
        @SuppressWarnings("unchecked")
        HashMap<String, Integer> map = 
            (HashMap<String, Integer>)prevaylentSystem;
        
        return map.get(PrevaylentTweetIndex.INDEX_KEY);
    }

}
