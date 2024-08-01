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

public interface Request {

  public void setEnterQueueTime();

  public void setExitQueueTime();

  public void setProcessCompletionTime();

  public URL getUrl();

  public long getEnterQueueTime();

  public long getExitQueueTime();

  public long getProcessCompletionTime();

  public HttpClient getClient();

  public int getAppserverID();

  public String printData();
}
