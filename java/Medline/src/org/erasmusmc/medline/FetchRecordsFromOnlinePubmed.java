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
import java.util.Iterator;
import java.util.List;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.erasmusmc.utilities.StringUtilities;

public class FetchRecordsFromOnlinePubmed { 
  private static int batchSize = 100;
  private static long lastQueryTime = 0;
  private static int minWaitTime = 5000; //Wait for 5 seconds to do next query
  
  public static List<MedlineRecord> getRecords(List<Integer> pmids){
    List<MedlineRecord> result = new ArrayList<MedlineRecord>();
    int offset = 0;
    boolean done = false;
    do{
      List<Integer> subset = new ArrayList<Integer>(batchSize);
      if (offset >= pmids.size())
        done = true;
      else{
        subset.addAll(pmids.subList(offset, Math.min(offset+batchSize, pmids.size())));
        result.addAll(fetchSubset(subset));
        offset += batchSize;
      }  
    } while (!done);
    return result;
  }

  private static List<MedlineRecord> fetchSubset(List<Integer> pmids) {
    long timePassed = System.currentTimeMillis() - lastQueryTime;
    if (timePassed < minWaitTime){
      try {
        System.out.println("Waiting to send next query");
        Thread.sleep(minWaitTime - timePassed);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    
    StringBuffer url = new StringBuffer();
    url.append("http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=pubmed&mode=xml&id=");
    Iterator<Integer> pmidIterator = pmids.iterator();
    while (pmidIterator.hasNext()){
      url.append(pmidIterator.next().toString());
      if (pmidIterator.hasNext())
        url.append(",");
    }
    List<MedlineRecord> result = new ArrayList<MedlineRecord>();
    
    //System.out.println(url.toString());
    
    HttpClient client = new HttpClient();
    GetMethod method = new GetMethod(url.toString());
    method.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
    method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(3, false));
    
    method.addRequestHeader(
        "Content-Type", "application/x-www-form-urlencoded; charset=ISO-8559-1");
    
    try {
      int statusCode = client.executeMethod(method);

      if (statusCode != HttpStatus.SC_OK) {
        System.err.println("Method failed: " + method.getStatusLine());
      }
      
      String responseBody = method.getResponseBodyAsString();
      //System.out.println(responseBody);
      /*
      byte[] bytes = new byte[1024];
      StringBuffer response = new StringBuffer();
      int len;
      while ((len = responseBody.read(bytes)) != -1) {
        for (int i = 0; i < len; i++)
          response.append((char)bytes[i]);
      }
      responseBody.close();
      */
      String[] lines = responseBody.split("\n");
      MedlineRecord newRecord = null;
      String lastName = "";
      String firstName = "";
      String collectiveName = "";
      boolean primaryPMID = false;
      for (String line : lines){
        String trimLine = line.trim();
        if (trimLine.startsWith("<PubmedArticle>")){
          primaryPMID = true;
        } else if (trimLine.startsWith("<PMID>") && primaryPMID){
          newRecord = new MedlineRecord(Integer.parseInt(StringUtilities.findBetween(trimLine, "<PMID>", "</PMID>")));
          result.add(newRecord);
          primaryPMID = false;
        } else if (trimLine.startsWith("<ArticleTitle>")){
          newRecord.title = StringUtilities.findBetween(trimLine, "<ArticleTitle>", "</ArticleTitle>");
        } else if (trimLine.startsWith("<AbstractText>")){
          newRecord.abstractTexts.add(StringUtilities.findBetween(trimLine, "<AbstractText>", "</AbstractText>"));
          //there could be more than 1 abstractText in an pubmed record(like PMID: 9459395),
          //so just take the first abstractText as the main abstractText.
          if (newRecord.abstractTexts.size() == 1) {
            newRecord.abstractText = newRecord.abstractTexts.get(0);
          }          
        } else if (trimLine.startsWith("<Title>")){
          newRecord.journal = StringUtilities.findBetween(trimLine, "<Title>", "</Title>");
        } else if (trimLine.startsWith("<PublicationType>")){
          newRecord.publicationType.add(StringUtilities.findBetween(trimLine, "<PublicationType>", "</PublicationType>"));
        } else if (trimLine.startsWith("<MedlineTA>")){
          newRecord.journalShortForm = StringUtilities.findBetween(trimLine, "<MedlineTA>", "</MedlineTA>");
        } else if (trimLine.startsWith("<Language>")){
          newRecord.language = StringUtilities.findBetween(trimLine, "<Language>", "</Language>").toLowerCase();
        } else if (trimLine.startsWith("<LastName>")){
          lastName = StringUtilities.findBetween(trimLine, "<LastName>", "</LastName>");
        } else if (trimLine.startsWith("<ForeName>")){
          firstName = StringUtilities.findBetween(trimLine, "<ForeName>", "</ForeName>");
        } else if (trimLine.startsWith("<CollectiveName>")){
          collectiveName = StringUtilities.findBetween(trimLine, "<CollectiveName>", "</CollectiveName>");
        } else if (trimLine.startsWith("</Author>")){
          StringBuilder sb = new StringBuilder();
          if (!firstName.equals("")){
            sb.append(firstName.charAt(0));
            sb.append(". ");
          }  
          sb.append(lastName);
          if (sb.length() != 0)
            newRecord.authors.add(sb.toString());
          else
            newRecord.authors.add(collectiveName);
          lastName = "";
          firstName = "";
          collectiveName = "";
        } else if (trimLine.startsWith("<DescriptorName ")){
        	MeSHHeader header = new MeSHHeader();
        	header.descriptor = StringUtilities.findBetween(trimLine, ">", "</DescriptorName>");
          newRecord.meshHeaders.add(header);
        }
      }
    } catch (HttpException e) {
      System.err.println("Fatal protocol violation: " + e.getMessage());
      e.printStackTrace();
    } catch (IOException e) {
      System.err.println("Fatal transport error: " + e.getMessage());
      e.printStackTrace();
    } finally {
      method.releaseConnection();
    }
    lastQueryTime = System.currentTimeMillis();
    return result;
  }

}
