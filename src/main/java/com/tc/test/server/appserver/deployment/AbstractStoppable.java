/*
 * Copyright 2003-2008 Terracotta, Inc.
 * Copyright Super iPaaS Integration LLC, an IBM Company 2024
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
