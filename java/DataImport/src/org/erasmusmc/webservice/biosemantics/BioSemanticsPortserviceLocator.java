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

/**
 * BioSemanticsPortserviceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2RC3 Feb 28, 2005 (10:15:14 EST) WSDL2Java emitter.
 */

package org.erasmusmc.webservice.biosemantics;

public class BioSemanticsPortserviceLocator extends org.apache.axis.client.Service implements org.erasmusmc.webservice.biosemantics.BioSemanticsPortservice {

    public BioSemanticsPortserviceLocator() {
    }


    public BioSemanticsPortserviceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public BioSemanticsPortserviceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for BioSemanticsPortPort
    private java.lang.String BioSemanticsPortPort_address = "http://localhost/biosemantics/biosemantics.dll/soap/BioSemanticsPort";

    @Override
	public java.lang.String getBioSemanticsPortPortAddress() {
        return BioSemanticsPortPort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String BioSemanticsPortPortWSDDServiceName = "BioSemanticsPortPort";

    public java.lang.String getBioSemanticsPortPortWSDDServiceName() {
        return BioSemanticsPortPortWSDDServiceName;
    }

    public void setBioSemanticsPortPortWSDDServiceName(java.lang.String name) {
        BioSemanticsPortPortWSDDServiceName = name;
    }

    @Override
	public org.erasmusmc.webservice.biosemantics.BioSemanticsPort getBioSemanticsPortPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(BioSemanticsPortPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getBioSemanticsPortPort(endpoint);
    }

    @Override
	public org.erasmusmc.webservice.biosemantics.BioSemanticsPort getBioSemanticsPortPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            org.erasmusmc.webservice.biosemantics.BioSemanticsPortbindingStub _stub = new org.erasmusmc.webservice.biosemantics.BioSemanticsPortbindingStub(portAddress, this);
            _stub.setPortName(getBioSemanticsPortPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setBioSemanticsPortPortEndpointAddress(java.lang.String address) {
        BioSemanticsPortPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (org.erasmusmc.webservice.biosemantics.BioSemanticsPort.class.isAssignableFrom(serviceEndpointInterface)) {
                org.erasmusmc.webservice.biosemantics.BioSemanticsPortbindingStub _stub = new org.erasmusmc.webservice.biosemantics.BioSemanticsPortbindingStub(new java.net.URL(BioSemanticsPortPort_address), this);
                _stub.setPortName(getBioSemanticsPortPortWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("BioSemanticsPortPort".equals(inputPortName)) {
            return getBioSemanticsPortPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://tempuri.org/", "BioSemanticsPortservice");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://tempuri.org/", "BioSemanticsPortPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        if ("BioSemanticsPortPort".equals(portName)) {
            setBioSemanticsPortPortEndpointAddress(address);
        }
        else { // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
