data <- read.table("/Users/Marcio Barros/Documents/GitHub/sbse-ant-unirio/results/metrics/package_metrics.data", header=TRUE);
versions <- c("1.1.0", "1.2.0", "1.3.0", "1.4.0", "1.5.0", "1.6.0", "1.7.0", "1.8.0", "1.8.2", "1.9.0");
data <- subset(data, version %in% versions);
data$version <- factor(data$version);

colnames <- c("CBO", "AFF", "EFF", "LCOM", "MQ", "MF", "EVM", "CS");
result <- matrix(nrow=length(versions), ncol=length(colnames), dimnames=list(versions, colnames));

for (version_ in versions)
{
	vdata <- subset(data, version == version_);
	
	result[version_, "CBO"] <- mean(vdata$cbo);
	result[version_, "EFF"] <- mean(vdata$eff);
	result[version_, "AFF"] <- mean(vdata$aff);
	result[version_, "LCOM"] <- mean(vdata[complete.cases(vdata), ]$lcom);
	result[version_, "MQ"] <- sum(vdata$mf);	
	result[version_, "MF"] <- mean(vdata$mf);	
	result[version_, "EVM"] <- sum(vdata$cs);	
	result[version_, "CS"] <- mean(vdata$cs);
}

result

pdf("c:/Users/Marcio Barros/Desktop/coupling.pdf", width=16, height=10)
par(mfrow=c(2, 3))
boxplot(data$cbo~data$version, main="CBO");
boxplot(data$aff~data$version, main="Afferent Coupling");
boxplot(data$eff~data$version, main="Efferent Coupling");
boxplot(data$lcom~data$version, main="LCOM");
boxplot(data$mf~data$version, main="Modularization Factor");
boxplot(data$cs~data$version, outline=FALSE, main="Cluster Score");
dev.off();
