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

import org.apache.commons.io.IOUtils;
import org.apache.tools.ant.taskdefs.War;
import org.apache.tools.ant.taskdefs.Zip.Duplicate;
import org.apache.tools.ant.types.ZipFileSet;
import org.codehaus.cargo.util.AntUtils;

import com.tc.logging.TCLogger;
import com.tc.logging.TCLogging;
import com.tc.test.TestConfigObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import junit.framework.Assert;

/*
 * <!ELEMENT web-app (icon?, display-name?, description?, distributable?, context-param, filter, filter-mapping,
 * listener, servlet, servlet-mapping, session-config?, mime-mapping, welcome-file-list?, error-page, taglib,
 * resource-env-ref, resource-ref, security-constraint, login-config?, security-role, env-entry, ejb-ref,
 * ejb-local-ref)>
 */

public class WARBuilder implements DeploymentBuilder {
  private static final TCLogger        logger                = TCLogging.getLogger(WARBuilder.class);
  private static final String          WEBAPP_VERSION_STRING = "xmlns=\"https://jakarta.ee/xml/ns/jakartaee\" " +
      "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
      "xsi:schemaLocation=\"https://jakarta.ee/xml/ns/jakartaee https://jakarta.ee/xml/ns/jakartaee/web-app_6_0.xsd\" " +
      "metadata-complete=\"false\" " +
      "version=\"%s\"";

  private FileSystemPath               warDirectoryPath;
  private final String                 warFileName;
  private final Set                    classDirectories      = new HashSet();
  private final Set                    libs                  = new HashSet();
  private final List                   resources             = new ArrayList();
  private final Map                    contextParams         = new HashMap();
  private final Map                    sessionConfig         = new HashMap();
  private final List                   listeners             = new ArrayList();
  private final List                   servlets              = new ArrayList();
  private final List<FilterDefinition> filters               = new ArrayList();
  private final Map                    taglibs               = new HashMap();
  private final FileSystemPath         tempDirPath;
  private final Map                    errorPages            = new HashMap();

  private String                       dispatcherServletName = null;

  private final TestConfigObject       testConfig;
  private final FileSystemPath         tmpResourcePath;
  private final boolean                clustered;
  private boolean                      neededWebXml          = true;
  private final List<String>           webXmlFragments       = new ArrayList<String>();
  private String                       webAppVersion         = "6.0";

  public WARBuilder(File tempDir, TestConfigObject config) throws IOException {
    this(File.createTempFile("test", ".war", tempDir).getAbsolutePath(), tempDir, config, true);
  }

  public WARBuilder(String warFileName, File tempDir) {
    this(warFileName, tempDir, TestConfigObject.getInstance(), true);
  }

  public WARBuilder(String warFileName, File tempDir, TestConfigObject config, boolean clustered) {
    this.warFileName = warFileName;
    this.tempDirPath = new FileSystemPath(tempDir);
    this.testConfig = config;

    this.tmpResourcePath = tempDirPath.mkdir("tempres");
    this.clustered = clustered;

    // this is needed for spring tests
    addDirectoryOrJARContainingClass(WARBuilder.class); // test framework
  }

  public DeploymentBuilder addClassesDirectory(FileSystemPath path) {
    classDirectories.add(path);
    return this;
  }

  @Override
  public void setWebAppVersion(String version) {
    webAppVersion = version;
  }

  @Override
  public Deployment makeDeployment() throws Exception {
    createWARDirectory();

    FileSystemPath warFile = makeWARFileName();
    logger.debug("Creating war file: " + warFile);

    War warTask = makeWarTask();
    warTask.setUpdate(false);
    // XXX: build-data.txt exists in all of classes folders
    // therefore there will be duplicates. Websphere doesn't like that
    // This option should be removed when we solve that problem
    Duplicate df = new Duplicate();
    df.setValue("preserve");
    warTask.setDuplicate(df);
    warTask.setDestFile(warFile.getFile());
    // end XXX
    if (neededWebXml) {
      warTask.setWebxml(warDirectoryPath.existingFile("WEB-INF/web.xml").getFile());
    } else {
      warTask.setNeedxmlfile(false);
    }
    addWEBINFDirectory(warTask);
    addClassesDirectories(warTask);
    addLibs(warTask);
    addResources(warTask);
    warTask.execute();

    return new WARDeployment(warFile, clustered);
  }

  @Override
  public boolean isClustered() {
    return clustered;
  }

  private FileSystemPath makeWARFileName() {
    File f = new File(warFileName);
    if (f.isAbsolute()) {
      return FileSystemPath.makeNewFile(warFileName);
    } else {
      return tempDirPath.file(warFileName);
    }
  }

  private void addLibs(War warTask) {
    for (Iterator it = libs.iterator(); it.hasNext();) {
      FileSystemPath lib = (FileSystemPath) it.next();
      ZipFileSet zipFileSet = new ZipFileSet();
      zipFileSet.setFile(lib.getFile());
      warTask.addLib(zipFileSet);
    }
  }

  private War makeWarTask() {
    return (War) new AntUtils().createAntTask("war");
  }

  private void addClassesDirectories(War warTask) {
    for (Iterator it = classDirectories.iterator(); it.hasNext();) {
      FileSystemPath path = (FileSystemPath) it.next();
      ZipFileSet zipFileSet = new ZipFileSet();
      zipFileSet.setDir(path.getFile());
      warTask.addClasses(zipFileSet);
    }
  }

  private void addResources(War warTask) {
    for (Iterator it = resources.iterator(); it.hasNext();) {
      ResourceDefinition definition = (ResourceDefinition) it.next();
      ZipFileSet zipfileset = new ZipFileSet();
      zipfileset.setDir(definition.location);
      zipfileset.setIncludes(definition.includes);
      if (definition.prefix != null) zipfileset.setPrefix(definition.prefix);
      if (definition.fullpath != null) zipfileset.setFullpath(definition.fullpath);
      warTask.addZipfileset(zipfileset);
    }
  }

  private void addWEBINFDirectory(War warTask) {
    ZipFileSet zipFileSet = new ZipFileSet();
    zipFileSet.setDir(warDirectoryPath.getFile());
    warTask.addFileset(zipFileSet);
  }

  public DeploymentBuilder addClassesDirectory(String directory) {
    return addClassesDirectory(FileSystemPath.existingDir(directory));
  }

  void createWARDirectory() throws IOException {
    this.warDirectoryPath = tempDirPath.mkdir("tempwar");

    FileSystemPath webInfDir = warDirectoryPath.mkdir("WEB-INF");
    if (neededWebXml) {
      createWebXML(webInfDir);
    }
  }

  private static String getWebAppVersionString(String version) {
    return String.format(WEBAPP_VERSION_STRING, version, version.replace('.', '_'));
  }

  private void createWebXML(FileSystemPath webInfDir) throws IOException {
    FileSystemPath webXML = webInfDir.file("web.xml");
    FileOutputStream fos = new FileOutputStream(webXML.getFile());
    try {
      logger.debug("Creating " + webXML.getFile().getAbsolutePath());
      PrintWriter pw = new PrintWriter(fos);

      pw.println("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");

      pw.println("<web-app " + getWebAppVersionString(webAppVersion) + ">\n");

      for (Iterator it = contextParams.entrySet().iterator(); it.hasNext();) {
        Map.Entry param = (Map.Entry) it.next();
        writeContextParam(pw, (String) param.getKey(), (String) param.getValue());
      }

      for (Object element : filters) {
        FilterDefinition definition = (FilterDefinition) element;
        writeFilter(pw, definition);
      }

      for (Object element : filters) {
        FilterDefinition definition = (FilterDefinition) element;
        logger.debug("Writing filter mapping[" + definition.name + " -> " + definition.mapping + "]");
        pw.println("  <filter-mapping>");
        pw.println("    <filter-name>" + definition.name + "</filter-name>");
        pw.println("    <url-pattern>" + definition.mapping + "</url-pattern>");

        if (!definition.dispatchers.isEmpty()) {
          for (Dispatcher dispatcher : definition.dispatchers) {
            pw.println("    <dispatcher>" + dispatcher + "</dispatcher>");
          }
        }

        pw.println("  </filter-mapping>");
      }

      for (Iterator it = listeners.iterator(); it.hasNext();) {
        writeListener(pw, ((Class) it.next()).getName());
      }

      for (Iterator it = servlets.iterator(); it.hasNext();) {
        ServletDefinition definition = (ServletDefinition) it.next();
        writeServlet(pw, definition);
      }

      for (Iterator it = servlets.iterator(); it.hasNext();) {
        ServletDefinition definition = (ServletDefinition) it.next();
        logger.debug("Writing servlet mapping[" + definition.name + " -> " + definition.mapping + "]");
        pw.println("  <servlet-mapping>");
        pw.println("    <servlet-name>" + definition.name + "</servlet-name>");
        pw.println("    <url-pattern>" + definition.mapping + "</url-pattern>");
        pw.println("  </servlet-mapping>");
      }

      for (Iterator it = sessionConfig.entrySet().iterator(); it.hasNext();) {
        Map.Entry entry = (Map.Entry) it.next();
        writeSessionConfig(pw, (String) entry.getKey(), (String) entry.getValue());
      }

      for (Iterator i = errorPages.entrySet().iterator(); i.hasNext();) {
        Entry e = (Entry) i.next();
        Integer status = (Integer) e.getKey();
        String location = (String) e.getValue();

        pw.println("  <error-page>");
        pw.println("    <error-code>" + status + "</error-code>");
        pw.println("    <location>" + location + "</location>");
        pw.println("  </error-page>");
      }

      if (!taglibs.isEmpty()) {
        pw.println("  <jsp-config>");
        for (Iterator it = taglibs.entrySet().iterator(); it.hasNext();) {
          Map.Entry taglib = (Map.Entry) it.next();
          logger.debug("Writing taglib[" + taglib.getKey() + "/" + taglib.getValue() + "]");
          pw.println("    <taglib>");
          pw.println("      <taglib-uri>" + taglib.getKey() + "</taglib-uri>");
          pw.println("      <taglib-location>" + taglib.getValue() + "</taglib-location>");
          pw.println("    </taglib>");
        }
        pw.println("  </jsp-config>");
      }

      for (String fragment : webXmlFragments) {
        pw.println(fragment);
      }

      pw.println("</web-app>");
      pw.flush();
      logger.debug("Finished creating " + webXML.getFile().getAbsolutePath());
    } finally {
      fos.close();
    }
  }

  private void writeContextParam(PrintWriter pw, String name, String value) {
    logger.debug("Writing context param[" + name + "/" + value + "]");
    pw.println("  <context-param>");
    pw.println("    <param-name>" + name + "</param-name>");
    pw.println("    <param-value>" + value + "</param-value>");
    pw.println("  </context-param>");
  }

  private void writeSessionConfig(PrintWriter pw, String name, String value) {
    logger.debug("Writing session config[" + name + "/" + value + "]");
    pw.println("  <session-config>");
    pw.println("    <" + name + ">" + value + "</" + name + ">");
    pw.println("  </session-config>");
  }

  private void writeListener(PrintWriter pw, String className) {
    logger.debug("Writing listener[" + className + "]");
    pw.println("  <listener>");
    pw.println("    <listener-class>" + className + "</listener-class>");
    pw.println("  </listener>");
  }

  private void writeServlet(PrintWriter pw, ServletDefinition definition) {
    logger.debug("Writing servlet[" + definition.name + " of type " + definition.servletClass.getName() + "]");
    pw.println("  <servlet>");
    pw.println("    <servlet-name>" + definition.name + "</servlet-name>");
    pw.println("    <servlet-class>" + definition.servletClass.getName() + "</servlet-class>");

    if (definition.initParameters != null) {
      for (Iterator it = definition.initParameters.entrySet().iterator(); it.hasNext();) {
        Map.Entry param = (Map.Entry) it.next();
        logger.debug("Writing servlet init parameter[" + param.getKey() + "/" + param.getValue() + "]");
        pw.println("    <init-param>");
        pw.println("      <param-name>" + param.getKey() + "</param-name>");
        pw.println("      <param-value>" + param.getValue() + "</param-value>");
        pw.println("    </init-param>");
      }
    }

    if (definition.loadOnStartup) {
      pw.println("    <load-on-startup>1</load-on-startup>");
    }

    pw.println("  </servlet>");
  }

  private void writeFilter(PrintWriter pw, FilterDefinition definition) {
    logger.debug("Writing filter[" + definition.name + " of type " + definition.filterClass.getName() + "]");
    pw.println("  <filter>");
    pw.println("    <filter-name>" + definition.name + "</filter-name>");
    pw.println("    <filter-class>" + definition.filterClass.getName() + "</filter-class>");

    if (definition.initParameters != null) {
      for (Iterator it = definition.initParameters.entrySet().iterator(); it.hasNext();) {
        Map.Entry param = (Map.Entry) it.next();
        logger.debug("Writing filter init param[" + param.getKey() + "/" + param.getValue() + "]");
        pw.println("    <init-param>");
        pw.println("      <param-name>" + param.getKey() + "</param-name>");
        pw.println("      <param-value>" + param.getValue() + "</param-value>");
        pw.println("    </init-param>");
      }
    }

    pw.println("  </filter>");
  }

  @Override
  public DeploymentBuilder addDirectoryOrJARContainingClass(Class type) {
    return addDirectoryOrJar(calculatePathToClass(type));
  }

  @Override
  public DeploymentBuilder addDirectoryOrJARContainingClassOfSelectedVersion(Class type, String[] variantNames) {
    String pathSeparator = System.getProperty("path.separator");

    for (String variantName : variantNames) {
      String selectedVariant = testConfig.selectedVariantFor(variantName);
      String path = testConfig.variantLibraryClasspathFor(variantName, selectedVariant);
      String[] paths = path.split(pathSeparator);
      for (String path2 : paths) {
        File filePath = new File(path2);
        if (!filePath.exists()) { throw new RuntimeException("Variant path doesn't exist: " + filePath); }
        addDirectoryOrJar(new FileSystemPath(filePath));
      }
    }

    return this;
  }

  @Override
  public DeploymentBuilder addDirectoryContainingResource(String resource) {
    return addDirectoryOrJar(calculatePathToResource(resource));
  }

  @Override
  public DeploymentBuilder addResource(String location, String includes, String prefix) {
    FileSystemPath path = getResourceDirPath(location, includes);
    File srcDir = extractResourceIfNeeded(path, location, includes);
    resources.add(new ResourceDefinition(srcDir, includes, prefix, null));
    return this;
  }

  @Override
  public DeploymentBuilder addFileAsResource(File file, String prefix) {
    File srcDir = file.getParentFile();
    resources.add(new ResourceDefinition(srcDir, file.getName(), prefix, null));
    return this;
  }

  private File extractResourceIfNeeded(FileSystemPath path, String location, String includes) {
    final File rv;

    if (!path.isDirectory() && path.getFile().getName().endsWith(".jar")) {
      JarFile jarFile = null;
      try {
        jarFile = new JarFile(path.getFile());
        String dir = location.startsWith("/") ? location.substring(1) : location;
        dir = dir != null ? (dir.trim().equals("") ? "" : dir + "/") : "";
        ZipEntry entry = jarFile.getEntry(dir + includes);

        File tmpParent = new File(tmpResourcePath.getFile(), dir);
        tmpParent.mkdirs();

        InputStream in = null;
        FileOutputStream fos = null;
        try {
          in = jarFile.getInputStream(entry);
          fos = new FileOutputStream(new File(tmpParent, includes));
          IOUtils.copy(in, fos);
        } finally {
          IOUtils.closeQuietly(in);
          IOUtils.closeQuietly(fos);
        }
        rv = tmpParent;
      } catch (Exception e) {
        if (e instanceof RuntimeException) { throw (RuntimeException) e; }
        throw new RuntimeException(e);
      } finally {
        if (jarFile != null) {
          try {
            jarFile.close();
          } catch (IOException ioe) {
            // ignore
          }
        }
      }
    } else {
      rv = path.getFile();
    }

    return rv;
  }

  @Override
  public DeploymentBuilder addResourceFullpath(String location, String includes, String fullpath) {
    FileSystemPath path = getResourceDirPath(location, includes);
    File srcDir = extractResourceIfNeeded(path, location, includes);
    resources.add(new ResourceDefinition(srcDir, includes, null, fullpath));
    return this;
  }

  public static FileSystemPath getResourceDirPath(String location, String includes) {
    String resource = location + "/" + includes;
    URL url = WARBuilder.class.getResource(resource);
    Assert.assertNotNull("Not found: " + resource, url);
    FileSystemPath path = calculateDirectory(url, includes);
    return path;
  }

  @Override
  public DeploymentBuilder addContextParameter(String name, String value) {
    contextParams.put(name, value);
    return this;
  }

  @Override
  public DeploymentBuilder addSessionConfig(String name, String value) {
    sessionConfig.put(name, value);
    return this;
  }

  @Override
  public DeploymentBuilder addListener(Class listenerClass) {
    listeners.add(listenerClass);
    return this;
  }

  @Override
  public DeploymentBuilder setDispatcherServlet(String name, String mapping, Class servletClass, Map params,
                                                boolean loadOnStartup) {
    Assert.assertNull(this.dispatcherServletName);
    this.dispatcherServletName = name;
    addServlet(name, mapping, servletClass, params, loadOnStartup);
    return this;
  }

  @Override
  public DeploymentBuilder addServlet(String name, String mapping, Class servletClass, Map params, boolean loadOnStartup) {
    servlets.add(new ServletDefinition(name, mapping, servletClass, params, loadOnStartup));
    addDirectoryOrJARContainingClass(servletClass);
    return this;
  }

  @Override
  public DeploymentBuilder addFilter(String name, String mapping, Class filterClass, Map params) {
    return addFilter(name, mapping, filterClass, params, null);
  }

  @Override
  public DeploymentBuilder addFilter(String name, String mapping, Class filterClass, Map params,
                                     Set<Dispatcher> dispatchers) {
    filters.add(new FilterDefinition(name, mapping, filterClass, params, dispatchers));
    return this;
  }

  @Override
  public FilterDefinition getFilterDefinition(String name) {
    for (FilterDefinition fd : filters) {
      if (fd.name.equals(name)) { return fd; }
    }

    return null;
  }

  @Override
  public DeploymentBuilder addTaglib(String uri, String location) {
    taglibs.put(uri, location);
    return this;
  }

  @Override
  public DeploymentBuilder addErrorPage(int status, String location) {
    errorPages.put(new Integer(status), location);
    return this;
  }

  private DeploymentBuilder addDirectoryOrJar(FileSystemPath path) {
    if (path.isDirectory()) {
      classDirectories.add(path);
    } else {
      libs.add(path);
    }
    return this;
  }

  @Override
  public DeploymentBuilder addDirectoryOrJAR(String path) {
    return addDirectoryOrJar(new FileSystemPath(new File(path)));
  }

  @Override
  public DeploymentBuilder addDirectoriesOrJARs(List<String> files) {
    for (String file : files) {
      addDirectoryOrJAR(file);
    }
    return this;
  }

  public static FileSystemPath calculatePathToClass(Class type) {
    URL url = type.getResource("/" + classToPath(type));
    Assert.assertNotNull("Not found: " + type, url);
    FileSystemPath filepath = calculateDirectory(url, "/" + classToPath(type));
    return filepath;
  }

  @Override
  public DeploymentBuilder setNeededWebXml(boolean flag) {
    neededWebXml = flag;
    return this;
  }

  @Override
  public void addWebXmlFragment(String fragment) {
    webXmlFragments.add(fragment);
  }

  @SuppressWarnings("resource")
  static public FileSystemPath calculatePathToClass(Class type, String pathString) {
    String pathSeparator = System.getProperty("path.separator");
    StringTokenizer st = new StringTokenizer(pathString, pathSeparator);
    URL[] urls = new URL[st.countTokens()];
    for (int i = 0; st.hasMoreTokens(); i++) {
      String token = st.nextToken();
      if (token.startsWith("/")) {
        token = "/" + token;
      }
      URL u = null;
      try {
        if (token.endsWith(".jar")) {
          u = new URL("jar", "", "file:/" + token + "!/");
        } else {
          u = new URL("file", "", token + "/");
        }
        urls[i] = u;
      } catch (Exception ex) {
        throw new RuntimeException(ex);
      }
    }
    URL url = new URLClassLoader(urls, null).getResource(classToPath(type));
    Assert.assertNotNull("Not found: " + type, url);
    FileSystemPath filepath = calculateDirectory(url, "/" + classToPath(type));
    return filepath;
  }

  public static FileSystemPath calculateDirectory(URL url, String classNameAsPath) {

    String urlAsString = null;
    try {
      urlAsString = java.net.URLDecoder.decode(url.toString(), "UTF-8");
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
    Assert.assertTrue("URL should end with: " + classNameAsPath, urlAsString.endsWith(classNameAsPath));
    if (urlAsString.startsWith("file:")) {
      return FileSystemPath.existingDir(urlAsString.substring("file:".length(),
                                                              urlAsString.length() - classNameAsPath.length()));
    } else if (urlAsString.startsWith("jar:file:")) {
      int n = urlAsString.indexOf('!');
      return FileSystemPath.makeExistingFile(urlAsString.substring("jar:file:".length(), n));
    } else throw new RuntimeException("unsupported protocol: " + url);
  }

  private static String classToPath(Class type) {
    return type.getName().replace('.', '/') + ".class";
  }

  private FileSystemPath calculatePathToResource(String resource) {
    URL url = getClass().getResource(resource);
    Assert.assertNotNull("Not found: " + resource, url);
    return calculateDirectory(url, resource);
  }

  private static class ResourceDefinition {
    public final File   location;
    public final String prefix;
    public final String includes;
    public final String fullpath;

    public ResourceDefinition(File location, String includes, String prefix, String fullpath) {
      this.location = location;
      this.includes = includes;
      this.prefix = prefix;
      this.fullpath = fullpath;
    }
  }

  private static class ServletDefinition {
    public final String  name;
    public final String  mapping;
    public final Class   servletClass;
    public final Map     initParameters;
    public final boolean loadOnStartup;

    public ServletDefinition(String name, String mapping, Class servletClass, Map initParameters, boolean loadOnStartup) {
      this.name = name;
      this.mapping = mapping;
      this.servletClass = servletClass;
      this.initParameters = initParameters;
      this.loadOnStartup = loadOnStartup;
    }
  }

  public static class FilterDefinition {
    private final String          name;
    private final String          mapping;
    private final Class           filterClass;
    private final Map             initParameters;
    private final Set<Dispatcher> dispatchers;

    public FilterDefinition(String name, String mapping, Class filterClass, Map initParameters,
                            Set<Dispatcher> dispatchers) {
      this.name = name;
      this.mapping = mapping;
      this.filterClass = filterClass;
      this.initParameters = initParameters;
      this.dispatchers = dispatchers == null ? Collections.EMPTY_SET : dispatchers;
    }

    public void setInitParam(String name, String value) {
      initParameters.put(name, value);
    }
  }

  public enum Dispatcher {
    ERROR, INCLUDE, FORWARD, REQUEST;
  }
}
