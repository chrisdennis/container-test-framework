/*
 * All content copyright (c) 2003-2008 Terracotta, Inc., except as may otherwise be noted in a separate copyright
 * notice. All rights reserved.
 */
package com.tc.test.server.appserver.deployment;

import com.tc.config.test.schema.L2ConfigBuilder;
import com.tc.config.test.schema.TerracottaConfigBuilder;
import com.tc.management.beans.L2MBeanNames;
import com.tc.management.beans.TCServerInfoMBean;
import com.tc.objectserver.control.ExtraProcessServerControl;
import com.tc.util.TcConfigBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

public class DSOServer extends AbstractStoppable {

  private static final String       SERVER_TEST_CONFIG = "server-config.xml";

  private ExtraProcessServerControl serverProc         = null;
  private final boolean             withPersistentStore;

  private int                       serverPort         = 9510;
  private int                       adminPort          = 9520;
  private int                       groupPort          = 9530;

  private final List                jvmArgs            = new ArrayList();

  private final File                workingDir;
  private TcConfigBuilder           configBuilder;

  public DSOServer(boolean withPersistentStore, File workingDir) {
    this.withPersistentStore = withPersistentStore;
    this.workingDir = workingDir;
  }

  public DSOServer(boolean withPersistentStore, File workingDir, TcConfigBuilder configBuilder) {
    this.withPersistentStore = withPersistentStore;
    this.workingDir = workingDir;
    this.configBuilder = configBuilder;
    this.serverPort = configBuilder.getTsaPort();
    this.adminPort = configBuilder.getJmxPort();
    this.groupPort = configBuilder.getGroupPort();
  }

  @Override
  protected void doStart() throws Exception {
    File configFile = writeConfig();
    serverProc = new ExtraProcessServerControl("localhost", serverPort, adminPort, configFile.getAbsolutePath(), false);
    serverProc.writeOutputTo(new FileOutputStream(new File(workingDir, "dso-server.log")));
    serverProc.getJvmArgs().addAll(jvmArgs);
    serverProc.start();
    String jmxUrl = "service:jmx:jmxmp://localhost:" + adminPort;
    final long timeout = (5 * 60) * 1000L + System.currentTimeMillis();
    while (System.currentTimeMillis() < timeout && !isServerFullyStarted(jmxUrl) && serverProc.isRunning()) {
      System.out.println("server starting but not yet ready, waiting...");
      Thread.sleep(1000L);
    }
  }

  private boolean isServerFullyStarted(String jmxUrl) {
    logger.debug("Connecting to DSO server at " + jmxUrl);
    JMXConnector jmxc = null;
    try {
      JMXServiceURL url = new JMXServiceURL(jmxUrl);
      jmxc = getJmxConnector(url);
      MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();
      TCServerInfoMBean serverBean = MBeanServerInvocationHandler.newProxyInstance(mbsc, L2MBeanNames.TC_SERVER_INFO,
                                                                                   TCServerInfoMBean.class, false);
      return serverBean.isActive() || serverBean.isPassiveStandby() || serverBean.isPassiveUninitialized();
    } catch (Exception e) {
      return false;
    } finally {
      if (jmxc != null) {
        try {
          jmxc.close();
        } catch (IOException ex) {
          //
        }
      }
    }
  }

  protected static JMXConnector getJmxConnector(final JMXServiceURL url) throws IOException {
    final Map<String, Object> env = new HashMap<String, Object>();
    return JMXConnectorFactory.connect(url, env);
  }

  @Override
  protected void doStop() throws Exception {
    logger.debug("Stopping...");
    serverProc.shutdown();
    logger.debug("...stopped");
  }

  private File writeConfig() throws IOException {
    File configFile = new File(workingDir, SERVER_TEST_CONFIG);
    if (configBuilder != null) {
      configBuilder.setTcConfigFile(configFile);
      configBuilder.saveToFile();
    } else {
      TerracottaConfigBuilder builder = TerracottaConfigBuilder.newMinimalInstance();

      L2ConfigBuilder l2 = builder.getServers().getL2s()[0];
      l2.setTSAPort(serverPort);
      l2.setJMXPort(adminPort);
      l2.setTSAGroupPort(groupPort);
      l2.setData(workingDir + File.separator + "data");
      l2.setLogs(workingDir + File.separator + "logs");
      if (withPersistentStore) {
        builder.getServers().setRestartable(true); // XXX make this one configurable
      }

      String configAsString = builder.toString();

      FileOutputStream fileOutputStream = new FileOutputStream(configFile);
      PrintWriter out = new PrintWriter((fileOutputStream));
      out.println(configAsString);
      out.flush();
      out.close();
    }
    return configFile;
  }

  @Override
  public String toString() {
    return "DSO server; serverport:" + serverPort + "; adminPort:" + adminPort;
  }

  public int getServerPort() {
    return serverPort;
  }

  public int getAdminPort() {
    return adminPort;
  }

  public List getJvmArgs() {
    return jvmArgs;
  }

}
