/*
 * Concept profile generation tool suite
 * Copyright (C) 2015 Biosemantics Group, Erasmus University Medical Center,
 *  Rotterdam, The Netherlands
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package org.erasmusmc.webservice;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.apache.axis.client.Stub;

import javax.xml.rpc.ServiceException;

import org.erasmusmc.acs.RemoteACSNetwork;
import org.erasmusmc.applications.thesaurusenricher.InternetDatabase;
import org.erasmusmc.encoding.Encoding;
import org.erasmusmc.semanticnetwork.SemanticGroup;
import org.erasmusmc.semanticnetwork.SemanticType;
import org.erasmusmc.webservice.biosemantics.ACSNetworkData;
import org.erasmusmc.webservice.biosemantics.BioSemanticsPort;
import org.erasmusmc.webservice.biosemantics.BioSemanticsPortserviceLocator;
import org.erasmusmc.webservice.biosemantics.InternetDatabaseData;
import org.erasmusmc.webservice.biosemantics.SemanticGroupData;
import org.erasmusmc.webservice.biosemantics.SemanticTypeData;

/**
 * <p>Title: ACS Viewer</p>
 * <p>Description: A viewer to visualize Associative Concept Spaces</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Erasmus MC, Medical Informatics</p>
 * @author Peter-Jan Roes
 * @version 1.0
 */

public class ServerConnection {
  public BioSemanticsPort bioSemanticsPort = null;
  private String path, dll = "Biosemantics.dll", subPath = "soap/Biosemantics";
  private URL url;

  private String userName;
  private String password;

  public void setDLL(String dll) {
    this.dll = dll;
  }

  public void setSubPath(String subPath) {
    this.subPath = subPath;
  }

  public void initializeWebservice(String path, String userName, String password) throws MalformedURLException, ServiceException {
    url = new URL(path + "/" + dll + "/" + subPath);
    BioSemanticsPortserviceLocator bioSemanticsLocator = new BioSemanticsPortserviceLocator();
    bioSemanticsPort = bioSemanticsLocator.getBioSemanticsPortPort(url);

    this.path     = path;
    this.userName = userName;
    this.password = password;

    setAuthentication((Stub) bioSemanticsPort);
    
    //testConnection();
  }

  public void setAuthentication(Stub stub) {
    stub.setUsername(userName);
    stub.setPassword(password);
    stub.setTimeout(1000000);
  }

  public String getPath() {
    return path;
  }

  public List<RemoteACSNetwork> getRemoteACSNetworks() throws java.rmi.RemoteException {
    ACSNetworkData[] remoteACSNetworkDatas = bioSemanticsPort.getACSNetworks();
    List<RemoteACSNetwork> result = new ArrayList<RemoteACSNetwork>();

    for (ACSNetworkData remoteACSNetworkData: remoteACSNetworkDatas) {
      RemoteACSNetwork remoteACSNetwork = new RemoteACSNetwork();
      remoteACSNetwork.setData(remoteACSNetworkData);
      result.add(remoteACSNetwork);
    }

    return result;
  }
  
  private void testConnection() throws Exception {
    String serverResponse = "";
    
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setDoInput(true);
    connection.setDoOutput(true);
    connection.setUseCaches(false);
    connection.setRequestMethod("POST");
    
    if (!userName.equals(""))
      connection.setRequestProperty("Authorization",("Basic " + Encoding.base64Encode(userName + ":" + password)));
    
    connection.setRequestProperty("SOAPAction", "urn:BioSemanticsIntf-ThesaurusPort#TestConnection");
    
    OutputStream out = connection.getOutputStream();
    Writer wout = new OutputStreamWriter(out);
    
    wout.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n"); 
    wout.write("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n");
    wout.write("<soapenv:Body>\n");
    wout.write("<ns1:TestConnection soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:ns1=\"urn:BioSemanticsIntf-ThesaurusPort\" />\n"); 
    wout.write("</soapenv:Body>\n");
    wout.write("</soapenv:Envelope>\n");
          
    wout.flush();
    wout.close();
          
    InputStream in = connection.getInputStream();
    BufferedReader reader = new BufferedReader(new InputStreamReader(new BufferedInputStream(in)));
    
    do
      serverResponse += reader.readLine();
    while (reader.ready());
    
    if (!serverResponse.equals("<?xml version=\"1.0\"?><SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:SOAP-ENC=\"http://schemas.xmlsoap.org/soap/encoding/\"><SOAP-ENV:Body SOAP-ENC:encodingStyle=\"http://schemas.xmlsoap.org/soap/envelope/\"><NS1:TestConnectionResponse xmlns:NS1=\"urn:BioSemanticsIntf-ThesaurusPort\"><return xsi:type=\"xsd:string\">The connection to this server is working fine!</return></NS1:TestConnectionResponse></SOAP-ENV:Body></SOAP-ENV:Envelope>"))
      throw new Exception("Cannot connect to server! Server replied: " + serverResponse);
    
    in.close();
  }
  
  public Map<Integer, SemanticType> getSemanticTypes(String thesaurusName, Map<String, SemanticGroup> semanticGroups) throws RemoteException {
    Map<Integer, SemanticType> result = new TreeMap<Integer, SemanticType>();
    SemanticTypeData[] semanticTypesData = bioSemanticsPort.getSemanticTypeData(thesaurusName);
    
    for (SemanticTypeData semanticTypeData: semanticTypesData) {
      SemanticType semanticType = new SemanticType();

      semanticType.ID = semanticTypeData.getID();
      semanticType.name = semanticTypeData.getName();
      semanticType.description = semanticTypeData.getDescription();

      result.put(semanticType.ID, semanticType);
    }

    for (SemanticTypeData semanticTypeData: semanticTypesData) {
      SemanticType semanticType = result.get(semanticTypeData.getID());
      semanticType.parent = result.get(semanticTypeData.getParentID());
      semanticType.group = semanticGroups.get(semanticTypeData.getGroupID());
    }
    
    return result;
  }
  
  public Map<String, SemanticGroup> getSemanticGroups(String thesaurusName) throws java.rmi.RemoteException {
    Map<String, SemanticGroup> result = new TreeMap<String, SemanticGroup>();
    SemanticGroupData[] semanticGroupData = bioSemanticsPort.getSemanticGroupData(thesaurusName);

    for (int i = 0; i < semanticGroupData.length; i++) {
      SemanticGroup semanticGroup = new SemanticGroup();
      semanticGroup.ID = semanticGroupData[i].getID();
      semanticGroup.name = semanticGroupData[i].getName();

      result.put(semanticGroup.ID, semanticGroup);
    }
    
    return result;
  }
  
  public Map<Integer, InternetDatabase> getInternetDatabases(String thesaurusName) throws java.rmi.RemoteException {
    Map<Integer, InternetDatabase> result = new TreeMap<Integer, InternetDatabase>();
    InternetDatabaseData[] internetDatabases = bioSemanticsPort.getInternetDatabaseData(thesaurusName);

    for (InternetDatabaseData internetDatabaseData: internetDatabases) {
      InternetDatabase internetDatabase = new InternetDatabase();

      internetDatabase.ID = internetDatabaseData.getID();
      internetDatabase.name = internetDatabaseData.getName();
      internetDatabase.URLFragments = internetDatabaseData.getURLFragments();

      result.put(internetDatabase.ID, internetDatabase);
    }
    
    return result;
  }
}