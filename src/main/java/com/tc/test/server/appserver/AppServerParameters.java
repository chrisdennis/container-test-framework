/*
 * Copyright 2003-2008 Terracotta, Inc.
 * Copyright Super iPaaS Integration LLC, an IBM Company 2024
 */
package com.tc.test.server.appserver;

import com.tc.test.server.ServerParameters;
import com.tc.test.server.appserver.deployment.Deployment;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Represents parameters common to appservers. Implementing methods should only be called by classes in this enclosing
 * package.
 */
public interface AppServerParameters extends ServerParameters {

  Map<String, File> deployables();

  Map<String, Deployment> deployments();

  Properties properties();

  String instanceName();

  Collection sars(); // jboss only

  Collection<ValveDefinition> valves(); // tomcat + variants

  List<User> getUsers();
}
