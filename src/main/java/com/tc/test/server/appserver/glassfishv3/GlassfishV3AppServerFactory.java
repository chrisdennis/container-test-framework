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
package com.tc.test.server.appserver.glassfishv3;

import com.tc.test.AppServerInfo;
import com.tc.test.server.appserver.AppServer;
import com.tc.test.server.appserver.AppServerFactory;
import com.tc.test.server.appserver.AppServerInstallation;
import com.tc.test.server.appserver.AppServerParameters;
import com.tc.test.server.appserver.StandardAppServerParameters;
import com.tc.test.server.appserver.glassfish.GlassfishAppServerInstallation;

import java.io.File;
import java.util.Properties;

public class GlassfishV3AppServerFactory extends AppServerFactory {

  @Override
  public AppServerParameters createParameters(final String instanceName, final Properties props) {
    return new StandardAppServerParameters(instanceName, props);
  }

  @Override
  public AppServerInstallation createInstallation(final File home, final File workingDir,
                                                  final AppServerInfo appServerInfo) throws Exception {
    GlassfishAppServerInstallation install = new GlassfishAppServerInstallation(home, workingDir, appServerInfo);
    return install;
  }

  @Override
  public AppServer createAppServer(final AppServerInstallation installation) {
    return new GlassfishV3AppServer((GlassfishAppServerInstallation) installation);
  }
}