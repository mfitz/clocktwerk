# !/bin/bash

##########################################################
#    Copyright 2013 Michael Fitzmaurice
# 
#    Licensed under the Apache License, Version 2.0 (the "License");
#    you may not use this file except in compliance with the License.
#    You may obtain a copy of the License at
# 
#        http://www.apache.org/licenses/LICENSE-2.0
# 
#    Unless required by applicable law or agreed to in writing, software
#    distributed under the License is distributed on an "AS IS" BASIS,
#    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#    See the License for the specific language governing permissions and
#    limitations under the License.
##########################################################

echo "Launching Twitter4J properties tool..."
echo ""
 
java -classpath lib:lib/* com.michaelfitzmaurice.clocktwerk.apps.TwitterPropertiesTool $1 $2

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

