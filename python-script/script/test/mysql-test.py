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

import datetime
import mysql.connector

cnx = mysql.connector.connect(user='root', password='root', host='127.0.0.1', port='3306', database='UMLS2010ABHomologeneJochemToxV1_6')
cursor = cnx.cursor()

name = 'COd3'

query = ("SELECT DISTINCT conceptid FROM UMLS2010ABHomologeneJochemToxV1_6.term where "
                 "LOWER(text) = LOWER(%s)")
cursor.execute(query, (name,))
for conceptid in cursor:
    concept_uri = "http://rdf.biosemantics.org/emco/v1.5/concepts/C"
    concept_uri = concept_uri + str(conceptid[0])
    print concept_uri

query = ("SELECT DISTINCT conceptid FROM UMLS2010ABHomologeneJochemToxV1_6.concept where LOWER(name) = "
                 "LOWER(%s)")
cursor.execute(query, (name,))
for (conceptid) in cursor:
    concept_uri = "http://rdf.biosemantics.org/emco/v1.5/concepts/C"
    concept_uri = concept_uri + str(conceptid[0])
    #print concept_uri

cursor.close()
cnx.close
