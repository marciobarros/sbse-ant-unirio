data <- read.table("/Users/Marcio Barros/Documents/GitHub/sbse-ant-unirio/results/metrics/revision_optimized.data", header=TRUE);

par(mfrow=c(1, 2))
boxplot(data$spc~data$version, main="Single Packages\nCommits");
boxplot(data$apc~data$version, main="Average Packages\nCommit");
