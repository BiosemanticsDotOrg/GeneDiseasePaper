: '
@author	: Rajaram kaliyaperumal
@author	: Mark thompson
@author	: Eelke van der horst

@since	: 22-10-2014
@version: 0.1  	
'

# variables 
inputFileName="../../dataset/test/test-co-occurrence" # input file
outputFilePath="../../output/test/gda-co-occurrence/gda-co-occurrence-np"

echo "current path = "$(pwd)

echo $(ruby co_occurence.rb --input ${inputFileName} --output ${outputFilePath}  --subtype ${subtype})
