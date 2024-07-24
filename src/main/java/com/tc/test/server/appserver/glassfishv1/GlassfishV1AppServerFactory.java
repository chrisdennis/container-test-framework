/*
 * Copyright 2003-2008 Terracotta, Inc.
 * Copyright Super iPaaS Integration LLC, an IBM Company 2024
 */
package com.tc.test.server.appserver.glassfishv1;

import com.tc.test.server.appserver.AppServer;
import com.tc.test.server.appserver.AppServerFactory;
import com.tc.test.server.appserver.AppServerInstallation;
import com.tc.test.server.appserver.glassfish.AbstractGlassfishAppServerFactory;
import com.tc.test.server.appserver.glassfish.GlassfishAppServerInstallation;

/**
 * This class creates specific implementations of return values for the given methods. To obtain an instance you must
 * call {@link AppServerFactory#createFactoryFromProperties()}.
 */
public final class GlassfishV1AppServerFactory extends AbstractGlassfishAppServerFactory {

  @Override
  public AppServer createAppServer(AppServerInstallation installation) {
    return new GlassfishV1AppServer((GlassfishAppServerInstallation) installation);
  }

}
