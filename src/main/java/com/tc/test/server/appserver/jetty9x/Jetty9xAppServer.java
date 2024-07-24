/*
 * Copyright Terracotta, Inc.
 * Copyright Super iPaaS Integration LLC, an IBM Company 2024
 */
package com.tc.test.server.appserver.jetty9x;

import com.tc.test.server.appserver.AppServerInstallation;
import com.tc.test.server.appserver.jetty7x.Jetty7xAppServer;

public class Jetty9xAppServer extends Jetty7xAppServer {

  public Jetty9xAppServer(AppServerInstallation installation) {
    super(installation);
  }
}
