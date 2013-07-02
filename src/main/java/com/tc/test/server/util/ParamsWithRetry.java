/*
 * All content copyright Terracotta, Inc., unless otherwise indicated. All rights reserved.
 */
package com.tc.test.server.util;

import com.tc.test.server.appserver.AppServerParameters;
import com.tc.test.server.appserver.User;
import com.tc.test.server.appserver.ValveDefinition;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class ParamsWithRetry implements AppServerParameters {

  private final AppServerParameters delegate;
  private final int                 retryNum;

  public ParamsWithRetry(final AppServerParameters delegate, final int retryNum) {
    this.delegate = delegate;
    this.retryNum = retryNum;
  }

  @Override
  public List<String> extraClasspath() {
    return delegate.extraClasspath();
  }

  @Override
  public String instanceName() {
    return delegate.instanceName() + (retryNum == 0 ? "" : "-retry" + retryNum);
  }

  @Override
  public String jvmArgs() {
    return delegate.jvmArgs();
  }

  @Override
  public Properties properties() {
    return delegate.properties();
  }

  @Override
  public Collection sars() {
    return delegate.sars();
  }

  @Override
  public Map deployables() {
    return delegate.deployables();
  }

  @Override
  public Collection<ValveDefinition> valves() {
    return delegate.valves();
  }

  @Override
  public Map deployments() {
    return delegate.deployments();
  }

  @Override
  public List<User> getUsers() {
    return delegate.getUsers();
  }
}
