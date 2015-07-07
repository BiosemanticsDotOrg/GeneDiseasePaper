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

import re

inputFile = open("/home/rajaram/mysql-data/analysis/matchscores.txt-coOcc-no-NaN.txt", "r")

minscore = 100.0

lineNumber = 0

for line in inputFile:
    if not ("#" in line or "gene" in line):
        matchscore = float(line.split(",")[2])
        cooccurance = line.split(",")[3]
        cooccurance = re.sub("[^0-9]", "", cooccurance)

        if len(cooccurance) > 0 and minscore > matchscore:
            minscore = matchscore

    lineNumber = lineNumber + 1
    if lineNumber % 10000000 == 0:
        print "Lines processed ==  "+str(lineNumber)
        print "Current minscore ==  "+str(minscore)


print "Minscore == "+str(minscore)

lineNumber = 0
noOfPairsGrtMin = 0
inputFile = open("/home/rajaram/mysql-data/analysis/matchscores.txt-coOcc-no-NaN.txt", "r")

for line in inputFile:
    if not ("#" in line or "gene" in line):
        matchscore = float(line.split(",")[2])
        cooccurance = line.split(",")[3]
        cooccurance = re.sub("[^0-9]", "", cooccurance)

        if matchscore > minscore and len(cooccurance) == 0:
            noOfPairsGrtMin = noOfPairsGrtMin + 1

    lineNumber = lineNumber+1
    if lineNumber % 10000000 == 0:
        print "Lines processed ==  "+str(lineNumber)
        print "Current pairs count ==  "+str(noOfPairsGrtMin)

print "Minscore == "+str(minscore)
print "noOfPairsGrtMin == "+str(noOfPairsGrtMin)


