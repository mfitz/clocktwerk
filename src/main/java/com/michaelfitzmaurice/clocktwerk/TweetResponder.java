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

import static java.lang.String.format;

import java.util.Collection;

import org.prevayler.Prevayler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;
import twitter4j.UserMentionEntity;

import com.michaelfitzmaurice.clocktwerk.prevayler.QueryLastMentionHandledId;
import com.michaelfitzmaurice.clocktwerk.prevayler.SetLastMentionHandledIdTransaction;

public class TweetResponder {
    
    public static final String LAST_MENTION_HANDLED_ID_KEY =
        "last_mention_handled_id";
    
    private static final transient Logger LOG = 
            LoggerFactory.getLogger(TweetResponder.class);
    
    private final Prevayler prevayler;
    private final Twitter twitterClient;
    private final TweetDatabase tweetDatabase;
    private final String myScreenName;

    public TweetResponder(Prevayler prevayler, 
                            TweetDatabase tweetDatabase,
                            Twitter twitterClient) 
    throws TweetException {
        
        this.prevayler = prevayler;
        this.twitterClient = twitterClient;
        this.tweetDatabase = tweetDatabase;
        this.myScreenName = myScreenName();
    }
    
    public Collection<Status> getNewMentions() 
    throws TweetException {
        
        long sinceId = lastSeenMention();
        try {
            LOG.info("Checking for new Twitter mentions of {} since Tweet {}", 
                        myScreenName, 
                        sinceId);
            Paging paging = new Paging(sinceId);
            ResponseList<Status> mentions = 
                twitterClient.getMentionsTimeline(paging);
            int numberOfMentions = mentions.size();
            LOG.info("Found {} new mentions", numberOfMentions );
            for (Status status : mentions) {
                User user = status.getUser();
                String mentionSummary = 
                        format("Date:%s, ID:%s, From:%s (%s), Text:'%s'", 
                                status.getCreatedAt(), 
                                status.getId(), 
                                user.getScreenName(), 
                                user.getName(), 
                                status.getText() );
                LOG.debug("New mention: [{}]", mentionSummary);
            }
            return mentions;
        } catch (TwitterException e) {
            throw new TweetException("Error getting Twitter mentions", e);
        }
      
        // TODO handle paging
    }

    public void respondToMentions(Collection<Status> mentions) {
        
        LOG.info("Responding to {} mentions on behalf of {}", 
                    mentions.size(), 
                    myScreenName);
        
        long latestMentionSeen = -1;
        for (Status mention : mentions) {
            String replyMessage = 
                replyBody( myScreenName, 
                            mention, 
                            tweetDatabase.getNextTweet() );
            StatusUpdate reply = new StatusUpdate(replyMessage);
            long mentionTweetId = mention.getId();
            reply.setInReplyToStatusId(mentionTweetId);
            LOG.debug("Replying to mention with ID {}", mentionTweetId);
            try {
                twitterClient.updateStatus(reply);
                LOG.debug("Replied to mention with ID {}", mentionTweetId);
            } catch (TwitterException e) {
                LOG.error("Failed to reply to Tweet", e);
            }
            // even if we failed to reply, record the mention as seen
            if (mentionTweetId > latestMentionSeen) {
                latestMentionSeen = mentionTweetId;
            }
        }
        LOG.info("Finished replying to new mentions");
        updateLastMentionSeen(latestMentionSeen);
    }

    private void updateLastMentionSeen(long latestMentionSeen) {
        
        if (latestMentionSeen != -1) {
            prevayler.execute( new SetLastMentionHandledIdTransaction(
                                                        latestMentionSeen) );
            LOG.info("Updated latest mention seen to {}", latestMentionSeen);
        }
    }

    private String replyBody(String myScreenName, 
                            Status mention, 
                            String messageBody) {
        
        String replyMessage = "@" + mention.getUser().getScreenName() + " ";
        for ( UserMentionEntity userMentionEntity : 
                        mention.getUserMentionEntities() ) {
            String mentionScreenName = userMentionEntity.getScreenName();
            if (mentionScreenName.equalsIgnoreCase(myScreenName) == false) {
                replyMessage += "@" + mentionScreenName + " ";                    
            }
        }
        replyMessage += messageBody;
        
        return replyMessage;
    }

    private String myScreenName() throws TweetException {
        
        try {
            return twitterClient.getScreenName();
        } catch (TwitterException e) {
            throw new TweetException(
                "Error getting authenticated user's screenname", e);
        }
    }
    
    private long lastSeenMention() throws TweetException {
        
        try {
            Long lastHandledMentionId = 
                (Long)prevayler.execute( new QueryLastMentionHandledId() );
            return lastHandledMentionId.longValue();
        } catch (Exception e) {
            throw new TweetException("Unable to read last handled mention ID", 
                                    e);
        }
    }

}
