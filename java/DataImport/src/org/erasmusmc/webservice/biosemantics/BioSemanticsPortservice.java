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
 * BioSemanticsPortservice.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2RC3 Feb 28, 2005 (10:15:14 EST) WSDL2Java emitter.
 */

package org.erasmusmc.webservice.biosemantics;

public interface BioSemanticsPortservice extends javax.xml.rpc.Service {
    public java.lang.String getBioSemanticsPortPortAddress();

    public org.erasmusmc.webservice.biosemantics.BioSemanticsPort getBioSemanticsPortPort() throws javax.xml.rpc.ServiceException;

    public org.erasmusmc.webservice.biosemantics.BioSemanticsPort getBioSemanticsPortPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
