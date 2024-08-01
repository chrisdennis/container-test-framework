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

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class FileSystemPath {

  private final File path;

  public boolean equals(Object obj) {
    if (!(obj instanceof FileSystemPath)) return false;
    FileSystemPath other = (FileSystemPath) obj; 
    return path.equals(other.path);
  }
  
  public int hashCode() {
    return path.hashCode();
  }
  
  private FileSystemPath(String path) {
    this.path = new File(path);
  }

  public FileSystemPath(File dir) {
    this.path = dir;
  }

  public static FileSystemPath existingDir(String path) {
    FileSystemPath f = new FileSystemPath(path);
    if (!f.isDirectory()) { throw new RuntimeException("Non-existent directory: " + path); }
    return f;
  }

  boolean isDirectory() {
    return path.isDirectory();
  }

  public static FileSystemPath makeExistingFile(String path) {
    FileSystemPath f = new FileSystemPath(path);
    if (!f.isFile()) { 
      throw new RuntimeException("Non-existent file: " + path); 
    }
    return f;
  }

  private boolean isFile() {
    return path.isFile();
  }

  public String toString() {
    try {
      return path.getCanonicalPath();
    } catch (IOException e) {
      return path.getAbsolutePath();
    }
  }

  public FileSystemPath existingSubdir(String subdirectoryPath) {
    return existingDir(path + "/" + subdirectoryPath);
  }

  public FileSystemPath existingFile(String fileName) {
    return makeExistingFile(this.path + "/" + fileName);
  }
  
  public File getFile() {
    return path;
  }

  public FileSystemPath subdir(String subdirectoryPath) {
    return new FileSystemPath(path + "/" + subdirectoryPath);
  }

  public void delete() throws IOException {
    if(path.exists()) {
      FileUtils.forceDelete(path);
    }
  }

  public FileSystemPath file(String fileName) {
    return new FileSystemPath((this.path + "/" + fileName));
  }

  public FileSystemPath mkdir(String subdir) {
    return subdir(subdir).mkdir();
  }

  private FileSystemPath mkdir() {
    path.mkdirs();
    return this;
  }

  public static FileSystemPath makeNewFile(String fileName) {
    return new FileSystemPath(fileName);
  }

}
