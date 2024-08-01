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
package com.tc.test.server.appserver.load;

import org.apache.commons.httpclient.HttpClient;

import java.net.URL;

public class ExitRequest implements Request {

  public void setEnterQueueTime() {
    throw new RuntimeException("ExitRequest object has no data!");
  }

  public void setExitQueueTime() {
    throw new RuntimeException("ExitRequest object has no data!");
  }

  public void setProcessCompletionTime() {
    throw new RuntimeException("ExitRequest object has no data!");
  }

  public URL getUrl() {
    throw new RuntimeException("ExitRequest object is not associated with an url!");
  }

  public long getEnterQueueTime() {
    throw new RuntimeException("ExitRequest object has no data!");
  }

  public long getExitQueueTime() {
    throw new RuntimeException("ExitRequest object has no data!");
  }

  public long getProcessCompletionTime() {
    throw new RuntimeException("ExitRequest object has no data!");
  }

  public HttpClient getClient() {
    throw new RuntimeException("ExitRequest object is not associated with a client!");
  }

  public int getAppserverID() {
    throw new RuntimeException("ExitRequest object is not associated with an app server!");
  }

  public String toString() {
    return "ExitRequest";
  }

  public String printData() {
    throw new RuntimeException("ExitRequest object has no data!");
  }
}
