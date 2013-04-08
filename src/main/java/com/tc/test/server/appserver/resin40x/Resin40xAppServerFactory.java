/*
 * All content copyright (c) 2003-2008 Terracotta, Inc., except as may otherwise be noted in a separate copyright
 * notice. All rights reserved.
 */
package com.tc.test.server.appserver.resin40x;

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
 * call {@link NewAppServerFactory.createFactoryFromProperties()}.
 */
public final class Resin40xAppServerFactory extends AppServerFactory {

  @Override
  public AppServerParameters createParameters(String instanceName, Properties props) {
    return new StandardAppServerParameters(instanceName, props);
  }

  @Override
  public AppServer createAppServer(AppServerInstallation installation) {
    return new Resin40xAppServer((Resin40xAppServerInstallation) installation);
  }

  @Override
  public AppServerInstallation createInstallation(File home, File workingDir, AppServerInfo appServerInfo)
      throws Exception {
    return new Resin40xAppServerInstallation(home, workingDir, appServerInfo);
  }
}