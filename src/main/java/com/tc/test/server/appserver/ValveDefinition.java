/*
 * Copyright Terracotta, Inc.
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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class ValveDefinition {

  private final String              className;
  private final Map<String, String> attributes   = new LinkedHashMap<String, String>();
  private boolean                   isExpressVal = false;

  public ValveDefinition(String className) {
    this.className = className;
    setAttribute("className", className);
  }

  public String getClassName() {
    return className;
  }

  public boolean isExpressVal() {
    return isExpressVal;
  }

  public void setExpressVal(boolean flag) {
    isExpressVal = flag;
  }

  public void setAttribute(String name, String value) {
    attributes.put(name, value);
  }

  public Map<String, String> getAttributes() {
    return attributes;
  }

  @Override
  public String toString() {
    return toXml();
  }

  public String toXml() {
    String xml = "<Valve";
    for (Entry<String, String> attr : attributes.entrySet()) {
      xml += " " + attr.getKey() + "=\"" + attr.getValue() + "\"";
    }

    xml += "/>";

    return xml;
  }
}
