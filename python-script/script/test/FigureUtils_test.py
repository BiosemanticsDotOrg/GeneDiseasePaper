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

import random
import numpy as np
import sys

class Figure2d:

    def __init__(self, inputFile, resultDir, noOfRows):
        self.inputFile = inputFile
        self.noOfRows = noOfRows
        self.resultDir = resultDir


    def chooseSizeOfSample(self, sampleSize, sampleIterations):
        """

        """
        stdevs = []

        if sampleSize < self.noOfRows:
            tempDB = self.loadFileInMemory(self.inputFile)
            for iteration in range(0, sampleIterations):
                randomNumbers = random.sample(xrange(1, (self.noOfRows+1)), sampleSize)
                matchScore = []
                for lineNum in randomNumbers:
                    line = tempDB.get(lineNum)
                    data = float(line.split(",")[2])
                    matchScore.append(data)
                # Compute median for matchscore
                stdev = np.std(matchScore)
                stdevs.append(stdev)
        else:
            print ("Sample size is greater than number samples in the file")

        print "Stdevs = " +str(stdevs)

    def loadFileInMemory(self, fileName):
        """
        """
        tempDB = {}
        file = open(fileName, "r")
        lineNumber = 0

        for line in file:
            lineNumber = lineNumber + 1
            tempDB[lineNumber] = line

        file.close()
        print "File loading done!!!"
        return tempDB

    def generateRandomSamplesMatchscoreFileInMemory(self, sampleSize, outputFileName):
        """

        """
        if sampleSize < self.noOfRows:
            tempDB = self.loadFileInMemory(self.inputFile)
            randomNumbers = random.sample(xrange(1, (self.noOfRows+1)), sampleSize)
            matchScoreRandomSample = open((self.resultDir+outputFileName), "w")
            matchScoreRandomSample.write("gene,disease,score,geneAbstract,diseaseAbstract\n")

            for lineNum in randomNumbers:
                line = tempDB.get(lineNum)
                matchScoreRandomSample.write(line)

            matchScoreRandomSample.close()
            print "The file is stored in the dir ==> "+self.resultDir

    def generateRandomSamplesMatchscoreFile(self, sampleSize, outputFileName):
        """

        """
        if sampleSize < self.noOfRows:
            randomNumbers = random.sample(xrange(1, (self.noOfRows+1)), sampleSize)
            print "Randoms numbers ==> "
            matchScoreRandomSample = open((self.resultDir+outputFileName), "w")
            matchScoreRandomSample.write("gene,disease,score,geneAbstract,diseaseAbstract\n")

            inputFile = open(self.inputFile, "r")
            lineNumber = 0
            rowsInFile = 0

            for line in inputFile:
                lineNumber = lineNumber + 1

                if lineNumber in randomNumbers:
                    matchScoreRandomSample.write(line)
                    rowsInFile = rowsInFile + 1

                if rowsInFile == sampleSize:
                    break

            matchScoreRandomSample.close()
            inputFile.close()
            print "The file is stored in the dir ==> "+self.resultDir

    def generateBinFileForFigure2d(self, sortColumn, maxCPLength, outputFileName):
        """

        """
        matchScoreRandomSample = open((self.resultDir+outputFileName), "w")
        matchScoreRandomSample.write("#binNumber,meanScore,standardDev\n")
        inputFile = open(self.inputFile, "r")
        lineNumber = 0
        rowsInFile = 0
        bin = dict()

        for line in inputFile:
            lineNumber = lineNumber + 1

            if not ("#" in line or "gene" in line):
                data = line.split(",")
                score = float(data[2])
                cpLength = int(data[sortColumn])

                binNumber




test = Figure2d(sys.argv[1], sys.argv[2], int(sys.argv[3]))
#test.chooseSizeOfSample(int(sys.argv[4]), int(sys.argv[5]))
test.generateRandomSamplesMatchscoreFile(int(sys.argv[4]), sys.argv[6])