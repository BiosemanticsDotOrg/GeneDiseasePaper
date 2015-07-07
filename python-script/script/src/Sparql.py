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

class Sparql:
    """
    To query SPARQL endpoint
    """

    def __init__(self, endpoint):
        """
        class constructor

        :param endpoint: SPARQL endpoint
        """
        self.__ENDPOINT = endpoint

    def run_query(self, query):
        """
        This method query's SPARQl endpoint and returns the query result
         as a JSON array.

         :param query: SPARQL query as a string
         :rtype : JSON array
         :returns : Array
        """
        sparql = SPARQLWrapper(self.__ENDPOINT)
        sparql.setQuery(query)
        sparql.setReturnFormat(JSON)
        #sparql.setTimeout(600000)
        # Quering the SPARQL ENDPOINT
        while True:
            try:
                sparql_results = sparql.query().convert()
                return sparql_results
                break
            except Exception as error:
                print error.message
                print "Going to sleep one second and then try again"
                time.sleep(1)
        return None
