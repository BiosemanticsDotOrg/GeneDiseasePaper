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

package JochemBuilder.SharedCurationScripts;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.erasmusmc.medline.MedlineIterator;
import org.erasmusmc.medline.MedlineListener;
import org.erasmusmc.medline.MedlineRecord;
import org.erasmusmc.ontology.Ontology;
import org.erasmusmc.ontology.OntologyFileLoader;
import org.erasmusmc.peregrine.ConceptPeregrine;
import org.erasmusmc.peregrine.ReleasedTerm;
import org.erasmusmc.peregrine.ResultConcept;
import org.erasmusmc.peregrine.ResultTerm;
import org.erasmusmc.peregrine.UMLSGeneChemTokenizer;
import org.erasmusmc.utilities.StringUtilities;

public class ChemicalOntologyFrequenceCount   implements MedlineListener {
	public static String pmidsFile = "/home/public/PMIDs/Random100.000.PMIDs";
	public static String ontologyFile = "/home/khettne/Projects/Jochem/Jochem_V1_3.ontology";
	public static String outputFile = "/home/khettne/Projects/Jochem/freq_Jochem_V1_3.txt";

	public static void main(String[] args) {

		System.out.println("Starting script. " + StringUtilities.now());
		OntologyFileLoader loader = new OntologyFileLoader();
		Ontology ontology = loader.load(ontologyFile);
		new ChemicalOntologyFrequenceCount(ontology);
	}

	public ChemicalOntologyFrequenceCount(Ontology ontology) {
		indexer.tokenizer = new UMLSGeneChemTokenizer();
		indexer.setOntology(ontology);
		medlineIterator.fetchSubstances = true;
		medlineIterator.pmidsFile = pmidsFile;

		System.out.println("Releasing thesaurus. " + StringUtilities.now());
		indexer.destroyOntologyDuringRelease = false;
		indexer.release();

		System.out.println("Starting indexation cycles. " + StringUtilities.now());
		medlineIterator.iterate(this);

		System.out.println("Generating results. " + StringUtilities.now());
		generateResults(outputFile, ontology);

		System.out.println("Done. " + StringUtilities.now());
	}

	private void generateResults(String filename, Ontology ontology) {
		try {
			FileOutputStream PSFFile = new FileOutputStream(filename);
			BufferedWriter bufferedWrite = new BufferedWriter(new OutputStreamWriter(PSFFile), 1000000);
			try {
				for (Entry<ReleasedTerm, Count> entry: releasedTerm2Count.entrySet()) {
					ReleasedTerm term = entry.getKey();
					StringBuffer line = new StringBuffer();
					line.append(entry.getValue().count);
					line.append("\t");
					int id = term.conceptId[0];
					int tid = term.termId[0];
					line.append(ontology.getConcept(id).getTerms().get(tid).text);
					line.append("\t");
					for (int cid: term.conceptId) {
						line.append(cid);
						line.append(";");
					}
					bufferedWrite.write(line.toString());
					bufferedWrite.newLine();
				}
				bufferedWrite.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void processMedlineRecords(List<MedlineRecord> records) {
		for (int i = 0; i < records.size(); i++) {
			MedlineRecord currentRecord = records.get(i);
			indexer.index(currentRecord.titleAbsMeshSubs());
			for (ResultConcept concept: indexer.resultConcepts){
				List<ResultTerm> terms = concept.terms;
				for (ResultTerm term: terms) {
					Count count = releasedTerm2Count.get(term.term);
					if (count == null) {
						count = new Count();
						releasedTerm2Count.put(term.term, count);
					}
					count.count++;
				}
			}
		}
	}

	private class Count {
		int count = 0;
	}

	protected class ReleasedTermComparator implements Comparator<ReleasedTerm> {
		@Override
		public int compare(ReleasedTerm arg0, ReleasedTerm arg1) {
			int result = arg0.conceptId[0] - arg1.conceptId[0];
			if (result == 0)
				result = arg0.termId[0] - arg1.termId[0];
			return result;
		}
	}

	private Map<ReleasedTerm, Count> releasedTerm2Count = new TreeMap<ReleasedTerm, Count>(new ReleasedTermComparator());
	private MedlineIterator medlineIterator = new MedlineIterator();
	private ConceptPeregrine indexer = new ConceptPeregrine();

}
