/*
 * All content copyright (c) 2003-2008 Terracotta, Inc., except as may otherwise be noted in a separate copyright
 * notice. All rights reserved.
 */
package com.tc.test.server.appserver;

import org.apache.commons.io.IOUtils;

import com.tc.test.server.appserver.deployment.Deployment;
import com.tc.util.runtime.Os;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * This concrete implementation allows it's creator to set values while the appserver itself interacts with the
 * immutable {@link AppServerParameters} interface.
 */
public class StandardAppServerParameters implements AppServerParameters {
  public static final String                START_TIMEOUT    = "START_TIMEOUT";

  private final Map<String, Deployment>     deployments      = new HashMap<String, Deployment>();
  private final Collection                  sars             = new ArrayList();
  private final Collection<ValveDefinition> valves           = new ArrayList();
  private final String                      instanceName;
  private final Properties                  props;
  private final List<String>                extraClassPath = new ArrayList<String>();
  private final List<User>                  users          = new ArrayList<User>();
  private String                            jvmArgs          = "";

  public StandardAppServerParameters(String instanceName, Properties props) {
    this.instanceName = instanceName;
    this.props = props;
  }

  public void addDeployment(String context, Deployment deployment) {
    deployments.put(context, deployment);
  }

  public final void addSar(File sar) {
    sars.add(sar);
  }

  public final void addValve(ValveDefinition def) {
    valves.add(def);
  }

  @Override
  public final String jvmArgs() {
    return jvmArgs;
  }

  public final void appendJvmArgs(String jvmArgsVar) {
    this.jvmArgs += jvmArgsVar + " ";
  }

  @Override
  public final List<String> extraClasspath() {
    return extraClassPath;
  }

  public final void appendClasspath(String classpathVar) {
    extraClassPath.add(classpathVar);
  }

  @Override
  public final Map<String, File> deployables() {
    Map<String, File> deployables = new HashMap<String, File>();
    for (Map.Entry<String, Deployment> e : deployments.entrySet()) {
      deployables.put(e.getKey(), e.getValue().getFileSystemPath().getFile());
    }
    return deployables;
  }

  @Override
  public Map<String, Deployment> deployments() {
    return deployments;
  }

  @Override
  public final String instanceName() {
    return instanceName;
  }

  @Override
  public final Properties properties() {
    return props;
  }

  public String writeTerracottaClassPathFile() {
    FileOutputStream fos = null;

    try {
      File tempFile = File.createTempFile("tc-classpath", instanceName);
      tempFile.deleteOnExit();
      fos = new FileOutputStream(tempFile);
      fos.write(System.getProperty("java.class.path").getBytes());

      String rv = tempFile.getAbsolutePath();
      if (Os.isWindows()) {
        rv = "/" + rv;
      }

      return rv;
    } catch (IOException ioe) {
      throw new AssertionError(ioe);
    } finally {
      IOUtils.closeQuietly(fos);
    }

  }

  public void appendSysProp(String name, int value) {
    appendSysProp(name, Integer.toString(value));
  }

  public void appendSysProp(String name, String value) {
    if (!name.startsWith("-")) {
      name = "-D" + name;
    }
    if (value == null) appendJvmArgs(name);
    else appendJvmArgs(name + "=" + value);
  }

  public void appendSysProp(String name) {
    appendSysProp(name, null);
  }

  public void appendSysProp(String name, boolean b) {
    appendSysProp(name, Boolean.toString(b));
  }

  @Override
  public Collection sars() {
    return sars;
  }

  @Override
  public Collection<ValveDefinition> valves() {
    return valves;
  }

  @Override
  public List<User> getUsers() {
    return users;
  }

  public void addUser(User user) {
    users.add(user);
  }

}
