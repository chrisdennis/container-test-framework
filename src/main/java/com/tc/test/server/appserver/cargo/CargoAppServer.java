/*
 * All content copyright (c) 2003-2008 Terracotta, Inc., except as may otherwise be noted in a separate copyright
 * notice. All rights reserved.
 */
package com.tc.test.server.appserver.cargo;

import org.codehaus.cargo.container.ContainerException;
import org.codehaus.cargo.container.ContainerType;
import org.codehaus.cargo.container.InstalledLocalContainer;
import org.codehaus.cargo.container.State;
import org.codehaus.cargo.container.configuration.ConfigurationType;
import org.codehaus.cargo.container.configuration.LocalConfiguration;
import org.codehaus.cargo.container.deployable.EAR;
import org.codehaus.cargo.container.deployable.WAR;
import org.codehaus.cargo.container.property.GeneralPropertySet;
import org.codehaus.cargo.container.property.ServletPropertySet;
import org.codehaus.cargo.generic.configuration.ConfigurationFactory;
import org.codehaus.cargo.generic.configuration.DefaultConfigurationFactory;

import com.tc.test.server.ServerParameters;
import com.tc.test.server.ServerResult;
import com.tc.test.server.appserver.AbstractAppServer;
import com.tc.test.server.appserver.AppServer;
import com.tc.test.server.appserver.AppServerInstallation;
import com.tc.test.server.appserver.AppServerParameters;
import com.tc.test.server.appserver.AppServerResult;
import com.tc.test.server.appserver.StandardAppServerParameters;
import com.tc.test.server.util.AppServerUtil;

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Generic {@link AppServer} implementation which delegates to the Codehaus CARGO package
 * http://docs.codehaus.org/display/CARGO/Home
 */
public abstract class CargoAppServer extends AbstractAppServer {

  private static final int        DEFAULT_START_TIMEOUT = 8 * 60 * 1000;
  private InstalledLocalContainer container;
  private int                     port;

  public CargoAppServer(final AppServerInstallation installation) {
    super(installation);
  }

  @Override
  public ServerResult start(final ServerParameters rawParams) throws Exception {
    StandardAppServerParameters params = (StandardAppServerParameters) rawParams;

    adjustParams(params);

    port = AppServerUtil.getPort();
    File instance = createInstance(params);
    setProperties(params, port, instance);

    ConfigurationFactory factory = new DefaultConfigurationFactory();
    LocalConfiguration config = (LocalConfiguration) factory.createConfiguration(cargoServerKey(),
                                                                                 ContainerType.INSTALLED,
                                                                                 ConfigurationType.STANDALONE,
                                                                                 instance.getAbsolutePath());
    setConfigProperties(config);
    addDeployables(config, params.deployables(), params.instanceName());

    container = container(config, params);
    config.setProperty(ServletPropertySet.PORT, Integer.toString(port));
    config.setProperty(GeneralPropertySet.JVMARGS, params.jvmArgs());
    config.setProperty(GeneralPropertySet.LOGGING, "low");
    container.setTimeout(Integer.valueOf(params.properties().getProperty(StandardAppServerParameters.START_TIMEOUT,
                                                                         DEFAULT_START_TIMEOUT + "")));
    container.setHome(serverInstallDirectory().getAbsolutePath());
    container.setLogger(new ConsoleLogger(params.instanceName()));
    setExtraClasspath(params);
    // config.setProperty(WebLogicPropertySet.BEA_HOME, container.getHome());

    container.setOutput(new File(instance.getParent(), params.instanceName() + ".log").getAbsolutePath());
    container.start();
    return new AppServerResult(port, this);
  }

  protected void adjustParams(final StandardAppServerParameters params) throws Exception {
    // override if desired
  }

  @Override
  public void stop(final ServerParameters rawParams) {
    if (container != null) {
      if (container.getState().equals(State.STARTED) || container.getState().equals(State.STARTING)
          || container.getState().equals(State.UNKNOWN)) {
        try {
          // XXX: clear out the jvmargs so that the VMs spawned for stop() don't try to use DSO
          // XXX: If you know a better way to do this, go for it
          String jvmArgs = container.getConfiguration().getPropertyValue(GeneralPropertySet.JVMARGS);
          try {
            container.getConfiguration().setProperty(GeneralPropertySet.JVMARGS, null);
            container.stop(); // NOTE: stop is not guaranteed to work
          } finally {
            container.getConfiguration().setProperty(GeneralPropertySet.JVMARGS, jvmArgs);
          }
        } catch (ContainerException e) {
          throw new RuntimeException(e);
        }
      }
    }
  }

  private void addDeployables(final LocalConfiguration config, final Map wars, final String instanceName) {
    for (Iterator it = wars.entrySet().iterator(); it.hasNext();) {
      Map.Entry entry = (Entry) it.next();
      String context = (String) entry.getKey();
      File deployableFile = (File) entry.getValue();
      System.out.println("Deployable: " + deployableFile);
      if (deployableFile.getName().endsWith("war")) {
        WAR deployable = new WAR(deployableFile.getPath());
        deployable.setContext(context);
        deployable.setLogger(new ConsoleLogger(instanceName));
        config.addDeployable(deployable);
      } else if (deployableFile.getName().endsWith("ear")) {
        EAR deployable = new EAR(deployableFile.getPath());
        deployable.setLogger(new ConsoleLogger(instanceName));
        config.addDeployable(deployable);
      } else {
        throw new RuntimeException("Unknown deployable: " + deployableFile);
      }

    }
  }

  protected final InstalledLocalContainer container() {
    return container;
  }

  protected abstract String cargoServerKey();

  protected abstract InstalledLocalContainer container(LocalConfiguration config, AppServerParameters params);

  protected void setConfigProperties(final LocalConfiguration config) throws Exception {
    // do nothing
  }

  protected void setExtraClasspath(final AppServerParameters params) {
    if (params.extraClasspath().size() > 0) {
      String[] extraClasspath = params.extraClasspath().toArray(new String[0]);
      container().setExtraClasspath(extraClasspath);
      System.out.println("XXX adding extra classpath for " + params.instanceName() + ": " + params.extraClasspath());
    }
  }

}
