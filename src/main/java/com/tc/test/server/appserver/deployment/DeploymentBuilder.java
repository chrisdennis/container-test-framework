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

package com.tc.test.server.appserver.deployment;

import com.tc.test.server.appserver.deployment.WARBuilder.FilterDefinition;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface DeploymentBuilder {

  public DeploymentBuilder addDirectoryOrJARContainingClass(Class type);

  public DeploymentBuilder addDirectoryOrJAR(String file);

  public DeploymentBuilder addDirectoriesOrJARs(List<String> files);

  public DeploymentBuilder addDirectoryOrJARContainingClassOfSelectedVersion(Class type, String[] variantNames);

  public DeploymentBuilder addDirectoryContainingResource(String resource);

  public DeploymentBuilder addResource(String location, String includes, String prefix);

  public DeploymentBuilder addResourceFullpath(String location, String includes, String fullpath);

  public DeploymentBuilder addFileAsResource(File file, String fullpath);

  public DeploymentBuilder addContextParameter(String name, String value);

  public DeploymentBuilder addSessionConfig(String name, String value);

  public DeploymentBuilder addListener(Class listenerName);

  public DeploymentBuilder addServlet(String name, String mapping, Class servletClass, Map params, boolean loadOnStartup);

  public DeploymentBuilder addFilter(String name, String mapping, Class filterClass, Map params);

  public DeploymentBuilder addFilter(String name, String mapping, Class filterClass, Map params,
                                     Set<WARBuilder.Dispatcher> dispatchers);

  public DeploymentBuilder setDispatcherServlet(String name, String mapping, Class servletClass, Map params,
                                                boolean loadOnStartup);

  public DeploymentBuilder addTaglib(String uri, String location);

  public DeploymentBuilder addErrorPage(int status, String location);

  public DeploymentBuilder setNeededWebXml(boolean flag);

  public Deployment makeDeployment() throws Exception;

  public boolean isClustered();

  public FilterDefinition getFilterDefinition(String name);

  public void addWebXmlFragment(String fragment);

  public void setWebAppVersion(String version);
}
