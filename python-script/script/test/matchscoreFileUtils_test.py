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



class MatchscoreFileUtils:

    def __init__(self, matchscoreFileName, noOfLines, resultDir):
        self.matchscoreFileName = matchscoreFileName
        self.resultDir = resultDir
        self.noOfLines = noOfLines


    def splitFileOnMedian(self):
        """
        <p>
        Split the matchscore file into two based on median of matchscore.
            a) Save the rows with the matchscore greater than or equals to median value in a separate file.
            b) Save the rows with the matchscore less than median value in a separate file.
        </p>
        """

        matchScoreFile = open(self.matchscoreFileName, "r")
        matchScoreGrtEqlMedianFile = open((self.resultDir+"matchscoresGrtEqlMedian"), "w")
        #matchScoreGrtEqlMedianFile.write("gene,disease,score,geneAbstract,diseaseAbstract\n")
        matchScoreLessMedianFile = open((self.resultDir+"matchscoresLessMedian"), "w")
        #matchScoreLessMedianFile.write("gene,disease,score,geneAbstract,diseaseAbstract\n")

        matchScore = []
        rowsGrtEqlMedian = 0
        rowsLessMedian = 0

        # Exacting data from matchscore column
        for line in matchScoreFile:
            if not ("#" in line or "gene" in line):
                data = float(line.split(",")[2])
                matchScore.append(data)
        matchScoreFile.close()
        matchScoreFile = open(self.matchscoreFileName, "r")
        # Compute median for matchscore
        median = np.median(matchScore)

        print ("Median = "+str(median))
        # Split the matchscore file based on median
        for line in matchScoreFile:
            if not ("#" in line or "gene" in line):
                data = float(line.split(",")[2])
                #print data
                if data >= median:
                    matchScoreGrtEqlMedianFile.write(line)
                    rowsGrtEqlMedian = rowsGrtEqlMedian+1
                else:
                    matchScoreLessMedianFile.write(line)
                    rowsLessMedian = rowsLessMedian+1

        matchScoreGrtEqlMedianFile.close()
        matchScoreFile.close()
        print ("GDAs >= median is :: "+str(rowsGrtEqlMedian))
        print ("GDAs < median is :: "+str(rowsLessMedian))

    def mergeTwoMatchScoreFiles(self, file1, file2, outputFile):
        """

        """
        matchScoreFile1 = open(file1, "r")
        matchScoreFile2 = open(file2, "r")
        matchScoreFileOutput = open((self.resultDir+outputFile), "w")
        matchScoreFileOutput.write("#gene,disease,score,geneAbstract,diseaseAbstract\n")

        for line in matchScoreFile1:
            if not ("#" in line or "gene" in line):
                matchScoreFileOutput.write(line)
        matchScoreFile1.close()

        for line in matchScoreFile2:
            if not ("#" in line or "gene" in line):
                matchScoreFileOutput.write(line)
        matchScoreFile2.close()

    def splitFileOnRank(self, upperRange, lowerRange):
        """

        """
        matchScoreFile = open(self.matchscoreFileName, "r")
        matchScoreHighRank = open((self.resultDir+"matchscoresHighRank"), "w")
        #matchScoreHighRank.write("gene,disease,score,geneAbstract,diseaseAbstract\n")
        matchScoreLowRank = open((self.resultDir+"matchscoresLowRank"), "w")
        #matchScoreLowRank.write("gene,disease,score,geneAbstract,diseaseAbstract\n")
        lineNumber = 0

        # Exacting data from matchscore column
        for line in matchScoreFile:
            lineNumber  = lineNumber+1
            if not ("#" in line or "gene" in line):
                if lineNumber <= upperRange:
                    matchScoreHighRank.write(line)
                elif (self.noOfLines-lineNumber) < lowerRange:
                    matchScoreLowRank.write(line)

        matchScoreLowRank.close()
        matchScoreHighRank.close()

    def addAbstractCountToMatchScoreFile(self, geneAbsFile, diseaseAbsFile):
        """

        """
        geneAbs = {}
        diseaseAbs = {}

        inputFile = open(geneAbsFile, "r")

        for line in inputFile:
            if not ("#" in line or "gene" in line):
                conceptID = int(line.split(",")[0])
                abstr = int(line.split(",")[2])
                geneAbs[conceptID] = abstr
        inputFile.close()

        inputFile = open(diseaseAbsFile, "r")

        for line in inputFile:
            if not ("#" in line or "gene" in line):
                conceptID = int(line.split(",")[0])
                abstr = int(line.split(",")[2])
                diseaseAbs[conceptID] = abstr
        inputFile.close()

        inputFile = open(self.matchscoreFileName, "r")
        matchScore_no_NaN = open((self.resultDir+"matchscores_no_NaN"), "w")
        matchScore_no_NaN.write("#gene,disease,score,geneAbstract,diseaseAbstract\n")

        lineNumber = 0

        for line in inputFile:
            if not ("#" in line or "gene" in line):
                data = float(line.split(",")[2])
                if data > 0:
                    lineNumber = lineNumber +1
                    gene = int(line.split(",")[0])
                    disease = int(line.split(",")[1])
                    noGeneAbs = geneAbs[gene]
                    noDiseaseAbs = diseaseAbs[disease]
                    line = line.replace("\n","")
                    line = line+","+str(noGeneAbs)+","+str(noDiseaseAbs)
                    #print line
                    if lineNumber > 1:
                        matchScore_no_NaN.write("\n")

                    matchScore_no_NaN.write(line)
                    if lineNumber % 1000000 == 0:
                        print "Line in output file "+str(lineNumber)
        inputFile.close()
        matchScore_no_NaN.close()



    def mergeCoOccuranceAndMatchscores(self, coOcurrance):
        """

        """
        matchScoreDB = {}

        inputFile = open(self.matchscoreFileName, "r")

        for line in inputFile:
            if not ("#" in line or "gene" in line):
                gene = (line.split(",")[0])
                disease = (line.split(",")[1])
                key = gene+","+disease
                matchScoreDB[key] = line
        inputFile.close()

        inputFile = open(coOcurrance, "r")
        matchScore_with_co_occurance = open((self.resultDir+"matchscores_with_co_occurance"), "w")
        matchScore_with_co_occurance.write("#gene,disease,score,geneAbstract,diseaseAbstract,coOccurance\n")

        lineNumber = 0

        for line in inputFile:
            if not ("#" in line or "gene" in line):
                gene = (line.split(",")[0])
                disease = (line.split(",")[1])
                coOcurranceAbs = (line.split(",")[2])
                key = gene+","+disease
                if matchScoreDB.has_key(key):
                    lineNumber = lineNumber +1
                    line = matchScoreDB.get(key)
                    line = line.replace("\n","")
                    line = line+","+str(coOcurranceAbs)
                    #print line
                    if lineNumber > 1:
                        matchScore_with_co_occurance.write("\n")

                    matchScore_with_co_occurance.write(line)
                    if lineNumber % 1000000 == 0:
                        print "Line in output file "+str(lineNumber)
        inputFile.close()
        matchScore_with_co_occurance.close()


    def mergeCPLengthCountToMatchScoreFile(self, geneCPLengthFile, diseaseCPLengthFile):
        """

        """
        geneCPLengths = {}
        diseaseCPLengths = {}

        inputFile = open(geneCPLengthFile, "r")

        for line in inputFile:
            if not ("#" in line or "gene" in line):
                conceptID = int(line.split(",")[0])
                cpLength = int(line.split(",")[1])
                geneCPLengths[conceptID] = cpLength
        inputFile.close()

        inputFile = open(diseaseCPLengthFile, "r")

        for line in inputFile:
            if not ("#" in line or "gene" in line):
                conceptID = int(line.split(",")[0])
                cpLength = int(line.split(",")[1])
                diseaseCPLengths[conceptID] = cpLength
        inputFile.close()

        inputFile = open(self.matchscoreFileName, "r")
        matchScore_no_NaN = open((self.resultDir+"matchscores_no_NaN_cpLength"), "w")
        matchScore_no_NaN.write("#gene,disease,score,geneCPLength,diseaseCPLength\n")

        lineNumber = 0

        for line in inputFile:
            if not ("#" in line or "gene" in line):
                data = line.split(",")
                gene = int(data[0])
                disease = int(data[1])
                score = data[2]
                if score > 0:
                    lineNumber = lineNumber + 1
                    geneCpLength = geneCPLengths[gene]
                    diseaseCPLength = diseaseCPLengths[disease]
                    #line = line.replace("\n","")
                    line = data[0]+","+data[1]+","+data[2]+","+str(geneCpLength)+","+str(diseaseCPLength)
                    #print line
                    if lineNumber > 1:
                        matchScore_no_NaN.write("\n")

                    matchScore_no_NaN.write(line)
                    if lineNumber % 1000000 == 0:
                        print "Line in output file "+str(lineNumber)
        inputFile.close()
        matchScore_no_NaN.close()








test = MatchscoreFileUtils(sys.argv[1], int(sys.argv[3]), sys.argv[2])
#test.splitFileOnRank(int(sys.argv[4]), int(sys.argv[5]))
#test.addAbstractCountToMatchScoreFile(sys.argv[6], sys.argv[7])
#test.mergeCoOccuranceAndMatchscores(sys.argv[6])
test.mergeCPLengthCountToMatchScoreFile(sys.argv[6], sys.argv[7])

#test.splitFileOnMedian()
# test.mergeTwoMatchScoreFiles("/home/rajaram/mysql-data/random-samples/randomMatchscoreSamplesGrtEqlMedian_500",
#                              "/home/rajaram/mysql-data/random-samples/randomMatchscoreSamplesLessMedian_500",
#                              "randomMatchscoreSamples1000")



