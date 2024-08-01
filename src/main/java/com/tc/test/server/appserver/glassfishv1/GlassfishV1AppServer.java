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
package com.tc.test.server.appserver.glassfishv1;

import com.tc.test.server.appserver.AppServerParameters;
import com.tc.test.server.appserver.glassfish.AbstractGlassfishAppServer;
import com.tc.test.server.appserver.glassfish.GlassfishAppServerInstallation;

import java.io.File;

/**
 * Glassfish AppServer implementation
 */
public final class GlassfishV1AppServer extends AbstractGlassfishAppServer {

  public GlassfishV1AppServer(final GlassfishAppServerInstallation installation) {
    super(installation);
  }

  @Override
  protected String[] getDisplayCommand(final String script, final AppServerParameters params) {
    return new String[] { script, "display" };
  }

  @Override
  protected File getStartScript(final AppServerParameters params) {
    return getInstanceFile("bin/" + getPlatformScript("startserv"));
  }

  @Override
  protected File getStopScript(final AppServerParameters params) {
    return getInstanceFile("bin/" + getPlatformScript("stopserv"));
  }
}