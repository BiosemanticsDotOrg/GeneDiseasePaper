# Gene-Disease Paper

This repository contains the source code for all concept-profile work described in the paper: "The implicitome: a resource for inferring gene-disease associations". Supplemental data available in DataDryad: http://dx.doi.org/10.5061/dryad.gn219  

The starting point for creating a new literature index is: 
./java/DataImport/src/Anni/LiteratureUpdateMasterScript.java

For reproducing the result from the paper (gene/disease concept profiles and match scores):
./java/NewConceptProfile/src/KnowledgeTransfer/BuildConceptProfileAndMatch.java

Code can be run by importing all projects in a modern IDE such as Eclipse or Netbeans and use the built-in functionality to resolve dependencies. Alternatively, import only the NewConceptProfile project and include the jar libraries that are available from DataDryad: http://dx.doi.org/10.5061/dryad.gn219/7. There, a snapshot of a Medline index can be found (http://dx.doi.org/10.5061/dryad.gn219/2; see also the Methods section of the paper), which greatly reduces the amount of time and resources required to run the code.

The codes not in the java/ subdirectory are used to produce the figures and analyses of the paper, please refer to the corresponding files for instructions. See also the software overview figure: http://dx.doi.org/10.5061/dryad.gn219/3 .

Software is available under AGPL V3.0 license. Dual licensing options may be available, please contact (corresponding) authors of the paper.
