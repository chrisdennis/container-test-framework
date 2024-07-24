/*
 * Copyright 2003-2008 Terracotta, Inc.
 * Copyright Super iPaaS Integration LLC, an IBM Company 2024
 */
package com.tc.test.server;

/**
 * Represents a generic server to be run as part of a unit test.
 */
public interface Server {

  ServerResult start(ServerParameters parameters) throws Exception;

  void stop(ServerParameters parameters) throws Exception;
}
