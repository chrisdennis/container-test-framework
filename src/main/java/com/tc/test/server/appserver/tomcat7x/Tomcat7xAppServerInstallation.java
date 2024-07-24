/*
 * Copyright 2003-2008 Terracotta, Inc.
 * Copyright Super iPaaS Integration LLC, an IBM Company 2024
 */
package com.tc.test.server.appserver.tomcat7x;

import com.tc.test.AppServerInfo;
import com.tc.test.server.appserver.AbstractAppServerInstallation;

import java.io.File;

/**
 * Defines the appserver name used by the installation process.
 */
public final class Tomcat7xAppServerInstallation extends AbstractAppServerInstallation {

  public Tomcat7xAppServerInstallation(File home, File workingDir, AppServerInfo appServerInfo) throws Exception {
    super(home, workingDir, appServerInfo);
  }

  public String serverType() {
    return "tomcat";
  }
}
