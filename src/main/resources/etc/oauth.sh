# !/bin/bash

echo "Launching Twitter4J properties tool..."
echo ""
 
java -classpath lib:lib/* com.michaelfitzmaurice.clocktwerk.TwitterPropertiesTool $1 $2

RETURN_CODE=$?
echo ""
echo "Return code from Twitter4J properties tool was" $RETURN_CODE
if [[ $RETURN_CODE != 0 ]] ; then
    echo "Error creating twitter4j.properties file !!!"
    exit $RETURN_CODE
else
    echo "Moving twitter4j.properties file to clocktwerk classpath..."
    mv twitter4j.properties lib
    echo "Clocktwerk classpath now contains twitter4j.properties file"
fi

