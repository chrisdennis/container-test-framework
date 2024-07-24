/*
 * Copyright 2003-2008 Terracotta, Inc.
 * Copyright Super iPaaS Integration LLC, an IBM Company 2024
 */
package com.tc.test.server.appserver.glassfishv2;

import com.tc.test.server.appserver.AppServer;
import com.tc.test.server.appserver.AppServerInstallation;
import com.tc.test.server.appserver.glassfish.AbstractGlassfishAppServerFactory;
import com.tc.test.server.appserver.glassfish.GlassfishAppServerInstallation;

public class GlassfishV2AppServerFactory extends AbstractGlassfishAppServerFactory {

  @Override
  public AppServer createAppServer(AppServerInstallation installation) {
    return new GlassfishV2AppServer((GlassfishAppServerInstallation) installation);
  }

}
