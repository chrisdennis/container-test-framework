/*
 * Copyright Terracotta, Inc.
 * Copyright Super iPaaS Integration LLC, an IBM Company 2024
 */
package com.tc.test.server.appserver.tomcat;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.codehaus.cargo.container.InstalledLocalContainer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.tc.test.AppServerInfo;
import com.tc.test.TestConfigObject;
import com.tc.test.server.appserver.AppServerParameters;
import com.tc.test.server.appserver.deployment.Deployment;
import com.tc.test.server.util.Assert;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class TomcatStartupActions {
  public static final String USE_NIO_PROTOCOL_KEY = "useNioProtocol";

  private TomcatStartupActions() {
    //
  }

  public static void modifyConfig(AppServerParameters params, InstalledLocalContainer container, int catalinaPropsLine) {
    try {
      modifyConfig0(params, container, catalinaPropsLine);
      if (Boolean.valueOf(params.properties().getProperty(USE_NIO_PROTOCOL_KEY))) {
        switchToNioProtocol(params, container);
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private static void modifyConfig0(AppServerParameters params, InstalledLocalContainer container, int catalinaPropsLine)
      throws Exception {
    try {
      File serverXml = new File(container.getConfiguration().getHome(), "conf/server.xml");
      Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(serverXml);

      Map<String, Deployment> deployments = params.deployments();
      NodeList contexts = doc.getElementsByTagName("Context");
      File webApps = new File(container.getConfiguration().getHome(), "webapps");

      for (int i = 0, n = contexts.getLength(); i < n; i++) {
        Node context = contexts.item(i);
        String contextPath = context.getAttributes().getNamedItem("path").getNodeValue();
        // remove leading /
        String appContext = contextPath.substring(1);
        Deployment deployment = deployments.get(appContext);
        Assert.assertNotNull(deployment);

        AppServerInfo appServerInfo = TestConfigObject.getInstance().appServerInfo();
        if (Integer.valueOf(appServerInfo.getMajor()) == 7) {
          // tomcat 7 won't unpack the war if docBase is not relative to webapps
          File docBase = new File(context.getAttributes().getNamedItem("docBase").getNodeValue());
          FileUtils.copyFileToDirectory(docBase, webApps);
          ((Element) context).setAttribute("docBase", docBase.getName());
        }
      }

      // set up jvmRoute
      String jvmRoute = params.properties().getProperty("jvmRoute");
      if (jvmRoute != null) {
        NodeList engines = doc.getElementsByTagName("Engine");
        for (int i = 0; i < engines.getLength(); i++) {
          Node engine = engines.item(i);
          if ("Catalina".equals(engine.getAttributes().getNamedItem("name").getNodeValue())) {
            ((Element) engine).setAttribute("jvmRoute", jvmRoute);
            break;
          }
        }
      }

      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      Transformer transformer = transformerFactory.newTransformer();

      FileOutputStream outputStream = null;
      try {
        outputStream = new FileOutputStream(serverXml);
        // Cannot rely on StreamResult(File) constructor as Jenkins can have strange char in file path
        transformer.transform(new DOMSource(doc), new StreamResult(outputStream));
      } finally {
        IOUtils.closeQuietly(outputStream);
      }

    } catch (IOException ioe) {
      throw new RuntimeException(ioe);
    }
  }

  public static void configureManagerApp(AppServerParameters params, InstalledLocalContainer container) {
    AppServerInfo appServerInfo = TestConfigObject.getInstance().appServerInfo();
    String managerPath = "server/webapps/manager";
    if (Integer.valueOf(appServerInfo.getMajor()) >= 6) {
      managerPath = "webapps/manager";
    }
    File managerApp = new File(container.getHome(), managerPath);

    String managerXml = "<Context path='/manager' debug='0' privileged='true' docBase='" + managerApp.getAbsolutePath()
                        + "'></Context>";
    File managerContextFile = new File(container.getConfiguration().getHome(), "/conf/Catalina/localhost/manager.xml");
    managerContextFile.getParentFile().mkdirs();
    FileOutputStream out = null;

    try {
      out = new FileOutputStream(managerContextFile);
      IOUtils.write(managerXml, out);
    } catch (Exception e) {
      throw new RuntimeException(e);
    } finally {
      IOUtils.closeQuietly(out);
    }
  }

  private static void switchToNioProtocol(AppServerParameters params, InstalledLocalContainer container)
      throws Exception {
    File serverXml = new File(container.getConfiguration().getHome(), "conf/server.xml");
    Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(serverXml);
    Element connector = (Element) doc.getElementsByTagName("Connector").item(0);
    connector.setAttribute("protocol", "org.apache.coyote.http11.Http11NioProtocol");
    TransformerFactory transformerFactory = TransformerFactory.newInstance();
    Transformer transformer = transformerFactory.newTransformer();
    transformer.transform(new DOMSource(doc), new StreamResult(serverXml));
  }
}
