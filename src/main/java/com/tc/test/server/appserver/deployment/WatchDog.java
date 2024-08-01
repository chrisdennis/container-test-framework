/*
 * Copyright 2003-2008 Terracotta, Inc.
 * Copyright Super iPaaS Integration LLC, an IBM Company 2024
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tc.test.server.appserver.deployment;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tc.util.runtime.ThreadDump;

import java.util.Timer;
import java.util.TimerTask;

public class WatchDog {
  protected Log        logger = LogFactory.getLog(getClass());

  private final Thread threadToWatch;
  private final Timer  timer;
  private TimerTask    timerTask;
  private TimerTask    dumpTask;

  private final int    timeoutInSecs;

  public WatchDog(int timeOutInSecs) {
    timeoutInSecs = timeOutInSecs;
    this.threadToWatch = Thread.currentThread();
    this.timer = new Timer();
  }

  public void startWatching() {
    logger.debug("Watching thread");
    timerTask = new TimerTask() {
      @Override
      public void run() {
        logger.error("Thread timeout..interrupting");
        threadToWatch.interrupt();

      }
    };

    dumpTask = new TimerTask() {
      @Override
      public void run() {
        ThreadDump.dumpAllJavaProcesses();
      }
    };

    timer.schedule(timerTask, timeoutInSecs * 1000);
    timer.schedule(dumpTask, (timeoutInSecs - 45) * 1000);
  }

  public void stopWatching() {
    logger.debug("watching cancelled..");
    timerTask.cancel();
    dumpTask.cancel();
    timer.cancel();
  }

}
