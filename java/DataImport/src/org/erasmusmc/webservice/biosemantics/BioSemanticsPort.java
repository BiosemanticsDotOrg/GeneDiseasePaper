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
 * BioSemanticsPort.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.2RC3 Feb 28, 2005 (10:15:14 EST) WSDL2Java emitter.
 */

package org.erasmusmc.webservice.biosemantics;

public interface BioSemanticsPort extends java.rmi.Remote {
    public java.lang.String testConnection() throws java.rmi.RemoteException;
    public java.lang.String[] getThesauri() throws java.rmi.RemoteException;
    public boolean isThesaurusOnline(java.lang.String thesaurus) throws java.rmi.RemoteException;
    public int[] searchConcepts(java.lang.String thesaurus, java.lang.String searchString) throws java.rmi.RemoteException;
    public org.erasmusmc.webservice.biosemantics.SemanticGroupData[] getSemanticGroupData(java.lang.String thesaurus) throws java.rmi.RemoteException;
    public org.erasmusmc.webservice.biosemantics.SemanticTypeData[] getSemanticTypeData(java.lang.String thesaurus) throws java.rmi.RemoteException;
    public org.erasmusmc.webservice.biosemantics.InternetDatabaseData[] getInternetDatabaseData(java.lang.String thesaurus) throws java.rmi.RemoteException;
    public org.erasmusmc.webservice.biosemantics.ConceptData[] getConceptData(java.lang.String thesaurus, int[] conceptIDs) throws java.rmi.RemoteException;
    public org.erasmusmc.webservice.biosemantics.InternetDatabaseLinkData[] getInternetDatabaseLinks(java.lang.String thesaurus, int[] conceptIDs) throws java.rmi.RemoteException;
    public org.erasmusmc.webservice.biosemantics.EnrichProjectData[] getEnrichProjectsData(java.lang.String userName, java.lang.String password) throws java.rmi.RemoteException;
    public org.erasmusmc.webservice.biosemantics.EnrichProjectMemberData getEnrichProjectMemberData(int projectID, java.lang.String userName, java.lang.String password) throws java.rmi.RemoteException;
    public org.erasmusmc.webservice.biosemantics.EnrichProjectMemberInfoData[] getEnrichProjectMembers(org.erasmusmc.webservice.biosemantics.EnrichProjectMemberData memberData) throws java.rmi.RemoteException;
    public int newProposedConcept(org.erasmusmc.webservice.biosemantics.EnrichProjectMemberData memberData) throws java.rmi.RemoteException;
    public void setProposedConceptData(org.erasmusmc.webservice.biosemantics.EnrichProjectMemberData memberData, org.erasmusmc.webservice.biosemantics.ProposedConceptData concept) throws java.rmi.RemoteException;
    public void removeProposedConcept(org.erasmusmc.webservice.biosemantics.EnrichProjectMemberData memberData, int conceptID) throws java.rmi.RemoteException;
    public int[] getProposedConcepts(org.erasmusmc.webservice.biosemantics.EnrichProjectMemberData memberData) throws java.rmi.RemoteException;
    public int[] getProposedConceptsForMember(org.erasmusmc.webservice.biosemantics.EnrichProjectMemberData memberData, int memberID) throws java.rmi.RemoteException;
    public org.erasmusmc.webservice.biosemantics.ProposedConceptData[] getProposedConceptData(org.erasmusmc.webservice.biosemantics.EnrichProjectMemberData memberData, int[] conceptIDs) throws java.rmi.RemoteException;
    public int newTerm(org.erasmusmc.webservice.biosemantics.EnrichProjectMemberData memberData) throws java.rmi.RemoteException;
    public void setTermData(org.erasmusmc.webservice.biosemantics.EnrichProjectMemberData memberData, org.erasmusmc.webservice.biosemantics.TermData term) throws java.rmi.RemoteException;
    public void removeTerm(org.erasmusmc.webservice.biosemantics.EnrichProjectMemberData memberData, int termID) throws java.rmi.RemoteException;
    public int[] getTerms(org.erasmusmc.webservice.biosemantics.EnrichProjectMemberData memberData) throws java.rmi.RemoteException;
    public int[] getTermsForMember(org.erasmusmc.webservice.biosemantics.EnrichProjectMemberData memberData, int memberID) throws java.rmi.RemoteException;
    public org.erasmusmc.webservice.biosemantics.TermData[] getTermData(org.erasmusmc.webservice.biosemantics.EnrichProjectMemberData memberData, int[] termIDs) throws java.rmi.RemoteException;
    public java.lang.String getDocumentText(org.erasmusmc.webservice.biosemantics.EnrichProjectMemberData memberData, java.lang.String documentName) throws java.rmi.RemoteException;
    public org.erasmusmc.webservice.biosemantics.ConceptOccurrenceData[] getDocumentOccurrenceData(org.erasmusmc.webservice.biosemantics.EnrichProjectMemberData memberData, java.lang.String documentName) throws java.rmi.RemoteException;
    public org.erasmusmc.webservice.biosemantics.DocumentInfoData[] listDocuments(org.erasmusmc.webservice.biosemantics.EnrichProjectMemberData memberData) throws java.rmi.RemoteException;
    public void uploadDocument(org.erasmusmc.webservice.biosemantics.EnrichProjectMemberData memberData, java.lang.String documentName, byte[] data) throws java.rmi.RemoteException;
    public org.erasmusmc.webservice.biosemantics.MatchResultData[] findDocuments(java.lang.String collexion, java.lang.String searchString) throws java.rmi.RemoteException;
    public org.erasmusmc.webservice.biosemantics.FingerprintItemData[] getFingerprintData(java.lang.String collexion, java.lang.String documentID) throws java.rmi.RemoteException;
    public void releaseProvisionalThesaurus(org.erasmusmc.webservice.biosemantics.EnrichProjectMemberData memberData) throws java.rmi.RemoteException;
    public org.erasmusmc.webservice.biosemantics.ACSNetworkData[] getACSNetworks() throws java.rmi.RemoteException;
    public byte[] getACSNetworkPart(java.lang.String networkID, int offset, int bytes) throws java.rmi.RemoteException;
}
