/*
 * Copyright 2003-2008 Terracotta, Inc.
 * Copyright Super iPaaS Integration LLC, an IBM Company 2024
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tc.test.server.appserver.weblogic10x;

import org.codehaus.cargo.container.InstalledLocalContainer;
import org.codehaus.cargo.container.configuration.LocalConfiguration;
import org.codehaus.cargo.container.property.GeneralPropertySet;
import org.codehaus.cargo.container.weblogic.WebLogic10xInstalledLocalContainer;

import com.tc.test.server.appserver.AppServerParameters;
import com.tc.test.server.appserver.weblogic.WeblogicAppServerBase;
import com.tc.util.ReplaceLine;

import java.io.File;
import java.io.IOException;

/**
 * Weblogic10x AppServer implementation
 */
public final class Weblogic10xAppServer extends WeblogicAppServerBase {

  public Weblogic10xAppServer(Weblogic10xAppServerInstallation installation) {
    super(installation);
  }

  @Override
  public File serverInstallDirectory() {
    return new File(super.serverInstallDirectory(), "wlserver");
  }

  @Override
  protected String cargoServerKey() {
    return "weblogic103x";
  }

  @Override
  protected InstalledLocalContainer container(LocalConfiguration config, AppServerParameters params) {
    return new TCWebLogic10xInstalledLocalContainer(config, params);
  }

  @Override
  protected void setConfigProperties(LocalConfiguration config) throws Exception {
    config.setProperty(GeneralPropertySet.HOSTNAME, "0.0.0.0");
  }

  private static class TCWebLogic10xInstalledLocalContainer extends WebLogic10xInstalledLocalContainer {

    public TCWebLogic10xInstalledLocalContainer(LocalConfiguration configuration, AppServerParameters params) {
      super(configuration);
    }

    @Override
    protected void startInternal() throws Exception {
      adjustConfig();
      super.startInternal();
    }

    private void adjustConfig() {
      String insert = "";
      insert += "    <native-io-enabled>false</native-io-enabled>\n";
      insert += "    <socket-reader-timeout-min-millis>1000</socket-reader-timeout-min-millis>\n";
      insert += "    <socket-reader-timeout-max-millis>1000</socket-reader-timeout-max-millis>\n";

      ReplaceLine.Token[] tokens = new ReplaceLine.Token[1];
      tokens[0] = new ReplaceLine.Token(28, "    <listen-port>", insert + "    <listen-port>");

      try {
        ReplaceLine.parseFile(tokens, new File(getConfiguration().getHome(), "/config/config.xml"));
      } catch (IOException ioe) {
        throw new RuntimeException(ioe);
      }
    }

    @Override
    public void addExtraClasspath(String location) {
      super.addExtraClasspath(location);
    }
  }
}
