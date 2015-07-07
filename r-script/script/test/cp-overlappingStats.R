"
@author Eelke van der horst
@author Mark thompson
@author Rajaram Kaliyaperumal

@version 0.1
@since 03-09-2014

" 
#overlapping stats file
mydf <- read.csv('/home/rajaram/overlappingConcepts', 
                 colClasses=c("integer"), header= T, fileEncoding= "windows-1252")

# Get data from 'conceptProfileLength' column
data <- mydf[,1] #mydf$conceptProfileLength 


idx <- which(data > 0)

conceptProfileLength <- data[idx]

#Plot parameters
xLimit <- c(0, 10000)
dataSize <- c(500)
title <- c("diseasesConceptProfileStats")
pointWidth <- c(0.1)


maxLength <- max(conceptProfileLength)
# Find profles median value
medianScore <- median(conceptProfileLength)

print ("Median ===>")
print(medianScore)

print ("Max Length ===>")
print(maxLength)

#png("/tmp/myplot.png", width=8, height=8, units="in", res=300)

hist(conceptProfileLength, prob=TRUE, ylim=c(0, 0.00002),  breaks=dataSize,  col="black", main="overlappingConceptProfileStats") # prob=TRUE for probabilities not counts

#dev.off() #only 129kb in size