data <- read.table("/Users/Marcio Barros/Documents/GitHub/sbse-ant-unirio/results/versioncontrol/log_versions.data", header=TRUE);

data$version[data$version == "1.4.1"] <- "1.4.0";
data$version[data$version == "1.5.1"] <- "1.5.0";
data$version[data$version == "1.5.2"] <- "1.5.0";
data$version[data$version == "1.5.3"] <- "1.5.0";
data$version[data$version == "1.5.4"] <- "1.5.0";
data$version[data$version == "1.6.1"] <- "1.6.0";
data$version[data$version == "1.6.2"] <- "1.6.0";
data$version[data$version == "1.6.3"] <- "1.6.0";
data$version[data$version == "1.6.4"] <- "1.6.0";
data$version[data$version == "1.6.5"] <- "1.6.0";
data$version[data$version == "1.7.1"] <- "1.7.0";
data$version[data$version == "1.8.1"] <- "1.8.0";
data$version[data$version == "1.8.3"] <- "1.8.2";
data$version[data$version == "1.8.4"] <- "1.8.2";
data$version <- factor(data$version);

versions <- rev(unique(data$version));
columns <- c("rev", "single", "revmc", "revsc", "pv_c", "revmp", "revsp", "sw_p", "pv_p");
result <- matrix(nrow=length(versions), ncol=length(columns), dimnames=list(versions, columns));
lastVersion <- "";

for (version_ in versions)
{
	versionData <- subset(data, version == version_);
	
	result[version_, "rev"] <- nrow(versionData);
	result[version_, "single"] <- nrow(subset(versionData, classes == 1)); 
	
	result[version_, "revmc"] <- mean(versionData$classes);
	result[version_, "revsc"] <- sd(versionData$classes);
	
	result[version_, "revmp"] <- mean(versionData$packages);
	result[version_, "revsp"] <- sd(versionData$packages);
	
	if (version_ != "1.1.0")
	{
		lastVersionData <- subset(data, version == lastVersion);
		result[version_, "pv_c"] <- wilcox.test(lastVersionData$classes, versionData$classes)$p.value;
		result[version_, "pv_p"] <- wilcox.test(lastVersionData$packages, versionData$packages)$p.value;
	}
	
	result[version_, "sw_p"] <- shapiro.test(versionData$classes)$p.value;		
	lastVersion = version_;
}

result

# prepare the plots
pdf("c:/Users/Marcio Barros/Desktop/revision_classes.pdf", width=16, height=5)
par(mfrow=c(1, 2))
boxplot(data$classes~data$version, range=0, cex.axis=0.75, main="Classes affected by commits (with outliers)")
boxplot(data$classes~data$version, outline=FALSE, cex.axis=0.75, main="Classes affected by commits (without outliers)")
dev.off();

pdf("c:/Users/Marcio Barros/Desktop/revision_packages.pdf", width=16, height=5)
par(mfrow=c(1, 2))
boxplot(data$packages~data$version, range=0, cex.axis=0.75, main="Packages affected by commits (with outliers)")
boxplot(data$packages~data$version, outline=FALSE, cex.axis=0.75, main="Packages affected by commits (without outliers)")
dev.off();
