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
package com.tc.test.server.appserver.deployment;

import com.tc.test.TempDirectoryHelper;

import java.io.File;
import java.io.IOException;

public class TempDirectoryUtil {

  private static TempDirectoryHelper tempDirectoryHelper;

  public static File getTempDirectory(Class type) throws IOException {
    return getTempDirectoryHelper(type).getDirectory();
  }

  protected static synchronized TempDirectoryHelper getTempDirectoryHelper(Class type) {
    if (tempDirectoryHelper == null) {
      tempDirectoryHelper = new TempDirectoryHelper(type, true);
    }

    return tempDirectoryHelper;
  }

}
