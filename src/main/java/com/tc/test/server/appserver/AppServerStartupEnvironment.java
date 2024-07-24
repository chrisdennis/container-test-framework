/*
 * Copyright 2003-2008 Terracotta, Inc.
 * Copyright Super iPaaS Integration LLC, an IBM Company 2024
 */
package com.tc.test.server.appserver;

import com.tc.test.AppServerInfo;

import java.io.File;

/**
 * This interface is to be called by implementations of {@link AbstractAppServer} only. Do not typecast concrete
 * installations to this type! Do not call these methods under any circumstances.
 */
interface AppServerStartupEnvironment extends AppServerInstallation {

  File workingDirectory();

  File serverInstallDirectory();

  AppServerInfo appServerInfo();

  boolean isRepoInstall();
}
