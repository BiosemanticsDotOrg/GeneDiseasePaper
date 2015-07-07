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

package org.erasmusmc.ids;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class DatabaseID implements Comparable<DatabaseID>, Serializable {
  private static final long serialVersionUID = 8383407194950741559L;
  public String database = "";
  public String ID = "";

  /**
   * Constructor
   * @param DatabaseID  The identifier for the database. For example: "SP" is often used for SwissProt
   * @param ID  The identifier of the concept in the given database. For instance, P12345
   */
  public DatabaseID(String DatabaseID, String ID){
    this.database = DatabaseID;
    this.ID = ID;
  }

  public static DatabaseID parseString2DatabaseID(String string){
  	int index = string.indexOf("_");
    if (index != -1 && string.length() > index+1)
      return new DatabaseID(string.substring(0,index), string.substring(index+1));
    throw new RuntimeException("Error parsing database ID string: " + string);
  }

  public int hashCode() {
    return database.concat(ID).hashCode();
  }

  public int compareTo(DatabaseID arg) {
    return database.concat(ID).compareTo(arg.database.concat(arg.ID));
  }

  public boolean equals(Object arg0) {
    DatabaseID arg = (DatabaseID) arg0;
    return (arg.ID.equals(ID) && arg.database.equals(database));
  }

  /** 
   * Returns a URL to the website with additional information about the concept for a variety of databases
   * @return
   */
  public String getURL(){
    if (database.equals("PMID"))
      return "http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?db=pubmed&cmd=Retrieve&dopt=AbstractPlus&list_uids=" + ID + "&query_hl=1&itool=pubmed_docsum";
    if (database.equals("SP") || database.equals("UP"))
      return "http://ca.expasy.org/uniprot/" + ID;
    if (database.equals("LL"))
      return "http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?db=gene&cmd=Retrieve&dopt=full_report&list_uids=" + ID;
    if (database.equals("EG"))
      return "http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?db=gene&cmd=Retrieve&dopt=full_report&list_uids=" + ID;
    if (database.equals("OM"))
      return "http://www.ncbi.nlm.nih.gov/entrez/dispomim.cgi?id=" + ID;
    if (database.equals("FB"))
      return "http://www.flybase.org/reports/" + ID + ".html";
    if (database.equals("MGI"))
      return "http://www.informatics.jax.org/searches/accession_report.cgi?id=MGI:" + ID;
    if (database.equals("RGD"))
      return "http://rgd.mcw.edu/tools/genes/genes_view.cgi?id=RGD:" + ID;
    if (database.equals("HG"))
      //return "http://www.gene.ucl.ac.uk/nomenclature/data/get_data.php?hgnc_id=" + ID;
      return "http://www.genenames.org/data/hgnc_data.php?hgnc_id=" + ID;
    if (database.equals("UG"))
      return "http://www.ncbi.nlm.nih.gov/UniGene/clust.cgi?ORG=" + ID.substring(0, 2) + "&CID=" + ID.substring(3, ID.length());
    if (database.equals("RQ"))
      return "http://www.ncbi.nlm.nih.gov/entrez/viewer.fcgi?val=" + ID;
    if (database.equals("GD"))
      return "http://www.gdb.org/gdb-bin/genera/genera/hgd/DBObject/GDB:" + ID;
    if (database.equals("GO"))
      return "http://www.godatabase.org/cgi-bin/amigo/go.cgi?view=details&search_constraint=terms&depth=0&query=" + ID;
    if (database.equals("HO"))
      return "http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&db=homologene&dopt=HomoloGene&list_uids=" + ID;
    if (database.equals("SGD"))
      return "http://db.yeastgenome.org/cgi-bin/locus.pl?dbid=" + ID;
    if (database.equals("KEGG"))
      return "http://www.genome.jp/dbget-bin/www_bget?cpd:" + ID;
    if (database.equals("KEGD"))
      return "http://www.genome.jp/dbget-bin/www_bget?drug:" + ID;
    if (database.equals("WB"))
      return "http://www.wormbase.org/db/gene/gene?name=" + ID;
    if (database.equals("RM"))
      return "http://www.ratmap.org/ShowSingleLocus.htm?accno=" + ID;
    if (database.equals("CHID"))
      return "http://chem.sis.nlm.nih.gov/chemidplus/ProxyServlet?objectHandle=DBMaint&actionHandle=default&nextPage=jsp/chemidlite/ResultScreen.jsp&TXTSUPERLISTID=" + ID;
    if (database.equals("CHEB"))
      return "http://www.ebi.ac.uk/chebi/searchFreeText.do?searchString=" + ID;
    if (database.equals("CAS"))
      return "http://www.ncbi.nlm.nih.gov/portal/query.fcgi?CMD=search&DB=pccompound&term=" + ID;
    if (database.equals("PUBC"))
      return "http://www.ncbi.nlm.nih.gov/portal/query.fcgi?CMD=search&DB=pccompound&term=" + ID;
    if (database.equals("PUBS"))
      return "http://www.ncbi.nlm.nih.gov/portal/query.fcgi?CMD=search&DB=pcsubstance&term=" + ID;
    if (database.equals("INCH"))
      return "http://www.ncbi.nlm.nih.gov/portal/query.fcgi?CMD=search&DB=pccompound&term=" + ID;
    if (database.equals("DRUG"))
      return "http://www.drugbank.ca/search/search?query=drugbank_id:+" + ID;
    if (database.equals("HMDB"))
      return "http://hmp.biology.ualberta.ca/~knox/hmdb/metabolites/"+ID;
    if (database.equals("WIKI"))
        return "http://conceptwiki.org/index.php/Concept:"+ID;
    return "";
  } 

  /**
   * Returns the full name of the database of this database identifier
   * @return
   */
  public String getDatabaseName(){
    return enumerateDatabases().get(database);
  }

  /**
   * Returns a list of all known databases
   * @return
   */
  public static Map<String, String> enumerateDatabases(){
    Map<String, String> result = new HashMap<String, String>();
    result.put("AF", "Affymetrix");
    result.put("ATC", "Anatomical Therapeutic Chemical classification");
    result.put("CAS", "Chemical Abstracts Service registry number");
    result.put("CHEB", "ChEBI");
    result.put("CHID", "ChemIDplus");
    result.put("DAILYMED", "DailyMed");
    result.put("DRUG", "Drug Bank");
    result.put("ECO", "EcoGene");
    result.put("EG", "Entrez-Gene");
    result.put("EMBL", "EMBL");
    result.put("EMC", "ErasmusMC internal thesaurus ID");
    result.put("EUADR_EVENT", "Events identified in the EU-ADR project");
    result.put("FB", "FlyBase");
    result.put("GD", "Human Genome Database");
    result.put("GO", "Gene Ontology");
    result.put("GOID", "Gene Ontology ID");
    result.put("GOTM", "Gene Ontology Term");
    result.put("HG", "HGNC");
    result.put("HMBD", "Human Metabolome Database");
    result.put("HO", "Homologene");
    result.put("ICD9CM", "ICD9-CM");
    result.put("INCH", "IUPAC International Chemical Identifier");
    result.put("KEGD", "KEGD");
    result.put("KEGG", "KEGG");
    result.put("LL", "Entrez-Gene");
    result.put("MESH", "Medical Subject Headings");
    result.put("MGI", "Mouse Genome Database");
    result.put("OLN", "Ordered Locus Name");
    result.put("OM", "OMIM");
    result.put("PMID", "Pubmed");
    result.put("PUBC", "PubChem Compound");
    result.put("PUBS", "PubChem Substance");
    result.put("RGD", "Rat Genome Database");
    result.put("RM", "RatMap");
    result.put("RQ", "RefSeq");
    result.put("SGD", "Saccharomyces Genome Database");
    result.put("SMILE", "Simplified Molecular Input Line Entry");
    result.put("SP", "Swiss-Prot");
    result.put("TAXON", "Taxonomy ID");
    result.put("UG", "UniGene");
    result.put("UMLS", "Unified Medical Language System");
    result.put("UP", "UniProt");
    result.put("WIKI", "ConceptWiki");
    result.put("WB", "WormBase");
    result.put("ZFIN", "ZFIN");
    return result;
  }

  public String toString() {
    return database + "_" + ID;
  }

  private void writeObject(java.io.ObjectOutputStream stream) throws IOException {
    stream.writeObject(database);
    stream.writeObject(ID);
  }

  private void readObject(java.io.ObjectInputStream stream) throws IOException, ClassNotFoundException {
    database = (String)stream.readObject();
    ID       = (String)stream.readObject();
  }
}