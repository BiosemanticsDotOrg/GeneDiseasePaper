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
 * ProposedConceptData.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2RC3 Feb 28, 2005 (10:15:14 EST) WSDL2Java emitter.
 */

package org.erasmusmc.webservice.biosemantics;

public class ProposedConceptData  implements java.io.Serializable {
    private int ID;
    private int userID;
    private java.lang.String name;
    private java.lang.String definition;
    private int[] semanticTypes;

    public ProposedConceptData() {
    }

    public ProposedConceptData(
           int ID,
           java.lang.String definition,
           java.lang.String name,
           int[] semanticTypes,
           int userID) {
           this.ID = ID;
           this.userID = userID;
           this.name = name;
           this.definition = definition;
           this.semanticTypes = semanticTypes;
    }


    /**
     * Gets the ID value for this ProposedConceptData.
     * 
     * @return ID
     */
    public int getID() {
        return ID;
    }


    /**
     * Sets the ID value for this ProposedConceptData.
     * 
     * @param ID
     */
    public void setID(int ID) {
        this.ID = ID;
    }


    /**
     * Gets the userID value for this ProposedConceptData.
     * 
     * @return userID
     */
    public int getUserID() {
        return userID;
    }


    /**
     * Sets the userID value for this ProposedConceptData.
     * 
     * @param userID
     */
    public void setUserID(int userID) {
        this.userID = userID;
    }


    /**
     * Gets the name value for this ProposedConceptData.
     * 
     * @return name
     */
    public java.lang.String getName() {
        return name;
    }


    /**
     * Sets the name value for this ProposedConceptData.
     * 
     * @param name
     */
    public void setName(java.lang.String name) {
        this.name = name;
    }


    /**
     * Gets the definition value for this ProposedConceptData.
     * 
     * @return definition
     */
    public java.lang.String getDefinition() {
        return definition;
    }


    /**
     * Sets the definition value for this ProposedConceptData.
     * 
     * @param definition
     */
    public void setDefinition(java.lang.String definition) {
        this.definition = definition;
    }


    /**
     * Gets the semanticTypes value for this ProposedConceptData.
     * 
     * @return semanticTypes
     */
    public int[] getSemanticTypes() {
        return semanticTypes;
    }


    /**
     * Sets the semanticTypes value for this ProposedConceptData.
     * 
     * @param semanticTypes
     */
    public void setSemanticTypes(int[] semanticTypes) {
        this.semanticTypes = semanticTypes;
    }

    private java.lang.Object __equalsCalc = null;
    @Override
	public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ProposedConceptData)) return false;
        ProposedConceptData other = (ProposedConceptData) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.ID == other.getID() &&
            this.userID == other.getUserID() &&
            ((this.name==null && other.getName()==null) || 
             (this.name!=null &&
              this.name.equals(other.getName()))) &&
            ((this.definition==null && other.getDefinition()==null) || 
             (this.definition!=null &&
              this.definition.equals(other.getDefinition()))) &&
            ((this.semanticTypes==null && other.getSemanticTypes()==null) || 
             (this.semanticTypes!=null &&
              java.util.Arrays.equals(this.semanticTypes, other.getSemanticTypes())));
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
        _hashCode += getID();
        _hashCode += getUserID();
        if (getName() != null) {
            _hashCode += getName().hashCode();
        }
        if (getDefinition() != null) {
            _hashCode += getDefinition().hashCode();
        }
        if (getSemanticTypes() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getSemanticTypes());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getSemanticTypes(), i);
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
        new org.apache.axis.description.TypeDesc(ProposedConceptData.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:BioSemanticsIntf", "ProposedConceptData"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "ID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("userID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "UserID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("name");
        elemField.setXmlName(new javax.xml.namespace.QName("", "Name"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("definition");
        elemField.setXmlName(new javax.xml.namespace.QName("", "Definition"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("semanticTypes");
        elemField.setXmlName(new javax.xml.namespace.QName("", "SemanticTypes"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
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
