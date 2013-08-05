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

import static com.michaelfitzmaurice.clocktwerk.ResponseListBuilder.aResponseList;
import static com.michaelfitzmaurice.clocktwerk.TweetResponder.LAST_MENTION_HANDLED_ID_KEY;
import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.createStrictMock;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reportMatcher;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.easymock.IArgumentMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.prevayler.Prevayler;
import org.prevayler.PrevaylerFactory;
import org.prevayler.Query;

import twitter4j.GeoLocation;
import twitter4j.HashtagEntity;
import twitter4j.MediaEntity;
import twitter4j.Paging;
import twitter4j.Place;
import twitter4j.RateLimitStatus;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.URLEntity;
import twitter4j.User;
import twitter4j.UserMentionEntity;

import com.michaelfitzmaurice.clocktwerk.prevayler.SetLastMentionHandledIdTransaction;

public class TweetResponderTest {
    
    private File persistenceDir;
    private Prevayler prevayler;
    private Long lastSeenMentionId;
    private Twitter mockTwitterClient;
    private TweetDatabase mockTweetDb;
    private Paging paging;
    private User twitterUser;
    private String replyText = 
        "We have received your query - it's in a MAHOOSIVE queue...";
    private User mentioningUser;
    
    @Before
    public void setup() throws Exception {
        
        persistenceDir = 
            new File(System.getProperty("java.io.tmpdir"), "test-prevayler");
        FileUtils.forceMkdir(persistenceDir);
        FileUtils.forceDeleteOnExit(persistenceDir);
        lastSeenMentionId = new Long( new Random().nextInt(Integer.MAX_VALUE) );
        HashMap<String, Long> prevayledMap = new HashMap<String, Long>();
        prevayledMap.put(LAST_MENTION_HANDLED_ID_KEY, lastSeenMentionId);
        prevayler = 
            PrevaylerFactory.createPrevayler(prevayledMap, 
                                        persistenceDir.getAbsolutePath() );
        paging = new Paging(lastSeenMentionId);
        
        mockTweetDb = createNiceMock(TweetDatabase.class);
        mockTweetDb.getNextTweet();
        expectLastCall().andReturn(replyText).anyTimes();
        replay(mockTweetDb);
        
        String twitterUserHandle = "Receiver";
        twitterUser = userFrom("Mr. " + twitterUserHandle, twitterUserHandle);
        
        String mentionerHandle = "MentionU";
        mentioningUser = userFrom("Miss " + mentionerHandle, mentionerHandle);
        
        mockTwitterClient = createStrictMock(Twitter.class);
        mockTwitterClient.getScreenName();
        expectLastCall().andReturn(twitterUserHandle);
        replay(mockTwitterClient);
    }
    
    @After
    public void tearDown() throws IOException {
        
        if ( persistenceDir.exists() ) {
            FileUtils.deleteDirectory(persistenceDir);
        }
    }
    
    @Test (expected = TweetException.class)
    public void wrapsExceptionReadingLastMentionHandledRecord() 
    throws Exception {
        
        Exception exception = new Exception("POW!");
        
        Prevayler mockPrevayler = createStrictMock(Prevayler.class);
        mockPrevayler.execute( (Query)anyObject() );
        expectLastCall().andThrow(exception);
        replay(mockPrevayler);
        
        TweetResponder responder = 
            new TweetResponder(mockPrevayler, null, mockTwitterClient);
        try {
            responder.getNewMentions();
        } catch (TweetException e) {
            assertSame( exception, e.getCause() );
            throw e;
        }
    }
    
    @Test (expected = TweetException.class)
    public void wrapsExceptionFromTwitterClientGettingMentions() 
    throws Exception {
        
        TwitterException twitterException = new TwitterException("POW!");
        mockTwitterClient = createStrictMock(Twitter.class);
        mockTwitterClient.getScreenName();
        expectLastCall().andThrow(twitterException);
        replay(mockTwitterClient);
        
                
        try {
            new TweetResponder(prevayler, null, mockTwitterClient);
        } catch (TweetException e) {
            assertSame( twitterException, e.getCause() );
            throw e;
        }
    }
    
    @Test
    public void onlyFetchesUnseenMentions()
    throws Exception {
        
        mockTwitterClient = createStrictMock(Twitter.class);
        mockTwitterClient.getScreenName();
        expectLastCall().andReturn( twitterUser.getScreenName() );
        mockTwitterClient.getMentionsTimeline(paging);
        expectLastCall().andReturn( aResponseList().build() );
        replay(mockTwitterClient);
        
        TweetResponder responder = 
                new TweetResponder(prevayler, null, mockTwitterClient);
        responder.getNewMentions();
        
        verify(mockTwitterClient);
    }
    
    @Test
    public void fetchesMentionsReturnedByTwitter()
    throws Exception {
        
        ResponseList<Status> mentions = 
            aResponseList()
                .withStatus( statusFrom( randomUser(), 4l, randomTweetBody() ) )
                .withStatus( statusFrom( randomUser(), 3l, randomTweetBody() ) )
                .withStatus( statusFrom( randomUser(), 2l, randomTweetBody() ) )
                .withStatus( statusFrom( randomUser(), 1l, randomTweetBody() ) )
                .withStatus( statusFrom( randomUser(), 0l, randomTweetBody() ) )
                .build();
        
        mockTwitterClient = createStrictMock(Twitter.class);
        mockTwitterClient.getScreenName();
        expectLastCall().andReturn( twitterUser.getScreenName() );
        mockTwitterClient.getMentionsTimeline(paging);
        expectLastCall().andReturn(mentions);
        replay(mockTwitterClient);
        
        TweetResponder responder = 
                new TweetResponder(prevayler, null, mockTwitterClient);
        Collection<Status> returnedMentions = responder.getNewMentions();
        assertEquals(mentions, returnedMentions);
    }
    
    @Test (expected = TweetException.class)
    public void wrapsExceptionGettingNewMentions()
    throws Exception {
        
        TwitterException twitterException = new TwitterException("POW!");
        mockTwitterClient = createStrictMock(Twitter.class);
        mockTwitterClient.getScreenName();
        expectLastCall().andReturn( mentioningUser.getScreenName() );
        mockTwitterClient.getMentionsTimeline(paging);
        expectLastCall().andThrow(twitterException);
        replay(mockTwitterClient);
        
        TweetResponder responder = 
                new TweetResponder(prevayler, null, mockTwitterClient);
        try {
            responder.getNewMentions();
        } catch (TweetException e) {
            assertSame( twitterException, e.getCause() );
            throw e;
        } finally {
            verify(mockTwitterClient);
        }
    }
    
    @Test
    public void queriesTweetDatabaseForReplies()
    throws Exception {
        
        mockTwitterClient = createNiceMock(Twitter.class);
        mockTwitterClient.getScreenName();
        expectLastCall().andReturn( twitterUser.getScreenName() );
        mockTwitterClient.updateStatus( (StatusUpdate)anyObject() );
        expectLastCall().andReturn(null);
        replay(mockTwitterClient);
        
        TweetResponder responder = 
                new TweetResponder(prevayler, mockTweetDb, mockTwitterClient);
        responder.respondToMentions( 
            aResponseList()
                .withStatus( statusFrom( randomUser(), 1l, randomTweetBody() ) )
                .build() );
        
        verify(mockTweetDb);
    }
    
    @Test
    public void postsUpdatesInReplyToMentions()
    throws Exception {
  
        long idOfMention = lastSeenMentionId.longValue() + 1; 
        
        mockTwitterClient = createStrictMock(Twitter.class);
        mockTwitterClient.getScreenName();
        expectLastCall().andReturn( twitterUser.getScreenName() );
        mockTwitterClient.updateStatus( 
            replyUpdateFor( new User[] {},
                            idOfMention, 
                            replyText) );
        expectLastCall().andReturn(null);
        replay(mockTwitterClient);
        
        TweetResponder responder = 
                new TweetResponder(prevayler, mockTweetDb, mockTwitterClient);
        Status mention = 
                statusFrom(mentioningUser, 
                            idOfMention, 
                            randomTweetBody() );
        responder.respondToMentions(
                aResponseList().withStatus(mention).build() );
        
        verify(mockTwitterClient);
    }
    
    @Test
    public void copiesAllMentionedUsersIntoReply()
    throws Exception {
        
        long idOfMention = lastSeenMentionId.longValue() + 1; 
        User[] mentioned = mentionedUsers(twitterUser, 3);
        
        mockTwitterClient = createStrictMock(Twitter.class);
        mockTwitterClient.getScreenName();
        expectLastCall().andReturn( twitterUser.getScreenName() );
        mockTwitterClient.updateStatus(
                replyUpdateFor( mentioned,
                                idOfMention, 
                                replyText) );
        expectLastCall().andReturn(null);
        replay(mockTwitterClient);
        
        TweetResponder responder = 
                new TweetResponder(prevayler, mockTweetDb, mockTwitterClient);
        Status mention = 
                statusFrom(mentioningUser, 
                            idOfMention, 
                            tweetBodyWithMentions(mentioned) );
        responder.respondToMentions(
            aResponseList().withStatus(mention).build() );
        
        verify(mockTwitterClient);
    }
    
    @Test
    public void swallowsExceptionsReplyingToMentions() 
    throws Exception {
     
        TwitterException exception = new TwitterException("BOOM!");
        mockTwitterClient = createStrictMock(Twitter.class);
        mockTwitterClient.getScreenName();
        expectLastCall().andReturn( twitterUser.getScreenName() );
        mockTwitterClient.updateStatus( (StatusUpdate)anyObject() );
        expectLastCall().andThrow(exception);
        replay(mockTwitterClient);
        
        TweetResponder responder = 
                new TweetResponder(prevayler, mockTweetDb, mockTwitterClient);
        Status mention = 
            statusFrom(randomUser(), 
                        lastSeenMentionId.longValue() + 1, 
                        randomTweetBody() );
        responder.respondToMentions(
            aResponseList().withStatus(mention).build() );
        
        verify(mockTwitterClient);
    }
    
    @Test
    public void doesNotUpdateLastMentionHandledRecordWhenNoNewMentionsAreFound()
    throws Exception {
        
        mockTwitterClient = createNiceMock(Twitter.class);
        mockTwitterClient.getScreenName();
        expectLastCall().andReturn( twitterUser.getScreenName() );
        replay(mockTwitterClient);
        
        Prevayler mockPrevayler = createStrictMock(Prevayler.class);
        replay(mockPrevayler);
        
        TweetResponder responder = 
                new TweetResponder(mockPrevayler, 
                                    mockTweetDb, 
                                    mockTwitterClient);
        responder.respondToMentions( aResponseList().build() );
        
        verify(mockPrevayler);
    }
    
    @Test
    public void updatesLastMentionHandledRecordAfterReplyingToMentions()
    throws Exception {
        
        User userWhoMentioned = randomUser();
        String bodyOfMentioningTweet = randomTweetBody();
        long idOfMention = lastSeenMentionId.longValue() + 1; 
        Status mention = 
            statusFrom(userWhoMentioned, 
                        idOfMention, 
                        bodyOfMentioningTweet);
        ResponseList<Status> mentions = 
            aResponseList().withStatus(mention).build();
        
        mockTwitterClient = createNiceMock(Twitter.class);
        mockTwitterClient.getScreenName();
        expectLastCall().andReturn( twitterUser.getScreenName() );
        mockTwitterClient.updateStatus( (StatusUpdate)anyObject() );
        expectLastCall().andReturn(null);
        replay(mockTwitterClient);
        
        SetLastMentionHandledIdTransaction transaction = 
                new SetLastMentionHandledIdTransaction(idOfMention);
        Prevayler mockPrevayler = createStrictMock(Prevayler.class);
        mockPrevayler.execute( eqSetLastMentionHandledIdTransaction(transaction) );
        replay(mockPrevayler);
        
        TweetResponder responder = 
                new TweetResponder(mockPrevayler, mockTweetDb, mockTwitterClient);
        responder.respondToMentions(mentions);
        
        verify(mockPrevayler);
    }
    
    ///////////////////////////////////////////////////////
    // helper methods
    ///////////////////////////////////////////////////////
    
    private StatusUpdate replyUpdateFor(User[] usersMentioned,
                                        long idOfMention, 
                                        String replyBody) {
        
        String fullReplyTweet = "@" + mentioningUser.getScreenName() + " ";
        for (User user : usersMentioned) {
            String userScreenName = user.getScreenName();
            if ( userScreenName.equalsIgnoreCase( twitterUser.getScreenName() ) 
                    == false) {
                fullReplyTweet += "@" + userScreenName + " ";    
            }
            
        }
        fullReplyTweet += replyBody;
        StatusUpdate replyStatus = new StatusUpdate(fullReplyTweet);
        replyStatus.setInReplyToStatusId(idOfMention);
        
        return replyStatus;
    }
    
    private String randomString() {
        return UUID.randomUUID().toString();
    }
    
    private User randomUser() {
        return userFrom( randomString(), randomString().substring(0, 8) );
    }
    
    private User[] randomUsers(int numberOfUsers) {
        
        User[] users = new User[numberOfUsers];
        for (int i = 0; i < numberOfUsers; i++) {
            String twitterHandle = "RandomU" + i;
            users[i] = userFrom( "Mr. " + twitterHandle, twitterHandle);
        }
        
        return users;
    }
    
    private User[] mentionedUsers(User addressee, 
                                    int numberOfRandomMentionedUsers) {
        
        User[] randoms = randomUsers(numberOfRandomMentionedUsers);
        User[] usersWhoWereMentioned = 
            new User[numberOfRandomMentionedUsers + 1];
        usersWhoWereMentioned[0] = addressee;
        System.arraycopy(randoms, 0, usersWhoWereMentioned, 1, randoms.length);
        
        return usersWhoWereMentioned;
    }
    
    private String randomTweetBody() {
        return randomString() + ", " 
                + randomString() + " "
                + randomString() + " "
                + randomString();
    }
    
    private String tweetBodyWithMentions(User[] usersMentioned) {
        String tweetBody = "";
        for (User user : usersMentioned) {
            tweetBody += "@" + user.getScreenName() + " ";
        }
        
        return tweetBody;
    }
    
    @SuppressWarnings("serial")
    private User userFrom(final String name, final String screenName) {
        
        return new User() {
            
            @Override
            public RateLimitStatus getRateLimitStatus() {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public int getAccessLevel() {
                // TODO Auto-generated method stub
                return 0;
            }
            
            @Override
            public int compareTo(User arg0) {
                // TODO Auto-generated method stub
                return 0;
            }
            
            @Override
            public boolean isVerified() {
                // TODO Auto-generated method stub
                return false;
            }
            
            @Override
            public boolean isTranslator() {
                // TODO Auto-generated method stub
                return false;
            }
            
            @Override
            public boolean isShowAllInlineMedia() {
                // TODO Auto-generated method stub
                return false;
            }
            
            @Override
            public boolean isProtected() {
                // TODO Auto-generated method stub
                return false;
            }
            
            @Override
            public boolean isProfileUseBackgroundImage() {
                // TODO Auto-generated method stub
                return false;
            }
            
            @Override
            public boolean isProfileBackgroundTiled() {
                // TODO Auto-generated method stub
                return false;
            }
            
            @Override
            public boolean isGeoEnabled() {
                // TODO Auto-generated method stub
                return false;
            }
            
            @Override
            public boolean isFollowRequestSent() {
                // TODO Auto-generated method stub
                return false;
            }
            
            @Override
            public boolean isContributorsEnabled() {
                // TODO Auto-generated method stub
                return false;
            }
            
            @Override
            public int getUtcOffset() {
                // TODO Auto-generated method stub
                return 0;
            }
            
            @Override
            public URLEntity getURLEntity() {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public String getURL() {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public String getTimeZone() {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public int getStatusesCount() {
                // TODO Auto-generated method stub
                return 0;
            }
            
            @Override
            public Status getStatus() {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public String getScreenName() {
                return screenName;
            }
            
            @Override
            public String getProfileTextColor() {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public String getProfileSidebarFillColor() {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public String getProfileSidebarBorderColor() {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public String getProfileLinkColor() {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public URL getProfileImageUrlHttps() {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public String getProfileImageURLHttps() {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public String getProfileImageURL() {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public String getProfileBannerURL() {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public String getProfileBannerRetinaURL() {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public String getProfileBannerMobileURL() {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public String getProfileBannerMobileRetinaURL() {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public String getProfileBannerIPadURL() {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public String getProfileBannerIPadRetinaURL() {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public String getProfileBackgroundImageUrlHttps() {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public String getProfileBackgroundImageUrl() {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public String getProfileBackgroundImageURL() {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public String getProfileBackgroundColor() {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public String getOriginalProfileImageURLHttps() {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public String getOriginalProfileImageURL() {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public String getName() {
                return name;
            }
            
            @Override
            public String getMiniProfileImageURLHttps() {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public String getMiniProfileImageURL() {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public String getLocation() {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public int getListedCount() {
                // TODO Auto-generated method stub
                return 0;
            }
            
            @Override
            public String getLang() {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public long getId() {
                // TODO Auto-generated method stub
                return 0;
            }
            
            @Override
            public int getFriendsCount() {
                // TODO Auto-generated method stub
                return 0;
            }
            
            @Override
            public int getFollowersCount() {
                // TODO Auto-generated method stub
                return 0;
            }
            
            @Override
            public int getFavouritesCount() {
                // TODO Auto-generated method stub
                return 0;
            }
            
            @Override
            public URLEntity[] getDescriptionURLEntities() {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public String getDescription() {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public Date getCreatedAt() {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public String getBiggerProfileImageURLHttps() {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public String getBiggerProfileImageURL() {
                // TODO Auto-generated method stub
                return null;
            }
        };
    }
    
    @SuppressWarnings("serial")
    private Status statusFrom(final User user, 
                                final long statusId,
                                final String statusBody) {
        
        final Date createdAt = new Date();
        
        return new Status() {
            
            @Override
            public UserMentionEntity[] getUserMentionEntities() {
                List<UserMentionEntity> userMentionEntities = 
                        new ArrayList<UserMentionEntity>();
//                userMentionEntities.add( userMentionEntity( user.getScreenName() ) );
                
                Pattern p = Pattern.compile("@\\w{8}");
                Matcher m = p.matcher(statusBody);
                while ( m.find() ) {
                    final String tweetHandle = m.group().substring(1);
                    userMentionEntities.add( userMentionEntity(tweetHandle) );
                }
                
                UserMentionEntity[] mentionEntities = 
                    new UserMentionEntity[userMentionEntities.size()];
                return userMentionEntities.toArray(mentionEntities);
            }
            
            @Override
            public URLEntity[] getURLEntities() {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public MediaEntity[] getMediaEntities() {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public HashtagEntity[] getHashtagEntities() {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public RateLimitStatus getRateLimitStatus() {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public int getAccessLevel() {
                // TODO Auto-generated method stub
                return 0;
            }
            
            @Override
            public int compareTo(Status o) {
                // TODO Auto-generated method stub
                return 0;
            }
            
            @Override
            public boolean isTruncated() {
                // TODO Auto-generated method stub
                return false;
            }
            
            @Override
            public boolean isRetweetedByMe() {
                // TODO Auto-generated method stub
                return false;
            }
            
            @Override
            public boolean isRetweet() {
                // TODO Auto-generated method stub
                return false;
            }
            
            @Override
            public boolean isPossiblySensitive() {
                // TODO Auto-generated method stub
                return false;
            }
            
            @Override
            public boolean isFavorited() {
                // TODO Auto-generated method stub
                return false;
            }
            
            @Override
            public User getUser() {
                return user;
            }
            
            @Override
            public String getText() {
                return statusBody;
            }
            
            @Override
            public String getSource() {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public Status getRetweetedStatus() {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public long getRetweetCount() {
                // TODO Auto-generated method stub
                return 0;
            }
            
            @Override
            public Place getPlace() {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public long getInReplyToUserId() {
                // TODO Auto-generated method stub
                return 0;
            }
            
            @Override
            public long getInReplyToStatusId() {
                // TODO Auto-generated method stub
                return 0;
            }
            
            @Override
            public String getInReplyToScreenName() {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public long getId() {
                return statusId;
            }
            
            @Override
            public GeoLocation getGeoLocation() {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public long getCurrentUserRetweetId() {
                // TODO Auto-generated method stub
                return 0;
            }
            
            @Override
            public Date getCreatedAt() {
                return createdAt;
            }
            
            @Override
            public long[] getContributors() {
                // TODO Auto-generated method stub
                return null;
            }
        };
    }
    
    @SuppressWarnings("serial")
    private UserMentionEntity userMentionEntity(final String tweetHandle) {
        return new UserMentionEntity() {
            
            @Override
            public int getStart() {
                // TODO Auto-generated method stub
                return 0;
            }
            
            @Override
            public String getScreenName() {
                return tweetHandle;
            }
            
            @Override
            public String getName() {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public long getId() {
                // TODO Auto-generated method stub
                return 0;
            }
            
            @Override
            public int getEnd() {
                // TODO Auto-generated method stub
                return 0;
            }
        };
    }

    // custom Matcher for command object passed to Prevayler
    public static SetLastMentionHandledIdTransaction 
        eqSetLastMentionHandledIdTransaction(
                SetLastMentionHandledIdTransaction expected) {

        reportMatcher( new SetLastMentionHandledIdTransactionEquals(expected) );
        return expected;
    }

    static class SetLastMentionHandledIdTransactionEquals implements IArgumentMatcher {

        private SetLastMentionHandledIdTransaction expected;
        SetLastMentionHandledIdTransactionEquals(
                SetLastMentionHandledIdTransaction expected){
            
            this.expected = expected;
        }

        @Override
        public void appendTo(StringBuffer arg0) {
            arg0.append("eqSetLastMentionHandledIdTransaction(");
            arg0.append( expected.getLastMentionHandledId() );
            arg0.append(")");
        }

        @Override
        public boolean matches(Object arg0) {
            if ( (arg0 instanceof SetLastMentionHandledIdTransaction) 
                    == false ){
                return false;
            }
            
            SetLastMentionHandledIdTransaction toMatch =
                (SetLastMentionHandledIdTransaction)arg0;
            boolean matches = 
                toMatch.getLastMentionHandledId().equals(
                                    expected.getLastMentionHandledId() );

            return matches;
        }
    }
}
