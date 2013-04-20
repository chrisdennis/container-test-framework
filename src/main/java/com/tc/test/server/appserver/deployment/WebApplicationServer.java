/*
 * All content copyright (c) 2003-2008 Terracotta, Inc., except as may otherwise be noted in a separate copyright
 * notice. All rights reserved.
 */
package com.tc.test.server.appserver.deployment;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.tc.test.server.appserver.StandardAppServerParameters;

import java.io.File;
import java.io.IOException;

public interface WebApplicationServer extends Server {

  public StandardAppServerParameters getServerParameters();

  public WebApplicationServer addWarDeployment(Deployment warDeployment, String context);

  public WebApplicationServer addEarDeployment(Deployment earDeployment);

  public HtmlPage ping(String url) throws IOException;

  public HtmlPage ping(String url, WebClient wc) throws IOException;

  public int getPort();

  public File getWorkingDirectory();

  public File getTcConfigFile();
}
