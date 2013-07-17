/*
 * All content copyright (c) 2003-2008 Terracotta, Inc., except as may otherwise be noted in a separate copyright notice.  All rights reserved.
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
