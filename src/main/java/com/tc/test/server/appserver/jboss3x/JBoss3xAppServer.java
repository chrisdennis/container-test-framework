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
package com.tc.test.server.appserver.jboss3x;

import org.codehaus.cargo.container.InstalledLocalContainer;
import org.codehaus.cargo.container.configuration.LocalConfiguration;
import org.codehaus.cargo.container.jboss.JBoss3xInstalledLocalContainer;
import org.codehaus.cargo.container.property.GeneralPropertySet;
import org.codehaus.cargo.container.spi.jvm.JvmLauncher;

import com.tc.test.AppServerInfo;
import com.tc.test.server.appserver.AppServerParameters;
import com.tc.test.server.appserver.cargo.CargoAppServer;
import com.tc.test.server.appserver.jboss_common.JBossHelper;
import com.tc.test.server.util.AppServerUtil;

import java.io.File;
import java.util.Collection;

/**
 * JBoss3x AppServer implementation
 */
public final class JBoss3xAppServer extends CargoAppServer {

  public JBoss3xAppServer(JBoss3xAppServerInstallation installation) {
    super(installation);
  }

  @Override
  protected String cargoServerKey() {
    return "jboss3x";
  }

  @Override
  protected InstalledLocalContainer container(LocalConfiguration config, AppServerParameters params) {
    return new TCJBoss3xInstalledLocalContainer(config, params.sars(), appServerInfo(), params);
  }

  @Override
  protected void setConfigProperties(LocalConfiguration config) throws Exception {
    config.setProperty(GeneralPropertySet.RMI_PORT, Integer.toString(AppServerUtil.getPort()));
  }

  private static class TCJBoss3xInstalledLocalContainer extends JBoss3xInstalledLocalContainer {

    private final Collection         sars;
    private final AppServerInfo      appServerInfo;
    private final AppServerParameters params;

    public TCJBoss3xInstalledLocalContainer(LocalConfiguration configuration, Collection sars,
                                            AppServerInfo appServerInfo, AppServerParameters params) {
      super(configuration);
      this.sars = sars;
      this.appServerInfo = appServerInfo;
      this.params = params;
    }

    @Override
    protected void doStart(JvmLauncher arg0) throws Exception {
      JBossHelper.startupActions(new File(getConfiguration().getHome()), sars, appServerInfo, params);
      super.doStart(arg0);
    }
  }

}
