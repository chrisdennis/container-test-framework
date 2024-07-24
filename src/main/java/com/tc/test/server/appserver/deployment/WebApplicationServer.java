/*
 * Copyright 2003-2008 Terracotta, Inc.
 * Copyright Super iPaaS Integration LLC, an IBM Company 2024
 */
package com.tc.test.server.appserver.deployment;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.tc.test.server.appserver.StandardAppServerParameters;

import java.io.File;
import java.io.IOException;

public interface WebApplicationServer extends Server {

  public StandardAppServerParameters getServerParameters();

  public WebApplicationServer addWarDeployment(Deployment warDeployment, String context);

  public WebApplicationServer addEarDeployment(Deployment earDeployment);

  public WebResponse ping(String url) throws IOException;

  public WebResponse ping(String url, WebClient wc) throws IOException;

  public int getPort();

  public File getWorkingDirectory();

  public File getTcConfigFile();
}
