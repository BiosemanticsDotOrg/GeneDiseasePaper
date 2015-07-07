: '
@author	: Rajaram kaliyaperumal
@author	: Mark thompson
@author	: Eelke van der horst

@since	: 22-10-2014
@version: 0.1  	
'

# variables 
inputFileName="../../dataset/test/test-matchscores" # input file
outputFilePath="../../output/test/gda/gda-np"
subtype="gda" # either gda(gene-disease association) or ppa(protein-protein association)

echo "current path = "$(pwd)

echo $(ruby concept_profile_matching.rb --input ${inputFileName} --output ${outputFilePath}  --subtype ${subtype})
