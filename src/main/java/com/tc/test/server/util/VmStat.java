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
package com.tc.test.server.util;

import com.tc.util.runtime.Os;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class VmStat {

  private static Process      process;
  private static File         scriptFile;
  private static final String scriptName     = "capture-vmstat.sh";
  private static final String scriptContents = "#!/bin/sh\nexec vmstat 1 > vmstat.txt\n";

  public static synchronized void start(File workingDir) throws IOException {
    if (process != null) {
      stop();
      throw new IllegalStateException("VmStat is already running. Stopping VmStat...");
    }
    // win* and mac not supported
    if (Os.isWindows() || Os.isMac()) return;

    String[] commandLine = new String[2];
    commandLine[0] = "/bin/bash";
    commandLine[1] = createScriptFile(workingDir).toString();

    Runtime runtime = Runtime.getRuntime();
    process = runtime.exec(commandLine, null, workingDir);

    String msg = "\n";
    msg += "*****************************\n";
    msg += "* Running vmstat in [" + workingDir + "]\n";
    msg += "*****************************\n";
    System.out.println(msg);
  }

  public static synchronized void stop() {
    if (process != null) {
      process.destroy();
      process = null;
      deleteScriptFile();
    }
  }

  private static synchronized File createScriptFile(File baseDir) throws IOException {
    if (scriptFile != null) return scriptFile;
    File script = new File(baseDir + File.separator + scriptName);
    script.createNewFile();
    FileOutputStream out = new FileOutputStream(script);
    out.write(scriptContents.getBytes());
    out.flush();
    out.close();
    return scriptFile = script;
  }

  private static synchronized void deleteScriptFile() {
    if (scriptFile != null) {
      scriptFile.delete();
      scriptFile = null;
    }
  }
}
