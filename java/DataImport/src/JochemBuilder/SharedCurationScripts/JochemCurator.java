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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.erasmusmc.ontology.Concept;
import org.erasmusmc.ontology.Ontology;
import org.erasmusmc.ontology.TermStore;
import org.erasmusmc.peregrine.ChemicalSBDtokenizer;
import org.erasmusmc.utilities.StringUtilities;

public class JochemCurator {

	private static final String FOLDER_PATH = "/home/bhsingh/Code/workspace/erasmus/trunk/src/DataImport/src/JochemBuilder/SharedCurationScripts/";
	public static int minWordSize = 2;

	public static boolean kristinasChemicalShortTokenFilterRule(String term, Set<String> stopwordsForFiltering) {
		term = term.toLowerCase();
		ChemicalSBDtokenizer tokenizer = new ChemicalSBDtokenizer();
		tokenizer.tokenize(term);
		String tokenizedTerm = "";
		for (String token : tokenizer.tokens) {
			tokenizedTerm = tokenizedTerm.concat(token);
		}
		if (tokenizedTerm.length() < minWordSize || StringUtilities.isNumber(tokenizedTerm)
				|| StringUtilities.isRomanNumeral(tokenizedTerm.toUpperCase())
				|| stopwordsForFiltering.contains(tokenizedTerm)) {
			return true;
		}
		return false;
	}

	public static Pattern signpattern = Pattern.compile("#");

	public static boolean signfilter(String term) {
		if (signpattern.matcher(term).find()) {
			return true;
		} else
			return false;
	}

	public static Pattern mixturePattern = Pattern
			.compile("\\((\\d+):(\\d+)\\)|\\((\\d+):(\\d+):(\\d+)\\)|\\((\\d+):(\\d+):(\\d+):(\\d+)\\)|\\((\\d+):(\\d+):(\\d+):(\\d+):(\\d+)\\)|\\((\\d+)CI\\)|\\((\\d+)CI,(\\d+)CI\\)|\\((\\d+)CI,(\\d+)CI,(\\d+)CI\\)|\\((\\d+)CI,(\\d+)CI,(\\d+)CI,(\\d+)CI\\)");

	// public static Pattern mixturePattern = Pattern.compile("\\((\\d+):(\\d+)\\)");
	public static boolean mixturefilter(String term) {
		if (mixturePattern.matcher(term).find()) {
			return true;
		} else
			return false;
	}

	public static void removeDuplicateTerms(List<TermStore> terms) {
		Set<String> previousTerms = new HashSet<String>();
		Iterator<TermStore> iterator = terms.iterator();
		while (iterator.hasNext()) {
			TermStore term = iterator.next();
			if (previousTerms.contains(term.text)) {
				iterator.remove();
			} else {
				previousTerms.add(term.text);
			}
		}
	}

	public static final String allEndBracketsOrParenthesisNotGreedyPattern = "(\\s\\[[^]]*\\]$)|(\\s\\([^)]*\\)$)";
	public static Pattern allEndBracketsOrParenthesisNotGreedyPatternExp = Pattern
			.compile(allEndBracketsOrParenthesisNotGreedyPattern);

	public static Set<String> dictionaries = getDictionaryNamesForChemicals();

	public static String rewriteNameForDictionaries(String term) {
		boolean found = false;
		String rewritten = "";
		Pattern p = allEndBracketsOrParenthesisNotGreedyPatternExp;
		Matcher m = p.matcher(term);
		while (m.find()) {
			String match = m.group().substring(2, m.group().length() - 1).toLowerCase().trim();
			for (String dict : dictionaries) {
				if (match.equals(dict.toLowerCase()) || (match.contains(dict.toLowerCase()) && match.contains(":"))
						|| (match.contains(dict.toLowerCase()) && match.contains("/"))) {
					found = true;
				}
			}
			if (found) {
				rewritten = m.replaceAll("").trim();
			}
		}
		return rewritten;
	}

	public static String findAndRewriteParenthesesAndBracketsAtEndOfTermRule(String term) {
		boolean found = false;
		String rewritten = "";
		Pattern p = allEndBracketsOrParenthesisNotGreedyPatternExp;
		Matcher m = p.matcher(term);
		while (m.find()) {
			found = true;
		}
		if (found) {
			rewritten = m.replaceAll("").trim();
		}
		return rewritten;
	}

	public static final String BeilsteinPatternString = "(Beilstein Handbook Reference)";
	public static Pattern BeilsteinPattern = Pattern.compile(BeilsteinPatternString, Pattern.CASE_INSENSITIVE);

	public static boolean filterNameForBeilsteinPattern(String term) {
		if (BeilsteinPattern.matcher(term).find())
			return true;
		return false;
	}

	public static boolean findAndSuppressChemicalMisc(String term, String termsToRemove) {
		Set<String> miscTerms = getUndesiredTermsToFilterOut(termsToRemove);
		String lcTerm = term.toLowerCase();
		// if (miscTerms.contains(lcTerm) || lcTerm.contains(" venom ")) return true;
		if (miscTerms.contains(lcTerm))
			return true;
		else
			return false;
	}

	public static Set<Integer> miscConcepts = getUndesiredConceptsToFilterOut();

	public static boolean findAndSuppressChemicalMiscConcept(Concept concept) {
		if (miscConcepts.contains(concept.getID()))
			return true;
		else
			return false;
	}

	public static Set<String> pharmas = getPharmaceuticalCompanies();

	public static String rewriteNameForPharmas(String term) {
		boolean found = false;
		String rewritten = "";
		Pattern p = allEndBracketsOrParenthesisNotGreedyPatternExp;
		Matcher m = p.matcher(term);
		while (m.find()) {
			String match = m.group().substring(2, m.group().length() - 1).toLowerCase().trim();
			for (String miscString : pharmas) {
				if (match.equals(miscString.toLowerCase())) {
					found = true;
				}
			}
			if (found) {
				rewritten = m.replaceAll("").trim();
			}
		}
		return rewritten;
	}

	public static String rewriteNameForPattern(String term) {
		String rewritten = "";
		if (term.contains("&#124;")) {
			rewritten = term.substring(0, term.indexOf("&#124;"));
			return rewritten;
		}
		return rewritten;
	}

	public static Set<String> getDictionaryNamesForChemicals() {
		Set<String> dictionaryNames = new HashSet<String>();
		dictionaryNames.add("BAN");
		dictionaryNames.add("JAN");
		dictionaryNames.add("INN");
		dictionaryNames.add("USAN");
		dictionaryNames.add("USP");
		dictionaryNames.add("USP X");
		dictionaryNames.add("USP XXI");
		dictionaryNames.add("NF");
		dictionaryNames.add("NF X");
		dictionaryNames.add("NF XII");
		dictionaryNames.add("NF XIII");
		dictionaryNames.add("NF XIV");
		dictionaryNames.add("ISO");
		dictionaryNames.add("BSI");
		dictionaryNames.add("NND");
		dictionaryNames.add("ANSI");
		dictionaryNames.add("UN");
		dictionaryNames.add("RN");
		dictionaryNames.add("DCIT");
		dictionaryNames.add("DCF");
		dictionaryNames.add("IUPAC");
		dictionaryNames.add("ESA");
		dictionaryNames.add("JP");
		dictionaryNames.add("VAN");
		dictionaryNames.add("TN");
		dictionaryNames.add("JP15");
		return dictionaryNames;
	}

	public static Set<String> getUndesiredTermPartsToFilterOut() {
		Set<String> result = new HashSet<String>();
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
				JochemCurator.class.getResourceAsStream("termsWithinParentesesToRemove.txt")));
		try {
			while (bufferedReader.ready()) {
				result.add(bufferedReader.readLine().trim().toLowerCase());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static Set<String> getUndesiredTermsToFilterOut(String filename) {
		Set<String> result = new HashSet<String>();
		File file = new File(FOLDER_PATH + filename);
		LineIterator it = null;
		try {
			it = FileUtils.lineIterator(file);

			while (it.hasNext()) {
				result.add(it.next().trim().toLowerCase());
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			LineIterator.closeQuietly(it);
		}
		return result;
	}

	public static Set<Integer> getUndesiredConceptsToFilterOut() {
		Set<Integer> things = new HashSet<Integer>();
		// InputStreamReader(JochemCurator.class.getResourceAsStream("conceptsToRemove.txt")));
		File file = new File(FOLDER_PATH + "conceptsToRemove.txt");
		LineIterator it = null;
		try {
			it = FileUtils.lineIterator(file);
			while (it.hasNext()) {
				String conceptLine = it.next().trim();
				String[] conceptNumbers = conceptLine.split(";");
				for (String conceptNumber : conceptNumbers) {
					if (conceptNumber.length() != 0)
						things.add(Integer.parseInt(conceptNumber));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			LineIterator.closeQuietly(it);
		}
		return things;
	}

	public static void removeSuppressedConcepts(Ontology ontology) {
		Set<Integer> suppressedConcepts = getUndesiredConceptsToFilterOut();
		for (Integer id : suppressedConcepts) {
			if (ontology.getConcept(id) != null) {
				ontology.removeConcept(id);
			}
		}
	}

	public static Set<String> getPharmaceuticalCompanies() {
		Set<String> result = new HashSet<String>();
		// InputStreamReader(JochemCurator.class.getResourceAsStream("pharmaceuticalCompanies.txt")));
		File file = new File(FOLDER_PATH + "pharmaceuticalCompanies.txt");
		LineIterator it = null;
		try {
			it = FileUtils.lineIterator(file);
			while (it.hasNext()) {
				result.add(it.next().trim().toLowerCase());
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			LineIterator.closeQuietly(it);
		}
		return result;
	}

	public static Set<Integer> getAllChemicalSemanticTypes() {
		Set<Integer> result = new TreeSet<Integer>();
		result.add(-103);
		result.add(-104);
		result.add(-109);
		result.add(-114);
		result.add(-115);
		result.add(-116);
		result.add(-118);
		result.add(-119);
		result.add(-110);
		result.add(-111);
		result.add(-196);
		result.add(-197);
		result.add(-120);
		result.add(-121);
		result.add(-195);
		result.add(-122);
		result.add(-123);
		result.add(-124);
		result.add(-125);
		result.add(-126);
		result.add(-127);
		result.add(-129);
		result.add(-192);
		result.add(-130);
		result.add(-131);
		result.add(-200);
		return result;
	}

	public static Set<Integer> getUndesiredSemanticTypes() {
		Set<Integer> result = new TreeSet<Integer>();
		result.add(aminoacidPeptideOrProtein);
		result.add(enzyme);
		result.add(receptor);
		// result.add(immunologicFactor);
		result.add(chemicalViewedFunctionally);
		// result.add(chemicalViewedStructually);
		result.add(biomedOrDentalMaterial);
		result.add(virus);
		result.add(plant);
		result.add(chemical);
		result.add(food);
		result.add(cell);
		result.add(geneOrGenome);
		result.add(spatialConcept);
		result.add(environmentalEffectOfHumans);
		result.add(bodySubstance);
		result.add(clinicalDrug);
		result.add(medicalDevice);
		result.add(cellComponent);
		result.add(nucleotideSequence);
		result.add(biomedicalOccupationOrdiscipline);
		result.add(manufacturedObject);
		result.add(bodyPartOrganOrOrganComponent);
		result.add(aminoAcidSequence);
		result.add(classification);
		result.add(drugDeliveryDevice);
		result.add(tissue);
		result.add(bacterium);
		result.add(fungus);
		result.add(molecularFunction);
		return result;
	}

	static int aminoacidPeptideOrProtein = -116;
	static int enzyme = -126;
	static int receptor = -192;
	// static int immunologicFactor = -129;
	static int chemicalViewedFunctionally = -120;
	// static int chemicalViewedStructually = -104;
	static int biomedOrDentalMaterial = -122;
	static int virus = -5;
	static int plant = -2;
	static int chemical = -103;
	static int food = -168;
	static int cell = -25;
	static int geneOrGenome = -28;
	static int spatialConcept = -82;
	static int environmentalEffectOfHumans = -69;
	static int bodySubstance = -31;
	static int clinicalDrug = -200;
	static int medicalDevice = -74;
	static int cellComponent = -26;
	static int nucleotideSequence = -86;
	static int biomedicalOccupationOrdiscipline = -91;
	static int manufacturedObject = -73;
	static int bodyPartOrganOrOrganComponent = -23;
	static int aminoAcidSequence = -87;
	static int classification = -185;
	static int drugDeliveryDevice = -203;
	static int tissue = -24;
	static int bacterium = -7;
	static int fungus = -4;
	static int molecularFunction = -44;

}
