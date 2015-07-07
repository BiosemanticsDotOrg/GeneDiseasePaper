: '
@author	: Rajaram kaliyaperumal
@author	: Mark thompson
@author	: Eelke van der horst

@since	: 23-10-2014
@version: 0.1  	
'

# variables 
matchscoreFile="../../dataset/test/test_matchscore"
coOccurenceFile="../../dataset/test/test_coocurrence"
genesStatsFile="../../dataset/src/genesStats"
diseasesStatsFile="../../dataset/src/diseasesStats"
resultDir="../../output/test/"

echo "current path = "$(pwd)

echo $(python MatchscoreFileUtils.py ${matchscoreFile} ${resultDir} ${genesStatsFile} ${diseasesStatsFile} ${coOccurenceFile})

echo "Done!!!"
