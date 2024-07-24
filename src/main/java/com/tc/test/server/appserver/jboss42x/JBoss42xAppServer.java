/*
 * Copyright 2003-2008 Terracotta, Inc.
 * Copyright Super iPaaS Integration LLC, an IBM Company 2024
 */
package com.tc.test.server.appserver.jboss42x;

import org.codehaus.cargo.container.InstalledLocalContainer;
import org.codehaus.cargo.container.configuration.LocalConfiguration;
import org.codehaus.cargo.container.jboss.JBoss42xInstalledLocalContainer;
import org.codehaus.cargo.container.jboss.JBossPropertySet;
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
 * JBoss42x AppServer implementation
 */
public final class JBoss42xAppServer extends CargoAppServer {

  public JBoss42xAppServer(JBoss42xAppServerInstallation installation) {
    super(installation);
  }

  @Override
  protected String cargoServerKey() {
    return "jboss42x";
  }

  @Override
  protected InstalledLocalContainer container(LocalConfiguration config, AppServerParameters params) {
    return new TCJBoss42xInstalledLocalContainer(config, params.sars(), appServerInfo(), params);
  }

  @Override
  protected void setConfigProperties(LocalConfiguration config) throws Exception {
    config.setProperty(GeneralPropertySet.RMI_PORT, Integer.toString(AppServerUtil.getPort()));
    config.setProperty(JBossPropertySet.JBOSS_AJP_PORT, Integer.toString(AppServerUtil.getPort()));
  }

  private static class TCJBoss42xInstalledLocalContainer extends JBoss42xInstalledLocalContainer {
    private final Collection         sars;
    private final AppServerInfo      appServerInfo;
    private final AppServerParameters params;
    
    public TCJBoss42xInstalledLocalContainer(LocalConfiguration configuration, Collection sars,
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
