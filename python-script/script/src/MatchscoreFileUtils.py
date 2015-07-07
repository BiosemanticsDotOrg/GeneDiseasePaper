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

__author__ = 'Eelke van der horst'
__author__ = 'Mark thompson'
__author__ = 'Rajaram kaliyaperumal'

import numpy as np
import sys
import mysql.connector
import operator



class MatchscoreFileUtils:
    """
    This class has methods to add a column to the matchscore file, merging two matchscore files etc.

    Input Required : matchscore file (see: http;//)

    Input Required : path to the directory where the script results should be stored.

    Output : CSV files.
      format : see methods description
      example row : see methods description

    """

    def __init__(self, matchscore_file, result_dir):
        """
        :param matchscore_file: Matchscore file full path
        :param result_dir: Result directory.
        """
        self.MATCHSCORE_FILE = matchscore_file
        self.RESULT_DIR = result_dir


    def add_abstract_count_to_matchscore_file(self, genes_stats_file, diseases_stats_file):
        """Add gene and disease abstracts count to matchscore file.

        :param  genes_stats_file: File include genes abstract count
        :param  diseases_stats_file: File include diseases abstract count

        Output : CSV file.
            format : <geneConceptId>,<diseaseConceptID>,<match score>,<no of gene abstracts>,<no of disease abstracts>
            example row : 3063788,744,3.0930463941267926E-4,1041,2208


        """
        # Dictionaries which acts as a temp DB. Key is gene/disease id, value is abstracts count
        genes_abstract = {}
        diseases_abstract = {}
        current_data_row = 0

        # Populate genes_abstract dictionary
        for line in open(genes_stats_file, "r"):
            if not ("#" in line or "gene" in line):
                line = line.replace("\n","")
                data = line.split(",")
                concept_id = data[0]
                no_of_abstracts = data[2]
                genes_abstract[concept_id] = no_of_abstracts

        # Populate diseases_abstract dictionary
        for line in open(diseases_stats_file, "r"):
            if not ("#" in line or "disease" in line):
                line = line.replace("\n","")
                data = line.split(",")
                concept_id = data[0]
                no_of_abstracts = data[2]
                diseases_abstract[concept_id] = no_of_abstracts

        # Add header to the output file.
        output_file = open((self.RESULT_DIR + "matchscores_with_abstract_count"), "w")

        for line in open(self.MATCHSCORE_FILE, "r"):
            current_data_row = current_data_row + 1
            line = line.replace("\n", "")

            if current_data_row == 1 and not ("#" in line or "gene" in line):
                 output_file.write("#gene,disease,score,geneAbstract,diseaseAbstract\n")
            elif current_data_row == 1:
                output_file.write(line+",geneAbstract,diseaseAbstract\n")

            if not ("#" in line or "gene" in line or "NaN" in line):
                data = line.split(",")
                gene_id = data[0]
                disease_id = data[1]
                no_of_gene_abstracts = genes_abstract[gene_id]
                no_of_disease_abstracts = diseases_abstract[disease_id]
                line = line + "," + str(no_of_gene_abstracts) + "," + str(no_of_disease_abstracts)
                line = line + "\n"
                output_file.write(line)
                # Print statement for stats
                if current_data_row % 1000000 == 0:
                    print "Line in output file " + str(current_data_row)
        output_file.close()



    def merge_cooccurrence_and_matchscores_files(self, co_occurrence_file):
        """Merge co-occurrence and matchscore files. (Note: This method use more RAM memory)

        :param  co_occurrence_file: File include co occurrence gene disease pairs

         Output : CSV file.
            format : <geneConceptID>, <diseaseConceptId>,<score>, <co occurance abstracts>
            example row : 3063788,1486,0.0014699738497795524,"[19344727, 22207712]"

        """

        # Dictionaries which acts as a temp DB. Key is gene and disease ids, value is matchscore file row.
        cooccurence_abstracts_list = {}
        current_data_row = 0

        # Populate cooccurence_abstracts_list dictionary
        for line in open(co_occurrence_file, "r"):
            if not ("#" in line or "gene" in line):
                line = line.replace("\n","")
                data = line.split(",")
                gene_id = data[0]
                disease_id = data[1]
                coocurrence_abstracts = line.replace((gene_id + "," + disease_id + ","),"")
                tmp_db_key = gene_id + "," + disease_id
                cooccurence_abstracts_list[tmp_db_key] = coocurrence_abstracts

        # Add header to the output file.
        matchscore_with_co_occurrence = open((self.RESULT_DIR + "matchscores_with_co_occurrence"), "w")

        for line in open(self.MATCHSCORE_FILE, "r"):
            current_data_row = current_data_row + 1
            line = line.replace("\n", "")

            if current_data_row == 1 and not ("#" in line or "gene" in line):
                 matchscore_with_co_occurrence.write("#gene,disease,score,coOccurance\n")
            elif current_data_row == 1:
                matchscore_with_co_occurrence.write(",coOccurance\n")

            if not ("#" in line or "gene" in line or "NaN" in line):
                data = line.split(",")
                gene_id = data[0]
                disease_id = data[1]
                tmp_db_key = gene_id + "," + disease_id
                #matchscore = float(data[2])
                if cooccurence_abstracts_list.has_key(tmp_db_key):
                    coocurrence_abstracts = cooccurence_abstracts_list.get(tmp_db_key)
                    line = line + "," + coocurrence_abstracts
                    line = line + "\n"
                    matchscore_with_co_occurrence.write(line)
                    # Print statement for stats
                    if current_data_row % 1000000 == 0:
                        print "Line in output file " + str(current_data_row)
        matchscore_with_co_occurrence.close()

    def add_percentile_to_sorted_matchscore_file(self, matchscore_file_sorted, no_of_associations):
        """ Add percentile value to the matchscore file. (Note: the input file for this method should be sorted on
        matchscore "desc order")

        :param matchscore_file_sorted : matchscore file sorted on matchscore
        :param no_of_associations : Number of gene disease associations in the input matchscore file

        """
        output_file = open((self.RESULT_DIR + "matchscores_percentile"), "w")
        previous_matchscore = 0
        percentile = 0
        current_data_row = 0
        no_of_associations = float(no_of_associations)

        for line in matchscore_file_sorted:
            current_data_row = current_data_row + 1
            line = line.replace("\n","")

            if current_data_row == 1 and not ("#" in line or "gene" in line):
                 output_file.write("#gene,disease,score,percentile\n")
            elif current_data_row == 1:
                output_file.write(",percentile\n")
            if ("#" not in line or "gene" not in line or "NaN" not in line):
                matchscore = float(line.split(",")[2])
                if previous_matchscore != matchscore:
                    percentile = (no_of_associations - (current_data_row - 1)) / no_of_associations
                    percentile = percentile * 100
                    previous_matchscore = matchscore
                    #print "matchscore == "+str(matchscore) + "percentile == " +str(percentile)
                line = line + "," + str(percentile) + "\n"
                output_file.write(line)
            if current_data_row % 1000000 == 0:
                print "Lines processed ==  "+str(current_data_row)
        output_file.close()
        matchscore_file_sorted.close()




    def add_cp_length_to_matchscore_file(self, genes_stats_file, diseases_stats_file):
        """Add concept profile length of gene/disease to the matchscore file.

        :param genes_stats_file: File include genes concept profile length.
        :param diseases_stats_file: File include diseases concept profile length.

        Output : CSV file.
            format : <geneConceptID>, <diseaseConceptId>,<score>, <geneCPLength>, <diseaseCPLength>
            example row : 3063788,744,0.000309304639413,7341,10294

        """

        # Dictionaries which acts as a temp DB. Key is gene/disease id, value is concept profile length
        genes_cp_length = {}
        diseases_cp_length = {}
        current_data_row = 0

        # Populate genes_cp_length dictionary
        for line in open(genes_stats_file, "r"):
            if not ("#" in line or "gene" in line):
                line = line.replace("\n","")
                data = line.split(",")
                gene_id = data[0]
                cp_length = data[1]
                genes_cp_length[gene_id] = cp_length

        # Populate diseases_cp_length dictionary
        for line in open(diseases_stats_file, "r"):
            if not ("#" in line or "disease" in line):
                line = line.replace("\n","")
                data = line.split(",")
                disease_id = data[0]
                cp_length = data[1]
                diseases_cp_length[disease_id] = cp_length

        # Add header to the output file.
        output_file = open((self.RESULT_DIR + "matchscores_with_cp_length"), "w")
        output_file.write("#gene,disease,score,geneCPLength,diseaseCPLength\n")

        for line in open(self.MATCHSCORE_FILE, "r"):
            if not ("#" in line or "gene" in line or "NaN" in line):
                line = line.replace("\n","")
                data = line.split(",")
                gene_id = data[0]
                disease_id = data[1]
                matchscore = float(data[2])
                current_data_row = current_data_row + 1
                gene_cp_length = genes_cp_length[gene_id]
                disease_cp_length = diseases_cp_length[disease_id]
                #line = line.replace("\n","")
                line = str(gene_id) + "," + str(disease_id) + "," + str(matchscore) + "," + str(gene_cp_length) \
                           + "," + str(disease_cp_length)

                if current_data_row > 1:
                    output_file.write("\n")
                    output_file.write(line)
                # Print statement for stats
                if current_data_row % 1000000 == 0:
                    print "Line in output file " + str(current_data_row)
        output_file.close()

    def __get_prefered_label__(self, concept_id):
        """ Query 'UMLS2010ABHomologeneJochemToxV1_6' mysql database and get prefered label of the given concept ID.

        :param concept_id : concept ID

        """

        mysql_user = "root"
        mysql_password = "blabla"
        mysql_database = "UMLS2010ABHomologeneJochemToxV1_6"
        mysql_port = "3307"
        mysql_host = "127.0.0.1"
        mysql_table = "term"
        prefered_label = ''

        cnx = mysql.connector.connect(user=mysql_user, password=mysql_password, host=mysql_host, port=mysql_port, database=mysql_database)
        cursor = cnx.cursor()

        query = ("SELECT text FROM "+mysql_table+" WHERE conceptid = '"+concept_id+"' AND termid = 0")
        cursor.execute(query)

        for text in cursor:
            prefered_label = text[0]

        return prefered_label

    def __read_concept_profile__(self, profile_dir, concept_id):
        """ Read the content of concept profile file

        :param profile_dir : The directory which contains concept profiles
        :param concept_id : concept ID
        """
        file_path = profile_dir + "/" + concept_id
        concept_profile = {}
        for line in open(file_path, 'r'):
            line = line.replace("\n","")
            data = line.split(",")
            id = data[0]
            value = data[1]
            concept_profile[id] = value

        return concept_profile


    def remove_nan(self):
        """This method removes rows with matchscore NaN

        """

        # Open output file.
        concept_profielen_input_file = open((self.RESULT_DIR + "matchscores_no_nan"), "w")
        current_data_row = 0
        for line in open(self.MATCHSCORE_FILE, "r"):

            current_data_row = current_data_row + 1

            if current_data_row == 1 and not ("#" in line or "gene" in line):
                concept_profielen_input_file.write("geneID,diseaseID,matchscore\n")

            if not ("NaN" in line):
                concept_profielen_input_file.write(line)
            # Print statement for stats
            if current_data_row % 1000000 == 0:
                print "Line in output file " + str(current_data_row)

        concept_profielen_input_file.close()






    def generate_concept_profielen_file(self, profile_dir, maxConcepts):
        """This method generate input file for the concept profielen.

        :param  profile_dir: The directory which contains concept profiles
        :param maxConcepts : Max number limit for overlapping concepts

         Output : text file.
            format : <geneConceptID>|<geneConceptID label>|<diseaseConceptId>|<diseaseConceptId label>|<overlappingConceptId>|<overlappingConceptId label>|<overlappingConcept contribution to matchscore>
            example row : 3063788|ALDH1A1|744|Abetalipoproteinemia|8672|chromosomes, human, pair 9|4.41399391269|3055596|ALDH1A3|4.39227481182|4353309|dasatinib|3.74968793914|4326272|Dasatinib|3.66856452971|1956421|neoplastic stem cells|2.83369169349|282588|drug resistance, neoplasm|1.36789823984|17267|gene expression regulation, leukemic|1.13149112005|4359354|11-cis-retinol|1.06982023965|373745|Retinol|1.03237032221|1519724|tyrosine kinase domain|0.992741715271

        """

        # Open output file.
        concept_profielen_input_file = open((self.RESULT_DIR + "concept_profielen_input_file"), "w")
        current_data_row = 0
        for line in open(self.MATCHSCORE_FILE, "r"):
            if not ("#" in line or "gene" in line):
                line = line.replace("\n","")
                data = line.split(",")
                gene_id = data[0]
                disease_id = data[1]
                matchscore = float(data[2])
                if matchscore > 0:
                    gene_cp = self.__read_concept_profile__(profile_dir, gene_id)
                    disease_cp = self.__read_concept_profile__(profile_dir, disease_id)
                    overlapping_concepts = []
                    overlapping_concepts_contribution = {}

                    output_line = gene_id + "|" + self.__get_prefered_label__(gene_id) + "|" + disease_id + "|" + self.__get_prefered_label__(disease_id)

                    # Find overlapping concepts contribution to matchscore
                    for concept in gene_cp.keys():
                        if concept in disease_cp.keys():
                            overlapping_concepts.append(concept)
                    # Compute overlapping concept's contribution to the matchscore
                    for concept in overlapping_concepts:
                        w1 = float(gene_cp.get(concept))
                        w2 = float(disease_cp.get(concept))
                        contribution = (w1 * w2) / matchscore
                        contribution = contribution * 100
                        overlapping_concepts_contribution[concept] = contribution

                    count = 0
                    for concept in sorted(overlapping_concepts_contribution, key=overlapping_concepts_contribution.get, reverse=True):
                        count = count + 1
                        output_line = output_line + "|" + concept + "|" + self.__get_prefered_label__(concept) + "|" +str(overlapping_concepts_contribution[concept])
                        if count == maxConcepts:
                            break
                    print output_line
                    concept_profielen_input_file.write(output_line)
                    concept_profielen_input_file.write("\n")

                current_data_row = current_data_row + 1
                # Print statement for stats
                if current_data_row % 1000000 == 0:
                    print "Line in output file " + str(current_data_row)
        concept_profielen_input_file.close()

"""
matchscore_file = "/home/rajaram/work/gene-disease/python-script/dataset/test/test_matchscore"
co_occurence_file = "/home/rajaram/work/gene-disease/python-script/dataset/test/test_coocurrence"
genes_stats_file = "/home/rajaram/work/gene-disease/python-script/dataset/src/genesStats"
diseases_stats_file = "/home/rajaram/work/gene-disease/python-script/dataset/src/diseasesStats"
result_dir = "/home/rajaram/work/gene-disease/python-script/output/test/"
cp_dir_path = "/home/rajaram/eelke_pc/Meuk/cpgp/results/profiles/"
"""

# Run 
#test = MatchscoreFileUtils(sys.argv[1], sys.argv[2])
#test.add_abstract_count_to_matchscore_file(sys.argv[3], sys.argv[4])
#test.add_cp_length_to_matchscore_file(sys.argv[3], sys.argv[4])
#test.merge_cooccurrence_and_matchscores_files(sys.argv[5])


matchscore_file = "/home/rajaram/matchscores_no_nan_sorted_by_matchscore"
result_dir = "/home/rajaram/"

test = MatchscoreFileUtils(matchscore_file, result_dir)
#test.generate_concept_profielen_file(sys.argv[6], int(sys.argv[7]))

inputFile = open("/home/rajaram/matchscores_no_nan_sorted_by_matchscore", "r")
no_of_associations = float(204072376)
test.add_percentile_to_sorted_matchscore_file(inputFile, no_of_associations)

#concept_id = "3063788"<
#path = "/home/rajaram/eelke_pc/Meuk/cpgp/results/profiles/"

#test.__read_concept_profile__(path, concept_id)
#print test.__get_prefered_label__(concept_id)

#matchscore_file = "/home/rajaram/matchscores_no_nan"
#result_dir = "/home/rajaram/"
#genes_stats_file = "/home/rajaram/work/gene-disease/python-script/dataset/src/genesStats"
#diseases_stats_file = "/home/rajaram/work/gene-disease/python-script/dataset/src/diseasesStats"

#test = MatchscoreFileUtils(matchscore_file, result_dir)
