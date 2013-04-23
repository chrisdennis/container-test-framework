/*
 * All content copyright Terracotta, Inc., unless otherwise indicated. All rights reserved.
 */
package com.tc.test.server.appserver.weblogic;

import org.codehaus.cargo.container.configuration.LocalConfiguration;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.tc.test.server.appserver.AppServerInstallation;
import com.tc.test.server.appserver.StandardAppServerParameters;
import com.tc.test.server.appserver.cargo.CargoAppServer;
import com.tc.test.server.appserver.deployment.Deployment;
import com.tc.test.server.appserver.deployment.WARBuilder;

import java.io.File;
import java.util.HashMap;

import junit.framework.Assert;

public abstract class WeblogicAppServerBase extends CargoAppServer {

  private static final String CONTEXT = "WLS_SHUTDOWN_HACK";

  public WeblogicAppServerBase(AppServerInstallation installation) {
    super(installation);
  }

  @Override
  protected void adjustParams(StandardAppServerParameters params) throws Exception {
    WARBuilder builder = new WARBuilder(CONTEXT + ".war", new File(this.sandboxDirectory(), "war"));
    builder.addServlet("shutdown", "/*", WeblogicShutdownServlet.class, new HashMap(), true);
    Deployment deployment = builder.makeDeployment();
    params.addDeployment(CONTEXT, deployment);
  }

  public static void doStop(LocalConfiguration configuration) throws Exception {
    // The standard weblogic mechanisms for stopping the server fail sporadically, so call this servlet to initiate
    // shutdown from inside the container.

    String port = configuration.getPropertyValue("cargo.servlet.port");
    WebClient wc = new WebClient();
    String fullURL = "http://localhost:" + port + "/WLS_SHUTDOWN_HACK/Go";
    WebResponse response = wc.getPage(fullURL);
    Assert.assertEquals("Server error:\n" + response.getContentAsString(), 200, response.getStatusCode());
    Assert.assertEquals("Server error:\n" + response.getContentAsString(), 0, response.getContentAsString()
        .length());
  }

}
