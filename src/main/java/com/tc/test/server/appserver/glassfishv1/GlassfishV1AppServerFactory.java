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

import com.tc.test.server.appserver.AppServer;
import com.tc.test.server.appserver.AppServerFactory;
import com.tc.test.server.appserver.AppServerInstallation;
import com.tc.test.server.appserver.glassfish.AbstractGlassfishAppServerFactory;
import com.tc.test.server.appserver.glassfish.GlassfishAppServerInstallation;

/**
 * This class creates specific implementations of return values for the given methods. To obtain an instance you must
 * call {@link AppServerFactory#createFactoryFromProperties()}.
 */
public final class GlassfishV1AppServerFactory extends AbstractGlassfishAppServerFactory {

  @Override
  public AppServer createAppServer(AppServerInstallation installation) {
    return new GlassfishV1AppServer((GlassfishAppServerInstallation) installation);
  }

}
