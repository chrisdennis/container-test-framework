/*
 * Copyright Terracotta, Inc.
 * Copyright Super iPaaS Integration LLC, an IBM Company 2024
 */
package com.tc.test.server.appserver;

public class User {

  private final String name;
  private final String password;

  public User(String name, String password) {
    this.name = name;
    this.password = password;
  }

  public String getPassword() {
    return password;
  }

  public String getName() {
    return name;
  }

}
