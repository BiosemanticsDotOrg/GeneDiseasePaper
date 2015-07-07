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
 * ConceptOccurrenceData.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2RC3 Feb 28, 2005 (10:15:14 EST) WSDL2Java emitter.
 */

package org.erasmusmc.webservice.biosemantics;

public class ConceptOccurrenceData  implements java.io.Serializable {
    private int conceptID;
    private int startOffset;
    private int endOffset;
    private java.lang.String thesaurus;

    public ConceptOccurrenceData() {
    }

    public ConceptOccurrenceData(
           int conceptID,
           int endOffset,
           int startOffset,
           java.lang.String thesaurus) {
           this.conceptID = conceptID;
           this.startOffset = startOffset;
           this.endOffset = endOffset;
           this.thesaurus = thesaurus;
    }


    /**
     * Gets the conceptID value for this ConceptOccurrenceData.
     * 
     * @return conceptID
     */
    public int getConceptID() {
        return conceptID;
    }


    /**
     * Sets the conceptID value for this ConceptOccurrenceData.
     * 
     * @param conceptID
     */
    public void setConceptID(int conceptID) {
        this.conceptID = conceptID;
    }


    /**
     * Gets the startOffset value for this ConceptOccurrenceData.
     * 
     * @return startOffset
     */
    public int getStartOffset() {
        return startOffset;
    }


    /**
     * Sets the startOffset value for this ConceptOccurrenceData.
     * 
     * @param startOffset
     */
    public void setStartOffset(int startOffset) {
        this.startOffset = startOffset;
    }


    /**
     * Gets the endOffset value for this ConceptOccurrenceData.
     * 
     * @return endOffset
     */
    public int getEndOffset() {
        return endOffset;
    }


    /**
     * Sets the endOffset value for this ConceptOccurrenceData.
     * 
     * @param endOffset
     */
    public void setEndOffset(int endOffset) {
        this.endOffset = endOffset;
    }


    /**
     * Gets the thesaurus value for this ConceptOccurrenceData.
     * 
     * @return thesaurus
     */
    public java.lang.String getThesaurus() {
        return thesaurus;
    }


    /**
     * Sets the thesaurus value for this ConceptOccurrenceData.
     * 
     * @param thesaurus
     */
    public void setThesaurus(java.lang.String thesaurus) {
        this.thesaurus = thesaurus;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ConceptOccurrenceData)) return false;
        ConceptOccurrenceData other = (ConceptOccurrenceData) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.conceptID == other.getConceptID() &&
            this.startOffset == other.getStartOffset() &&
            this.endOffset == other.getEndOffset() &&
            ((this.thesaurus==null && other.getThesaurus()==null) || 
             (this.thesaurus!=null &&
              this.thesaurus.equals(other.getThesaurus())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        _hashCode += getConceptID();
        _hashCode += getStartOffset();
        _hashCode += getEndOffset();
        if (getThesaurus() != null) {
            _hashCode += getThesaurus().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(ConceptOccurrenceData.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:BioSemanticsIntf", "ConceptOccurrenceData"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("conceptID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "ConceptID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("startOffset");
        elemField.setXmlName(new javax.xml.namespace.QName("", "StartOffset"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("endOffset");
        elemField.setXmlName(new javax.xml.namespace.QName("", "EndOffset"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("thesaurus");
        elemField.setXmlName(new javax.xml.namespace.QName("", "Thesaurus"));
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
