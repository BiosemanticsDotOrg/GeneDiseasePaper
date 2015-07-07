"
@author Eelke van der horst
@author Mark thompson
@author Rajaram Kaliyaperumal

@version 0.1
@since 03-09-2014

" 
library(lattice)
library(latticeExtra)
highScore <- read.csv('/home/rajaram/mysql-data/random-samples-rank-based/matchscores_sortedRank_top_1', 
                      colClasses=c("integer", "integer", "double", "integer", "integer"), header= T, fileEncoding= "windows-1252")

lowScore <- read.csv('/home/rajaram/mysql-data/random-samples-rank-based/matchscores_sortedRank_bottom_1', 
                     colClasses=c("integer", "integer", "double", "integer", "integer"), header= T, fileEncoding= "windows-1252")


highScoreGeneAbs <- (highScore[,4])
highScoreDiseaseAbs <-(highScore[,5])

maxLengthHighScoringGene <- max(highScoreGeneAbs)
maxLengthHighScoringDisease <- max(highScoreDiseaseAbs)

lowScoreGeneAbs <- (lowScore[,4])
lowScoreDiseaseAbs <- (lowScore[,5])

maxLengthLowScoringGene <- max(lowScoreGeneAbs)
maxLengthLowScoringDisease <- max(lowScoreDiseaseAbs)

#yLimit <- c(0,0.4)

plot(density(highScoreGeneAbs), lty="solid", col="blue", lwd=2, main="High scoring assocations",
     xlab="Number of abstracts")#, xlim=c(0,10000))
lines(density(highScoreDiseaseAbs), lty="solid", col="red", lwd=2)
legend('topright',  c("Gene","Disease"), lty=1, col=c('blue', 'red'), bty='n')

plot(density(lowScoreGeneAbs),  lty="solid", col="blue", lwd=2, main="Low scoring assocations",
     xlab="Number of abstracts")
lines(density(lowScoreDiseaseAbs), lty="solid", col="red", lwd=2)
legend('topright',  c("Gene","Disease"), lty=1, col=c('blue', 'red'), bty='n')

plot(density(highScoreGeneAbs),  lty="solid", col="blue", lwd=2, main="Genes of high and low scoring assocation",
     xlab="Number of abstracts")
lines(density(lowScoreGeneAbs), lty="solid", col="red", lwd=2)
legend('topright',  c("High scoring gene","Low scoring gene"), lty=1, col=c('blue', 'red'), bty='n')

plot(density(highScoreDiseaseAbs),  lty="solid", col="blue", lwd=2, main="Diseases of high and low scoring assocation",
     xlab="Number of abstracts")
lines(density(lowScoreDiseaseAbs), lty="solid", col="red", lwd=2)
legend('topright',  c("High scoring gene","Low scoring gene"), lty=1, col=c('blue', 'red'), bty='n')