/*
 * Copyright Terracotta, Inc.
 * Copyright Super iPaaS Integration LLC, an IBM Company 2024
 */
package com.tc.test.server.appserver.jetty94x;

import org.apache.commons.io.IOUtils;

import com.tc.process.Exec;
import com.tc.process.Exec.Result;
import com.tc.test.server.ServerParameters;
import com.tc.test.server.ServerResult;
import com.tc.test.server.appserver.AbstractAppServer;
import com.tc.test.server.appserver.AppServerInstallation;
import com.tc.test.server.appserver.AppServerParameters;
import com.tc.test.server.appserver.AppServerResult;
import com.tc.test.server.util.AppServerUtil;
import com.tc.util.PortChooser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Jetty94xAppServer extends AbstractAppServer {
  private static final String  JAVA_CMD           = System.getProperty("java.home") + File.separator + "bin"
      + File.separator + "java";
  private static final String  STOP_KEY           = "secret";
  private static final long    START_STOP_TIMEOUT = 240 * 1000;

  private String               instanceName;
  private File                 instanceDir;
  private File                 workDir;

  private int                  jetty_port         = 0;
  private int                  stop_port          = 0;
  private Thread               runner             = null;

  public Jetty94xAppServer(final AppServerInstallation installation) {
    super(installation);
  }

  @Override
  public ServerResult start(final ServerParameters parameters) throws Exception {
    AppServerParameters params = (AppServerParameters) parameters;
    return startJetty(params);
  }

  @Override
  public void stop(final ServerParameters rawParams) throws Exception {
    final String[] cmd = new String[] { JAVA_CMD, "-jar",
        this.serverInstallDirectory() + File.separator + "start.jar",
        "STOP.PORT=" + stop_port,
        "STOP.KEY=" + STOP_KEY,
        "--stop" };

    System.err.println("Stopping instance " + instanceName + "...");
    Result result = Exec.execute(cmd, null, null, this.serverInstallDirectory());
    if (result.getExitCode() != 0) {
      System.err.println(result);
    }

    if (runner != null) {
      runner.join(START_STOP_TIMEOUT);
      if (runner.isAlive()) {
        System.err.println("Instance " + instanceName + " on port " + jetty_port + " still alive.");
      } else {
        System.err.println("jetty instance " + instanceName + " stopped");
      }
    }

  }

  private AppServerResult startJetty(final AppServerParameters params) throws Exception {
    prepareDeployment(params);

    String[] jvmargs = params.jvmArgs().replaceAll("'", "").split("\\s+");
    List cmd = new ArrayList();
    cmd.add(0, JAVA_CMD);
    cmd.addAll(new ArrayList(Arrays.asList(jvmargs)));
    cmd.add("-Djetty.home=" + this.serverInstallDirectory());
    cmd.add("-Djetty.base=" + instanceDir);
    cmd.add("-Djava.io.tmpdir=" + workDir.getAbsolutePath());
    cmd.add("-jar");
    cmd.add(this.serverInstallDirectory() + File.separator + "start.jar");
    cmd.add("--module=http");
    cmd.add("jetty.http.port=" + jetty_port);
    cmd.add("--module=deploy");
    cmd.add("--module=jsp");
    cmd.add("--module=client");
    cmd.add("--module=sessions");
    cmd.add("--module=stats");
    cmd.add("STOP.PORT=" + stop_port);
    cmd.add("STOP.KEY=" + STOP_KEY);
    cmd.add("--debug");
    cmd.add("--start-log-file=startup.log");

    final String[] cmdArray = (String[]) cmd.toArray(new String[] {});
    final String nodeLogFile = new File(instanceDir + ".log").getAbsolutePath();

    runner = new Thread("runner for " + instanceName) {
      @Override
      public void run() {
        try {
          Result result = Exec.execute(cmdArray, nodeLogFile, null, instanceDir);
          if (result.getExitCode() != 0) {
            System.err.println(result);
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    };
    runner.start();
    System.err.println("Starting jetty " + instanceName + " on port " + jetty_port + "...");
    System.err.println("Cmd: " + Arrays.asList(cmdArray));
    AppServerUtil.waitForPort(jetty_port, START_STOP_TIMEOUT);
    System.err.println("Started " + instanceName + " on port " + jetty_port);
    return new AppServerResult(jetty_port, this);
  }

  private void prepareDeployment(final AppServerParameters params) throws Exception {
    instanceName = params.instanceName();
    instanceDir = new File(sandboxDirectory(), instanceName);
    ensureDirectory(instanceDir);
    ensureDirectory(getContextsDirectory());

    File wars_dir = getWarsDirectory();
    ensureDirectory(wars_dir);

    // move wars into the correct location
    Map wars = params.deployables();
    if (wars != null && wars.size() > 0) {

      Set war_entries = wars.entrySet();
      Iterator war_entries_it = war_entries.iterator();
      while (war_entries_it.hasNext()) {
        Map.Entry war_entry = (Map.Entry) war_entries_it.next();
        File war_file = (File) war_entry.getValue();
        String context = (String) war_entry.getKey();
        writeContextFile(war_file, context);
      }
    }

    // setup deployment config
    PortChooser portChooser = new PortChooser();
    jetty_port = portChooser.chooseRandomPort();
    stop_port = portChooser.chooseRandomPort();

    workDir = new File(sandboxDirectory(), "work");
    workDir.mkdirs();

    File logsDir = new File(instanceDir, "logs");
    ensureDirectory(logsDir);

    setProperties(params, jetty_port, instanceDir);
  }

  private void writeContextFile(final File war, final String context) throws IOException {
    String warShortName = war.getName().toLowerCase().replace(".war", "");

    FileOutputStream fos = null;
    try {
      fos = new FileOutputStream(new File(getContextsDirectory(), warShortName + ".xml"));
      fos.write(contextFile(war.getAbsolutePath(), context).getBytes());
    } finally {
      IOUtils.closeQuietly(fos);
    }
  }

  private static void ensureDirectory(final File dir) throws Exception {
    if (!dir.exists() && dir.mkdirs() == false) { throw new Exception("Can't create directory ("
        + dir.getAbsolutePath()); }
  }

  private File getWarsDirectory() {
    return new File(instanceDir, "webapps");
  }

  private File getContextsDirectory() {
    return getWarsDirectory();
  }

  private static String contextFile(final String warFile, final String contextPath) {
    String s = "<?xml version=\"1.0\"  encoding=\"ISO-8859-1\"?>\n";
    s += "<Configure class=\"org.eclipse.jetty.webapp.WebAppContext\">\n";
    s += "  <Set name=\"contextPath\">/" + contextPath + "</Set>\n";
    s += "  <Set name=\"war\">" + warFile + "</Set>\n";
    s += "\n";
    s += "</Configure>\n";
    return s;
  }
}
