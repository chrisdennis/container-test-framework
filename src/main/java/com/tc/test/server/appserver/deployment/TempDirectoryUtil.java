/*
 * Copyright 2003-2008 Terracotta, Inc.
 * Copyright Super iPaaS Integration LLC, an IBM Company 2024
 */
package com.tc.test.server.appserver.deployment;

import com.tc.test.TempDirectoryHelper;

import java.io.File;
import java.io.IOException;

public class TempDirectoryUtil {

  private static TempDirectoryHelper tempDirectoryHelper;

  public static File getTempDirectory(Class type) throws IOException {
    return getTempDirectoryHelper(type).getDirectory();
  }

  protected static synchronized TempDirectoryHelper getTempDirectoryHelper(Class type) {
    if (tempDirectoryHelper == null) {
      tempDirectoryHelper = new TempDirectoryHelper(type, true);
    }

    return tempDirectoryHelper;
  }

}
