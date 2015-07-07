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

__author__ = 'rajaram'

from SPARQLWrapper import SPARQLWrapper, JSON
import time

class Sparql:


    def query(self, subject):
        sparql = SPARQLWrapper("http://implicitome.cloud.tilaa.nl:8890/sparql")
        label = ""
        #subject = "http://rdf.biosemantics.org/emco/v1.5/concepts/C3053655"
        query = "select DISTINCT ?label {?s <http://www.w3.org/2000/01/rdf-schema#label> ?label}"
        query = query.replace("?s", "<"+str(subject)+">")
        sparql.setQuery(query)
        sparql.setReturnFormat(JSON)
        results = sparql.query().convert()
        for result in results["results"]["bindings"]:
            label = result["label"]["value"]
            #print data
        return label


    def convert(self, fileName):
        inputFile = open(fileName, "r")
        outputFile = open((fileName+"_"), "w")

        lineNumber = 0

        for line in inputFile:
            if not ("#" in line or "gene" in line):

                #if lineNumber > 0:
                 #   outputFile.write("\n")

                lineData = line.split(",")
                subject = "http://rdf.biosemantics.org/emco/v1.5/concepts/C"+lineData[0]
                node = self.query(subject)

                subject = "http://rdf.biosemantics.org/emco/v1.5/concepts/C"+lineData[1]
                target = self.query(subject)

                line = (node+","+target+","+lineData[2])
                outputFile.write(line)
                lineNumber = lineNumber + 1
        inputFile.close()
        outputFile.close()


test = Sparql()
test.convert("/home/rajaram/test")



