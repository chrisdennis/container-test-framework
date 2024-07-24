/*
 * Copyright 2003-2008 Terracotta, Inc.
 * Copyright Super iPaaS Integration LLC, an IBM Company 2024
 */
package com.tc.test.server.appserver.deployment;


public abstract class AbstractDBServer extends AbstractStoppable implements Stoppable {
    private int serverPort = 0;
    private String dbName = null;

    public int getServerPort() {
      return serverPort;
    }

    public void setServerPort(int serverPort) {
      this.serverPort = serverPort;
    } 

    public String getDbName() {
      return dbName;
    }

    public void setDbName(String dbName) {
      this.dbName = dbName;
    }
}
