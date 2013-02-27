package com.michaelfitzmaurice.clocktwerk.apps;

public class Clocktwerk {

    //  // TODO - make tweet file location a sys prop
    //  LOG.info("Looking for tweets file...");
    //  URL tweetFileUrl = 
    //      new Object().getClass().getResource("/tweets.txt");
    //  if (tweetFileUrl == null) {
    //      throw new IOException("No tweets.txt file found on the classpath");
    //  }
    //  
    //  LOG.info("Found tweets file at {}", tweetFileUrl);
    //  File tweetFile = new File( tweetFileUrl.getPath() );

    //private Twitter authenticateToTwitter() throws IOException {
    //  
    //  LOG.info("Setting up Twitter client...");
    //  // TODO - make twitter4j.properties location a sys prop
    //  Twitter twitter = new TwitterFactory().getInstance();
    //  if (twitter.getAuthorization().isEnabled() == false) {
    //      String msg = 
    //          "Not authenticated to twitter - check OAuth consumer " 
    //          + "key/secret in twitter4j.properties file";
    //      LOG.error(msg);
    //      throw new IOException(msg);
    //  }
    //  LOG.info("Twitter client connected and authenticated");
    //    
    //    return twitter;
    //}
    //
    //private TweetIndex getTweetIndex() throws IOException {
    //  
    //  Prevayler prevayler = null;
    //    try {
    //        prevayler = 
    //                PrevaylerFactory.createPrevayler( 
    //                        new HashMap<String, Integer>() );
    //    } catch (ClassNotFoundException e) {
    //        throw new IOException("Error reading from Prevayler", e);
    //    }
    //    
    //    return new PrevaylentTweetIndex(prevayler);
    //}
    
    public static void main(String[] args) {
        
        // set-up twitter client
        // set up prevaylent tweet index
        // locate tweets file
        // set-up tweet database
        // set-up scheduler
//      ScheduledExecutorService scheduler = 
//      Executors.newSingleThreadScheduledExecutor();
        // set-up TweetDaemon
        // start TweetDaemon
    }

}
