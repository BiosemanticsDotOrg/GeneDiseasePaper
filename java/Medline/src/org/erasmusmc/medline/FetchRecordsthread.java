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

package org.erasmusmc.medline;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.erasmusmc.utilities.StringUtilities;

public class FetchRecordsthread extends MySQLthread{
  
  public List<Integer> pmids = new ArrayList<Integer>();
  
  public List<MedlineRecord> records;
  public List<Integer> unretrievedPmids = new ArrayList<Integer>();
  
  public boolean fetchTitleAndAbstract = true;
  public boolean fetchMesh = true;  
  public boolean fetchGeneSymbol = false;  
  public boolean fetchJournal = false;
  public boolean fetchJournalShortForm = false;
  public boolean fetchSubstances = false;
  public boolean fetchPublicationType = false;
  public boolean fetchPublicationDate = false;
  public boolean fetchAuthors = false;
  public boolean fetchLanguage = false;
  public boolean fetchAffiliation = false;
  public boolean fetchISSN = false;
  public boolean fetchVolumeIssuePages = false;
  
  public boolean generateUnretrievedPmidsList = true;
  public FetchRecordsthread(String server){
    super(server);
  }
  public FetchRecordsthread(String server, String database, String user, String password){
    super(server, database, user, password);
  }
  public FetchRecordsthread(){
    super();
  }
  protected void process(){
    String query = "(" + StringUtilities.join(pmids,",") +  ")";
    pmid2record.clear();
    unretrievedPmids.clear();
    records = new ArrayList<MedlineRecord>();
    ResultSet rs;
    StringBuffer baseQuery = new StringBuffer();
    baseQuery.append("select pmid");
    if (fetchTitleAndAbstract) baseQuery.append(",article_title");  
    if (fetchJournal) baseQuery.append(",journal_title");
    if (fetchJournalShortForm) baseQuery.append(",medline_ta");
    if (fetchPublicationDate) baseQuery.append(",pub_date_year, pub_date_month, pub_date_day, medline_date");
    if (fetchAffiliation) baseQuery.append(",article_affiliation");
    if (fetchISSN) baseQuery.append(",issn,issn_linking");
    if (fetchVolumeIssuePages) baseQuery.append(",volume,issue,start_page,end_page,medline_pgn");
    baseQuery.append(" from medline_citation where pmid in ");
    try {
      if (fetchTitleAndAbstract || fetchJournal || fetchJournalShortForm || fetchPublicationDate || fetchAffiliation || fetchISSN || fetchVolumeIssuePages) {
        rs = stmt.executeQuery(baseQuery.toString() + query);
        
        rs.beforeFirst();
        while (rs.next()){
          int fetchPMID = Integer.parseInt(rs.getString("pmid"));
          MedlineRecord fetchRecord = newRecord(fetchPMID);
          if (fetchTitleAndAbstract) {
            fetchRecord.title = rs.getString("article_title");
          }
          if (fetchJournal) fetchRecord.journal = rs.getString("journal_title");
          if (fetchJournalShortForm) fetchRecord.journalShortForm = rs.getString("medline_ta");
          if (fetchPublicationDate){
            fetchRecord.publicationDate = parseDate(rs.getString("pub_date_year"), rs.getString("pub_date_month"), rs.getString("pub_date_day"), rs.getString("medline_date"));
          }
          if (fetchAffiliation) fetchRecord.affiliation = rs.getString("article_affiliation");
          if (fetchISSN) {
          	fetchRecord.issn = rs.getString("issn");
          	fetchRecord.issnLinking = rs.getString("issn_linking");
          }
          if (fetchVolumeIssuePages){
          	fetchRecord.volume = rs.getString("volume");
          	fetchRecord.issue = rs.getString("issue");
          	fetchRecord.pages = parsePages(rs.getString("start_page"),rs.getString("end_page"),rs.getString("medline_pgn"));
          }
          	
        }
      }
    
      if (fetchMesh) {
        rs = stmt.executeQuery("select pmid,descriptor_name,descriptor_name_major_yn from medline_mesh_heading where pmid in "+query);
        rs.beforeFirst();
        while (rs.next()){
          int fetchPMID = Integer.parseInt(rs.getString("pmid"));
          MedlineRecord fetchRecord = pmid2record.get(fetchPMID);
          if (fetchRecord==null)
            fetchRecord = newRecord(fetchPMID);
          MeSHHeader meSHHeader = new MeSHHeader();
          meSHHeader.descriptor = rs.getString("descriptor_name");
          meSHHeader.descriptorMajor = rs.getBoolean("descriptor_name_major_yn");
          fetchRecord.meshHeaders.add(meSHHeader);
        }
        
        rs = stmt.executeQuery("select pmid,descriptor_name,qualifier_name,qualifier_name_major_yn from medline_mesh_heading_qualifier where pmid in "+query);
        rs.beforeFirst();
        while (rs.next()){
          int fetchPMID = Integer.parseInt(rs.getString("pmid"));
          MedlineRecord fetchRecord = pmid2record.get(fetchPMID);
          String descriptor = rs.getString("descriptor_name");
          for (MeSHHeader meSHHeader : fetchRecord.meshHeaders)
          	if (meSHHeader.descriptor.equals(descriptor)){
          		if (meSHHeader.qualifier != null){
          			MeSHHeader copy = new MeSHHeader();
          			copy.descriptor = meSHHeader.descriptor;
          			copy.descriptorMajor = meSHHeader.descriptorMajor;
          			fetchRecord.meshHeaders.add(copy);
          			meSHHeader = copy;
          		}
          		meSHHeader.qualifier = rs.getString("qualifier_name");
          		meSHHeader.qualifierMajor = rs.getBoolean("qualifier_name_major_yn");
          		break;
          	}          
        }
      }
      if (fetchPublicationType) {
        rs = stmt.executeQuery("select pmid,publication_type from medline_article_publication_type where pmid in "+query);
        rs.beforeFirst();
        while (rs.next()){
          int fetchPMID = Integer.parseInt(rs.getString("pmid"));
          MedlineRecord fetchRecord = pmid2record.get(fetchPMID);
          if (fetchRecord==null)
            fetchRecord = newRecord(fetchPMID);
          fetchRecord.publicationType.add(rs.getString("publication_type"));

        }
      }
      if (fetchSubstances) {
        rs = stmt.executeQuery("select pmid,name_of_substance from medline_chemical_list where pmid in "+query.toString());
        rs.beforeFirst();
        while (rs.next()){
          int fetchPMID = Integer.parseInt(rs.getString("pmid"));
          MedlineRecord fetchRecord = pmid2record.get(fetchPMID);
          if (fetchRecord==null)
            fetchRecord = newRecord(fetchPMID);
          fetchRecord.substances.add(rs.getString("name_of_substance"));
          
        }
      }
      if (fetchGeneSymbol) {
        rs = stmt.executeQuery("select pmid,gene_symbol from medline_gene_symbol_list where pmid in "+query.toString());
        rs.beforeFirst();
        while (rs.next()){
          int fetchPMID = Integer.parseInt(rs.getString("pmid"));
          MedlineRecord fetchRecord = pmid2record.get(fetchPMID);
          if (fetchRecord==null)
            fetchRecord = newRecord(fetchPMID);
          fetchRecord.geneSymbols.add(rs.getString("gene_symbol"));
        }
      }
      if (fetchAuthors) {
        rs = stmt.executeQuery("select pmid,author_order,last_name,initials,collective_name from medline_author where pmid in "+query.toString());
        rs.beforeFirst();
        while (rs.next()){
          int fetchPMID = Integer.parseInt(rs.getString("pmid"));
          MedlineRecord fetchRecord = pmid2record.get(fetchPMID);
          String lastName = rs.getString("last_name");
          String initials = rs.getString("initials");
          String collectiveName = rs.getString("collective_name");
          StringBuffer name = new StringBuffer();
          if (lastName != null){
            name.append(lastName);
            if (initials != null){
              name.append(" ");
              name.append(initials);
            }
          } else {
            if (collectiveName != null){
              name.append(collectiveName);
            }
          }
          if (name.length() != 0){
            if (fetchRecord==null)
              fetchRecord = newRecord(fetchPMID);   
            int author_order = rs.getInt("author_order");
            for (int i = fetchRecord.authors.size(); i <= author_order; i++)
              fetchRecord.authors.add(null);
            fetchRecord.authors.set(author_order,name.toString());
          }
        }
      }
      
      if (fetchLanguage) {
        rs = stmt.executeQuery("select pmid,language from medline_article_language where pmid in "+query.toString());
        rs.beforeFirst();
        while (rs.next()){
          int fetchPMID = Integer.parseInt(rs.getString("pmid"));
          MedlineRecord fetchRecord = pmid2record.get(fetchPMID);
          if (fetchRecord==null)
            fetchRecord = newRecord(fetchPMID);
          fetchRecord.language = rs.getString("language");
        }
      }
      
      if (fetchTitleAndAbstract){
        rs = stmt.executeQuery("select pmid,abstract_order,abstract_label,abstract_nlmcategory,abstract_text from medline_abstract where pmid in "+query.toString());
        rs.beforeFirst();
        while (rs.next()){
        	int fetchPMID = Integer.parseInt(rs.getString("pmid"));
        	MedlineRecord fetchRecord = pmid2record.get(fetchPMID);
        	if (fetchRecord==null)
        		fetchRecord = newRecord(fetchPMID);
        	//Add this to add section headers to abstracts:
        	/*else
        		if (fetchRecord.abstractText.length() != 0)
        			fetchRecord.abstractText += "\n\n";
        	
        	String label = rs.getString("abstract_label");
        	if (label == null){
        		String nlmcategory = rs.getString("abstract_label");
        		if (nlmcategory != null)
        			fetchRecord.abstractText += nlmcategory + "\n\n"; 
        	} else
        		fetchRecord.abstractText += label + "\n\n";
        		*/
          fetchRecord.abstractText += rs.getString("abstract_text");
        }
      }
      
    }catch( Exception e ) {
      e.printStackTrace();
    }
    if (generateUnretrievedPmidsList){
      for(Integer pmid:pmids){
        if(!pmid2record.containsKey(pmid)){
          unretrievedPmids.add(pmid);
        }
      }
    }
    
  }

  private String parsePages(String startPage, String endPage, String pages) {
		if (pages != null)
			return pages;
		
		if (startPage != null)
			if (endPage != null)
				return startPage+"-"+endPage;
			else
				return startPage;
		return null;
	}

	private List<String> months = getMonths();
  
  private static List<String> getMonths(){
    List<String> result = new ArrayList<String>(12);
    result.add("Jan");
    result.add("Feb");
    result.add("Mar");
    result.add("Apr");
    result.add("May");
    result.add("Jun");
    result.add("Jul");
    result.add("Aug");
    result.add("Sep");
    result.add("Oct");
    result.add("Nov");
    result.add("Dec");
    return result;
  }

  private Date parseDate(String yearString, String monthString, String dayString, String medlineString) {
    int year = 0;
    if (yearString == null){
      
      if (medlineString == null)
        return null;
      
      for (Integer i = 1975; i < 2100; i++)
        if (medlineString.contains(i.toString())){
          year = i;
          break;
        }
      if (year == 0)
        return null;
    } else {
      year = Integer.parseInt(yearString);
    }
    int month = 0;
    if (monthString == null){
      if (medlineString != null){
        for (int i = 0; i < months.size(); i++){
          if (medlineString.contains(months.get(i))){
            month = i;
            break;
          }
        }
      }
    } else {
      month = months.indexOf(monthString);
    }
    int day = dayString == null ? 1 : Integer.parseInt(dayString);
    return new GregorianCalendar(year, month, day).getTime();
  }
  private MedlineRecord newRecord(int fetchPMID) {
    MedlineRecord fetchRecord = new MedlineRecord(fetchPMID);
    records.add(fetchRecord);
    pmid2record.put(fetchPMID,fetchRecord);
    return fetchRecord;
  }



  private Map<Integer, MedlineRecord> pmid2record = new TreeMap<Integer, MedlineRecord>();
  
}
