Clocktwerk
===========

Posts tweets read from a file to a configured account at 
a constant interval. Uses OAuth based authentication (see 
https://dev.twitter.com/docs/auth/oauth/faq), hence does not
need access to the Twitter login credentials of the account
being updated. 

Building
===========

Clocktwerk uses Apache Maven (http://maven.apache.org/).
To compile Clocktwerk and build a distro in zip format,
use:

    mvn clean assembly:assembly 

Authentication to Twitter
===========================

OAuth credentials are pulled from a twitter4.properties 
file on the classpath. Clocktwerk includes a tool to 
generate this file for you. Once you have built a distro 
and unpacked it to your runtime location, you can use the 
oauth.sh script to generate this file; just follow the 
instructions from the script:

    ./oauth.sh

Feeding Clocktwerk your tweets
===========================

Tweets are pulled from a tweets.txt file on the classpath.
Once you have prepared your tweets file, drop it into the
lib directory for an easy way to add it to the classpath.

Each line in the file constitutes a single tweet; tweets 
longer than 140 characters are ignored. No further 
validation is performed against the content of each 
tweet. When the last tweet from the file has been posted, 
Clocktwerk starts again from the first line, thus 
tweeting infinitely at the specified interval until the 
process is killed.