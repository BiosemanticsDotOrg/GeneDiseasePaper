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

package org.erasmusmc.dataimport.wikipedia;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.erasmusmc.ontology.Concept;
import org.erasmusmc.ontology.TermStore;
import org.erasmusmc.utilities.ReadTextFile;
import org.erasmusmc.utilities.StringUtilities;
import org.erasmusmc.utilities.WriteTextFile;

public class WikiOntology {
  public static String wikiFile = "/media/OS/home/data/Wikipedia/enwiki-latest-pages-articles.xml";
  public static String targetOntology = "/media/OS/home/public/thesauri/Wikipedia.psf";
  private Map<String, Data> title2data = new HashMap<String, Data>();
  private Pattern pattern = Pattern.compile("\\* ?\\[\\[");
	public static void main(String[] args) {
		new WikiOntology();
	}
	
	public WikiOntology(){	
		boolean firstTitle = true;
		boolean addEntry = true;
		boolean disambiguation = true;
		int cid = 0;
		String definition = "";
		List<String> terms = new ArrayList<String>();
		String title = "";
		int count = 0;
		for (String line : new ReadTextFile(wikiFile)){
			String trimLine = line.trim();
			String lcLine = trimLine.toLowerCase();
			if (lcLine.equals("<page>")){
				count++;
				if (count % 10000 == 0)
					System.out.println(count);
				//if (count == 250000)
				//	break;
				firstTitle = true;
				addEntry = true;
				disambiguation = false;
			} else if (firstTitle && lcLine.startsWith("<title>")){
				firstTitle = false;
				title = StringUtilities.findBetween(trimLine, "<title>", "</title>");
				if (title.endsWith("(disambiguation)")){
					disambiguation = true;
					addEntry = false;
					title = title.replace("(disambiguation)", "").trim();
				} else if (title.toLowerCase().startsWith("file:") || title.toLowerCase().startsWith("category:") || title.toLowerCase().startsWith("list of") || title.toLowerCase().startsWith("wikipedia:") || title.toLowerCase().startsWith("template:") || title.toLowerCase().startsWith("portal:"))
					addEntry = false;
			} else if (lcLine.contains("#redirect [[")){ 
				String redirect = StringUtilities.findBetween(trimLine, "[[", "]]");
				addTerm(redirect, title);
				addEntry = false;
			} else if (disambiguation && lcLine.startsWith("*") && pattern.matcher(lcLine).find()){
				String redirect = StringUtilities.findBetween(trimLine, "[[", "]]");
				if (redirect.length() != 0)
					addTerm(redirect, title);
			} else if (disambiguation && lcLine.contains("{{disambig}}")){
				disambiguation = false;
			} else if (addEntry && trimLine.equals("</page>")){
				addTerm(title, title);
				for (String term : terms)
					addTerm(title, term);
			}
		}
		
		dumpToFile();
	}
	
	private void dumpToFile() {
		WriteTextFile out = new WriteTextFile(targetOntology);
		int cid = 0;
		for (Map.Entry<String, Data> entry : title2data.entrySet()){
			out.writeln("0|"+StringUtilities.join(entry.getValue(), ";") + "|" + cid++);
		}
		out.close();
		
	}

	private void addTerm(String title, String term){
		Data data = title2data.get(title);
		if (data == null){
			data = new Data(1);
			title2data.put(title, data);
		}
		if (!data.contains(term))
		  data.add(term);
	}

	private Concept newConcept(int cid, String title, List<String> terms,	String definition) {
		List<TermStore> termStores = new ArrayList<TermStore>(terms.size());
		Set<String> seenTerms = new HashSet<String>(terms.size());
		for (String term : terms)
			if (seenTerms.add(term)){
				TermStore termStore = new TermStore(term);
				termStores.add(termStore);
			}
				
		Concept concept = new Concept(cid);
		concept.setTerms(termStores);
		concept.setDefinition(definition);
		return concept;
	}
	
	private class Data extends ArrayList<String>{
	  public Data(int size){
	  	super(size);
	  }
		String definition;		
	}

}
