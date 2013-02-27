Clocktwerk
===========

A Twitterbot that automates the posting
of Twitter updates at regular intervals. Clocktwerk 
uses OAuth authentication 
(see https://dev.twitter.com/docs/auth/oauth/faq), 
hence does not need access to the Twitter login 
credentials of the account being updated. 

Building
===========

Clocktwerk uses Apache Maven (http://maven.apache.org/).
To compile and build a distro in zip format, use:

    mvn clean assembly:assembly 

To generate a test coverage report:

    mvn clean cobertura:clean cobertura:cobertura

Authentication to Twitter
===========================

Clocktwerk uses the Twitter API to send updates. To
authorise this for the account you
want to automate, you will need to create a Twitter
application on the Twitter developer site. Login to
Twitter as the user that Clocktwerk will be tweeting
on behalf of, then follow the instructions at 
https://dev.twitter.com/apps/new. You will need to
select "Read and Write" as the application type, but
if you neglect to do so, you can edit the settings for
your app later. A good idea is to name the app after the
account you are automating, e.g. "Username Bot", where
Username is the target twitter account. 

Once you have created your application, Twitter will 
have allocated a 'Consumer key' and 'Consumer secret' 
that identifies your app. You will need these values
in order to allow Clocktwerk to authenticate to 
Twitter.

The relavant OAuth credentials are read from 
a twitter4.properties file on the Java classpath. 
Clocktwerk includes a tool to generate this file for you. 
Once you have built a distro and unpacked it to your 
runtime location, you can use the oauth.sh script to 
generate this file and drop it onto the classpath; just 
follow the instructions from the script:

    ./oauth.sh [Consumer key] [Consumer secret]

The script will build a twitter4j.properties file that
includes the authentication details granted by your 
Twitter user to your new application. The oauth script
moves this file into the lib directory, thus placing it
on the Java classpath. You could put the file anywhere
else you prefer, as long as you add the directory
to the classpath when you launch the Clocktwerk
application.

Feeding Clocktwerk your tweets
===========================

Tweets are pulled from a tweets.txt file on the JVM classpath.
Once you have prepared your tweets file, dropping it into the
lib directory is an easy way to place it on the classpath.
However, as with the twitter4.properties file, you can put
his file anywhere you like so long as you modify the JVM
classpath accordingly.

Each line in the file constitutes a single tweet; tweets 
longer than 140 characters are ignored. No further 
validation is performed against the content of each 
tweet. When the last tweet from the file has been posted, 
Clocktwerk starts again from the first line, thus 
tweeting infinitely at the specified interval until the 
process is killed.

Clocktwerk persists the index into tweets.txt. Hence, when 
you restart the application, tweeting continues from the 
location previously reached, rather than starting again 
from the beginning of the file.

Tweeting interval
===========================

By default, Clocktwerk tweets every 6 hours, sending
the first tweet when the application starts up. You can
override this default using a Java system property to
specify the interval between tweets in milliseconds. For
example, to tweet once every 24 hours, you would edit 
tweet-daemon.sh thus (86400000 being 24 hours expressed
as milliseconds)

    nohup java -classpath lib:lib/* -Dtweetdaemon.tweetinterval.milliseconds=86400000 com.michaelfitzmaurice.clocktwerk.TweetDaemon & 
