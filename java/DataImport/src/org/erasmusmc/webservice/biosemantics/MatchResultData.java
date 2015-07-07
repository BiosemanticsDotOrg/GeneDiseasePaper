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
 * MatchResultData.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2RC3 Feb 28, 2005 (10:15:14 EST) WSDL2Java emitter.
 */

package org.erasmusmc.webservice.biosemantics;

public class MatchResultData  implements java.io.Serializable {
    private java.lang.String documentID;
    private java.lang.String title;
    private java.lang.String linkOut;
    private double rank;
    private int hits;
    private java.lang.String dateCreated;
    private java.lang.String dateModified;
    private int vectorLength;

    public MatchResultData() {
    }

    public MatchResultData(
           java.lang.String dateCreated,
           java.lang.String dateModified,
           java.lang.String documentID,
           int hits,
           java.lang.String linkOut,
           double rank,
           java.lang.String title,
           int vectorLength) {
           this.documentID = documentID;
           this.title = title;
           this.linkOut = linkOut;
           this.rank = rank;
           this.hits = hits;
           this.dateCreated = dateCreated;
           this.dateModified = dateModified;
           this.vectorLength = vectorLength;
    }


    /**
     * Gets the documentID value for this MatchResultData.
     * 
     * @return documentID
     */
    public java.lang.String getDocumentID() {
        return documentID;
    }


    /**
     * Sets the documentID value for this MatchResultData.
     * 
     * @param documentID
     */
    public void setDocumentID(java.lang.String documentID) {
        this.documentID = documentID;
    }


    /**
     * Gets the title value for this MatchResultData.
     * 
     * @return title
     */
    public java.lang.String getTitle() {
        return title;
    }


    /**
     * Sets the title value for this MatchResultData.
     * 
     * @param title
     */
    public void setTitle(java.lang.String title) {
        this.title = title;
    }


    /**
     * Gets the linkOut value for this MatchResultData.
     * 
     * @return linkOut
     */
    public java.lang.String getLinkOut() {
        return linkOut;
    }


    /**
     * Sets the linkOut value for this MatchResultData.
     * 
     * @param linkOut
     */
    public void setLinkOut(java.lang.String linkOut) {
        this.linkOut = linkOut;
    }


    /**
     * Gets the rank value for this MatchResultData.
     * 
     * @return rank
     */
    public double getRank() {
        return rank;
    }


    /**
     * Sets the rank value for this MatchResultData.
     * 
     * @param rank
     */
    public void setRank(double rank) {
        this.rank = rank;
    }


    /**
     * Gets the hits value for this MatchResultData.
     * 
     * @return hits
     */
    public int getHits() {
        return hits;
    }


    /**
     * Sets the hits value for this MatchResultData.
     * 
     * @param hits
     */
    public void setHits(int hits) {
        this.hits = hits;
    }


    /**
     * Gets the dateCreated value for this MatchResultData.
     * 
     * @return dateCreated
     */
    public java.lang.String getDateCreated() {
        return dateCreated;
    }


    /**
     * Sets the dateCreated value for this MatchResultData.
     * 
     * @param dateCreated
     */
    public void setDateCreated(java.lang.String dateCreated) {
        this.dateCreated = dateCreated;
    }


    /**
     * Gets the dateModified value for this MatchResultData.
     * 
     * @return dateModified
     */
    public java.lang.String getDateModified() {
        return dateModified;
    }


    /**
     * Sets the dateModified value for this MatchResultData.
     * 
     * @param dateModified
     */
    public void setDateModified(java.lang.String dateModified) {
        this.dateModified = dateModified;
    }


    /**
     * Gets the vectorLength value for this MatchResultData.
     * 
     * @return vectorLength
     */
    public int getVectorLength() {
        return vectorLength;
    }


    /**
     * Sets the vectorLength value for this MatchResultData.
     * 
     * @param vectorLength
     */
    public void setVectorLength(int vectorLength) {
        this.vectorLength = vectorLength;
    }

    private java.lang.Object __equalsCalc = null;
    @Override
	public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof MatchResultData)) return false;
        MatchResultData other = (MatchResultData) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.documentID==null && other.getDocumentID()==null) || 
             (this.documentID!=null &&
              this.documentID.equals(other.getDocumentID()))) &&
            ((this.title==null && other.getTitle()==null) || 
             (this.title!=null &&
              this.title.equals(other.getTitle()))) &&
            ((this.linkOut==null && other.getLinkOut()==null) || 
             (this.linkOut!=null &&
              this.linkOut.equals(other.getLinkOut()))) &&
            this.rank == other.getRank() &&
            this.hits == other.getHits() &&
            ((this.dateCreated==null && other.getDateCreated()==null) || 
             (this.dateCreated!=null &&
              this.dateCreated.equals(other.getDateCreated()))) &&
            ((this.dateModified==null && other.getDateModified()==null) || 
             (this.dateModified!=null &&
              this.dateModified.equals(other.getDateModified()))) &&
            this.vectorLength == other.getVectorLength();
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
        if (getDocumentID() != null) {
            _hashCode += getDocumentID().hashCode();
        }
        if (getTitle() != null) {
            _hashCode += getTitle().hashCode();
        }
        if (getLinkOut() != null) {
            _hashCode += getLinkOut().hashCode();
        }
        _hashCode += new Double(getRank()).hashCode();
        _hashCode += getHits();
        if (getDateCreated() != null) {
            _hashCode += getDateCreated().hashCode();
        }
        if (getDateModified() != null) {
            _hashCode += getDateModified().hashCode();
        }
        _hashCode += getVectorLength();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(MatchResultData.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:BioSemanticsIntf", "MatchResultData"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("documentID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "DocumentID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("title");
        elemField.setXmlName(new javax.xml.namespace.QName("", "Title"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("linkOut");
        elemField.setXmlName(new javax.xml.namespace.QName("", "LinkOut"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("rank");
        elemField.setXmlName(new javax.xml.namespace.QName("", "Rank"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "double"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("hits");
        elemField.setXmlName(new javax.xml.namespace.QName("", "Hits"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dateCreated");
        elemField.setXmlName(new javax.xml.namespace.QName("", "DateCreated"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("dateModified");
        elemField.setXmlName(new javax.xml.namespace.QName("", "DateModified"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("vectorLength");
        elemField.setXmlName(new javax.xml.namespace.QName("", "VectorLength"));
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
