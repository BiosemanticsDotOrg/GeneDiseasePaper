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

package org.erasmusmc.ontology;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.erasmusmc.ids.DatabaseID;
import org.erasmusmc.ontology.Concept.DisambiguationType;
import org.erasmusmc.ontology.ontologyutilities.OntologyCurator;
import org.erasmusmc.utilities.ReadTextFile;
import org.erasmusmc.utilities.StringUtilities;
import org.erasmusmc.utilities.WriteTextFile;

public class OntologyFileLoader {
	public static boolean semanticTypesNegative = true;

	private boolean loadTermsOnly = false;
	private String version = "UNKNOWN";

	public static void main(String[] args) {
		String source = null;
		String target = null;

		try {
			if (args.length == 0) {
				throw new IllegalArgumentException();
			}

			source = args[0];
			target = source.replaceAll("\\.psf$", ".ontology");

			if (source.equals(target)) {
				throw new IllegalArgumentException();
			}
		} catch (IllegalArgumentException e) {
			System.out.println("Usage: some_file.psf");
			System.out.println("The output file some_file.ontology will be created.");
			System.exit(0);
		}

		String name = "UMLS";
		convertPSFToOntology(source, target, name, 3000000);
	}

	public static void convertPSFToOntology(String source, String target, String name, int minConceptID) {
		OntologyPSFLoader psfLoader = new OntologyPSFLoader();
		psfLoader.loadFromPSF(source);
		OntologyStore ontology = psfLoader.ontology;
		ontology.setName(name);
		OntologyCurator curator = new OntologyCurator();
		curator.curateAndPrepare(ontology);
		if (minConceptID != -1)
			for (Concept concept : ontology)
				if (concept.getID() > 0)
					if (concept.getID() < minConceptID)
						concept.setDisambiguationType(DisambiguationType.loose);
					else
						concept.setDisambiguationType(DisambiguationType.strict);

		OntologyFileLoader loader = new OntologyFileLoader();
		loader.save(ontology, target);
	}

	public static void convertOntologyFileToPsf(String source, String target, String name) {
		OntologyFileLoader loader = new OntologyFileLoader();
		OntologyStore ontology = loader.load(source);
		ontology.setName(name);
		OntologyCurator curator = new OntologyCurator();
		curator.curateAndPrepare(ontology);	
		boolean doVoc = true;
		boolean doSem = true;
		try {
			FileOutputStream PSFFile = new FileOutputStream(target);
			BufferedWriter bufferedWrite = new BufferedWriter(new OutputStreamWriter(PSFFile, "UTF-8"), 1000000);
			StringBuffer firstline = new StringBuffer();
			firstline.append("LEVEL|");
			if (doVoc) {
				firstline.append("VOC|");
			}
			if (doSem) {
				firstline.append("SEM|");
			}
			firstline.append("DEFAULT|0");
			bufferedWrite.write(firstline.toString());
			bufferedWrite.newLine();

			// Add concepts
			Iterator<Concept> values = ontology.getConceptIterator();
			while (values.hasNext()) {
				Concept concept = values.next();
				StringBuffer line = new StringBuffer();
				line.append(0 + "|");
				if (concept.ID < 0){
					line.append("|"+"|"+concept.getName());
				}else {

					if (doVoc) {
						line.append(relatedToString(ontology, concept.getID(), DefaultTypes.fromVocabulary) + "|");
					}
					if (doSem) {
						line.append(semrelatedToString(ontology, concept.getID(), DefaultTypes.isOfSemanticType) + "|");
					}

					if (concept.terms != null) {
						if (concept.terms.size() != 0) {
							line.append(StringUtilities.escape(concept.terms.get(0).text));
						}
						for (int j = 1; j < concept.terms.size(); j++) {
							line.append(";" + StringUtilities.escape(concept.terms.get(j).text));
						}
					}
					else {
						line.append(StringUtilities.escape(concept.getName()));
					}
					boolean hasDef = false;
					if (!((concept.definition == null) || concept.definition.equals(""))) {
						line.append("?" + StringUtilities.escape(concept.definition));
						hasDef = true;
					}
					// Append the database identifiers (if any)
					List<DatabaseID> databaseIDs = ontology.getDatabaseIDsForConcept(concept.getID());
					if (databaseIDs != null) {
						boolean first = true;
						for (DatabaseID databaseID: databaseIDs) {
							if (first && !hasDef) {
								first = false;
								line.append("?");
							} else
								line.append("\\;");
							line.append(databaseID.database);
							line.append("_");
							line.append(StringUtilities.escape(databaseID.ID));
						}
					}
				}
				line.append("|" + Integer.toString(concept.ID));
				bufferedWrite.write(line.toString());
				bufferedWrite.newLine();
			}

			// Add hierarchy:
			Iterator<Concept> conceptIterator = ontology.getConceptIterator();
			while (conceptIterator.hasNext()) {
				Concept concept = conceptIterator.next();
				List<Relation> relations = ontology.getRelationsForConceptAsSubject(concept.getID(), DefaultTypes.isParentOf);
				if (relations != null && relations.size() != 0) {
					StringBuffer line = new StringBuffer();
					line.append("H|");
					Iterator<Relation> relationIterator = relations.iterator();
					while (relationIterator.hasNext()) {
						Relation relation = relationIterator.next();
						line.append(relation.object);
						if (relationIterator.hasNext())
							line.append(";");
					}
					line.append("|");
					line.append(concept.getID());
					bufferedWrite.write(line.toString());
					bufferedWrite.newLine();
				}
			}
			bufferedWrite.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static String relatedToString(OntologyStore ontology,int conceptID, int relationType) {
		List<Relation> vocs = ontology.getRelationsForConceptAsSubject(conceptID, relationType);

		StringBuffer buffer = new StringBuffer();
		if (vocs.size() != 0)
			buffer.append(ontology.getConcept(vocs.get(0).object).getName());
		for (int i = 1; i < vocs.size(); i++) {
			buffer.append(";");
			buffer.append(ontology.getConcept(vocs.get(i).object).getName());
		}
		return buffer.toString();
	}
	private static String semrelatedToString(OntologyStore ontology,int conceptID, int relationType) {
		List<Relation> sems = ontology.getRelationsForConceptAsSubject(conceptID, relationType);

		StringBuffer buffer = new StringBuffer();
		if (sems.size() != 0 && ontology.getConcept(sems.get(0).object) == null)
			System.out.println(sems.get(0).toString());

		if (sems.size() != 0) {
			Integer id = ontology.getConcept(sems.get(0).object).getID();
			if (semanticTypesNegative)
				id = -id;
			buffer.append(id);
		}
		for (int i = 1; i < sems.size(); i++) {
			buffer.append(";");
			Integer id = ontology.getConcept(sems.get(i).object).getID();
			if (semanticTypesNegative)
				id = -id;
			buffer.append(id);
		}
		return buffer.toString();
	}

	public void setLoadTermsOnly(boolean value) {
		loadTermsOnly = value;
	}

	public boolean getLoadTermsOnly() {
		return loadTermsOnly;
	}

	public String getVersion() {
		return version;
	}

	public OntologyStore load(String filename) {
		File file = new File(filename);
		if (!file.exists()){
			System.err.println("File not found: " + filename);
			return null;
		}

		OntologyStore ontology = new OntologyStore();

		Iterator<String> iterator = new ReadTextFile(filename).iterator();
		loadHeader(iterator, ontology);
		loadBody(iterator, ontology);

		return ontology;
	}

	private void loadBody(Iterator<String> iterator, OntologyStore ontology) {
		Map<String, Integer> vocs = new HashMap<String, Integer>();
		Concept concept = new Concept(null);
		concept.setTerms(new ArrayList<TermStore>());
		String nameSpace = null;
		while (iterator.hasNext()) {
			String line = iterator.next();
			String key = extractKey(line);
			if (key != null) {
				if (key.equals("--")) {
					((ArrayList<TermStore>) concept.getTerms()).trimToSize();
					((ArrayList<DatabaseID>) ontology.getDatabaseIDsForConcept(concept.getID())).trimToSize();
					((ArrayList<Relation>) ontology.getRelationsForConceptAsSubject(concept.getID())).trimToSize();
					ontology.setConcept(concept);
					concept = new Concept(null);
					concept.setTerms(new ArrayList<TermStore>());
					nameSpace = null;
				} else {
					String value = extractValue(line);
					if (key.equals("NS")) {
						nameSpace = value;
					} else if (key.equals("ID")) {
						if (nameSpace != null && nameSpace.equals("SemType")) {
							concept.setID(-Integer.parseInt(value));
						} else if (nameSpace != null && nameSpace.equals("Voc")) {
							concept.setID(getVocID(vocs, ontology, value));
							concept.setName(value); // Dirty mapping for OntologyStore: store Voc Id in name field
						} else
							concept.setID(Integer.parseInt(value));
					} else if (key.equals("NA") && !loadTermsOnly) {
						if (nameSpace != null && nameSpace.equals("Voc"))
							// Dirty mapping for OntogyStore: store voc name in definition field
							concept.setDefinition(unescape(value));
						else
							concept.setName(unescape(value));
					} else if (key.equals("TM")) {
						concept.getTerms().add(parseTerm(value));
					} else if (key.equals("DF") && !loadTermsOnly) {
						concept.setDefinition(unescape(value));
					} else if (key.equals("DB") && !loadTermsOnly) {
						ontology.setDatabaseIDForConcept(concept.getID(), DatabaseID.parseString2DatabaseID(value));
					} else if (key.equals("ST") && !loadTermsOnly) {
						int semTypeID = -Integer.parseInt(value);
						ontology.setRelation(new Relation(concept.getID(), DefaultTypes.isOfSemanticType, semTypeID));
					} else if (key.equals("VO") && !loadTermsOnly) {
						int vocID = getVocID(vocs, ontology, value);
						ontology.setRelation(new Relation(concept.getID(), DefaultTypes.fromVocabulary, vocID));
					} else if (key.equals("PA") && !loadTermsOnly) {
						int parentID = Integer.parseInt(value);
						ontology.setRelation(new Relation(parentID, DefaultTypes.isParentOf, concept.getID()));
					} else if (key.equals("DI")) {
						if (value.toLowerCase().equals("st"))
							concept.setDisambiguationType(DisambiguationType.strict);
						else if (value.toLowerCase().equals("lo"))
							concept.setDisambiguationType(DisambiguationType.loose);
					}

				}
			}
		}
	}

	private void loadHeader(Iterator<String> iterator, OntologyStore ontology) {
		iterator.next();
		version = iterator.next().substring(3);
		ontology.setName(iterator.next().substring(3));
		iterator.next();
	}

	private int getVocID(Map<String, Integer> vocs, OntologyStore ontology2, String value) {
		Integer vocID = vocs.get(value);
		if (vocID == null) {
			vocID = -1000 - vocs.size();
			vocs.put(value, vocID);
			Concept concept = new Concept(vocID);
			concept.setName(value);
			ontology2.setConcept(concept);
		}
		return vocID;
	}

	private TermStore parseTerm(String value) {
		String[] cols = value.split("\t@");
		TermStore term = new TermStore(cols[0]);
		term.orderSensitive = true;
		term.caseSensitive = true;
		term.normalised = false;
		if (cols.length == 2) {
			if (cols[1].contains("ci"))
				term.caseSensitive = false;
			if (cols[1].contains("no"))
				term.normalised = true;
		}
		return term;
	}

	private String extractValue(String line) {
		return line.substring(3);
	}

	private String extractKey(String line) {
		if (line.length() < 2)
			return null;
		return line.substring(0, 2);
	}

	public void save(OntologyStore ontology, String filename) {
		WriteTextFile out = new WriteTextFile(filename);
		writeHeader(out, ontology);
		for (Concept concept : ontology)
			writeConcept(ontology, concept, out);
		out.close();
	}

	private void writeHeader(WriteTextFile out, OntologyStore ontology) {
		out.writeln("# ErasmusMC ontology file");
		out.writeln("VR 1.0");
		out.writeln("ON " + ontology.getName());
		out.writeln("--");
	}

	private void writeConcept(OntologyStore ontology, Concept concept, WriteTextFile out) {
		if (concept.getID() <= -1000) { // It is a Voc
			writeAsVoc(ontology, concept, out);
		} else if (concept.getID() < 0) { // It is a SemType
			writeAsSemanticType(ontology, concept, out);
		} else {
			writeAsRegular(ontology, concept, concept.getID().toString(), out);
		}
	}

	private void writeAsVoc(OntologyStore ontology, Concept concept, WriteTextFile out) {
		if (concept.getName().length() == 0) {
			return;
		}

		out.writeln("NS Voc");
		out.writeln("ID " + concept.getName());
		String definition = escape(concept.getDefinition());
		if (definition.length() != 0)
			out.writeln("NA " + definition);
		out.writeln("--");
	}

	private void writeAsSemanticType(OntologyStore ontology, Concept concept, WriteTextFile out) {
		out.writeln("NS SemType");
		writeAsRegular(ontology, concept, Integer.toString(-concept.getID()), out);
	}

	private void writeAsRegular(OntologyStore ontology, Concept concept, String conceptID, WriteTextFile out) {
		out.writeln("ID " + conceptID);
		if (concept.getName().length() > 0) {
			out.writeln("NA " + escape(concept.getName()));
		}
		for (TermStore term : concept.getTerms())
			out.writeln("TM " + escape(term.text) + termFlags(term));
		if (concept.getDefinition().length() != 0)
			out.writeln("DF " + escape(concept.getDefinition()));
		for (DatabaseID dbID : ontology.getDatabaseIDsForConcept(concept.getID()))
			out.writeln("DB " + dbID.toString());
		for (Relation relation : ontology.getRelationsForConceptAsSubject(concept.getID(),
				DefaultTypes.isOfSemanticType))
			out.writeln("ST " + (-relation.object));
		for (Relation relation : ontology.getRelationsForConceptAsSubject(concept.getID(), DefaultTypes.fromVocabulary))
			out.writeln("VO " + escape(ontology.getConcept(relation.object).getName()));
		for (Relation relation : ontology.getRelationsForConceptAsObject(concept.getID(), DefaultTypes.isParentOf))
			out.writeln("PA " + relation.subject);
		if (concept.getDisambiguationType() == DisambiguationType.loose)
			out.writeln("DI lo");
		else if (concept.getDisambiguationType() == DisambiguationType.strict)
			out.writeln("DI st");
		out.writeln("--");
	}

	private String termFlags(TermStore term) {
		StringBuilder sb = new StringBuilder();
		if (!term.caseSensitive)
			sb.append("\t@match=ci");
		if (term.normalised)
			if (sb.length() == 0)
				sb.append("\t@match=no");
			else
				sb.append(",no");
		return sb.toString();
	}

	private String escape(String text) {
		return text.replace("\t", "\\t").replace("\n", "\\n");
	}

	private String unescape(String text) {
		return text.replace("\\t", "\t").replace("\\n", "\n");
	}
}
