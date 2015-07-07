library(ggplot2)
# Read csv file
word_count_case_insen <- read.csv('/home/rajaram/PycharmProjects/gene-disease/python-script/output/word_count_case_insen_2015-05-30.csv', 
                 header= T, fileEncoding= "windows-1252")
word_count_case_sen <- read.csv('/home/rajaram/PycharmProjects/gene-disease/python-script/output/word_count_case_sen_2015-05-30.csv', 
                                  header= T, fileEncoding= "windows-1252")
concept_coverage <- read.csv('/home/rajaram/PycharmProjects/gene-disease/python-script/output/concept_coverage_2015-05-30.csv', 
                                header= T, fileEncoding= "windows-1252")

#hist(conceptProfileLength, prob=FALSE, xlim= xLimit,   col="black", 
 #    breaks=dataSize, main=title, xlab=xLabel, ylab=yLabel)
xLimit <- c(2,10)
yLimit <- c(0,1750)
plot <- qplot(count, data = word_count_case_insen, geom = "histogram", binwidth = 1)
#plot <- plot  + xlim(xLimit)
#plot <- plot + ylim(yLimit)
word_count_case_insen_plot <- plot
word_count_case_insen_plot

max_count <- max(word_count_case_sen$count)
xLimit <- as.character(c(2:20))
plot <- qplot(factor(count), data = word_count_case_sen, geom = "bar") 
plot <- plot  + geom_text(stat="bin", colour="red", aes(label = ..count.., y=(..count..-..count..) + 7000))
plot <- plot + xlab("Count")
plot <- plot + ggtitle("Concepts label case sens count")
plot <- plot + xlim(xLimit)
plot <- plot + guides(fill=guide_legend(title=NULL))
plot(plot)

plot <- qplot(overlaps_with, data = concept_coverage, geom = "histogram", binwidth = 1)
#plot <- plot  + xlim(xLimit)
#plot <- plot + ylim(yLimit)
concept_coverage_plot <- plot
concept_coverage_plot
