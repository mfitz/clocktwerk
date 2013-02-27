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

import static java.lang.String.format;
import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reportMatcher;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.Random;

import org.easymock.IArgumentMatcher;
import org.junit.Test;
import org.prevayler.Prevayler;

public class PrevaylentTweetIndexTest {
    
    @Test (expected = RuntimeException.class)
    public void wrapsExceptionFromPrevaylerIncrementingAndGettingIndex()
    throws Exception {
        
        Exception exception = new Exception("POW!");
        
        Prevayler mockPrevayler = createStrictMock(Prevayler.class);
        mockPrevayler.execute( 
                (IncrementAndGetTweetIndexTransaction)anyObject() );
        expectLastCall().andThrow(exception);
        replay(mockPrevayler);

        PrevaylentTweetIndex index = new PrevaylentTweetIndex(mockPrevayler);
        
        try {
            index.incrementAndGetIndex();
        } catch (RuntimeException e) {
            assertSame("Expected root exception to be wrapped", 
                        exception, 
                        e.getCause() );
            throw e;
        } finally {
            verify(mockPrevayler);
        }
    }
    
    @Test
    public void delegatesIncrementingAndGettingIndexToPrevayler()
    throws Exception {
        
        Integer randomIndexValue = randomPositiveInteger();
        
        Prevayler mockPrevayler = createStrictMock(Prevayler.class);
        mockPrevayler.execute( 
                (IncrementAndGetTweetIndexTransaction)anyObject() );
        expectLastCall().andReturn(randomIndexValue);
        replay(mockPrevayler);

        PrevaylentTweetIndex index = new PrevaylentTweetIndex(mockPrevayler);
        String errorMsg = 
            format("Index was not that returned by the Prevayler object -", 
                    randomIndexValue);
        assertEquals(errorMsg, 
                    randomIndexValue.intValue(), 
                    index.incrementAndGetIndex() );
        
        verify(mockPrevayler);
    }
    
    @Test (expected = RuntimeException.class)
    public void wrapsExceptionFromPrevaylerGettingIndex()
    throws Exception {
        
        Exception exception = new Exception("POW!");
        
        Prevayler mockPrevayler = createStrictMock(Prevayler.class);
        mockPrevayler.execute( (QueryTweetIndex)anyObject() );
        expectLastCall().andThrow(exception);
        replay(mockPrevayler);

        PrevaylentTweetIndex index = new PrevaylentTweetIndex(mockPrevayler);
        
        try {
            index.getIndex();
        } catch (RuntimeException e) {
            assertSame("Expected root exception to be wrapped", 
                        exception, 
                        e.getCause() );
            throw e;
        } finally {
            verify(mockPrevayler);
        }
    }
    
    @Test
    public void delegatesGettingIndexToPrevayler()
    throws Exception {
        
        Integer randomIndexValue = randomPositiveInteger();
        
        Prevayler mockPrevayler = createStrictMock(Prevayler.class);
        mockPrevayler.execute( (QueryTweetIndex)anyObject() );
        expectLastCall().andReturn(randomIndexValue);
        replay(mockPrevayler);

        PrevaylentTweetIndex index = new PrevaylentTweetIndex(mockPrevayler);
        String errorMsg = 
            format("Index was not that returned by the Prevayler object -", 
                    randomIndexValue);
        assertEquals(errorMsg, 
                    randomIndexValue.intValue(), 
                    index.getIndex() );
        
        verify(mockPrevayler);
    }
    
    @Test (expected = RuntimeException.class)
    public void wrapsExceptionFromPrevaylerGettingNumberOfTweets()
    throws Exception {
        
        Exception exception = new Exception("POW!");
        
        Prevayler mockPrevayler = createStrictMock(Prevayler.class);
        mockPrevayler.execute( (QueryNumberOfTweets)anyObject() );
        expectLastCall().andThrow(exception);
        replay(mockPrevayler);

        PrevaylentTweetIndex index = new PrevaylentTweetIndex(mockPrevayler);
        
        try {
            index.getNumberOfTweets();
        } catch (RuntimeException e) {
            assertSame("Expected root exception to be wrapped", 
                        exception, 
                        e.getCause() );
            throw e;
        } finally {
            verify(mockPrevayler);
        }
    }
    
    @Test
    public void delegatesGettingNumberOfTweetsToPrevayler()
    throws Exception {
        
        Integer randomNumberOfTweets = randomPositiveInteger();
        
        Prevayler mockPrevayler = createStrictMock(Prevayler.class);
        mockPrevayler.execute( (QueryNumberOfTweets)anyObject() );
        expectLastCall().andReturn(randomNumberOfTweets);
        replay(mockPrevayler);

        PrevaylentTweetIndex index = new PrevaylentTweetIndex(mockPrevayler);
        String errorMsg = 
            format("Number of tweets was not that returned by the Prevayler object -", 
                    randomNumberOfTweets);
        assertEquals(errorMsg, 
                    randomNumberOfTweets.intValue(), 
                    index.getNumberOfTweets() );
        
        verify(mockPrevayler);
    }
    
    @Test
    public void usesSpecialValueWhenPrevaylerDoesNotKnowNumberOfTweets()
    throws Exception {
        
        Prevayler mockPrevayler = createStrictMock(Prevayler.class);
        mockPrevayler.execute( (QueryNumberOfTweets)anyObject() );
        expectLastCall().andReturn(null);
        replay(mockPrevayler);

        PrevaylentTweetIndex index = new PrevaylentTweetIndex(mockPrevayler);
        String errorMsg = 
            "Should return -1 to represent unknown number of tweets";
        assertEquals( errorMsg, -1, index.getNumberOfTweets() );
        
        verify(mockPrevayler);
    }
    
    @Test
    public void delegatesSettingNumberOfTweetsValueToPrevayler()
    throws Exception {
        
        Integer numberOfTweetsValue = randomPositiveInteger();
        SetNumberOfTweetsTransaction transaction = 
            new SetNumberOfTweetsTransaction( numberOfTweetsValue.intValue() );
        
        Prevayler mockPrevayler = createStrictMock(Prevayler.class);
        mockPrevayler.execute( eqSetNumberOfTweetsTransaction(transaction) );
        replay(mockPrevayler);

        PrevaylentTweetIndex index = new PrevaylentTweetIndex(mockPrevayler);
        index.setNumberOfTweets( numberOfTweetsValue.intValue() );
        
        verify(mockPrevayler);
    }
    
    ///////////////////////////////////////////////////////
    // helper methods
    ///////////////////////////////////////////////////////

    private Integer randomPositiveInteger() {
        return  new Integer( new Random().nextInt(100000) );
    }
    
    // custom Matcher for command object passed to Prevayler
    
    public static SetNumberOfTweetsTransaction eqSetNumberOfTweetsTransaction(
            SetNumberOfTweetsTransaction expected) {

        reportMatcher( new SetNumberOfTweetsTransactionEquals(expected) );
        return expected;
    }

    static class SetNumberOfTweetsTransactionEquals implements IArgumentMatcher {

        private SetNumberOfTweetsTransaction expected;
        SetNumberOfTweetsTransactionEquals(SetNumberOfTweetsTransaction expected){
            this.expected = expected;
        }

        @Override
        public void appendTo(StringBuffer arg0) {
            arg0.append("eqSetNumberOfTweetsTransaction(");
            arg0.append( expected.getNumberOfTweets() );
            arg0.append(")");
        }

        @Override
        public boolean matches(Object arg0) {
            if ( (arg0 instanceof SetNumberOfTweetsTransaction) == false ){
                return false;
            }

            return ( (SetNumberOfTweetsTransaction)arg0).getNumberOfTweets().equals(
                    expected.getNumberOfTweets() );
        }
    }

}
