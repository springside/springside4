#!/bin/sh
# ------------------------------------------------------------------------
# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
# 
# http://www.apache.org/licenses/LICENSE-2.0
# 
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
# ------------------------------------------------------------------------

# load system-wide activemq configuration
if [ -f "/etc/activemq.conf" ] ; then
  . /etc/activemq.conf
fi

# provide default values for people who don't use RPMs
if [ -z "$usejikes" ] ; then
  usejikes=false;
fi

# load user activemq configuration
if [ -f "$HOME/.activemqrc" ] ; then
  . "$HOME/.activemqrc"
fi

# OS specific support.  $var _must_ be set to either true or false.
cygwin=false;
darwin=false;
case "`uname`" in
  CYGWIN*) cygwin=true ;;
  Darwin*) darwin=true
           if [ -z "$JAVA_HOME" ] ; then
             JAVA_HOME=/System/Library/Frameworks/JavaVM.framework/Home
           fi
           ;;
esac

if [ -z "$ACTIVEMQ_HOME" ] ; then
  # try to find ACTIVEMQ
  if [ -d /opt/activemq ] ; then
    ACTIVEMQ_HOME=/opt/activemq
  fi

  if [ -d "${HOME}/opt/activemq" ] ; then
    ACTIVEMQ_HOME="${HOME}/opt/activemq"
  fi

  ## resolve links - $0 may be a link to activemq's home
  PRG="$0"
  progname=`basename "$0"`
  saveddir=`pwd`

  # need this for relative symlinks
  dirname_prg=`dirname "$PRG"`
  cd "$dirname_prg"

  while [ -h "$PRG" ] ; do
    ls=`ls -ld "$PRG"`
    link=`expr "$ls" : '.*-> \(.*\)$'`
    if expr "$link" : '.*/.*' > /dev/null; then
    PRG="$link"
    else
    PRG=`dirname "$PRG"`"/$link"
    fi
  done

  ACTIVEMQ_HOME=`dirname "$PRG"`/..

  cd "$saveddir"

  # make it fully qualified
  ACTIVEMQ_HOME=`cd "$ACTIVEMQ_HOME" && pwd`
fi

# For Cygwin, ensure paths are in UNIX format before anything is touched
if $cygwin ; then
  [ -n "$ACTIVEMQ_HOME" ] &&
    ACTIVEMQ_HOME=`cygpath --unix "$ACTIVEMQ_HOME"`
  [ -n "$JAVA_HOME" ] &&
    JAVA_HOME=`cygpath --unix "$JAVA_HOME"`
  [ -n "$CLASSPATH" ] &&
    CLASSPATH=`cygpath --path --unix "$CLASSPATH"`
fi

if [ -z "$JAVACMD" ] ; then
  if [ -n "$JAVA_HOME"  ] ; then
    if [ -x "$JAVA_HOME/jre/sh/java" ] ; then
      # IBM's JDK on AIX uses strange locations for the executables
      JAVACMD="$JAVA_HOME/jre/sh/java"
    else
      JAVACMD="$JAVA_HOME/bin/java"
    fi
  else
    JAVACMD=`which java 2> /dev/null `
    if [ -z "$JAVACMD" ] ; then
        JAVACMD=java
    fi
  fi
fi

if [ ! -x "$JAVACMD" ] ; then
  echo "Error: JAVA_HOME is not defined correctly."
  echo "  We cannot execute $JAVACMD"
  exit 1
fi

if [ -z "$ACTIVEMQ_BASE" ] ; then
  ACTIVEMQ_BASE="$ACTIVEMQ_HOME"
fi

# For Cygwin, switch paths to Windows format before running java
if $cygwin; then
  ACTIVEMQ_HOME=`cygpath --windows "$ACTIVEMQ_HOME"`
  ACTIVEMQ_BASE=`cygpath --windows "$ACTIVEMQ_BASE"`
  ACTIVEMQ_CLASSPATH=`cygpath --path --windows "$ACTIVEMQ_CLASSPATH"`
  JAVA_HOME=`cygpath --windows "$JAVA_HOME"`
  CLASSPATH=`cygpath --path --windows "$CLASSPATH"`
  CYGHOME=`cygpath --windows "$HOME"`
fi

# Set default classpath
ACTIVEMQ_CLASSPATH="${ACTIVEMQ_BASE}/conf;"$ACTIVEMQ_CLASSPATH

if [ $1 = "start" ] ; then
    if [ -z "$ACTIVEMQ_OPTS" ] ; then
        ACTIVEMQ_OPTS="-Xms2048M -Xmx2048M -Xmn768M -XX:MaxPermSize=96M -Dorg.apache.activemq.UseDedicatedTaskRunner=true -Djava.util.logging.config.file=logging.properties"
    fi

    if [ -z "$SUNJMX" ] ; then
        SUNJMX="-Dcom.sun.management.jmxremote.port=1616 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false"
        #SUNJMX="-Dcom.sun.management.jmxremote"
    fi

    ACTIVEMQ_OPTS="$ACTIVEMQ_OPTS $SUNJMX $SSL_OPTS"
fi

# Uncomment to enable YourKit profiling
#ACTIVEMQ_DEBUG_OPTS="-agentlib:yjpagent"

# Uncomment to enable remote debugging
#ACTIVEMQ_DEBUG_OPTS="-Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"

if [ -n "$CYGHOME" ]; then
    exec "$JAVACMD" $ACTIVEMQ_OPTS $ACTIVEMQ_DEBUG_OPTS -Dactivemq.classpath="${ACTIVEMQ_CLASSPATH}" -Dactivemq.home="${ACTIVEMQ_HOME}" -Dactivemq.base="${ACTIVEMQ_BASE}" -Dcygwin.user.home="$CYGHOME" -jar "${ACTIVEMQ_HOME}/bin/run.jar" $@
else
    exec "$JAVACMD" $ACTIVEMQ_OPTS $ACTIVEMQ_DEBUG_OPTS -Dactivemq.classpath="${ACTIVEMQ_CLASSPATH}" -Dactivemq.home="${ACTIVEMQ_HOME}" -Dactivemq.base="${ACTIVEMQ_BASE}" -jar "${ACTIVEMQ_HOME}/bin/run.jar" $@
fi
