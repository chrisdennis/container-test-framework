/*
 * Copyright 2003-2008 Terracotta, Inc.
 * Copyright Super iPaaS Integration LLC, an IBM Company 2024
 */
package com.tc.test.server.appserver.deployment;

import junit.extensions.TestSetup;
import junit.framework.Test;

public class ErrorTestSetup extends TestSetup {

  public ErrorTestSetup(Test test) {
    super(test);
  }

  public void setUp() {
    if (!getTest().getClass().getName().contains("Abstract")) {
      throw new RuntimeException("Container test needs to have TestSetup for proper cleanup!");
    }
  }
}
