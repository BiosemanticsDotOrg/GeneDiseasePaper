"
@author Eelke van der horst
@author Mark thompson
@author Rajaram Kaliyaperumal

@version 0.1
@since 03-09-2014

" 
# Read csv file
genedf <- read.csv('/home/rajaram/eelke_pc/results/analysis2/geneConceptProfileStats', header= T, fileEncoding= "windows-1252")
# Read csv file
diseasedf <- read.csv('/home/rajaram/eelke_pc/results/analysis2/diseasesConceptProfileStats', header= T, fileEncoding= "windows-1252")
# Get column names
columnNames <- colnames(diseasedf, do.NULL = TRUE, prefix = "col")
#Plot parameters
xLimit <- c(1.5, 5)
yLimit <- c(0, 6)
title <- c("geneConceptProfileStats")
xLabel <-c("cpLength")
yLabel <-c("abstract")
pointWidth <- c(0.1)
markerStyle <- c(19)

plot(log10(genedf$conceptProfileLength), log10(genedf$NoOfPMids4Gene), main=title, xlab=xLabel, ylab=yLabel, xlim=xLimit, ylim=yLimit, pch=markerStyle, lwd=pointWidth)
# Add new points to the plot
points(log10(diseasedf$conceptProfileLength), log10(diseasedf$NoOfPMids4Disease), col="grey", pch=markerStyle, lwd=pointWidth)
