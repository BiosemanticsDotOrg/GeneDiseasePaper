# Concept profile generation and analysis for Gene-Disease paper
# Copyright (C) 2015 Biosemantics Group, Leiden University Medical Center
#  Leiden, The Netherlands
# 
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU Affero General Public License as published
# by the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
# 
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU Affero General Public License for more details.
# 
# You should have received a copy of the GNU Affero General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>

__author__ = 'Rajaram Kaliyaperumal'
__version__ = "1.0"
__copyright__ = "Copyright 2015, biosemantics.org"
__maintainer__ = "Rajaram Kaliyaperumal"
__email__ = "R.kaliyaperumal@lumc.nl"
__status__ = "Prototype"


from SPARQLWrapper import SPARQLWrapper, JSON
import time
import Sparql
import Constants as const
import csv
import math
import mysql.connector

class ConceptAnalysis:
    """
    To query the RDF dataset via SPARQL ENDPOINT.
    """

    def __init__(self, endpoint):
        """
        Class Constructor
        :param endpoint:   URL of the SPARQL ENDPOINT
        """

        self.__ENDPOINT = endpoint
        self.__SPARQL_FACTOR = Sparql.Sparql(self.__ENDPOINT)

    def get_concepts(self, sparql_query):
        """
        :param sparql_query:
        :return:
        """

        concepts = []
        query_template_file = open(sparql_query, "r")
        query = query_template_file.read()
        for row in self.__SPARQL_FACTOR.run_query(query)["results"]["bindings"]:
            concept = row["concept"]["value"]
            concepts.append(concept)
        return concepts

    def get_known_names_of_concepts(self, concept_uri, is_case_senentive):
        """

        :param concept_uri:
        :param is_case_senentive:
        :return:
        """

        query_template_file = open(const.KNOWN_NAMES_SPARQL_QUERY, "r")
        query = query_template_file.read()
        query = query.replace("?concept", "<"+ concept_uri +">")
        known_names = []
        # Get query results
        for row in self.__SPARQL_FACTOR.run_query(query)["results"]["bindings"]:
            label = None
            alt_label = None
            pre_label = None
            if ("label" in row):
                if is_case_senentive:
                    label = (row["label"]["value"])
                else:
                    label = (row["label"]["value"]).lower()
            if ("altLabel" in row):
                if is_case_senentive:
                    alt_label = (row["altLabel"]["value"])
                else:
                    alt_label = (row["altLabel"]["value"]).lower()
            if ("preLabel" in row):
                if is_case_senentive:
                    pre_label = (row["preLabel"]["value"])
                else:
                    pre_label = (row["preLabel"]["value"]).lower()

            if (label is not None) and (label not in known_names):
                known_names.append(label)
            if (alt_label is not None) and (alt_label not in known_names):
                known_names.append(alt_label)
            if (pre_label is not None) and (pre_label not in known_names):
                known_names.append(pre_label)
        return known_names

    def search(self, target_dict, lookup):
        """

        :param target_dict:
        :param lookup:
        :return:
        """
        disease_uris = []
        for key, values in target_dict.items():
            if set(lookup).intersection(set(values)):
                #print ("Matches \t \t gene_name = "+str(lookup) + "\n \t \t disease_name = " + str(values))
                disease_uris.append(key)
        return disease_uris

    def get_concept_by_known_name(self, name):
        """

        :param name:
        :return:
        """
        cnx = mysql.connector.connect(user='root', password="root", database='UMLS2010ABHomologeneJochemToxV1_6')
        cursor = cnx.cursor()
        concepts = []
        query = ("SELECT DISTINCT conceptid FROM UMLS2010ABHomologeneJochemToxV1_6.term where "
                 "LOWER(text) = LOWER(%s)")
        cursor.execute(query, (name,))
        for (conceptid) in cursor:
            concept_uri = "http://rdf.biosemantics.org/emco/v1.5/concepts/C"
            id = conceptid[0]
            if id > 0:
                concept_uri = concept_uri + str(id)
                concepts.append(concept_uri)

        query = ("SELECT DISTINCT conceptid FROM UMLS2010ABHomologeneJochemToxV1_6.concept where LOWER(name) = "
                 "LOWER(%s)")
        cursor.execute(query, (name,))
        for (conceptid) in cursor:
            concept_uri = "http://rdf.biosemantics.org/emco/v1.5/concepts/C"
            id = conceptid[0]
            if id > 0:
                concept_uri = concept_uri + str(id)
                concepts.append(concept_uri)
        cursor.close()
        cnx.close
        return concepts

    def get_overlapping_concepts_mysql(self,concepts_list, is_case_senentive):
        """

        :param concepts_list:
        :param is_case_senentive:
        :return:
        """
        counter = 0
        output_file  = open((const.OUTPUT_DIR + "/concept_coverage.csv"), "wb")
        csv_writer = csv.writer(output_file, delimiter=',', quotechar='"', quoting=csv.QUOTE_NONE)
        csv_writer.writerow(["concept_uri", "overlaps_with","overlaping_concepts"])
        concept_db = {}
        for concept in concepts_list:
            counter = counter + 1
            known_names = self.get_known_names_of_concepts(concept, is_case_senentive)
            overlapping_concepts = []
            for name in known_names:
                for overlapping_concept in self.get_concept_by_known_name(name):
                    overlapping_concepts.append(overlapping_concept)
            overlapping_uris = set(overlapping_concepts)
            overlapping_uris.remove(concept)
            num_of_overlapping_concepts = len(overlapping_uris)
            uris = ""
            if (num_of_overlapping_concepts > 0):
                print ("\nConcept :\t"+concept + "\t overlaps with = " + str(num_of_overlapping_concepts))
                counter = counter + 1
                uris = str1 = ''.join(str(e + ";") for e in list(overlapping_uris))
                uris = uris[:-1]
            csv_writer.writerow([concept, num_of_overlapping_concepts, uris])
        print("Number of pairs = " + str(counter))
        output_file.close()

    def get_overlapping_concepts(self,concepts_list, is_case_senentive):
        """

        :param concepts_list:
        :param is_case_senentive:
        :return:
        """
        counter = 0
        output_file  = open((const.OUTPUT_DIR + "/concept_coverage.csv"), "wb")
        csv_writer = csv.writer(output_file, delimiter=',', quotechar='"', quoting=csv.QUOTE_NONE)
        csv_writer.writerow(["concept_uri", "overlaps_with","overlaping_concepts"])
        concept_db = {}
        known_names_db = {}
        for concept in concepts_list:
            known_names = self.get_known_names_of_concepts(concept, is_case_senentive)
            concept_db [concept] = known_names
            for name in known_names:
                if name not in known_names_db:
                    concepts = []
                    concepts.append(concept)
                    known_names_db[name] = concepts
                else:
                    concepts = known_names_db.get(name)
                    if concept not in concepts:
                        concepts.append(concept)
                        known_names_db[name] = concepts
        for concept in concept_db.keys():
            known_names = concept_db[concept]
            overlapping_concepts = []
            for name in known_names:
                concepts = known_names_db.get(name)
                for overlapping_concept in concepts:
                    overlapping_concepts.append(overlapping_concept)
            overlapping_uris = set(overlapping_concepts)
            overlapping_uris.remove(concept)
            num_of_overlapping_concepts = len(overlapping_uris)
            uris = ""
            if (num_of_overlapping_concepts > 0):
                print ("\nConcept :\t"+concept + "\t overlaps with = " + str(num_of_overlapping_concepts))
                counter = counter + 1
                uris = str1 = ''.join(str(e + ";") for e in list(overlapping_uris))
                uris = uris[:-1]
            csv_writer.writerow([concept, num_of_overlapping_concepts, uris])
        print("Number of pairs = " + str(counter))
        output_file.close()

    def get_word_count(self, concepts, is_case_senentive):
        """

        :param concepts:
        :param is_case_senentive:
        :return:
        """
        output_file = ""
        if is_case_senentive:
            output_file  = open((const.OUTPUT_DIR + "/word_count_case_sen.csv"), "wb")
        else:
            output_file  = open((const.OUTPUT_DIR + "/word_count_case_insen.csv"), "wb")
        csv_writer = csv.writer(output_file, delimiter=',', quotechar='"', quoting=csv.QUOTE_NONNUMERIC)
        csv_writer.writerow(["word", "count"])
        concept_db = {}
        for concept in concepts:
            known_names = self.get_known_names_of_concepts(concept, is_case_senentive)
            concept_db [concept] = known_names

        word_count = {}
        for concept in concept_db.keys():
            known_names = concept_db[concept]
            for word in known_names:
                if (word in word_count):
                    count = word_count.get(word)
                    count = count + 1
                    word_count[word] = count
                else:
                    count = 1
                    word_count[word] = count
        for word in word_count.keys():
            count = str(word_count[word])
            print(word + "\t count = " +count)
            csv_writer.writerow([word.encode('utf-8'), count])
        output_file.close()


test = ConceptAnalysis("http://136.243.4.200:8890/sparql")
concepts = test.get_concepts(const.CONCEPTS_SPARQL_QUERY)
is_case_senentive = False
test.get_overlapping_concepts(concepts, is_case_senentive)
#test.get_word_count(concepts, is_case_senentive)
#test.get_overlapping_concepts_mysql(concepts, is_case_senentive)
#is_case_senentive = True
#test.get_word_count(concepts, is_case_senentive)




