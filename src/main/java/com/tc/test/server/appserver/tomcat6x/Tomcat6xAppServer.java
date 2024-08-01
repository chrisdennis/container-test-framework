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
package com.tc.test.server.appserver.tomcat6x;

import org.codehaus.cargo.container.InstalledLocalContainer;
import org.codehaus.cargo.container.configuration.LocalConfiguration;
import org.codehaus.cargo.container.property.GeneralPropertySet;
import org.codehaus.cargo.container.tomcat.Tomcat6xInstalledLocalContainer;
import org.codehaus.cargo.container.tomcat.TomcatPropertySet;

import com.tc.test.server.appserver.AppServerParameters;
import com.tc.test.server.appserver.cargo.CargoAppServer;
import com.tc.test.server.appserver.tomcat.TomcatStartupActions;
import com.tc.test.server.util.AppServerUtil;

/**
 * Tomcat6x AppServer implementation
 */
public final class Tomcat6xAppServer extends CargoAppServer {

  public Tomcat6xAppServer(Tomcat6xAppServerInstallation installation) {
    super(installation);
  }

  @Override
  protected String cargoServerKey() {
    return "tomcat6x";
  }

  @Override
  protected InstalledLocalContainer container(LocalConfiguration config, AppServerParameters params) {
    return new TCTomcat6xInstalledLocalContainer(config, params);
  }

  private static class TCTomcat6xInstalledLocalContainer extends Tomcat6xInstalledLocalContainer {

    private final AppServerParameters params;

    public TCTomcat6xInstalledLocalContainer(LocalConfiguration config, AppServerParameters params) {
      super(config);
      this.params = params;
      config.setProperty(GeneralPropertySet.RMI_PORT, Integer.toString(AppServerUtil.getPort()));
      config.setProperty(TomcatPropertySet.AJP_PORT, Integer.toString(AppServerUtil.getPort()));
      config.setProperty(TomcatPropertySet.CONNECTOR_EMPTY_SESSION_PATH, "false");
    }

    @Override
    protected void startInternal() throws Exception {
      TomcatStartupActions.modifyConfig(params, this, 47);
      TomcatStartupActions.configureManagerApp(params, this);
      super.startInternal();
    }
  }

}
