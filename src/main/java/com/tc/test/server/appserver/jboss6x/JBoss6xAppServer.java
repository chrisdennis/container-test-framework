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
package com.tc.test.server.appserver.jboss6x;

import org.apache.commons.io.FileUtils;
import org.codehaus.cargo.container.InstalledLocalContainer;
import org.codehaus.cargo.container.configuration.LocalConfiguration;
import org.codehaus.cargo.container.jboss.JBoss6xInstalledLocalContainer;
import org.codehaus.cargo.container.jboss.JBossPropertySet;
import org.codehaus.cargo.container.property.GeneralPropertySet;
import org.codehaus.cargo.container.spi.jvm.JvmLauncher;

import com.tc.test.AppServerInfo;
import com.tc.test.server.ServerParameters;
import com.tc.test.server.appserver.AppServerParameters;
import com.tc.test.server.appserver.cargo.CargoAppServer;
import com.tc.test.server.appserver.jboss_common.JBossHelper;
import com.tc.test.server.util.AppServerUtil;

import java.io.File;
import java.util.Collection;

/**
 * JBoss6x AppServer implementation
 */
public final class JBoss6xAppServer extends CargoAppServer {

  public JBoss6xAppServer(JBoss6xAppServerInstallation installation) {
    super(installation);
  }

  @Override
  protected String cargoServerKey() {
    return "jboss6x";
  }

  @Override
  protected InstalledLocalContainer container(LocalConfiguration config, AppServerParameters params) {
    return new TCJBoss6xInstalledLocalContainer(config, params.sars(), appServerInfo(), params);
  }

  @Override
  protected void setConfigProperties(LocalConfiguration config) throws Exception {
    config.setProperty(GeneralPropertySet.RMI_PORT, Integer.toString(AppServerUtil.getPort()));
    config.setProperty(JBossPropertySet.JBOSS_AJP_PORT, Integer.toString(AppServerUtil.getPort()));
    config.setProperty(JBossPropertySet.JBOSS_JMX_PORT, Integer.toString(AppServerUtil.getPort()));
    config.setProperty(JBossPropertySet.JBOSS_JRMP_PORT, Integer.toString(AppServerUtil.getPort()));
    config.setProperty(JBossPropertySet.JBOSS_NAMING_PORT, Integer.toString(AppServerUtil.getPort()));
    config.setProperty(JBossPropertySet.JBOSS_CLASSLOADING_WEBSERVICE_PORT, Integer.toString(AppServerUtil.getPort()));
    config.setProperty(JBossPropertySet.JBOSS_JRMP_INVOKER_PORT, Integer.toString(AppServerUtil.getPort()));
    config.setProperty(JBossPropertySet.JBOSS_INVOKER_POOL_PORT, Integer.toString(AppServerUtil.getPort()));
    config.setProperty(JBossPropertySet.JBOSS_REMOTING_TRANSPORT_PORT, Integer.toString(AppServerUtil.getPort()));
    config.setProperty(JBossPropertySet.JBOSS_EJB3_REMOTING_PORT, Integer.toString(AppServerUtil.getPort()));
    config.setProperty(JBossPropertySet.JBOSS_TRANSACTION_RECOVERY_MANAGER_PORT,
                       Integer.toString(AppServerUtil.getPort()));
    config.setProperty(JBossPropertySet.JBOSS_TRANSACTION_STATUS_MANAGER_PORT,
                       Integer.toString(AppServerUtil.getPort()));
    config.setProperty(JBossPropertySet.REMOTEDEPLOY_PORT, Integer.toString(AppServerUtil.getPort()));
  }

  @Override
  public void stop(ServerParameters rawParams) {
    try {
      super.stop(rawParams);
    } finally {
      FileUtils.deleteQuietly(new File(this.instanceDir(), "data"));
      FileUtils.deleteQuietly(new File(this.instanceDir(), "deployers"));
    }
  }

  private static class TCJBoss6xInstalledLocalContainer extends JBoss6xInstalledLocalContainer {
    private final Collection          sars;
    private final AppServerInfo       appServerInfo;
    private final AppServerParameters params;

    public TCJBoss6xInstalledLocalContainer(LocalConfiguration configuration, Collection sars,
                                            AppServerInfo appServerInfo, AppServerParameters params) {
      super(configuration);
      this.sars = sars;
      this.appServerInfo = appServerInfo;
      this.params = params;
    }

    @Override
    protected void doStart(JvmLauncher java) throws Exception {
      JBossHelper.startupActions(new File(getConfiguration().getHome()), sars, appServerInfo, params);
      super.doStart(java);
    }
  }

}
