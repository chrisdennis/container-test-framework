/*
 * Copyright Terracotta, Inc.
 * Copyright Super iPaaS Integration LLC, an IBM Company 2024
 */
package com.tc.test.server.appserver.weblogic;

import java.lang.reflect.Method;

import javax.security.auth.Subject;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This bit of trickery uses methods internal to weblogic to initiate shutdown
 */
public class WeblogicShutdownServlet extends HttpServlet implements Runnable {

  private static final long DELAY = 1000;

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
    Thread t = new Thread(this, "shutdown thread");
    t.start();
  }

  public void run() {
    try {
      System.err.println("\n*********************\n" + "Shutdown in " + DELAY + " ms\n*********************\n");
      Thread.sleep(DELAY);

      System.setProperty("weblogic.security.authenticatePushSubject", "true");
      Object env = newObject("weblogic.jndi.Environment");
      invoke(env, "setSecurityPrincipal", new Class[] { String.class }, "weblogic");
      invoke(env, "setSecurityCredentials", new Class[] { Object.class }, "weblogic");

      Subject subject = new Subject();
      invokeStatic("weblogic.security.auth.Authenticate", "authenticate", env, subject);

      Object serverRuntime = invokeStatic("weblogic.t3.srvr.ServerRuntime", "theOne");
      invoke(serverRuntime, "forceShutdown", new Class[] {});
    } catch (Throwable t) {
      t.printStackTrace();
    }
  }

  private static Method findMethod(String type, String method, Object... args) {
    try {
      Class clazz = Class.forName(type);
      for (Method m : clazz.getMethods()) {
        if (!m.getName().equals(method) || !(m.getParameterTypes().length == args.length)) {
          continue;
        }

        boolean found = false;
        Class[] paramTypes = m.getParameterTypes();
        for (int i = 0; i < args.length; i++) {
          found = paramTypes[i].isAssignableFrom(args[i].getClass());
        }

        if (found) {
          return m;
        }
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return null;
  }

  private static Object invokeStatic(String type, String method, Object... args) {
    Class[] argTypes = new Class[args.length];
    for (int i = 0; i < args.length; i++) {
      argTypes[i] = args[i].getClass();
    }

    try {
      return Class.forName(type).getMethod(method, argTypes).invoke(null, args);
    } catch (NoSuchMethodException nsme) {
      Method m = findMethod(type, method, args);
      if (m == null) {
        throw new RuntimeException(nsme);
      }
      try {
        return m.invoke(null, args);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private static Object invoke(Object target, String method, Class[] argTypes, Object... args) {
    try {
      return target.getClass().getMethod(method, argTypes).invoke(target, args);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private static Object newObject(String type) {
    try {
      return Class.forName(type).newInstance();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

}
