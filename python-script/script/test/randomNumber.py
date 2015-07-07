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

import random

my_randoms = random.sample(xrange(0, 2036), 100)

print  my_randoms

print 351 in my_randoms

a = 3

print my_randoms.__len__()

for x in range(0, a):
    print x




inputFile = open("/home/rajaram/mysql-data/analysis/matchscores_no_NaN_cpLength_no_header_sorted_score", "r")
outputFile = open("/home/rajaram/matchscores_percentile", "w")
lineNumber = 0

# Remove NaN rows
# for line in inputFile:
#     lineNumber = lineNumber+1
#     if not ("#" in line or "gene" in line):
#         matchscore = float(line.split(",")[2])
#         if matchscore > 0:
#             outputFile.write(line)
#     if lineNumber % 1000000 ==0:
#         print "Lines processed ==  "+str(lineNumber)
# outputFile.close()
# inputFile.close()

# Add percentile score

previousMatchscore = 0
percentile = 0
no_of_associations = float(204072352)
outputFile.write("gene,disease,matchscore,percentile")
outputFile.write("\n")
for line in inputFile:
    if not ("#" in line or "gene" in line):
        matchscore = float(line.split(",")[2])
        if matchscore > 0:
            if previousMatchscore == 0 or previousMatchscore != matchscore:
                percentile = (no_of_associations - lineNumber) / no_of_associations
                percentile = percentile * 100
                previousMatchscore = matchscore
                #print "matchscore == "+str(matchscore) + "percentile == " +str(percentile)
                line = line.replace("\n","")
                line = line+","+str(percentile)
                #print line
            if lineNumber > 0:
                outputFile.write("\n")
            outputFile.write(line)
    lineNumber = lineNumber+1
    if lineNumber % 1000000 ==0:
        print "Lines processed ==  "+str(lineNumber)
outputFile.close()
inputFile.close()


















# for line in inputFile:
#     if not "gene" in line:
#         data = float(line.split(",")[2])
#         if data > 0:
#             lineNumber = lineNumber +1
#             print line
#
# print "Lines = "+str(lineNumber)