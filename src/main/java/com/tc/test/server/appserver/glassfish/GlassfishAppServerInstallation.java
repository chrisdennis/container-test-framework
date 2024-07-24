/*
 * Copyright 2003-2008 Terracotta, Inc.
 * Copyright Super iPaaS Integration LLC, an IBM Company 2024
 */
package com.tc.test.server.appserver.glassfish;

import com.tc.test.AppServerInfo;
import com.tc.test.server.appserver.AbstractAppServerInstallation;

import java.io.File;

/**
 * Defines the appserver name used by the installation process.
 */
public final class GlassfishAppServerInstallation extends AbstractAppServerInstallation {

  public GlassfishAppServerInstallation(File home, File workingDir, AppServerInfo appServerInfo) throws Exception {
    super(home, workingDir, appServerInfo);
  }

  public String serverType() {
    return "glassfish";
  }
}
