data <- read.table("/Users/Marcio Barros/Documents/GitHub/sbse-ant-unirio/results/metrics/size_optimized.data", header=TRUE);

pdf("c:/Users/Marcio Barros/Desktop/size_optimized.pdf", width=16, height=5)
par(mfrow=c(1, 4))
boxplot(data$pack~data$version, main="Packages");
boxplot(data$cp~data$version, main="Class/Package");
boxplot(data$eleg~data$version, main="Class Elegance");
boxplot(data$scp~data$version, main="Single Class\nPackages");
dev.off();
