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
package com.tc.test.server.appserver;

import com.tc.test.server.Server;
import com.tc.test.server.ServerParameters;
import com.tc.test.server.ServerResult;

/**
 * Data Object returned by {@link AbstractAppServer#start(ServerParameters)}.
 */
public final class AppServerResult implements ServerResult {

  private int    serverPort;
  private Server ref;

  public AppServerResult(int serverPort, Server ref) {
    this.serverPort = serverPort;
    this.ref = ref;
  }

  public int serverPort() {
    return serverPort;
  }

  public Server ref() {
    return ref;
  }
}
