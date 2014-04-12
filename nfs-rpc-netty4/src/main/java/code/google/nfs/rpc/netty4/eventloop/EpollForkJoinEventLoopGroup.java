/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package code.google.nfs.rpc.netty4.eventloop;

import io.netty.channel.*;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.ScheduledFuture;
import io.netty.util.internal.chmv8.ForkJoinPool;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class EpollForkJoinEventLoopGroup implements EventLoopGroup {
  ForkJoinPool forkJoinPool;

  public EpollForkJoinEventLoopGroup() {
    this(0);
  }

  public EpollForkJoinEventLoopGroup(int nThreads) {

  }

  public boolean isShuttingDown() {
    return false;
  }

  public Future<?> shutdownGracefully() {
    return null;
  }

  public Future<?> shutdownGracefully(long quietPeriod, long timeout, TimeUnit unit) {
    return null;
  }

  public Future<?> terminationFuture() {
    return null;
  }

  public void shutdown() {

  }

  public List<Runnable> shutdownNow() {
    return null;
  }

  public boolean isShutdown() {
    return false;
  }

  public boolean isTerminated() {
    return false;
  }

  public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
    return false;
  }

  public EventLoop next() {
    return null;
  }

  public Iterator<EventExecutor> iterator() {
    return null;
  }

  public Future<?> submit(Runnable task) {
    return null;
  }

  public <T> List<java.util.concurrent.Future<T>> invokeAll(Collection<? extends Callable<T>> tasks)
      throws InterruptedException {
    return null;
  }

  public <T> List<java.util.concurrent.Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout,
                                                            TimeUnit unit)
      throws InterruptedException {
    return null;
  }

  public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
    return null;
  }

  public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
      throws InterruptedException, ExecutionException, TimeoutException {
    return null;
  }

  public <T> Future<T> submit(Runnable task, T result) {
    return null;
  }

  public <T> Future<T> submit(Callable<T> task) {
    return null;
  }

  public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
    return null;
  }

  public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
    return null;
  }

  public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
    return null;
  }

  public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
    return null;
  }

  public ChannelFuture register(Channel channel) {
    return null;
  }

  public ChannelFuture register(Channel channel, ChannelPromise promise) {
    return null;
  }

  public void execute(Runnable command) {

  }
}
