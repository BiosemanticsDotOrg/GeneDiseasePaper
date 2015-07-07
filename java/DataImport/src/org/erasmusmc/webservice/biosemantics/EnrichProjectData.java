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
 * EnrichProjectData.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2RC3 Feb 28, 2005 (10:15:14 EST) WSDL2Java emitter.
 */

package org.erasmusmc.webservice.biosemantics;

public class EnrichProjectData  implements java.io.Serializable {
    private int ID;
    private java.lang.String name;
    private java.lang.String description;
    private java.lang.String[] thesauri;
    private java.lang.String proposedTerms;
    private int leaderID;
    private java.lang.String leader;

    public EnrichProjectData() {
    }

    public EnrichProjectData(
           int ID,
           java.lang.String description,
           java.lang.String leader,
           int leaderID,
           java.lang.String name,
           java.lang.String proposedTerms,
           java.lang.String[] thesauri) {
           this.ID = ID;
           this.name = name;
           this.description = description;
           this.thesauri = thesauri;
           this.proposedTerms = proposedTerms;
           this.leaderID = leaderID;
           this.leader = leader;
    }


    /**
     * Gets the ID value for this EnrichProjectData.
     * 
     * @return ID
     */
    public int getID() {
        return ID;
    }


    /**
     * Sets the ID value for this EnrichProjectData.
     * 
     * @param ID
     */
    public void setID(int ID) {
        this.ID = ID;
    }


    /**
     * Gets the name value for this EnrichProjectData.
     * 
     * @return name
     */
    public java.lang.String getName() {
        return name;
    }


    /**
     * Sets the name value for this EnrichProjectData.
     * 
     * @param name
     */
    public void setName(java.lang.String name) {
        this.name = name;
    }


    /**
     * Gets the description value for this EnrichProjectData.
     * 
     * @return description
     */
    public java.lang.String getDescription() {
        return description;
    }


    /**
     * Sets the description value for this EnrichProjectData.
     * 
     * @param description
     */
    public void setDescription(java.lang.String description) {
        this.description = description;
    }


    /**
     * Gets the thesauri value for this EnrichProjectData.
     * 
     * @return thesauri
     */
    public java.lang.String[] getThesauri() {
        return thesauri;
    }


    /**
     * Sets the thesauri value for this EnrichProjectData.
     * 
     * @param thesauri
     */
    public void setThesauri(java.lang.String[] thesauri) {
        this.thesauri = thesauri;
    }


    /**
     * Gets the proposedTerms value for this EnrichProjectData.
     * 
     * @return proposedTerms
     */
    public java.lang.String getProposedTerms() {
        return proposedTerms;
    }


    /**
     * Sets the proposedTerms value for this EnrichProjectData.
     * 
     * @param proposedTerms
     */
    public void setProposedTerms(java.lang.String proposedTerms) {
        this.proposedTerms = proposedTerms;
    }


    /**
     * Gets the leaderID value for this EnrichProjectData.
     * 
     * @return leaderID
     */
    public int getLeaderID() {
        return leaderID;
    }


    /**
     * Sets the leaderID value for this EnrichProjectData.
     * 
     * @param leaderID
     */
    public void setLeaderID(int leaderID) {
        this.leaderID = leaderID;
    }


    /**
     * Gets the leader value for this EnrichProjectData.
     * 
     * @return leader
     */
    public java.lang.String getLeader() {
        return leader;
    }


    /**
     * Sets the leader value for this EnrichProjectData.
     * 
     * @param leader
     */
    public void setLeader(java.lang.String leader) {
        this.leader = leader;
    }

    private java.lang.Object __equalsCalc = null;
    @Override
	public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof EnrichProjectData)) return false;
        EnrichProjectData other = (EnrichProjectData) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.ID == other.getID() &&
            ((this.name==null && other.getName()==null) || 
             (this.name!=null &&
              this.name.equals(other.getName()))) &&
            ((this.description==null && other.getDescription()==null) || 
             (this.description!=null &&
              this.description.equals(other.getDescription()))) &&
            ((this.thesauri==null && other.getThesauri()==null) || 
             (this.thesauri!=null &&
              java.util.Arrays.equals(this.thesauri, other.getThesauri()))) &&
            ((this.proposedTerms==null && other.getProposedTerms()==null) || 
             (this.proposedTerms!=null &&
              this.proposedTerms.equals(other.getProposedTerms()))) &&
            this.leaderID == other.getLeaderID() &&
            ((this.leader==null && other.getLeader()==null) || 
             (this.leader!=null &&
              this.leader.equals(other.getLeader())));
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
        if (getName() != null) {
            _hashCode += getName().hashCode();
        }
        if (getDescription() != null) {
            _hashCode += getDescription().hashCode();
        }
        if (getThesauri() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getThesauri());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getThesauri(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getProposedTerms() != null) {
            _hashCode += getProposedTerms().hashCode();
        }
        _hashCode += getLeaderID();
        if (getLeader() != null) {
            _hashCode += getLeader().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(EnrichProjectData.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:BioSemanticsIntf", "EnrichProjectData"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "ID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("name");
        elemField.setXmlName(new javax.xml.namespace.QName("", "Name"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("description");
        elemField.setXmlName(new javax.xml.namespace.QName("", "Description"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("thesauri");
        elemField.setXmlName(new javax.xml.namespace.QName("", "Thesauri"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("proposedTerms");
        elemField.setXmlName(new javax.xml.namespace.QName("", "ProposedTerms"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("leaderID");
        elemField.setXmlName(new javax.xml.namespace.QName("", "LeaderID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("leader");
        elemField.setXmlName(new javax.xml.namespace.QName("", "Leader"));
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
