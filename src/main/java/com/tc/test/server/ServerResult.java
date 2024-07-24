/*
 * Copyright 2003-2008 Terracotta, Inc.
 * Copyright Super iPaaS Integration LLC, an IBM Company 2024
 */
package com.tc.test.server;

/**
 * Return values after a server has been initialized and started.
 */
public interface ServerResult {
  
  int serverPort();
  
  Server ref(); // reference to current server instance
}
