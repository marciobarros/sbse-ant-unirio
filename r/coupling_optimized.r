data <- read.table("/Users/Marcio Barros/Documents/GitHub/sbse-ant-unirio/results/metrics/coupling_optimized.data", header=TRUE);

pdf("c:/Users/Marcio Barros/Desktop/coupling_optimized.pdf", width=16, height=10)
par(mfrow=c(2, 3));
par(oma=c(0, 0, 0, 0));
par(mar=c(2, 3, 3, 2));
boxplot(data$cbo~data$version, main="CBO");
boxplot(data$aff~data$version, main="Afferent Coupling");
boxplot(data$eff~data$version, main="Efferent Coupling");
boxplot(data$lcom~data$version, main="LCOM");
boxplot(data$mq~data$version, main="MQ");
boxplot(data$evm~data$version, main="EVM");
dev.off();
