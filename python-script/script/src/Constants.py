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


GENE_CONCEPTS_SPARQL_QUERY = "../../resources/sparql_queries/get_gene_concepts.sparql"
DISEASE_CONCEPTS_SPARQL_QUERY = "../../resources/sparql_queries/get_disease_concepts.sparql"
KNOWN_NAMES_SPARQL_QUERY = "../../resources/sparql_queries/get_known_names_of_concept.sparql"
CONCEPTS_SPARQL_QUERY = "../../resources/sparql_queries/get_all_concepts.sparql"
GET_CONCEPTS_BY_NAME = "../../resources/sparql_queries/get_concept_by_know_names.sparql"

OUTPUT_DIR  = "../../output"