data <- read.table("/Users/Marcio Barros/Documents/GitHub/sbse-ant-unirio/results/metrics/revision_optimized.data", header=TRUE);

pdf("c:/Users/Marcio Barros/Desktop/revisions_optimized.pdf", width=16, height=5)
par(mfrow=c(1, 2))
boxplot(data$spc~data$version, main="Single Packages\nCommits");
boxplot(data$apc~data$version, main="Average Packages\nCommit");
dev.off();
