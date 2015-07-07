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

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.erasmusmc.ids.DatabaseID;
import org.semanticweb.owl.vocab.DublinCoreVocabulary;
import org.semanticweb.skos.AddAssertion;
import org.semanticweb.skos.SKOSAnnotation;
import org.semanticweb.skos.SKOSChange;
import org.semanticweb.skos.SKOSConcept;
import org.semanticweb.skos.SKOSDataFactory;
import org.semanticweb.skos.SKOSDataProperty;
import org.semanticweb.skos.SKOSDataset;
import org.semanticweb.skos.SKOSObjectRelationAssertion;
import org.semanticweb.skos.properties.SKOSDefinitionDataProperty;
import org.semanticweb.skos.properties.SKOSHiddenLabelProperty;
import org.semanticweb.skos.properties.SKOSNarrowerProperty;
import org.semanticweb.skos.properties.SKOSPrefLabelProperty;
import org.semanticweb.skos.properties.SKOSScopeNoteDataProperty;
import org.semanticweb.skosapibinding.SKOSFormatExt;
import org.semanticweb.skosapibinding.SKOSManager;

public abstract class OntologyScosSerializer {

	/**
	 * Serializes the given ontlogy. The sample usage code is:<br>
	 * 
	 * <code>
	 * OntologyPSFLoader ontologyPSFLoader = new OntologyPSFLoader();<br>
	 * ontologyPSFLoader.loadFromPSF("input.psf");<br>
	 * OntologyScosSerializer.serialize(ontologyPSFLoader.ontology, http://www.biosemantics.org/chemlist", "output.xml");<br>
	 * </code>
	 */
	public static void serialize(Ontology ontology, String baseURI, String fileName) throws Exception {

		final Iterator<Concept> iter = ontology.getConceptIterator();
		final List<SKOSChange> addAssertions = new ArrayList<SKOSChange>();
		final Map<Concept, SKOSConcept> map = new HashMap<Concept, SKOSConcept>();

		final SKOSManager manager = new SKOSManager();
		final SKOSDataset dataset = manager.createSKOSDataset(URI.create(baseURI));
		final SKOSDataFactory dataFactory = manager.getSKOSDataFactory();

		// Flyweight objects:
		final SKOSDefinitionDataProperty definitionDataProperty = dataFactory.getSKOSDefinitionDataProperty();
		final SKOSPrefLabelProperty prefLabelProperty = dataFactory.getSKOSPrefLabelProperty();
		final SKOSHiddenLabelProperty hiddenLabelProperty = dataFactory.getSKOSHiddenLabelProperty();

		while (iter.hasNext()) {
			final Concept concept = iter.next();

			final SKOSConcept skosConcept = dataFactory.getSKOSConcept(URI.create(baseURI + "#concept_"
					+ getXmlId(concept.getName())));

			addAssertions.add(new AddAssertion(dataset, dataFactory.getSKOSEntityAssertion(skosConcept)));

			map.put(concept, skosConcept);

			if (concept.definition != null && concept.definition.length() > 0) {
				addAssertions.add(new AddAssertion(dataset, dataFactory.getSKOSDataRelationAssertion(skosConcept,
						definitionDataProperty, concept.definition)));
			}

			boolean firstTerm = true;

			for (final TermStore termStore : concept.getTerms()) {
				final SKOSDataProperty p;

				if (firstTerm) {
					p = prefLabelProperty;
					firstTerm = false;
				} else {
					p = hiddenLabelProperty;
				}

				addAssertions.add(new AddAssertion(dataset, dataFactory.getSKOSDataRelationAssertion(skosConcept, p,
						termStore.text)));
			}

			for (final DatabaseID databaseId : ontology.getDatabaseIDsForConcept(concept.ID.intValue())) {
				final SKOSAnnotation skosAnnotation = dataFactory.getSKOSAnnotation(DublinCoreVocabulary.IDENTIFIER
						.getURI(), dataFactory.getSKOSResource(URI.create("urn:" + getXmlId(databaseId.database) + ":"
						+ getXmlId(databaseId.ID))));

				addAssertions.add(new AddAssertion(dataset, dataFactory.getSKOSAnnotationAssertion(skosConcept,
						skosAnnotation)));
			}
		}

		// Flyweight objects:
		final SKOSNarrowerProperty narrowerProperty = dataFactory.getSKOSNarrowerProperty();
		final SKOSScopeNoteDataProperty scopeNoteDataProperty = dataFactory.getSKOSScopeNoteDataProperty();

		for (Relation relation : ontology.getRelations()) {
			if (relation.predicate == DefaultTypes.isParentOf || relation.predicate == DefaultTypes.isOfSemanticType) {
				final SKOSConcept skosObjectEntity = map.get(ontology.getConcept(relation.object));
				final SKOSConcept skosSubjectEntity = map.get(ontology.getConcept(relation.subject));

				assert skosObjectEntity != null && skosSubjectEntity != null : "Relation refers to concepts which do not exist";

				final SKOSObjectRelationAssertion propertyAssertion = dataFactory.getSKOSObjectRelationAssertion(
						skosObjectEntity, narrowerProperty, skosSubjectEntity);

				addAssertions.add(new AddAssertion(dataset, propertyAssertion));
			} else if (relation.predicate == DefaultTypes.fromVocabulary) {
				final SKOSConcept skosObjectEntity = map.get(ontology.getConcept(relation.object));

				assert skosObjectEntity != null;

				addAssertions.add(new AddAssertion(dataset, dataFactory.getSKOSDataRelationAssertion(skosObjectEntity,
						scopeNoteDataProperty, Integer.toString(relation.subject))));
			}
		}

		manager.applyChanges(addAssertions);

		manager.save(dataset, SKOSFormatExt.RDFXML, new File(fileName).toURI());

		ontology.getRelations();
	}

	private static final String getXmlId(String id) {
		final StringBuffer sb = new StringBuffer();

		boolean prependDelimiter = false;

		for (int i = 0; i < id.length(); i++) {
			if (Character.isUnicodeIdentifierPart(id.charAt(i))) {
				if (prependDelimiter) {
					sb.append('_');
					prependDelimiter = false;
				}

				sb.append(id.charAt(i));
			} else {
				prependDelimiter = true;
			}
		}

		return sb.toString();
	}

	public static void main(String[] args) {
		final OntologyPSFLoader ontologyPSFLoader = new OntologyPSFLoader();

		ontologyPSFLoader.loadFromPSF(args[0]);

		try {
			OntologyScosSerializer.serialize(ontologyPSFLoader.ontology, "http://www.biosemantics.org/chemlist",
					args[1]);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
