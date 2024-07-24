/*
 * Copyright 2003-2008 Terracotta, Inc.
 * Copyright Super iPaaS Integration LLC, an IBM Company 2024
 */
package com.tc.test.server.appserver.jetty8x;

import com.tc.test.AppServerInfo;
import com.tc.test.server.appserver.AppServer;
import com.tc.test.server.appserver.AppServerFactory;
import com.tc.test.server.appserver.AppServerInstallation;
import com.tc.test.server.appserver.AppServerParameters;
import com.tc.test.server.appserver.StandardAppServerParameters;

import java.io.File;
import java.util.Properties;

/**
 * This class creates specific implementations of return values for the given methods. To obtain an instance you must
 * call {@link AppServerFactory#createFactoryFromProperties()}.
 */
public final class Jetty8xAppServerFactory extends AppServerFactory {

  @Override
  public AppServerParameters createParameters(String instanceName, Properties props) {
    return new StandardAppServerParameters(instanceName, props);
  }

  @Override
  public AppServer createAppServer(AppServerInstallation installation) {
    return new Jetty8xAppServer(installation);
  }

  @Override
  public AppServerInstallation createInstallation(File home, File workingDir, AppServerInfo appServerInfo)
      throws Exception {
    return new Jetty8xAppServerInstallation(home, workingDir, appServerInfo);
  }
}
