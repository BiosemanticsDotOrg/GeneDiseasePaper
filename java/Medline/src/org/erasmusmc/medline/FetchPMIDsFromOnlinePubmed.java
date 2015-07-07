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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.erasmusmc.utilities.StringUtilities;
import org.erasmusmc.utilities.WriteTextFile;

public class FetchPMIDsFromOnlinePubmed {
	
  public static int maxAttempts = 10;
  public static String response;  
  private static long lastQueryTime = 0;
  private static long extraDelay = 0;
  private static int minWaitTime = 1000; //Wait for 1 second to do next query
  public static int batchSize = 100000; //Seems like bug is finally fixed. (Temporarily set to 1000, but should be 100000 when NLM fixes bug in their service)
  public static int maxReturn = Integer.MAX_VALUE;
  public static void main(String[] args){
  	//Test:
  	minWaitTime = 1000;
  	batchSize = 100000;
  	savePMIDs("breast cancer", "/Users/mulligen/Desktop/resultPMIDS.txt","e.vanmulligen@erasmusmc.nl");
//  	List<Integer> pmids = getPMIDs("breast cancer", "m.schuemie@erasmusmc.nl");
//  	System.out.println(pmids.size());
//  	Set<Integer> uniquePMIDs = new HashSet<Integer>(pmids);
//  	System.out.println(uniquePMIDs.size());
  }
  
  /**
   * Helper function. Gets PMIDs from PubMed, then saves them to the file.
   * @param query
   * @param email
   * @param filename
   */
  public static void savePMIDs(String query, String filename, String email){
    List<Integer> pmids = getPMIDs(query, email);
    WriteTextFile out = new WriteTextFile(filename);
    for (Integer pmid : pmids)
    	out.writeln(pmid);
    out.close();
  }
  
  /**
   * 
   * @param query The Pubmed query (e.g. 'Schuemie MJ[Author]')
   * @return A list of PMIDs matching your searching criteria
   */
  public static List<Integer> getPMIDs(String query, String email){
    boolean done = false;
    List<Integer> pmids = new ArrayList<Integer>();
    while (!done){
    	String url = generateULR(pmids.size(), query, email);
      System.out.println("Sending query to Pubmed: " + url);
      String response = getHTML(url);
      done = parseResponse(response, pmids);
    }
    return pmids;
  }
  
  private static String generateULR(int offset, String query, String email) {
    StringBuffer url = new StringBuffer();
    url.append("http://eutils.ncbi.nlm.nih.gov/entrez/eutils/esearch.fcgi?retmax=");
    url.append(batchSize);
    if (offset != 0){
    	url.append("&retstart=");
    	url.append(offset);
    }
    url.append("&term=");
    url.append(query.replace(" ", "+"));
    url.append("&email=");
    url.append(email);
		return url.toString();
	}

	private static boolean parseResponse(String response, List<Integer> pmids) {
    String[] lines = response.split("\n");
    boolean ids = false;
    boolean haveCount = false;
    int count = 0;
    for (String line : lines){
    	
      if (line.contains("<OutputMessage>")){
        System.err.println(line.trim());
        return true;
      } else if (!haveCount && line.contains("<Count>")) {
      	count = Integer.parseInt(StringUtilities.findBetween(line, "<Count>", "</Count>"));
      	haveCount = true;
      } 
      if (ids){
        if (line.contains("</IdList>"))
          ids = false;
        else {
          String pmid = StringUtilities.findBetween(line, "<Id>", "</Id>");
          try {
            pmids.add(Integer.parseInt(pmid));
          } catch (NumberFormatException e){
            System.err.println(e.getMessage() + ", Problem parsing PMID: " + line);
            return true;
          }
        }
      }
      if (line.contains("<IdList>"))
        ids = true;
    }
    return (pmids.size() == count || pmids.size() >= maxReturn);
  }

  private static String getHTML(String url) {
    HttpClient client = new HttpClient();
    GetMethod method = new GetMethod(url);
    method.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
    method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));
    method.addRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=ISO-8559-1");
    response = "";
    
    try {
      int statusCode;
      int attempts = 0;
      do {
        checkWaitTime();
        statusCode = client.executeMethod(method);
        response = method.getResponseBodyAsString();  
        resetWaitTime();
        if (statusCode != HttpStatus.SC_OK) {
          System.err.println("Method failed: " + method.getStatusLine());
        } else if (response.contains("<ERROR>")){
            System.out.println(response);
            statusCode = HttpStatus.SC_PARTIAL_CONTENT;
            extraDelay = 60000; //wait minute extra
        }
        attempts++;
      } while (statusCode != HttpStatus.SC_OK && attempts <= maxAttempts);  
      if (attempts > maxAttempts)
      	System.err.println("Failed after " + attempts + " attempts on URL: " + url);
      
    } catch (HttpException e) {
      System.err.println("Fatal protocol violation: " + e.getMessage());
      e.printStackTrace();
    } catch (IOException e) {
      System.err.println("Fatal transport error: " + e.getMessage());
      e.printStackTrace();
    } finally {
      method.releaseConnection();
    }
    return response;
  }

  private static void resetWaitTime() {
    lastQueryTime = System.currentTimeMillis();
  }

  private static void checkWaitTime() {
    long timePassed = System.currentTimeMillis() - lastQueryTime;
    if (timePassed < minWaitTime + extraDelay){
      try {
        System.out.println("Waiting to send next query");
        Thread.sleep(minWaitTime + extraDelay - timePassed);
        extraDelay = 0;
      } catch (InterruptedException e) {
        e.printStackTrace();
      } 
    }   
  }
}