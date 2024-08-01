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

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.tc.test.server.appserver.StandardAppServerParameters;

import java.io.File;
import java.io.IOException;

public interface WebApplicationServer extends Server {

  public StandardAppServerParameters getServerParameters();

  public WebApplicationServer addWarDeployment(Deployment warDeployment, String context);

  public WebApplicationServer addEarDeployment(Deployment earDeployment);

  public WebResponse ping(String url) throws IOException;

  public WebResponse ping(String url, WebClient wc) throws IOException;

  public int getPort();

  public File getWorkingDirectory();

  public File getTcConfigFile();
}
