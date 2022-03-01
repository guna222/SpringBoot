#!/bin/sh

JVM_OPTS="${JVM_OPTS:-'-Xmx128m -Xms128m'}"

# This should be changed if you use Play sessions
PLAY_SECRET="${PLAY_SECRET:-sample1234}"

# WARNING: lagom.cluster.join-self=on is not safe to use when running more than
# one instance of the service. For production scenarios you must configure
# clustering correctly for your deployment environment.
# See https://www.lagomframework.com/documentation/current/java/Cluster.html

CONFIG="-Dplay.http.secret.key=$PLAY_SECRET -Dlagom.cluster.join-self=off -Dconfig.file=./application.conf -Dlogback.configurationFile=$LOGBACK_RESOURCE_LOC/logback.xml"

DIR=$(dirname $0)

java -cp "/app/*" $JAVA_OPTS $CONFIG play.core.server.ProdServerStart
