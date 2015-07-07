mydf <- read.csv('/home/rajaram/test', 
                 colClasses=c("integer", "integer", "double", 
                              "integer", "integer","character"), 
                 header= F, fileEncoding= "windows-1252")[ ,6:6]
data <- log(as.double(mydf, digits=22))

xLabel <- c("log(percentile)")

png("/tmp/myplot.png", width=10, height=8, units="in", res=300)

hist(data, xlab = xLabel)


dev.off() #only 129kb in size