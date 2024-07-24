/*
 * Copyright 2003-2008 Terracotta, Inc.
 * Copyright Super iPaaS Integration LLC, an IBM Company 2024
 */
package com.tc.test.server.appserver;

import java.io.File;

/**
 * Represents an application server installation. Instantiated implementations should be shared across multiple
 * appservers.
 */
public interface AppServerInstallation {

  void uninstall() throws Exception;

  File dataDirectory();

  File sandboxDirectory();
}