/**
 *    Copyright 2012 Michael Fitzmaurice
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
/**
 *    Copyright 2012 Michael Fitzmaurice
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

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

/**
 * An application to perform the Oauth workflow and write the resultant
 * access token details in the twitter4j.properties file. If no
 * such file is found on the classpath, it will be created, 
 * containing the following Oauth-related properties:
 * 
 * <ul>
 *   <li>oauth.consumerKey</li>
 *   <li>oauth.consumerSecret</li>
 *   <li>oauth.accessToken</li>
 *   <li>oauth.accessTokenSecret</li>
 * </ul>
 * 
 * @author Michael Fitzmaurice https://github.com/mfitz
 */
public class TwitterPropertiesTool {
    
    private static final String TWITTER4J_PROPERTIES_FILE_NAME = 
        "twitter4j.properties";
    private static final String OAUTH_CONSUMER_KEY_PROPERTY = 
        "oauth.consumerKey";
    private static final String OAUTH_CONSUMER_SECRET_PROPERTY = 
        "oauth.consumerSecret";
    private static final String OAUTH_ACCESS_TOKEN_SECRET_PROPERTY = 
        "oauth.accessTokenSecret";
    private static final String OAUTH_ACCESS_TOKEN_PROPERTY = 
        "oauth.accessToken";
    
    /**
     * Usage: java  twitter4j.examples.oauth.GetAccessToken [consumer key] [consumer secret]
     *
     * @param args
     */
    public static void main(String[] args) {
        
        File file = new File(TWITTER4J_PROPERTIES_FILE_NAME);
        Properties prop = new Properties();
        InputStream is = null;
        
        try {
            if ( file.exists() ) {
                System.out.println("Using existing twitter4j.properties file: " 
                                    + file.getAbsolutePath() );
                is = new FileInputStream(file);
                prop.load(is);
            }
            
            if (args.length < 2) {
                verifyOAuthProperties(prop);
            } else {
                createSkeletonPropertiesFile(args[0], args[1], prop);
            }
        } catch (IOException ioe) {
            printStackTraceAndExitSystem(ioe, null);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ignore) {}
            }
        }
        
        try {
            Twitter twitter = new TwitterFactory().getInstance();
            RequestToken requestToken = twitter.getOAuthRequestToken();
            System.out.println("Got request token.");
            System.out.println("Request token: " + requestToken.getToken());
            System.out.println("Request token secret: " + requestToken.getTokenSecret());
            AccessToken accessToken = null;

            BufferedReader br = new BufferedReader( new InputStreamReader(System.in) );
            while (null == accessToken) {
                System.out.println("Open the following URL in an authenticated " 
                                    + "Twitter session as the target user:");
                System.out.println( requestToken.getAuthorizationURL() );
                try {
                    Desktop.getDesktop().browse(new URI(requestToken.getAuthorizationURL()));
                } catch (IOException ignore) {
                } catch (URISyntaxException e) {
                    throw new AssertionError(e);
                }
                System.out.print("Enter the authorization PIN and hit enter:");
                String pin = br.readLine();
                try {
                    if (pin.length() > 0) {
                        accessToken = twitter.getOAuthAccessToken(requestToken, pin);
                    } else {
                        accessToken = twitter.getOAuthAccessToken(requestToken);
                    }
                } catch (TwitterException te) {
                    if (401 == te.getStatusCode()) {
                        System.out.println("Unable to get the access token.");
                    } else {
                        te.printStackTrace();
                    }
                }
            }
            System.out.println("Got access token.");
            System.out.println("Access token: " + accessToken.getToken());
            System.out.println("Access token secret: " + accessToken.getTokenSecret());

            OutputStream os = null;
            try {
                prop.setProperty(OAUTH_ACCESS_TOKEN_PROPERTY, 
                                accessToken.getToken() );
                prop.setProperty(OAUTH_ACCESS_TOKEN_SECRET_PROPERTY, 
                                accessToken.getTokenSecret() );
                os = new FileOutputStream(file);
                prop.store(os, TWITTER4J_PROPERTIES_FILE_NAME);
            } catch (IOException ioe) {
                printStackTraceAndExitSystem(ioe, null);
            } finally {
                if (os != null) {
                    try {
                        os.close();
                    } catch (IOException ignore) {
                    }
                }
            }
            System.out.println("Successfully stored access token to " + file.getAbsolutePath() + ".");
            System.exit(0);
        } catch (TwitterException te) {
            printStackTraceAndExitSystem(te, "Failed to get accessToken: " 
                                            + te.getMessage() );
        } catch (IOException ioe) {
            printStackTraceAndExitSystem(ioe, "Failed to read the system input.");
        }
    }

    private static void printStackTraceAndExitSystem(Exception exception, 
                                                    String message) {
        
        if (message != null) {
            System.err.println(message);
        }
        exception.printStackTrace();
        System.exit(-1);
    }

    private static void createSkeletonPropertiesFile(String consumerKey, 
                                                      String consumerSecret, 
                                                      Properties prop) 
    throws FileNotFoundException, IOException {
        
        prop.setProperty(OAUTH_CONSUMER_KEY_PROPERTY, consumerKey);
        prop.setProperty(OAUTH_CONSUMER_SECRET_PROPERTY, consumerSecret);
        OutputStream os = new FileOutputStream(TWITTER4J_PROPERTIES_FILE_NAME);
        prop.store(os, TWITTER4J_PROPERTIES_FILE_NAME);
        os.close();
    }

    private static void verifyOAuthProperties(Properties properties) {
        
        if (null == properties.getProperty(OAUTH_CONSUMER_KEY_PROPERTY)
                || null == properties.getProperty(
                                OAUTH_CONSUMER_SECRET_PROPERTY) ) {
            printUsageEndExit();
        }
    }

    private static void printUsageEndExit() {
        
        System.err.println(
             "Usage: java twitter4j.examples.oauth.GetAccessToken " 
                + "[consumer key] [consumer secret]");
        System.err.println(
            "\nRun directory must contain either no twitter4j.properties file, " 
                + "in which case you must supply OAuth consumer key & " 
                + "consumer secret as command line parameters, or a " 
                + "twitter4j.properties file containing both oauth.consumerKey " 
                + "& oauth.consumerSecret key value pairs");
        System.exit(-1);
    }
}
