/*
 * Copyright Terracotta, Inc.
 * Copyright Super iPaaS Integration LLC, an IBM Company 2024
 */
package com.tc.test.server.util;

public class RetryException extends Exception {
  public RetryException(final String msg) {
    super(msg);
  }
}
