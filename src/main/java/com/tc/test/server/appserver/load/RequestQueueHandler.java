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

import com.tc.test.server.util.HttpUtil;

import java.util.concurrent.BlockingQueue;

public class RequestQueueHandler extends Thread {

  private final BlockingQueue<Request> queue;

  public RequestQueueHandler(BlockingQueue<Request> queue) {
    this.queue = queue;
  }

  public void run() {
    while (true) {
      try {
        Request request = this.queue.take();
        if (request instanceof ExitRequest) {
          return;
        } else {
          request.setExitQueueTime();
          HttpUtil.getInt(request.getUrl(), request.getClient());
          request.setProcessCompletionTime();
        }
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }
}
