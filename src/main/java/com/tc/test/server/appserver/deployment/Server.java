/*
 * Copyright 2003-2008 Terracotta, Inc.
 * Copyright Super iPaaS Integration LLC, an IBM Company 2024
 */
package com.tc.test.server.appserver.deployment;


import javax.management.MBeanServerConnection;

public interface Server extends Stoppable {
  public Server restart() throws Exception;
  public MBeanServerConnection getMBeanServerConnection() throws Exception;
}
