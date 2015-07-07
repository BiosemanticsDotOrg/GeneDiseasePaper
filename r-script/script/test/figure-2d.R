"
@author Eelke van der horst
@author Mark thompson
@author Rajaram Kaliyaperumal

@version 0.1
@since 03-09-2014

" 
library(lattice)
library(latticeExtra)
highScore <- read.csv('/home/rajaram/mysql-data/random-samples-rank-based/randomMatchscoreSamplesHighRanked_500', 
                      colClasses=c("integer", "integer", "double", "integer", "integer"), header= T, fileEncoding= "windows-1252")

lowScore <- read.csv('/home/rajaram/mysql-data/random-samples-rank-based/randomMatchscoreSamplesLowRanked_500', 
                     colClasses=c("integer", "integer", "double", "integer", "integer"), header= T, fileEncoding= "windows-1252")

highScoreX <- (highScore[,4])
highScoreY <- (highScore[,5])

lowScoreX <- (lowScore[,4])
lowScoreY <- (lowScore[,5])

xLabel <- c("no. of abstracts for gene")
yLabel <- c("no. of abstracts for disease")
pointWidth <- c(0.1)
markerStyle <- c(19)
xLimit <- c(0, 200)
yLimit <- c(0, 200)
title <- c ("Fig 2d-eelke version - high and low scores")


#plot(x, y, type="p", pch= c(8), main = title, col="darkgreen", lwd=pointWidth, grid = TRUE)
#lines(x2, y2, type="p", col="darkred", lwd=pointWidth)

highScorePlot <- xyplot(highScoreY ~ highScoreX, grid = TRUE, 
        type = c("p"), pch= c(4), col="darkred", lwd=pointWidth, main = title)

lowScorePlot <- xyplot(lowScoreY ~ lowScoreX, grid = TRUE, 
                type = c("p"), pch= c(4), col="darkgreen", lwd=pointWidth, main = title)


highScorePlot+lowScorePlot


# xyplot(log10(y) ~ log10(x), grid = TRUE,  
#        type = c("p", "smooth"), col.line = "red", 
#        col="black", pch="*", lwd = 3, main = title)