#!/usr/bin/env bash

# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.


# Runs a Benchmark command as a daemon.
#
# Environment Variables
#
#   BENCHMARK_CONF_DIR  Alternate conf dir. Default is ${BENCHMARK_HOME}/conf.
#   BENCHMARK_LOG_DIR   Where log files are stored.  PWD by default.
#   BENCHMARK_MASTER    host:path where benchmark code should be rsync'd from
#   BENCHMARK_PID_DIR   The pid files are stored. /tmp by default.
#   BENCHMARK_IDENT_STRING   A string representing this instance of benchmark. $USER by default
#   BENCHMARK_NICENESS The scheduling priority for daemons. Defaults to 0.
##

usage="Usage: benchmark-daemon.sh [--config <conf-dir>] [--hosts hostlistfile] (start|stop) <benchmark-command> <args...>"

# if no args specified, show usage
if [ $# -le 1 ]; then
  echo $usage
  exit 1
fi

bin=`dirname "$0"`
bin=`cd "$bin"; pwd`

. "$bin"/benchmark-config.sh

# get arguments
startStop=$1
shift
command=$1
shift

benchmark_rotate_log ()
{
    log=$1;
    num=5;
    if [ -n "$2" ]; then
	num=$2
    fi
    if [ -f "$log" ]; then # rotate logs
	while [ $num -gt 1 ]; do
	    prev=`expr $num - 1`
	    [ -f "$log.$prev" ] && mv "$log.$prev" "$log.$num"
	    num=$prev
	done
	mv "$log" "$log.$num";
    fi
}

if [ -f "${BENCHMARK_CONF_DIR}/benchmark-env.sh" ]; then
  . "${BENCHMARK_CONF_DIR}/benchmark-env.sh"
fi

if [ "$BENCHMARK_IDENT_STRING" = "" ]; then
  export BENCHMARK_IDENT_STRING="$USER"
fi

# get log directory
if [ "$BENCHMARK_LOG_DIR" = "" ]; then
  export BENCHMARK_LOG_DIR="$BENCHMARK_HOME/logs"
fi
mkdir -p "$BENCHMARK_LOG_DIR"
chown $BENCHMARK_IDENT_STRING $BENCHMARK_LOG_DIR

if [ "$BENCHMARK_PID_DIR" = "" ]; then
  BENCHMARK_PID_DIR=/tmp
fi

# some variables
export BENCHMARK_LOGFILE=benchmark-$BENCHMARK_IDENT_STRING-$command-$HOSTNAME.log
export BENCHMARK_ROOT_LOGGER_APPENDER="${BENCHMARK_ROOT_LOGGER_APPENDER:-DRFA}"
export BENCHMARK_PULLSERVER_STANDALONE="${BENCHMARK_PULLSERVER_STANDALONE:-false}"
log=$BENCHMARK_LOG_DIR/benchmark-$BENCHMARK_IDENT_STRING-$command-$HOSTNAME.out
pid=$BENCHMARK_PID_DIR/benchmark-$BENCHMARK_IDENT_STRING-$command.pid

# Set default scheduling priority
if [ "$BENCHMARK_NICENESS" = "" ]; then
    export BENCHMARK_NICENESS=0
fi

case $startStop in

  (start)

    mkdir -p "$BENCHMARK_PID_DIR"

    if [ -f $pid ]; then
      if kill -0 `cat $pid` > /dev/null 2>&1; then
        echo $command running as process `cat $pid`.  Stop it first.
        exit 1
      fi
    fi

    if [ "$BENCHMARK_MASTER" != "" ]; then
      echo rsync from $BENCHMARK_MASTER
      rsync -a -e ssh --delete --exclude=.svn --exclude='logs/*' --exclude='contrib/hod/logs/*' $BENCHMARK_MASTER/ "$BENCHMARK_HOME"
    fi

    benchmark_rotate_log $log
    echo starting $command, logging to $log
    cd "$BENCHMARK_HOME"
    nohup nice -n $BENCHMARK_NICENESS "$BENCHMARK_HOME"/bin/benchmark --config $BENCHMARK_CONF_DIR $command "$@" > "$log" 2>&1 < /dev/null &
    echo $! > $pid
    sleep 1; head "$log"
    ;;
          
  (stop)

    if [ -f $pid ]; then
      if kill -0 `cat $pid` > /dev/null 2>&1; then
        echo stopping $command
        kill `cat $pid`
      else
        echo no $command to stop
      fi
    else
      echo no $command to stop
    fi
    ;;

  (*)
    echo $usage
    exit 1
    ;;

esac