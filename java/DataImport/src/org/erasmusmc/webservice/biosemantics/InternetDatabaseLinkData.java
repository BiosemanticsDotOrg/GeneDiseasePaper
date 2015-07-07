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
 * InternetDatabaseLinkData.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2RC3 Feb 28, 2005 (10:15:14 EST) WSDL2Java emitter.
 */

package org.erasmusmc.webservice.biosemantics;

public class InternetDatabaseLinkData  implements java.io.Serializable {
    private int conceptID;
    private int databaseID;
    private java.lang.String[] parameters;

    public InternetDatabaseLinkData() {
    }

    public InternetDatabaseLinkData(
           int conceptID,
           int databaseID,
           java.lang.String[] parameters) {
           this.conceptID = conceptID;
           this.databaseID = databaseID;
           this.parameters = parameters;
    }


    /**
     * Gets the conceptID value for this InternetDatabaseLinkData.
     * 
     * @return conceptID
     */
    public int getConceptID() {
        return conceptID;
    }


    /**
     * Sets the conceptID value for this InternetDatabaseLinkData.
     * 
     * @param conceptID
     */
    public void setConceptID(int conceptID) {
        this.conceptID = conceptID;
    }


    /**
     * Gets the databaseID value for this InternetDatabaseLinkData.
     * 
     * @return databaseID
     */
    public int getDatabaseID() {
        return databaseID;
    }


    /**
     * Sets the databaseID value for this InternetDatabaseLinkData.
     * 
     * @param databaseID
     */
    public void setDatabaseID(int databaseID) {
        this.databaseID = databaseID;
    }


    /**
     * Gets the parameters value for this InternetDatabaseLinkData.
     * 
     * @return parameters
     */
    public java.lang.String[] getParameters() {
        return parameters;
    }


    /**
     * Sets the parameters value for this InternetDatabaseLinkData.
     * 
     * @param parameters
     */
    public void setParameters(java.lang.String[] parameters) {
        this.parameters = parameters;
    }

    private java.lang.Object __equalsCalc = null;
    @Override
	public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof InternetDatabaseLinkData)) return false;
        InternetDatabaseLinkData other = (InternetDatabaseLinkData) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.conceptID == other.getConceptID() &&
            this.databaseID == other.getDatabaseID() &&
            ((this.parameters==null && other.getParameters()==null) || 
             (this.parameters!=null &&
              java.util.Arrays.equals(this.parameters, other.getParameters())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    @Override
	public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        _hashCode += getConceptID();
        _hashCode += getDatabaseID();
        if (getParameters() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getParameters());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getParameters(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(InternetDatabaseLinkData.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:BioSemanticsIntf", "InternetDatabaseLinkData"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("conceptID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "ConceptID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("databaseID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "DatabaseID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("parameters");
        elemField.setXmlName(new javax.xml.namespace.QName("", "Parameters"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
