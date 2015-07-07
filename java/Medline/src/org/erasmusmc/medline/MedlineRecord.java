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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.erasmusmc.utilities.StringUtilities;

public class MedlineRecord implements Serializable{
  public Integer pmid;
  public String title = "";
  public String abstractText = "";
  public List<String> abstractTexts = new ArrayList<String>(0);
  public List<MeSHHeader> meshHeaders = new ArrayList<MeSHHeader>(0);
  public List<String> substances = new ArrayList<String>(0);
  public List<String> geneSymbols = new ArrayList<String>(0);
  public List<String> authors = new ArrayList<String>(0);
  public String journal;
  public String journalShortForm;
  public String volume;
  public String issue;
  public String pages;
  public String language;
  public String affiliation;
  public Date publicationDate;
  public List<String> publicationType = new ArrayList<String>(0);
  public String issn;
  public String issnLinking;
  public String titleAbsMesh(){
  	StringBuilder string = new StringBuilder();
    string.append(title);
    string.append("\n");
    string.append(abstractText);
    for (int i = 0; i < meshHeaders.size(); i++){
      string.append("\n");
      string.append(meshHeaders.get(i).descriptor);
    }
    return string.toString();
  }
  public String titleAbs(){
    StringBuilder string = new StringBuilder();
    string.append(title);
    if (abstractText != null){
    	string.append("\n");
    	string.append(abstractText);
    }
    return string.toString();
  }
  public String MeshSub(){
  	StringBuilder string = new StringBuilder();
    Set<String> strings = new HashSet<String>((int) Math.round(3d/2d*(meshHeaders.size() + substances.size())));
    for (MeSHHeader msh: meshHeaders)      strings.add(msh.descriptor);
    for (String sbs: substances)      strings.add(sbs);
    string.append("\n");
    string.append(StringUtilities.join(strings, "\n"));
    return string.toString();
  }
  public String titleAbsMeshSubs(){
  	StringBuilder string = new StringBuilder();
    if (title != null)
      string.append(title);
    string.append("\n");
    if (abstractText != null)
      string.append(abstractText);
    Set<String> strings = new HashSet<String>((int) Math.round(3d/2d*(meshHeaders.size() + substances.size())));
    for (MeSHHeader msh: meshHeaders)      strings.add(msh.descriptor);
    for (String sbs: substances)      strings.add(sbs);
    string.append("\n");
    string.append(StringUtilities.join(strings, "\n"));
    return string.toString();
  }
  public MedlineRecord(int pmid){
    this.pmid = pmid;
  }
  
  public String extractEMailFromAffiliation() {
    if (affiliation == null)
      return null;
    boolean atSign = false;
    boolean before = false;
    boolean after = false;
    int i = affiliation.length();
    while (i > 0){
      i--;
      char ch = affiliation.charAt(i);
      if (Character.isLetterOrDigit(ch)){
        if (atSign)
          before = true;
        else 
          after = true;
      } else if (ch == '@') {
        atSign = true;
      } else if (ch != '.'){
        break;
      }
    }
    if (atSign && before && after){
      if (affiliation.charAt(affiliation.length()-1) == '.')
        return affiliation.substring(i+1, affiliation.length()-1);
      else
        return affiliation.substring(i+1, affiliation.length());
    }
    return null;
  }
  
  public String findAuthor(String email) {
    if (authors.size() == 1)
      return authors.get(0);
    else {
      String emaillc = email.toLowerCase();
      String matchedOnInitial = null;
      for (int a = 0; a < authors.size(); a++){
        String author = authors.get(a);
        int i = 0;
        while (i < author.length()){
          char ch = author.charAt(i);
          if (!Character.isLetterOrDigit(ch) && ch != '-'){
            break; 
          }      
          i++;
        }
        if (i != 0){
          String authorlc = StringUtilities.replaceInternationalChars(author).toLowerCase();
          if (emaillc.contains(authorlc.substring(0,Math.min(i,4))))
            return author;
          if ((a == 0 || a == authors.size()-1) && i < author.length()-1) { //assume an inital follows
            if (emaillc.charAt(0) == authorlc.charAt(i+1))
              if (matchedOnInitial == null)
                matchedOnInitial = author;
              else 
                matchedOnInitial = null;
              
          }
        }
      }
      return matchedOnInitial;
    }
  }
  
  private static final long serialVersionUID = -8634246341450930138L;
}
