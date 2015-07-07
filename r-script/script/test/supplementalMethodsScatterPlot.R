library(ggplot2)
library(reshape)
library(lattice)
library(latticeExtra)

genedf <- read.csv('/home/rajaram/mysql-data/randomMatchscoreSamples_5000', 
                   colClasses=c("integer", "integer", "double", "integer", "integer"), header= T, fileEncoding= "windows-1252")


x <- log(genedf$geneAbstract)
y <- log(genedf$diseaseAbstract)
z <- log(genedf$score)

#x <- (genedf$geneAbstract)
#y <- (genedf$diseaseAbstract)
#z <- (genedf$score)

xLabel <- c("log(No. of abstracts for gene)")
yLabel <- c("log(matchscore)")
pointWidth <- c(0.1)
markerStyle <- c(19)
xLimit <- c(0, 20000)
yLimit <- c(0, 40000)

#png("/tmp/myplot.png", width=16, height=16, units="in", res=300)



d <- data.frame(x, y, z)
dg<-qplot(x, y, colour = z, data = d, xlab = xLabel, ylab = yLabel)
#dg<-qplot(x, y, colour = z, data = d, xlab = xLabel, ylab = yLabel, xlim = xLimit, ylim = yLimit)
dg + scale_colour_gradient(low="green", high="red",  name="Match score\n(As numeric)")


xyGene <- xyplot(z ~ x, grid = TRUE,  
       type = c("p", "smooth"), col.line = "violet", xlab = c("log(No. of abstracts)"), ylab = yLabel,
       col="blue", pch="*", lwd = 3, main="Fig.2d")

xyDisease <- xyplot(z ~ y, grid = TRUE, xlab = c("log(No. of abstracts)"), ylab = yLabel, 
           type = c("p", "smooth"), col.line = "brown",
           col="red", pch="*", lwd = 3, main="Fig.2d")

leg <- legend('bottomright',  c("High scoring gene","Low scoring gene"), lty=1, col=c('blue', 'red'), bty='n')


xyGene+xyDisease


#dev.off() #only 129kb in size