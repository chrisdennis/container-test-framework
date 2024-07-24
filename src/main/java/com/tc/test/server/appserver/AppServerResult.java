/*
 * Copyright 2003-2008 Terracotta, Inc.
 * Copyright Super iPaaS Integration LLC, an IBM Company 2024
 */
package com.tc.test.server.appserver;

import com.tc.test.server.Server;
import com.tc.test.server.ServerParameters;
import com.tc.test.server.ServerResult;

/**
 * Data Object returned by {@link AbstractAppServer#start(ServerParameters)}.
 */
public final class AppServerResult implements ServerResult {

  private int    serverPort;
  private Server ref;

  public AppServerResult(int serverPort, Server ref) {
    this.serverPort = serverPort;
    this.ref = ref;
  }

  public int serverPort() {
    return serverPort;
  }

  public Server ref() {
    return ref;
  }
}
