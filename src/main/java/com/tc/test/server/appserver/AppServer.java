/*
 * Copyright 2003-2008 Terracotta, Inc.
 * Copyright Super iPaaS Integration LLC, an IBM Company 2024
 */
package com.tc.test.server.appserver;

import com.tc.test.server.Server;

/**
 * Knows how to start and stop an application server. Application Servers must be started in serial to avoid race
 * conditions in allocating ports.
 */
public interface AppServer extends Server {
  // available for future feature enhancements
}
