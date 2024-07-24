/*
 * Copyright 2003-2008 Terracotta, Inc.
 * Copyright Super iPaaS Integration LLC, an IBM Company 2024
 */
package com.tc.test.server.appserver.deployment;

import java.util.Map;

public interface ProxyBuilder {
  public static final String EXPORTER_TYPE_KEY = "exporter-type";
  public static final String HTTP_CLIENT_KEY = "http-client";
  
  public Object createProxy(Class serviceType, String url, Map initialContext) throws Exception;
}
