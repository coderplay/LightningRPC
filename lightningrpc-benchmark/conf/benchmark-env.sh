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

# Set Benchmark-specific environment variables here.

# The only required environment variable is JAVA_HOME.  All others are
# optional.  When running a distributed configuration it is best to
# set JAVA_HOME in this file, so that it is correctly defined on
# remote nodes.

# The java implementation to use.  Required.
# export JAVA_HOME=/usr/java/default

# Extra Java CLASSPATH elements.  Optional.
# export BENCHMARK_CLASSPATH=/xxx/extlib/*:/xxx/xxx.jar

# The maximum amount of heap to use, in MB. Default is 1000.
export BENCHMARK_SERVER_HEAPSIZE=20480

# The maximum amount of heap to use, in MB. Default is 1000.
export BENCHMARK_CLIENT_HEAPSIZE=20480

# Extra Java runtime options.  Empty by default.
# export BENCHMARK_OPTS=-server

# Extra BenchmarkMaster's java runtime options for BenchmarkMaster. Empty by default
export BENCHMARK_SERVER_OPTS=' -XX:+UseConcMarkSweepGC  -XX:+PreserveFramePointer '

# Extra BenchmarkWorker's java runtime options. Empty by default
export BENCHMARK_CLIENT_OPTS=' -XX:+UseConcMarkSweepGC  -XX:+PreserveFramePointer '

# Where log files are stored.  $BENCHMARK_HOME/logs by default.
# export BENCHMARK_LOG_DIR=${BENCHMARK_HOME}/logs

# The directory where pid files are stored. /tmp by default.
# export BENCHMARK_PID_DIR=/var/benchmark/pids

# A string representing this instance of benchmark. $USER by default.
# export BENCHMARK_IDENT_STRING=$USER

# The scheduling priority for daemon processes.  See 'man nice'.
# export BENCHMARK_NICENESS=10
