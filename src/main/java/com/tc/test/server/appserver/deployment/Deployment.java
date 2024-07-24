/*
 * Copyright 2003-2008 Terracotta, Inc.
 * Copyright Super iPaaS Integration LLC, an IBM Company 2024
 */
package com.tc.test.server.appserver.deployment;

import java.util.Properties;

public interface Deployment {
  public FileSystemPath getFileSystemPath();

  public boolean isClustered();

  public Properties properties();
}
