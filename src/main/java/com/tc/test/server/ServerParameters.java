/*
 * Copyright 2003-2008 Terracotta, Inc.
 * Copyright Super iPaaS Integration LLC, an IBM Company 2024
 */
package com.tc.test.server;

import java.util.List;

/**
 * Arguments passed to a server to be utilized in it's initialization.
 */
public interface ServerParameters {

  String jvmArgs();

  List<String> extraClasspath();
}