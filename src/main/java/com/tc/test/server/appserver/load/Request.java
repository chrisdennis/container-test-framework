/*
 * Copyright 2003-2008 Terracotta, Inc.
 * Copyright Super iPaaS Integration LLC, an IBM Company 2024
 */
package com.tc.test.server.appserver.load;

import org.apache.commons.httpclient.HttpClient;

import java.net.URL;

public interface Request {

  public void setEnterQueueTime();

  public void setExitQueueTime();

  public void setProcessCompletionTime();

  public URL getUrl();

  public long getEnterQueueTime();

  public long getExitQueueTime();

  public long getProcessCompletionTime();

  public HttpClient getClient();

  public int getAppserverID();

  public String printData();
}
