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
 * BioSemanticsPortbindingStub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2RC3 Feb 28, 2005 (10:15:14 EST) WSDL2Java emitter.
 */

package org.erasmusmc.webservice.biosemantics;

public class BioSemanticsPortbindingStub extends org.apache.axis.client.Stub implements org.erasmusmc.webservice.biosemantics.BioSemanticsPort {
    private java.util.Vector cachedSerClasses = new java.util.Vector();
    private java.util.Vector cachedSerQNames = new java.util.Vector();
    private java.util.Vector cachedSerFactories = new java.util.Vector();
    private java.util.Vector cachedDeserFactories = new java.util.Vector();

    static org.apache.axis.description.OperationDesc [] _operations;

    static {
        _operations = new org.apache.axis.description.OperationDesc[33];
        _initOperationDesc1();
        _initOperationDesc2();
        _initOperationDesc3();
        _initOperationDesc4();
    }

    private static void _initOperationDesc1(){
        org.apache.axis.description.OperationDesc oper;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("TestConnection");
        oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        oper.setReturnClass(java.lang.String.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[0] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetThesauri");
        oper.setReturnType(new javax.xml.namespace.QName("urn:StandardTypes", "StringArray"));
        oper.setReturnClass(java.lang.String[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[1] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("IsThesaurusOnline");
        oper.addParameter(new javax.xml.namespace.QName("", "Thesaurus"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        oper.setReturnClass(boolean.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[2] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("SearchConcepts");
        oper.addParameter(new javax.xml.namespace.QName("", "Thesaurus"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.addParameter(new javax.xml.namespace.QName("", "SearchString"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.setReturnType(new javax.xml.namespace.QName("urn:StandardTypes", "IntegerArray"));
        oper.setReturnClass(int[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[3] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetSemanticGroupData");
        oper.addParameter(new javax.xml.namespace.QName("", "Thesaurus"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.setReturnType(new javax.xml.namespace.QName("urn:BioSemanticsIntf", "SemanticGroupDataArray"));
        oper.setReturnClass(org.erasmusmc.webservice.biosemantics.SemanticGroupData[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[4] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetSemanticTypeData");
        oper.addParameter(new javax.xml.namespace.QName("", "Thesaurus"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.setReturnType(new javax.xml.namespace.QName("urn:BioSemanticsIntf", "SemanticTypeDataArray"));
        oper.setReturnClass(org.erasmusmc.webservice.biosemantics.SemanticTypeData[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[5] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetInternetDatabaseData");
        oper.addParameter(new javax.xml.namespace.QName("", "Thesaurus"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.setReturnType(new javax.xml.namespace.QName("urn:BioSemanticsIntf", "InternetDatabaseDataArray"));
        oper.setReturnClass(org.erasmusmc.webservice.biosemantics.InternetDatabaseData[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[6] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetConceptData");
        oper.addParameter(new javax.xml.namespace.QName("", "Thesaurus"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.addParameter(new javax.xml.namespace.QName("", "ConceptIDs"), new javax.xml.namespace.QName("urn:StandardTypes", "IntegerArray"), int[].class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.setReturnType(new javax.xml.namespace.QName("urn:BioSemanticsIntf", "ConceptDataArray"));
        oper.setReturnClass(org.erasmusmc.webservice.biosemantics.ConceptData[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[7] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetInternetDatabaseLinks");
        oper.addParameter(new javax.xml.namespace.QName("", "Thesaurus"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.addParameter(new javax.xml.namespace.QName("", "ConceptIDs"), new javax.xml.namespace.QName("urn:StandardTypes", "IntegerArray"), int[].class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.setReturnType(new javax.xml.namespace.QName("urn:BioSemanticsIntf", "InternetDatabaseLinkDataArray"));
        oper.setReturnClass(org.erasmusmc.webservice.biosemantics.InternetDatabaseLinkData[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[8] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetEnrichProjectsData");
        oper.addParameter(new javax.xml.namespace.QName("", "UserName"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.addParameter(new javax.xml.namespace.QName("", "Password"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.setReturnType(new javax.xml.namespace.QName("urn:BioSemanticsIntf", "EnrichProjectDataArray"));
        oper.setReturnClass(org.erasmusmc.webservice.biosemantics.EnrichProjectData[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[9] = oper;

    }

    private static void _initOperationDesc2(){
        org.apache.axis.description.OperationDesc oper;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetEnrichProjectMemberData");
        oper.addParameter(new javax.xml.namespace.QName("", "ProjectID"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.addParameter(new javax.xml.namespace.QName("", "UserName"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.addParameter(new javax.xml.namespace.QName("", "Password"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.setReturnType(new javax.xml.namespace.QName("urn:BioSemanticsIntf", "EnrichProjectMemberData"));
        oper.setReturnClass(org.erasmusmc.webservice.biosemantics.EnrichProjectMemberData.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[10] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetEnrichProjectMembers");
        oper.addParameter(new javax.xml.namespace.QName("", "MemberData"), new javax.xml.namespace.QName("urn:BioSemanticsIntf", "EnrichProjectMemberData"), org.erasmusmc.webservice.biosemantics.EnrichProjectMemberData.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.setReturnType(new javax.xml.namespace.QName("urn:BioSemanticsIntf", "EnrichProjectMemberInfoDataArray"));
        oper.setReturnClass(org.erasmusmc.webservice.biosemantics.EnrichProjectMemberInfoData[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[11] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("NewProposedConcept");
        oper.addParameter(new javax.xml.namespace.QName("", "MemberData"), new javax.xml.namespace.QName("urn:BioSemanticsIntf", "EnrichProjectMemberData"), org.erasmusmc.webservice.biosemantics.EnrichProjectMemberData.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        oper.setReturnClass(int.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[12] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("SetProposedConceptData");
        oper.addParameter(new javax.xml.namespace.QName("", "MemberData"), new javax.xml.namespace.QName("urn:BioSemanticsIntf", "EnrichProjectMemberData"), org.erasmusmc.webservice.biosemantics.EnrichProjectMemberData.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.addParameter(new javax.xml.namespace.QName("", "Concept"), new javax.xml.namespace.QName("urn:BioSemanticsIntf", "ProposedConceptData"), org.erasmusmc.webservice.biosemantics.ProposedConceptData.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[13] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("RemoveProposedConcept");
        oper.addParameter(new javax.xml.namespace.QName("", "MemberData"), new javax.xml.namespace.QName("urn:BioSemanticsIntf", "EnrichProjectMemberData"), org.erasmusmc.webservice.biosemantics.EnrichProjectMemberData.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.addParameter(new javax.xml.namespace.QName("", "ConceptID"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[14] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetProposedConcepts");
        oper.addParameter(new javax.xml.namespace.QName("", "MemberData"), new javax.xml.namespace.QName("urn:BioSemanticsIntf", "EnrichProjectMemberData"), org.erasmusmc.webservice.biosemantics.EnrichProjectMemberData.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.setReturnType(new javax.xml.namespace.QName("urn:StandardTypes", "IntegerArray"));
        oper.setReturnClass(int[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[15] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetProposedConceptsForMember");
        oper.addParameter(new javax.xml.namespace.QName("", "MemberData"), new javax.xml.namespace.QName("urn:BioSemanticsIntf", "EnrichProjectMemberData"), org.erasmusmc.webservice.biosemantics.EnrichProjectMemberData.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.addParameter(new javax.xml.namespace.QName("", "MemberID"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.setReturnType(new javax.xml.namespace.QName("urn:StandardTypes", "IntegerArray"));
        oper.setReturnClass(int[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[16] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetProposedConceptData");
        oper.addParameter(new javax.xml.namespace.QName("", "MemberData"), new javax.xml.namespace.QName("urn:BioSemanticsIntf", "EnrichProjectMemberData"), org.erasmusmc.webservice.biosemantics.EnrichProjectMemberData.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.addParameter(new javax.xml.namespace.QName("", "ConceptIDs"), new javax.xml.namespace.QName("urn:StandardTypes", "IntegerArray"), int[].class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.setReturnType(new javax.xml.namespace.QName("urn:BioSemanticsIntf", "ProposedConceptDataArray"));
        oper.setReturnClass(org.erasmusmc.webservice.biosemantics.ProposedConceptData[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[17] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("NewTerm");
        oper.addParameter(new javax.xml.namespace.QName("", "MemberData"), new javax.xml.namespace.QName("urn:BioSemanticsIntf", "EnrichProjectMemberData"), org.erasmusmc.webservice.biosemantics.EnrichProjectMemberData.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        oper.setReturnClass(int.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[18] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("SetTermData");
        oper.addParameter(new javax.xml.namespace.QName("", "MemberData"), new javax.xml.namespace.QName("urn:BioSemanticsIntf", "EnrichProjectMemberData"), org.erasmusmc.webservice.biosemantics.EnrichProjectMemberData.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.addParameter(new javax.xml.namespace.QName("", "Term"), new javax.xml.namespace.QName("urn:BioSemanticsIntf", "TermData"), org.erasmusmc.webservice.biosemantics.TermData.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[19] = oper;

    }

    private static void _initOperationDesc3(){
        org.apache.axis.description.OperationDesc oper;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("RemoveTerm");
        oper.addParameter(new javax.xml.namespace.QName("", "MemberData"), new javax.xml.namespace.QName("urn:BioSemanticsIntf", "EnrichProjectMemberData"), org.erasmusmc.webservice.biosemantics.EnrichProjectMemberData.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.addParameter(new javax.xml.namespace.QName("", "TermID"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[20] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetTerms");
        oper.addParameter(new javax.xml.namespace.QName("", "MemberData"), new javax.xml.namespace.QName("urn:BioSemanticsIntf", "EnrichProjectMemberData"), org.erasmusmc.webservice.biosemantics.EnrichProjectMemberData.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.setReturnType(new javax.xml.namespace.QName("urn:StandardTypes", "IntegerArray"));
        oper.setReturnClass(int[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[21] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetTermsForMember");
        oper.addParameter(new javax.xml.namespace.QName("", "MemberData"), new javax.xml.namespace.QName("urn:BioSemanticsIntf", "EnrichProjectMemberData"), org.erasmusmc.webservice.biosemantics.EnrichProjectMemberData.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.addParameter(new javax.xml.namespace.QName("", "MemberID"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.setReturnType(new javax.xml.namespace.QName("urn:StandardTypes", "IntegerArray"));
        oper.setReturnClass(int[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[22] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetTermData");
        oper.addParameter(new javax.xml.namespace.QName("", "MemberData"), new javax.xml.namespace.QName("urn:BioSemanticsIntf", "EnrichProjectMemberData"), org.erasmusmc.webservice.biosemantics.EnrichProjectMemberData.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.addParameter(new javax.xml.namespace.QName("", "TermIDs"), new javax.xml.namespace.QName("urn:StandardTypes", "IntegerArray"), int[].class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.setReturnType(new javax.xml.namespace.QName("urn:BioSemanticsIntf", "TermDataArray"));
        oper.setReturnClass(org.erasmusmc.webservice.biosemantics.TermData[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[23] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetDocumentText");
        oper.addParameter(new javax.xml.namespace.QName("", "MemberData"), new javax.xml.namespace.QName("urn:BioSemanticsIntf", "EnrichProjectMemberData"), org.erasmusmc.webservice.biosemantics.EnrichProjectMemberData.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.addParameter(new javax.xml.namespace.QName("", "DocumentName"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        oper.setReturnClass(java.lang.String.class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[24] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetDocumentOccurrenceData");
        oper.addParameter(new javax.xml.namespace.QName("", "MemberData"), new javax.xml.namespace.QName("urn:BioSemanticsIntf", "EnrichProjectMemberData"), org.erasmusmc.webservice.biosemantics.EnrichProjectMemberData.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.addParameter(new javax.xml.namespace.QName("", "DocumentName"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.setReturnType(new javax.xml.namespace.QName("urn:BioSemanticsIntf", "ConceptOccurrenceDataArray"));
        oper.setReturnClass(org.erasmusmc.webservice.biosemantics.ConceptOccurrenceData[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[25] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("ListDocuments");
        oper.addParameter(new javax.xml.namespace.QName("", "MemberData"), new javax.xml.namespace.QName("urn:BioSemanticsIntf", "EnrichProjectMemberData"), org.erasmusmc.webservice.biosemantics.EnrichProjectMemberData.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.setReturnType(new javax.xml.namespace.QName("urn:BioSemanticsIntf", "DocumentInfoDataArray"));
        oper.setReturnClass(org.erasmusmc.webservice.biosemantics.DocumentInfoData[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[26] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("UploadDocument");
        oper.addParameter(new javax.xml.namespace.QName("", "MemberData"), new javax.xml.namespace.QName("urn:BioSemanticsIntf", "EnrichProjectMemberData"), org.erasmusmc.webservice.biosemantics.EnrichProjectMemberData.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.addParameter(new javax.xml.namespace.QName("", "DocumentName"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.addParameter(new javax.xml.namespace.QName("", "Data"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "base64Binary"), byte[].class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[27] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("FindDocuments");
        oper.addParameter(new javax.xml.namespace.QName("", "Collexion"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.addParameter(new javax.xml.namespace.QName("", "SearchString"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.setReturnType(new javax.xml.namespace.QName("urn:BioSemanticsIntf", "MatchResultDataArray"));
        oper.setReturnClass(org.erasmusmc.webservice.biosemantics.MatchResultData[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[28] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetFingerprintData");
        oper.addParameter(new javax.xml.namespace.QName("", "Collexion"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.addParameter(new javax.xml.namespace.QName("", "DocumentID"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.setReturnType(new javax.xml.namespace.QName("urn:BioSemanticsIntf", "FingerprintData"));
        oper.setReturnClass(org.erasmusmc.webservice.biosemantics.FingerprintItemData[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[29] = oper;

    }

    private static void _initOperationDesc4(){
        org.apache.axis.description.OperationDesc oper;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("ReleaseProvisionalThesaurus");
        oper.addParameter(new javax.xml.namespace.QName("", "MemberData"), new javax.xml.namespace.QName("urn:BioSemanticsIntf", "EnrichProjectMemberData"), org.erasmusmc.webservice.biosemantics.EnrichProjectMemberData.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.setReturnType(org.apache.axis.encoding.XMLType.AXIS_VOID);
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[30] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetACSNetworks");
        oper.setReturnType(new javax.xml.namespace.QName("urn:BioSemanticsIntf", "ACSNetworkDataArray"));
        oper.setReturnClass(org.erasmusmc.webservice.biosemantics.ACSNetworkData[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[31] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetACSNetworkPart");
        oper.addParameter(new javax.xml.namespace.QName("", "NetworkID"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.addParameter(new javax.xml.namespace.QName("", "Offset"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.addParameter(new javax.xml.namespace.QName("", "Bytes"), new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, org.apache.axis.description.ParameterDesc.IN, false, false);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "base64Binary"));
        oper.setReturnClass(byte[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("", "return"));
        oper.setStyle(org.apache.axis.constants.Style.RPC);
        oper.setUse(org.apache.axis.constants.Use.ENCODED);
        _operations[32] = oper;

    }

    public BioSemanticsPortbindingStub() throws org.apache.axis.AxisFault {
         this(null);
    }

    public BioSemanticsPortbindingStub(java.net.URL endpointURL, javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
         this(service);
         super.cachedEndpoint = endpointURL;
    }

    public BioSemanticsPortbindingStub(javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
        if (service == null) {
            super.service = new org.apache.axis.client.Service();
        } else {
            super.service = service;
        }
        ((org.apache.axis.client.Service)super.service).setTypeMappingVersion("1.2");
            java.lang.Class cls;
            javax.xml.namespace.QName qName;
            java.lang.Class beansf = org.apache.axis.encoding.ser.BeanSerializerFactory.class;
            java.lang.Class beandf = org.apache.axis.encoding.ser.BeanDeserializerFactory.class;
            java.lang.Class enumsf = org.apache.axis.encoding.ser.EnumSerializerFactory.class;
            java.lang.Class enumdf = org.apache.axis.encoding.ser.EnumDeserializerFactory.class;
            java.lang.Class arraysf = org.apache.axis.encoding.ser.ArraySerializerFactory.class;
            java.lang.Class arraydf = org.apache.axis.encoding.ser.ArrayDeserializerFactory.class;
            java.lang.Class simplesf = org.apache.axis.encoding.ser.SimpleSerializerFactory.class;
            java.lang.Class simpledf = org.apache.axis.encoding.ser.SimpleDeserializerFactory.class;
            java.lang.Class simplelistsf = org.apache.axis.encoding.ser.SimpleListSerializerFactory.class;
            java.lang.Class simplelistdf = org.apache.axis.encoding.ser.SimpleListDeserializerFactory.class;
            qName = new javax.xml.namespace.QName("urn:BioSemanticsIntf", "ConceptData");
            cachedSerQNames.add(qName);
            cls = org.erasmusmc.webservice.biosemantics.ConceptData.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:BioSemanticsIntf", "EnrichProjectDataArray");
            cachedSerQNames.add(qName);
            cls = org.erasmusmc.webservice.biosemantics.EnrichProjectData[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("urn:BioSemanticsIntf", "EnrichProjectData");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("urn:BioSemanticsIntf", "FingerprintItemData");
            cachedSerQNames.add(qName);
            cls = org.erasmusmc.webservice.biosemantics.FingerprintItemData.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:BioSemanticsIntf", "FingerprintData");
            cachedSerQNames.add(qName);
            cls = org.erasmusmc.webservice.biosemantics.FingerprintItemData[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("urn:BioSemanticsIntf", "FingerprintItemData");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("urn:BioSemanticsIntf", "ProposedConceptData");
            cachedSerQNames.add(qName);
            cls = org.erasmusmc.webservice.biosemantics.ProposedConceptData.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:BioSemanticsIntf", "InternetDatabaseLinkDataArray");
            cachedSerQNames.add(qName);
            cls = org.erasmusmc.webservice.biosemantics.InternetDatabaseLinkData[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("urn:BioSemanticsIntf", "InternetDatabaseLinkData");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("urn:StandardTypes", "StringArray");
            cachedSerQNames.add(qName);
            cls = java.lang.String[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("urn:BioSemanticsIntf", "EnrichProjectMemberData");
            cachedSerQNames.add(qName);
            cls = org.erasmusmc.webservice.biosemantics.EnrichProjectMemberData.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:BioSemanticsIntf", "SemanticGroupData");
            cachedSerQNames.add(qName);
            cls = org.erasmusmc.webservice.biosemantics.SemanticGroupData.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:BioSemanticsIntf", "EnrichProjectData");
            cachedSerQNames.add(qName);
            cls = org.erasmusmc.webservice.biosemantics.EnrichProjectData.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:BioSemanticsIntf", "InternetDatabaseData");
            cachedSerQNames.add(qName);
            cls = org.erasmusmc.webservice.biosemantics.InternetDatabaseData.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:BioSemanticsIntf", "ConceptOccurrenceData");
            cachedSerQNames.add(qName);
            cls = org.erasmusmc.webservice.biosemantics.ConceptOccurrenceData.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:BioSemanticsIntf", "MatchResultData");
            cachedSerQNames.add(qName);
            cls = org.erasmusmc.webservice.biosemantics.MatchResultData.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:BioSemanticsIntf", "ProposedConceptDataArray");
            cachedSerQNames.add(qName);
            cls = org.erasmusmc.webservice.biosemantics.ProposedConceptData[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("urn:BioSemanticsIntf", "ProposedConceptData");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("urn:BioSemanticsIntf", "ACSNetworkDataArray");
            cachedSerQNames.add(qName);
            cls = org.erasmusmc.webservice.biosemantics.ACSNetworkData[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("urn:BioSemanticsIntf", "ACSNetworkData");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("urn:BioSemanticsIntf", "DocumentInfoDataArray");
            cachedSerQNames.add(qName);
            cls = org.erasmusmc.webservice.biosemantics.DocumentInfoData[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("urn:BioSemanticsIntf", "DocumentInfoData");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("urn:BioSemanticsIntf", "SemanticGroupDataArray");
            cachedSerQNames.add(qName);
            cls = org.erasmusmc.webservice.biosemantics.SemanticGroupData[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("urn:BioSemanticsIntf", "SemanticGroupData");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("urn:BioSemanticsIntf", "TermDataArray");
            cachedSerQNames.add(qName);
            cls = org.erasmusmc.webservice.biosemantics.TermData[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("urn:BioSemanticsIntf", "TermData");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("urn:BioSemanticsIntf", "InternetDatabaseDataArray");
            cachedSerQNames.add(qName);
            cls = org.erasmusmc.webservice.biosemantics.InternetDatabaseData[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("urn:BioSemanticsIntf", "InternetDatabaseData");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("urn:BioSemanticsIntf", "ConceptOccurrenceDataArray");
            cachedSerQNames.add(qName);
            cls = org.erasmusmc.webservice.biosemantics.ConceptOccurrenceData[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("urn:BioSemanticsIntf", "ConceptOccurrenceData");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("urn:BioSemanticsIntf", "MatchResultDataArray");
            cachedSerQNames.add(qName);
            cls = org.erasmusmc.webservice.biosemantics.MatchResultData[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("urn:BioSemanticsIntf", "MatchResultData");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("urn:BioSemanticsIntf", "EnrichProjectMemberInfoDataArray");
            cachedSerQNames.add(qName);
            cls = org.erasmusmc.webservice.biosemantics.EnrichProjectMemberInfoData[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("urn:BioSemanticsIntf", "EnrichProjectMemberInfoData");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("urn:BioSemanticsIntf", "ACSNetworkData");
            cachedSerQNames.add(qName);
            cls = org.erasmusmc.webservice.biosemantics.ACSNetworkData.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:BioSemanticsIntf", "DocumentInfoData");
            cachedSerQNames.add(qName);
            cls = org.erasmusmc.webservice.biosemantics.DocumentInfoData.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:BioSemanticsIntf", "SemanticTypeData");
            cachedSerQNames.add(qName);
            cls = org.erasmusmc.webservice.biosemantics.SemanticTypeData.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:BioSemanticsIntf", "EnrichProjectMemberInfoData");
            cachedSerQNames.add(qName);
            cls = org.erasmusmc.webservice.biosemantics.EnrichProjectMemberInfoData.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:StandardTypes", "IntegerArray");
            cachedSerQNames.add(qName);
            cls = int[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("urn:BioSemanticsIntf", "InternetDatabaseLinkData");
            cachedSerQNames.add(qName);
            cls = org.erasmusmc.webservice.biosemantics.InternetDatabaseLinkData.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:BioSemanticsIntf", "ConceptDataArray");
            cachedSerQNames.add(qName);
            cls = org.erasmusmc.webservice.biosemantics.ConceptData[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("urn:BioSemanticsIntf", "ConceptData");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("urn:BioSemanticsIntf", "TermData");
            cachedSerQNames.add(qName);
            cls = org.erasmusmc.webservice.biosemantics.TermData.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("urn:BioSemanticsIntf", "SemanticTypeDataArray");
            cachedSerQNames.add(qName);
            cls = org.erasmusmc.webservice.biosemantics.SemanticTypeData[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("urn:BioSemanticsIntf", "SemanticTypeData");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

    }

    protected org.apache.axis.client.Call createCall() throws java.rmi.RemoteException {
        try {
            org.apache.axis.client.Call _call =
                    (org.apache.axis.client.Call) super.service.createCall();
            if (super.maintainSessionSet) {
                _call.setMaintainSession(super.maintainSession);
            }
            if (super.cachedUsername != null) {
                _call.setUsername(super.cachedUsername);
            }
            if (super.cachedPassword != null) {
                _call.setPassword(super.cachedPassword);
            }
            if (super.cachedEndpoint != null) {
                _call.setTargetEndpointAddress(super.cachedEndpoint);
            }
            if (super.cachedTimeout != null) {
                _call.setTimeout(super.cachedTimeout);
            }
            if (super.cachedPortName != null) {
                _call.setPortName(super.cachedPortName);
            }
            java.util.Enumeration keys = super.cachedProperties.keys();
            while (keys.hasMoreElements()) {
                java.lang.String key = (java.lang.String) keys.nextElement();
                _call.setProperty(key, super.cachedProperties.get(key));
            }
            // All the type mapping information is registered
            // when the first call is made.
            // The type mapping information is actually registered in
            // the TypeMappingRegistry of the service, which
            // is the reason why registration is only needed for the first call.
            synchronized (this) {
                if (firstCall()) {
                    // must set encoding style before registering serializers
                    _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
                    _call.setEncodingStyle(org.apache.axis.Constants.URI_SOAP11_ENC);
                    for (int i = 0; i < cachedSerFactories.size(); ++i) {
                        java.lang.Class cls = (java.lang.Class) cachedSerClasses.get(i);
                        javax.xml.namespace.QName qName =
                                (javax.xml.namespace.QName) cachedSerQNames.get(i);
                        java.lang.Object x = cachedSerFactories.get(i);
                        if (x instanceof Class) {
                            java.lang.Class sf = (java.lang.Class)
                                 cachedSerFactories.get(i);
                            java.lang.Class df = (java.lang.Class)
                                 cachedDeserFactories.get(i);
                            _call.registerTypeMapping(cls, qName, sf, df, false);
                        }
                        else if (x instanceof javax.xml.rpc.encoding.SerializerFactory) {
                            org.apache.axis.encoding.SerializerFactory sf = (org.apache.axis.encoding.SerializerFactory)
                                 cachedSerFactories.get(i);
                            org.apache.axis.encoding.DeserializerFactory df = (org.apache.axis.encoding.DeserializerFactory)
                                 cachedDeserFactories.get(i);
                            _call.registerTypeMapping(cls, qName, sf, df, false);
                        }
                    }
                }
            }
            return _call;
        }
        catch (java.lang.Throwable _t) {
            throw new org.apache.axis.AxisFault("Failure trying to get the Call object", _t);
        }
    }

    @Override
	public java.lang.String testConnection() throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[0]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("urn:BioSemanticsIntf-BioSemanticsPort#TestConnection");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("urn:BioSemanticsIntf-BioSemanticsPort", "TestConnection"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (java.lang.String) _resp;
            } catch (java.lang.Exception _exception) {
                return (java.lang.String) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    @Override
	public java.lang.String[] getThesauri() throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[1]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("urn:BioSemanticsIntf-BioSemanticsPort#GetThesauri");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("urn:BioSemanticsIntf-BioSemanticsPort", "GetThesauri"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (java.lang.String[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (java.lang.String[]) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    @Override
	public boolean isThesaurusOnline(java.lang.String thesaurus) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[2]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("urn:BioSemanticsIntf-BioSemanticsPort#IsThesaurusOnline");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("urn:BioSemanticsIntf-BioSemanticsPort", "IsThesaurusOnline"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {thesaurus});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return ((java.lang.Boolean) _resp).booleanValue();
            } catch (java.lang.Exception _exception) {
                return ((java.lang.Boolean) org.apache.axis.utils.JavaUtils.convert(_resp, boolean.class)).booleanValue();
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    @Override
	public int[] searchConcepts(java.lang.String thesaurus, java.lang.String searchString) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[3]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("urn:BioSemanticsIntf-BioSemanticsPort#SearchConcepts");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("urn:BioSemanticsIntf-BioSemanticsPort", "SearchConcepts"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {thesaurus, searchString});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (int[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (int[]) org.apache.axis.utils.JavaUtils.convert(_resp, int[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    @Override
	public org.erasmusmc.webservice.biosemantics.SemanticGroupData[] getSemanticGroupData(java.lang.String thesaurus) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[4]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("urn:BioSemanticsIntf-BioSemanticsPort#GetSemanticGroupData");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("urn:BioSemanticsIntf-BioSemanticsPort", "GetSemanticGroupData"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {thesaurus});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.erasmusmc.webservice.biosemantics.SemanticGroupData[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.erasmusmc.webservice.biosemantics.SemanticGroupData[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.erasmusmc.webservice.biosemantics.SemanticGroupData[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    @Override
	public org.erasmusmc.webservice.biosemantics.SemanticTypeData[] getSemanticTypeData(java.lang.String thesaurus) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[5]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("urn:BioSemanticsIntf-BioSemanticsPort#GetSemanticTypeData");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("urn:BioSemanticsIntf-BioSemanticsPort", "GetSemanticTypeData"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {thesaurus});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.erasmusmc.webservice.biosemantics.SemanticTypeData[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.erasmusmc.webservice.biosemantics.SemanticTypeData[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.erasmusmc.webservice.biosemantics.SemanticTypeData[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    @Override
	public org.erasmusmc.webservice.biosemantics.InternetDatabaseData[] getInternetDatabaseData(java.lang.String thesaurus) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[6]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("urn:BioSemanticsIntf-BioSemanticsPort#GetInternetDatabaseData");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("urn:BioSemanticsIntf-BioSemanticsPort", "GetInternetDatabaseData"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {thesaurus});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.erasmusmc.webservice.biosemantics.InternetDatabaseData[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.erasmusmc.webservice.biosemantics.InternetDatabaseData[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.erasmusmc.webservice.biosemantics.InternetDatabaseData[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    @Override
	public org.erasmusmc.webservice.biosemantics.ConceptData[] getConceptData(java.lang.String thesaurus, int[] conceptIDs) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[7]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("urn:BioSemanticsIntf-BioSemanticsPort#GetConceptData");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("urn:BioSemanticsIntf-BioSemanticsPort", "GetConceptData"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {thesaurus, conceptIDs});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.erasmusmc.webservice.biosemantics.ConceptData[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.erasmusmc.webservice.biosemantics.ConceptData[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.erasmusmc.webservice.biosemantics.ConceptData[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    @Override
	public org.erasmusmc.webservice.biosemantics.InternetDatabaseLinkData[] getInternetDatabaseLinks(java.lang.String thesaurus, int[] conceptIDs) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[8]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("urn:BioSemanticsIntf-BioSemanticsPort#GetInternetDatabaseLinks");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("urn:BioSemanticsIntf-BioSemanticsPort", "GetInternetDatabaseLinks"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {thesaurus, conceptIDs});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.erasmusmc.webservice.biosemantics.InternetDatabaseLinkData[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.erasmusmc.webservice.biosemantics.InternetDatabaseLinkData[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.erasmusmc.webservice.biosemantics.InternetDatabaseLinkData[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    @Override
	public org.erasmusmc.webservice.biosemantics.EnrichProjectData[] getEnrichProjectsData(java.lang.String userName, java.lang.String password) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[9]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("urn:BioSemanticsIntf-BioSemanticsPort#GetEnrichProjectsData");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("urn:BioSemanticsIntf-BioSemanticsPort", "GetEnrichProjectsData"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {userName, password});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.erasmusmc.webservice.biosemantics.EnrichProjectData[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.erasmusmc.webservice.biosemantics.EnrichProjectData[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.erasmusmc.webservice.biosemantics.EnrichProjectData[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    @Override
	public org.erasmusmc.webservice.biosemantics.EnrichProjectMemberData getEnrichProjectMemberData(int projectID, java.lang.String userName, java.lang.String password) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[10]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("urn:BioSemanticsIntf-BioSemanticsPort#GetEnrichProjectMemberData");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("urn:BioSemanticsIntf-BioSemanticsPort", "GetEnrichProjectMemberData"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {new java.lang.Integer(projectID), userName, password});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.erasmusmc.webservice.biosemantics.EnrichProjectMemberData) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.erasmusmc.webservice.biosemantics.EnrichProjectMemberData) org.apache.axis.utils.JavaUtils.convert(_resp, org.erasmusmc.webservice.biosemantics.EnrichProjectMemberData.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    @Override
	public org.erasmusmc.webservice.biosemantics.EnrichProjectMemberInfoData[] getEnrichProjectMembers(org.erasmusmc.webservice.biosemantics.EnrichProjectMemberData memberData) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[11]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("urn:BioSemanticsIntf-BioSemanticsPort#GetEnrichProjectMembers");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("urn:BioSemanticsIntf-BioSemanticsPort", "GetEnrichProjectMembers"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {memberData});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.erasmusmc.webservice.biosemantics.EnrichProjectMemberInfoData[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.erasmusmc.webservice.biosemantics.EnrichProjectMemberInfoData[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.erasmusmc.webservice.biosemantics.EnrichProjectMemberInfoData[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    @Override
	public int newProposedConcept(org.erasmusmc.webservice.biosemantics.EnrichProjectMemberData memberData) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[12]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("urn:BioSemanticsIntf-BioSemanticsPort#NewProposedConcept");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("urn:BioSemanticsIntf-BioSemanticsPort", "NewProposedConcept"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {memberData});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return ((java.lang.Integer) _resp).intValue();
            } catch (java.lang.Exception _exception) {
                return ((java.lang.Integer) org.apache.axis.utils.JavaUtils.convert(_resp, int.class)).intValue();
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    @Override
	public void setProposedConceptData(org.erasmusmc.webservice.biosemantics.EnrichProjectMemberData memberData, org.erasmusmc.webservice.biosemantics.ProposedConceptData concept) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[13]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("urn:BioSemanticsIntf-BioSemanticsPort#SetProposedConceptData");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("urn:BioSemanticsIntf-BioSemanticsPort", "SetProposedConceptData"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {memberData, concept});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        extractAttachments(_call);
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    @Override
	public void removeProposedConcept(org.erasmusmc.webservice.biosemantics.EnrichProjectMemberData memberData, int conceptID) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[14]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("urn:BioSemanticsIntf-BioSemanticsPort#RemoveProposedConcept");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("urn:BioSemanticsIntf-BioSemanticsPort", "RemoveProposedConcept"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {memberData, new java.lang.Integer(conceptID)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        extractAttachments(_call);
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    @Override
	public int[] getProposedConcepts(org.erasmusmc.webservice.biosemantics.EnrichProjectMemberData memberData) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[15]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("urn:BioSemanticsIntf-BioSemanticsPort#GetProposedConcepts");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("urn:BioSemanticsIntf-BioSemanticsPort", "GetProposedConcepts"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {memberData});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (int[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (int[]) org.apache.axis.utils.JavaUtils.convert(_resp, int[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    @Override
	public int[] getProposedConceptsForMember(org.erasmusmc.webservice.biosemantics.EnrichProjectMemberData memberData, int memberID) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[16]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("urn:BioSemanticsIntf-BioSemanticsPort#GetProposedConceptsForMember");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("urn:BioSemanticsIntf-BioSemanticsPort", "GetProposedConceptsForMember"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {memberData, new java.lang.Integer(memberID)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (int[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (int[]) org.apache.axis.utils.JavaUtils.convert(_resp, int[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    @Override
	public org.erasmusmc.webservice.biosemantics.ProposedConceptData[] getProposedConceptData(org.erasmusmc.webservice.biosemantics.EnrichProjectMemberData memberData, int[] conceptIDs) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[17]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("urn:BioSemanticsIntf-BioSemanticsPort#GetProposedConceptData");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("urn:BioSemanticsIntf-BioSemanticsPort", "GetProposedConceptData"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {memberData, conceptIDs});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.erasmusmc.webservice.biosemantics.ProposedConceptData[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.erasmusmc.webservice.biosemantics.ProposedConceptData[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.erasmusmc.webservice.biosemantics.ProposedConceptData[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    @Override
	public int newTerm(org.erasmusmc.webservice.biosemantics.EnrichProjectMemberData memberData) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[18]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("urn:BioSemanticsIntf-BioSemanticsPort#NewTerm");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("urn:BioSemanticsIntf-BioSemanticsPort", "NewTerm"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {memberData});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return ((java.lang.Integer) _resp).intValue();
            } catch (java.lang.Exception _exception) {
                return ((java.lang.Integer) org.apache.axis.utils.JavaUtils.convert(_resp, int.class)).intValue();
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    @Override
	public void setTermData(org.erasmusmc.webservice.biosemantics.EnrichProjectMemberData memberData, org.erasmusmc.webservice.biosemantics.TermData term) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[19]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("urn:BioSemanticsIntf-BioSemanticsPort#SetTermData");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("urn:BioSemanticsIntf-BioSemanticsPort", "SetTermData"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {memberData, term});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        extractAttachments(_call);
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    @Override
	public void removeTerm(org.erasmusmc.webservice.biosemantics.EnrichProjectMemberData memberData, int termID) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[20]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("urn:BioSemanticsIntf-BioSemanticsPort#RemoveTerm");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("urn:BioSemanticsIntf-BioSemanticsPort", "RemoveTerm"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {memberData, new java.lang.Integer(termID)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        extractAttachments(_call);
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    @Override
	public int[] getTerms(org.erasmusmc.webservice.biosemantics.EnrichProjectMemberData memberData) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[21]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("urn:BioSemanticsIntf-BioSemanticsPort#GetTerms");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("urn:BioSemanticsIntf-BioSemanticsPort", "GetTerms"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {memberData});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (int[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (int[]) org.apache.axis.utils.JavaUtils.convert(_resp, int[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    @Override
	public int[] getTermsForMember(org.erasmusmc.webservice.biosemantics.EnrichProjectMemberData memberData, int memberID) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[22]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("urn:BioSemanticsIntf-BioSemanticsPort#GetTermsForMember");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("urn:BioSemanticsIntf-BioSemanticsPort", "GetTermsForMember"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {memberData, new java.lang.Integer(memberID)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (int[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (int[]) org.apache.axis.utils.JavaUtils.convert(_resp, int[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    @Override
	public org.erasmusmc.webservice.biosemantics.TermData[] getTermData(org.erasmusmc.webservice.biosemantics.EnrichProjectMemberData memberData, int[] termIDs) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[23]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("urn:BioSemanticsIntf-BioSemanticsPort#GetTermData");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("urn:BioSemanticsIntf-BioSemanticsPort", "GetTermData"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {memberData, termIDs});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.erasmusmc.webservice.biosemantics.TermData[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.erasmusmc.webservice.biosemantics.TermData[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.erasmusmc.webservice.biosemantics.TermData[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    @Override
	public java.lang.String getDocumentText(org.erasmusmc.webservice.biosemantics.EnrichProjectMemberData memberData, java.lang.String documentName) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[24]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("urn:BioSemanticsIntf-BioSemanticsPort#GetDocumentText");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("urn:BioSemanticsIntf-BioSemanticsPort", "GetDocumentText"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {memberData, documentName});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (java.lang.String) _resp;
            } catch (java.lang.Exception _exception) {
                return (java.lang.String) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    @Override
	public org.erasmusmc.webservice.biosemantics.ConceptOccurrenceData[] getDocumentOccurrenceData(org.erasmusmc.webservice.biosemantics.EnrichProjectMemberData memberData, java.lang.String documentName) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[25]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("urn:BioSemanticsIntf-BioSemanticsPort#GetDocumentOccurrenceData");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("urn:BioSemanticsIntf-BioSemanticsPort", "GetDocumentOccurrenceData"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {memberData, documentName});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.erasmusmc.webservice.biosemantics.ConceptOccurrenceData[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.erasmusmc.webservice.biosemantics.ConceptOccurrenceData[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.erasmusmc.webservice.biosemantics.ConceptOccurrenceData[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    @Override
	public org.erasmusmc.webservice.biosemantics.DocumentInfoData[] listDocuments(org.erasmusmc.webservice.biosemantics.EnrichProjectMemberData memberData) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[26]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("urn:BioSemanticsIntf-BioSemanticsPort#ListDocuments");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("urn:BioSemanticsIntf-BioSemanticsPort", "ListDocuments"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {memberData});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.erasmusmc.webservice.biosemantics.DocumentInfoData[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.erasmusmc.webservice.biosemantics.DocumentInfoData[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.erasmusmc.webservice.biosemantics.DocumentInfoData[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    @Override
	public void uploadDocument(org.erasmusmc.webservice.biosemantics.EnrichProjectMemberData memberData, java.lang.String documentName, byte[] data) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[27]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("urn:BioSemanticsIntf-BioSemanticsPort#UploadDocument");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("urn:BioSemanticsIntf-BioSemanticsPort", "UploadDocument"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {memberData, documentName, data});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        extractAttachments(_call);
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    @Override
	public org.erasmusmc.webservice.biosemantics.MatchResultData[] findDocuments(java.lang.String collexion, java.lang.String searchString) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[28]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("urn:BioSemanticsIntf-BioSemanticsPort#FindDocuments");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("urn:BioSemanticsIntf-BioSemanticsPort", "FindDocuments"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {collexion, searchString});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.erasmusmc.webservice.biosemantics.MatchResultData[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.erasmusmc.webservice.biosemantics.MatchResultData[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.erasmusmc.webservice.biosemantics.MatchResultData[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    @Override
	public org.erasmusmc.webservice.biosemantics.FingerprintItemData[] getFingerprintData(java.lang.String collexion, java.lang.String documentID) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[29]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("urn:BioSemanticsIntf-BioSemanticsPort#GetFingerprintData");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("urn:BioSemanticsIntf-BioSemanticsPort", "GetFingerprintData"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {collexion, documentID});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.erasmusmc.webservice.biosemantics.FingerprintItemData[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.erasmusmc.webservice.biosemantics.FingerprintItemData[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.erasmusmc.webservice.biosemantics.FingerprintItemData[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    @Override
	public void releaseProvisionalThesaurus(org.erasmusmc.webservice.biosemantics.EnrichProjectMemberData memberData) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[30]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("urn:BioSemanticsIntf-BioSemanticsPort#ReleaseProvisionalThesaurus");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("urn:BioSemanticsIntf-BioSemanticsPort", "ReleaseProvisionalThesaurus"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {memberData});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        extractAttachments(_call);
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    @Override
	public org.erasmusmc.webservice.biosemantics.ACSNetworkData[] getACSNetworks() throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[31]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("urn:BioSemanticsIntf-BioSemanticsPort#GetACSNetworks");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("urn:BioSemanticsIntf-BioSemanticsPort", "GetACSNetworks"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.erasmusmc.webservice.biosemantics.ACSNetworkData[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.erasmusmc.webservice.biosemantics.ACSNetworkData[]) org.apache.axis.utils.JavaUtils.convert(_resp, org.erasmusmc.webservice.biosemantics.ACSNetworkData[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    @Override
	public byte[] getACSNetworkPart(java.lang.String networkID, int offset, int bytes) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[32]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("urn:BioSemanticsIntf-BioSemanticsPort#GetACSNetworkPart");
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("urn:BioSemanticsIntf-BioSemanticsPort", "GetACSNetworkPart"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {networkID, new java.lang.Integer(offset), new java.lang.Integer(bytes)});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (byte[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (byte[]) org.apache.axis.utils.JavaUtils.convert(_resp, byte[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

}
