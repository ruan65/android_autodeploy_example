#!/bin/sh

ANDROID_HOME=/home/a/Android/Sdk/
JAVA_HOME=/usr/lib/jvm/java-8-oracle
PATH=$PATH:$ANDROID_HOME/tools:$ANDROID_HOME/platform-tools:$JAVA_HOME/bin
echo "Test out" > /home/a/outnew
cd /home/a/dev/reaproto/reaproto_2/reaproto/ruch_mobile_chat_client/
./gradlew installDebug >> /home/a/outnew
echo "Last  out" >> /home/a/outnew
