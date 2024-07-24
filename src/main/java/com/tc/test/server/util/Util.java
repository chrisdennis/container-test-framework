/*
 * Copyright Terracotta, Inc.
 * Copyright Super iPaaS Integration LLC, an IBM Company 2024
 */
package com.tc.test.server.util;

import com.tc.util.runtime.Os;

import java.net.URL;
import java.net.URLDecoder;
import java.security.CodeSource;
import java.security.ProtectionDomain;

public class Util {
  public static String jarFor(Class c) {
    ProtectionDomain protectionDomain = c.getProtectionDomain();
    CodeSource codeSource = protectionDomain.getCodeSource();
    URL url = codeSource.getLocation();
    String path = url.getPath();
    if (Os.isWindows() && path.startsWith("/")) {
      path = path.substring(1);
    }
    return URLDecoder.decode(path);
  }

}