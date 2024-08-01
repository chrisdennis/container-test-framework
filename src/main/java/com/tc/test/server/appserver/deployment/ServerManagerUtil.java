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

import java.util.Collection;

public class ServerManagerUtil {

  protected final static Log logger = LogFactory.getLog(ServerManagerUtil.class);
  private static ServerManager theServerManager;

  private static synchronized ServerManager start(Class testClass, boolean withPersistentStore, boolean useProxy,
                                                  Boolean isSessionLocking, Boolean isSynchronousWrite,
                                                  Collection extraJvmArgs) throws Exception {
    ServerManager existingServerManager = getExistingServerManager();
    if (existingServerManager != null) {
      logger.debug("Using existing ServerManager");
      return existingServerManager;
    }
    logger.debug("Creating server manager");
    ServerManager serverManager = new ServerManager(testClass, extraJvmArgs, isSessionLocking, isSynchronousWrite,
                                                    useProxy);
    serverManager.start(withPersistentStore);
    return serverManager;
  }

  private static synchronized void stop(ServerManager serverManager) {
    ServerManager existingServerManager = getExistingServerManager();
    if (existingServerManager != null) {
      logger.debug("Not stopping existing ServerManager");
      return;
    }
    logger.debug("Stopping ServerManager");
    serverManager.stop();
  }

  private static synchronized ServerManager getExistingServerManager() {
    return theServerManager;
  }

  public static synchronized ServerManager startAndBind(Class testClass, boolean withPersistentStore, boolean useProxy,
                                                        Boolean isSessionLocking, Boolean isSynchronousWrite,
                                                        Collection extraJvmArgs)
      throws Exception {
    ServerManager sm = start(testClass, withPersistentStore, useProxy, isSessionLocking, isSynchronousWrite,
                             extraJvmArgs);
    theServerManager = sm;
    return sm;
  }

  public static synchronized void stopAndRelease(ServerManager sm) {
    theServerManager = null;
    stop(sm);
  }

  public static synchronized void stopAllWebServers(ServerManager serverManager) {
    getExistingServerManager().stopAllWebServers();
  }

}
