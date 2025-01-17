/*
 * Copyright 2003-2008 Terracotta, Inc.
 * Copyright Super iPaaS Integration LLC, an IBM Company 2024
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tc.test.server.appserver;

import org.apache.commons.io.FileUtils;

import com.tc.test.AppServerInfo;
import com.tc.util.Assert;

import java.io.File;

/**
 * Manages the installed environment pertaining to an appserver. This class is supplied as a constructor argument to
 * {@link AbstractAppServer}.
 */
public abstract class AbstractAppServerInstallation implements AppServerStartupEnvironment {

  private final AppServerInfo appServerInfo;
  private final File          workingDirectory;
  private final File          serverInstall;
  private final File          dataDirectory;
  private final File          sandboxDirectory;
  private final boolean       isRepoInstall;

  /**
   * Use existing installation (example: CATALINA_HOME)
   */
  public AbstractAppServerInstallation(File home, File workingDir, AppServerInfo appServerInfo) throws Exception {
    Assert.assertTrue(home.isDirectory());
    Assert.assertTrue(workingDir.isDirectory());
    this.appServerInfo = appServerInfo;
    this.serverInstall = home;
    this.isRepoInstall = false;
    this.workingDirectory = workingDir;
    (this.dataDirectory = new File(workingDirectory + File.separator + AppServerConstants.DATA_DIR)).mkdir();
    this.sandboxDirectory = workingDirectory;
    // description file for the working directory with filename indicating the server type. Can add more desciptive
    // information if needed.
    new File(workingDir + File.separator + appServerInfo.toString()).createNewFile();

  }

  public final File dataDirectory() {
    return dataDirectory;
  }

  public abstract String serverType();

  public final void uninstall() throws Exception {
    FileUtils.deleteDirectory(workingDirectory.getParentFile());
  }

  public final File workingDirectory() {
    return workingDirectory;
  }

  public final File serverInstallDirectory() {
    return serverInstall;
  }

  public File sandboxDirectory() {
    return sandboxDirectory;
  }

  public boolean isRepoInstall() {
    return isRepoInstall;
  }
  
  public AppServerInfo appServerInfo() {
    return appServerInfo;
  }
}
