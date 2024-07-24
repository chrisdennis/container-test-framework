/*
 * Copyright 2003-2008 Terracotta, Inc.
 * Copyright Super iPaaS Integration LLC, an IBM Company 2024
 */
package com.tc.test.server.appserver.deployment;

public interface Stoppable {

  void start() throws Exception;

  void stop() throws Exception;

  public void stopIgnoringExceptions();

  boolean isStopped();

}
