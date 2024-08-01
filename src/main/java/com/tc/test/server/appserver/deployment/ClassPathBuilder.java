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


import java.io.File;
import java.io.FilenameFilter;

public class ClassPathBuilder {

  StringBuffer sb = new StringBuffer();
  
  public void addDir(String dir) {
    FileSystemPath dirPath = FileSystemPath.existingDir(dir);
    addFileOrDir(dirPath);
  }

  private void addFileOrDir(FileSystemPath dirPath) {
    if (sb.length() > 0) sb.append(";");
    sb.append(dirPath);
  }

  public String makeClassPath() {
    return sb.toString();
  }

  public void addJARsInDir(String dirContainingJARS) {
    FileSystemPath dirPath = FileSystemPath.existingDir(dirContainingJARS);
    String[] jars = dirPath.getFile().list(new FilenameFilter(){

      public boolean accept(File dir, String name) {
        return name.endsWith(".jar");
      }});
    for (int i = 0; i < jars.length; i++) {
      String jar = jars[i];
      addFileOrDir(dirPath.existingFile(jar));
    }
    
  }


}
