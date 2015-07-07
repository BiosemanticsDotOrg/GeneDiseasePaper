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
 * The main handler for the medline documents. Handles the top level node
 * <MedlineCitation>
 *
 * @author Ariel Schwartz
 * @author Gaurav Bhalotia
 *      
 */
package org.erasmusmc.dataimport.Medline.xmlparsers.medline;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.erasmusmc.dataimport.Medline.xmlparsers.GenericXMLParser;
import org.erasmusmc.dataimport.Medline.xmlparsers.NodeHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class MedlineCitation extends NodeHandler {

	private String pmid;
	private int authorPlace = 0;
	private static boolean isIncremental = true;
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

	/* Default constructor
	 *
	 * @param xmlFileName The name of the file that is being parsed, to be stored in the
	 *        record for medline_citation
	 */
	public MedlineCitation(String xmlFileName) throws Exception {

		ignoreDuplicateKeyError = true;

		/* The table in which the parsed entries are to be entered from this node */
		tableName = "medline_citation";

		/* The node name being handled by this class */
		xmlNodeName = "MedlineCitation";

		//Note: If anything is added to the MedlineCitation node, you have to change four things:
		//1. Add a field to the table in the database
		//2-4. Change the three arrays below

		/* The various column names in the table */
		String[] columnNameDef = { "pmid", "date_created", "date_completed", "date_revised", "issn", "volume", "issue", "pub_date_year", "pub_date_month", "pub_date_day", "pub_date_season", "medline_date", "journal_print_yn", "coden", "journal_title", "iso_abbreviation", "article_title", "start_page", "end_page", "medline_pgn", "abstract_text", "copyright_info", "article_affiliation", "article_author_list_comp_yn", "data_bank_list_comp_yn", "grantlist_complete_yn", "vernacular_title", "date_of_electronic_publication", "elec_pub_official_date_yn", "country", "medline_ta", "nlm_unique_id", "xml_file_name", "number_of_references", "keyword_list_owner", "citation_owner", "article_date_day", "article_date_month", "article_date_year", "cited_medium", "issn_type", "pub_model", "article_date_type", "citation_status", "elocationid", "elocationid_eidtype","elocationid_validyn","pub_date","issn_linking" };

		columnName = columnNameDef;

		/* The corresponding XML tags, The tags names starts from the current xmlnode */
		String[] xmlElementNameDef = { "MedlineCitation.PMID", "MedlineCitation.DateCreated", "MedlineCitation.DateCompleted", "MedlineCitation.DateRevised", "MedlineCitation.Article.Journal.ISSN", "MedlineCitation.Article.Journal.JournalIssue.Volume", "MedlineCitation.Article.Journal.JournalIssue.Issue", "MedlineCitation.Article.Journal.JournalIssue.PubDate.Year", "MedlineCitation.Article.Journal.JournalIssue.PubDate.Month", "MedlineCitation.Article.Journal.JournalIssue.PubDate.Day", "MedlineCitation.Article.Journal.JournalIssue.PubDate.Season", "MedlineCitation.Article.Journal.JournalIssue.PubDate.MedlineDate", "MedlineCitation.Article.Journal.JournalIssue.PrintYN", "MedlineCitation.Article.Journal.Coden", "MedlineCitation.Article.Journal.Title", "MedlineCitation.Article.Journal.ISOAbbreviation", "MedlineCitation.Article.ArticleTitle", "MedlineCitation.Article.Pagination.StartPage", "MedlineCitation.Article.Pagination.EndPage", "MedlineCitation.Article.Pagination.MedlinePgn", "MedlineCitation.Article.Abstract.AbstractText", "MedlineCitation.Article.Abstract.CopyrightInformation", "MedlineCitation.Article.Affiliation", "MedlineCitation.Article.AuthorList.CompleteYN", "MedlineCitation.Article.DataBankList.CompleteYN", "MedlineCitation.Article.GrantList.CompleteYN", "MedlineCitation.Article.VernacularTitle", "MedlineCitation.Article.ElectronicPubDate", "MedlineCitation.Article.ElectronicPubDate.OfficialDateYN", "MedlineCitation.MedlineJournalInfo.Country", "MedlineCitation.MedlineJournalInfo.MedlineTA", "MedlineCitation.MedlineJournalInfo.NlmUniqueID", "XmlFileName", "MedlineCitation.NumberOfReferences", "MedlineCitation.KeywordList.Owner", "MedlineCitation.Owner", "MedlineCitation.Article.ArticleDate.Day", "MedlineCitation.Article.ArticleDate.Month", "MedlineCitation.Article.ArticleDate.Year", "MedlineCitation.Article.Journal.JournalIssue.CitedMedium", "MedlineCitation.Article.Journal.ISSN.IssnType", "MedlineCitation.Article.PubModel", "MedlineCitation.Article.ArticleDate.DateType", "MedlineCitation.Status", "MedlineCitation.Article.ELocationID", "MedlineCitation.Article.ELocationID.EIdType","MedlineCitation.Article.ELocationID.ValidYN", "MedlineCitation.Article.Journal.JournalIssue.PubDate", "MedlineCitation.MedlineJournalInfo.ISSNLinking"};

		xmlElementName = xmlElementNameDef;

		/* The SQL types for the various columns above */
		int columnTypeDef[] = { Types.INTEGER, Types.DATE, Types.DATE, Types.DATE, Types.CHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.CHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.CLOB, Types.VARCHAR, Types.VARCHAR, Types.CHAR, Types.CHAR, Types.CHAR, Types.VARCHAR, Types.DATE, Types.CHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.INTEGER, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR};

		columnType = columnTypeDef;
		initialize();

		/* Add any data that does not come through XML to the hashtable */
		putColumnValue("XmlFileName", xmlFileName);
	}

	/**
	 * The method to handle the event when a new element is found, this is overwriting
	 * the method defined in the super class NodeHandler.java
	 */
	@Override
	public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {

		NodeHandler handler;

		String descriptorName = null;
		String majorTopicYN = null;

		/* Take decisions based on the element found, if it needs to be handled
		 * by a child handler then instantiate an object for the same and set the handler
		 * else call the handler from the super class
		 */
		try {
			if (currentElement != null) {
				if (pmid == null)
					pmid = getColumnValue("MedlineCitation.PMID");

				if (qName.equals("AuthorList")) {
					//DELETE Data for PMID on update if exists
					PreparedStatement pstmt_delete = GenericXMLParser.getDbConnection().prepareStatement("DELETE FROM medline_author where pmid = ?");
					pstmt_delete.setInt(1, Integer.parseInt(pmid));
					pstmt_delete.executeUpdate();
					pstmt_delete.close();
				}
				else if (qName.equals("ChemicalList")) {
//					DELETE Data for PMID on update if exists
					PreparedStatement pstmt_delete = GenericXMLParser.getDbConnection().prepareStatement("DELETE FROM medline_chemical_list where pmid = ?");
					pstmt_delete.setInt(1, Integer.parseInt(pmid));
					pstmt_delete.executeUpdate();
					pstmt_delete.close();
				}
				else if (qName.equals("DataBankList")) {
					//DELETE Data for PMID on update if exists
					PreparedStatement pstmt_delete = GenericXMLParser.getDbConnection().prepareStatement("DELETE FROM medline_data_bank where pmid = ?");
					pstmt_delete.setInt(1, Integer.parseInt(pmid));
					pstmt_delete.executeUpdate();
					pstmt_delete.close();
				}
				else if (qName.equals("GrantList")) {
					//DELETE Data for PMID on update if exists
					PreparedStatement pstmt_delete = GenericXMLParser.getDbConnection().prepareStatement("DELETE FROM medline_grant where pmid = ?");
					pstmt_delete.setInt(1, Integer.parseInt(pmid));
					pstmt_delete.executeUpdate();
					pstmt_delete.close();
				}
				else if (qName.equals("KeywordList")) {
					//DELETE Data for PMID on update if exists
					PreparedStatement pstmt_delete = GenericXMLParser.getDbConnection().prepareStatement("DELETE FROM medline_keyword_list where pmid = ?");
					pstmt_delete.setInt(1, Integer.parseInt(pmid));
					pstmt_delete.executeUpdate();
					pstmt_delete.close();
				}
				else if (qName.equals("MeshHeadingList")) {
					//DELETE Data for PMID on update if exists
					PreparedStatement pstmt_delete = GenericXMLParser.getDbConnection().prepareStatement("DELETE FROM medline_mesh_heading where pmid = ?");
					pstmt_delete.setInt(1, Integer.parseInt(pmid));
					pstmt_delete.executeUpdate();
					pstmt_delete.close();
				}
				else if (qName.equals("PersonalNameSubjectList")) {
					//DELETE Data for PMID on update if exists
					PreparedStatement pstmt_delete = GenericXMLParser.getDbConnection().prepareStatement("DELETE FROM medline_personal_name_subject where pmid = ?");
					pstmt_delete.setInt(1, Integer.parseInt(pmid));
					pstmt_delete.executeUpdate();
					pstmt_delete.close();
				}

				if (currentElement.equals("AuthorList") && qName.equals("Author")) {
					handler = new Author(pmid, authorPlace++);
					setContentHandler(handler, namespaceURI, localName, qName, atts);
				}
				else if (currentElement.equals("AccessionNumberList") && qName.equals("AccessionNumber")) {
					String dataBankName = getColumnValue("MedlineCitation.Article.DataBankList.DataBank.DataBankName");
					handler = new DataBank(pmid, dataBankName);
					setContentHandler(handler, namespaceURI, localName, qName, atts);
				}
				else if (currentElement.equals("ChemicalList") && qName.equals("Chemical")) {
					handler = new Chemical(pmid);
					setContentHandler(handler, namespaceURI, localName, qName, atts);
				}
				else if (currentElement.equals("GeneSymbolList") && qName.equals("GeneSymbol")) {
					handler = new GeneSymbol(pmid);
					setContentHandler(handler, namespaceURI, localName, qName, atts);
				}
				else if (currentElement.equals("KeywordList") && qName.equals("Keyword")) {
					handler = new Keyword(pmid);
					setContentHandler(handler, namespaceURI, localName, qName, atts);
				}
				else if (currentElement.equals("PublicationTypeList") && qName.equals("PublicationType")) {
					handler = new ArticlePublicationType(pmid);
					setContentHandler(handler, namespaceURI, localName, qName, atts);
				}
				else if (currentElement.equals("GrantList") && qName.equals("Grant")) {
					handler = new Grant(pmid);
					setContentHandler(handler, namespaceURI, localName, qName, atts);
				}
				else if (currentElement.equals("MeshHeadingList") && qName.equals("MeshHeading")) {
					handler = new MeshHeading(pmid);
					setContentHandler(handler, namespaceURI, localName, qName, atts);
				}
				else if (currentElement.equals("CommentsCorrectionsList")) {
					//DELETE Corrections for PMID on update if exists
					PreparedStatement pstmt_delete = GenericXMLParser.getDbConnection().prepareStatement("DELETE FROM medline_comments_corrections where pmid = ?");
					pstmt_delete.setInt(1, Integer.parseInt(pmid));
					pstmt_delete.executeUpdate();
					pstmt_delete.close();

					handler = new CommentsCorrections(pmid, qName);
					setContentHandler(handler, namespaceURI, localName, qName, atts);
				}
				else if (qName.equals("CitationSubset")) {
					handler = new CitationSubset(pmid);
					setContentHandler(handler, namespaceURI, localName, qName, atts);
				}
				else if (qName.equals("Language")) {
					handler = new ArticleLanguage(pmid);
					setContentHandler(handler, namespaceURI, localName, qName, atts);
				}
				else if (currentElement.equals("PersonalNameSubjectList") && qName.equals("PersonalNameSubject")) {
					handler = new PersonalNameSubject(pmid);
					setContentHandler(handler, namespaceURI, localName, qName, atts);
				}
				else if (qName.equals("OtherID")) {
					handler = new OtherID(pmid);
					setContentHandler(handler, namespaceURI, localName, qName, atts);
				}
				else if (qName.equals("OtherAbstract")) {
					handler = new OtherAbstract(pmid);
					setContentHandler(handler, namespaceURI, localName, qName, atts);
				}
				else if (qName.equals("SpaceFlightMission")) {
					handler = new SpaceFlightMission(pmid);
					setContentHandler(handler, namespaceURI, localName, qName, atts);
				}
				else if (currentElement.equals("InvestigatorList") && qName.equals("Investigator")) {
					handler = new Investigator(pmid);
					setContentHandler(handler, namespaceURI, localName, qName, atts);
				}
				else if (qName.equals("GeneralNote")) {
					handler = new GeneralNote(pmid);
					setContentHandler(handler, namespaceURI, localName, qName, atts);
				}
				else {
					super.startElement(namespaceURI, localName, qName, atts);
				}
			}
			else {
				super.startElement(namespaceURI, localName, qName, atts);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new SAXException("Problem creating Child. PMID: " + pmid + " for element " + qName);
		}
	}

	/* Extends the endElement method from the super class NodeHandler
	 * checks if a medline citation has ended, in which case flushes the
	 * values found including all the childhandler to the database
	 */
	@Override
	public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
		if (qName.equals("PubDate"))
			storePubDate();
		super.endElement(namespaceURI, localName, qName);
		if (qName.equals(xmlNodeName)) {
			try {
				updateDB();
			} catch (Exception e) {
				e.printStackTrace();
				throw new SAXException("problem updating the database");
			}
		}
	}

	//Publication date is stored in separate table in Date format for large queries:
	private void storePubDate() {
		String yearString = (String)columnValues.get("MedlineCitation.Article.Journal.JournalIssue.PubDate.Year");
		String monthString = (String)columnValues.get("MedlineCitation.Article.Journal.JournalIssue.PubDate.Month");
		String dayString = (String)columnValues.get("MedlineCitation.Article.Journal.JournalIssue.PubDate.Day");
		String medlineString = (String)columnValues.get("MedlineCitation.Article.Journal.JournalIssue.PubDate.MedlineDate");
		String date = parseDate(yearString, monthString, dayString, medlineString);		
		if (date == null)
			System.err.println("No valid publication date for PMID " + pmid);
		else {
			columnValues.put("MedlineCitation.Article.Journal.JournalIssue.PubDate", date);
		}
		/*String sql = "REPLACE INTO pmid_date (pmid,pub_date) VALUES (" + pmid + ",\"" + date + "\")"; 
    	try {
    		PreparedStatement pstmt_insert = GenericXMLParser.getDbConnection().prepareStatement(sql);
    		pstmt_insert.executeUpdate();
    		pstmt_insert.close();
    	} catch (SQLException e) {
    		System.err.println("ERROR IN SQL: " + sql);
    		e.printStackTrace();
    	}
    }*/
	}

    private static Pattern yearPattern = Pattern.compile("(19|20)[0-9][0-9]");
    
	private String parseDate(String yearString, String monthString, String dayString, String medlineString) {
		String year = null;
		if (yearString == null){
			if (medlineString == null)
				return null;
			
			Matcher matcher = yearPattern.matcher(medlineString);
			if (matcher.find())
				year = matcher.group();
		} else {
			year = yearString;
		}
		String month = null;
		if (monthString == null){
			month = "1";
			if (medlineString != null){
				for (int i = 0; i < months.size(); i++){
					if (medlineString.contains(months.get(i))){
						month = Integer.toString(i+1);
						break;
					}
				}
			} 
		} else {
			month = Integer.toString(months.indexOf(monthString)+1).toString();
		}
		String day = dayString == null ? "1" : dayString;
		return year + "-" + month + "-"+ day;
	}

	/**
	 * Handles SQLException. Should be overloaded by inheriting classes to handle special cases
	 * @returns true if the exception has been handled, false otherwise 
	 */
	@Override
	protected boolean handleSQLException(SQLException e) {
		if (ignoreDuplicateKeyError && e.getErrorCode() == DB2_DUPLICATE_ERROR) {

			if (isIncremental) {
				try {
					PreparedStatement pstmt_delete = GenericXMLParser.getDbConnection().prepareStatement("DELETE FROM medline_citation where pmid = ?");
					pstmt_delete.setInt(1, Integer.parseInt(pmid));
					pstmt_delete.executeUpdate();
					pstmt_delete.close();
					/* Execute the insert again. Note this could cause problems in multithreaded implementation */
					pstmt.executeUpdate();
					return true;
				} catch (SQLException e1) {
					/* Doesn't work again so give up and report */
					MedlineParser.eCount++;
					if (MedlineParser.eCount % 500 == 0) {
						System.out.println("Total " + MedlineParser.eCount + " Values not inserted");
					}
					updateChildren = false;
					return true;
				}
			}
			else {
				MedlineParser.eCount++;
				if (MedlineParser.eCount % 500 == 0) {
					System.out.println("Total " + MedlineParser.eCount + " Values not inserted");
				}
				updateChildren = false;
				return true;
				/* Don't do anything, the tuple for this primary key has already been inserted */
			}
		}
		else {
			System.err.println("ERROR CODE == " + e.getErrorCode());
			return false;
		}
	}
}
