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

import com.tc.util.concurrent.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class AbstractStoppable implements Stoppable {

  protected volatile boolean stopped = true;
  protected Log              logger  = LogFactory.getLog(getClass());

  @Override
  public void start() throws Exception {
    logger.info("### Starting " + this);
    long l1 = System.currentTimeMillis();
    stopped = false;
    doStart();
    long l2 = System.currentTimeMillis();
    logger.info("### Started " + this + "; " + (l2 - l1) / 1000f);
  }

  @Override
  public void stop() throws Exception {
    logger.info("### Stopping " + this);
    long l1 = System.currentTimeMillis();
    stopped = true;
    for (int i = 0; i < 3; i++) {
      try {
        doStop();
        break;
      } catch (Exception e) {
        e.printStackTrace();
        ThreadUtil.reallySleep(1000);
      }
    }
    long l2 = System.currentTimeMillis();
    logger.info("### Stopped " + this + "; " + (l2 - l1) / 1000f);
  }

  protected abstract void doStop() throws Exception;

  protected abstract void doStart() throws Exception;

  @Override
  public boolean isStopped() {
    return stopped;
  }

  @Override
  public void stopIgnoringExceptions() {
    try {
      stop();
    } catch (Exception e) {
      logger.error(e);
    }
  }

}
