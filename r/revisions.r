data <- read.table("/Users/Marcio/Documents/GitHub/Pesquisa/SBSE/sbse-ant-unirio/revisions.data", header=TRUE);
versions <- rev(unique(data$version));
columns <- c("rev", "single", "revmc", "revsc", "pv_c", "revmp", "revsp", "pv_p", "revmc2", "revsc2", "pv_c2", "revmp2", "revsp2", "pv_p2", "team", "inTeam", "outTeam");
result <- matrix(nrow=length(versions), ncol=length(columns), dimnames=list(versions, columns));

oldTeam <- c();
oldClasses <- c(0);
oldPackages <- c(0);
oldClasses2 <- c(0);
oldPackages2 <- c(0);

for (version_ in versions)
{
	vdata <- subset(data, version == version_);
	
	team <- unique(vdata$author);
	inTeam <- setdiff(team, oldTeam);
	outTeam <- setdiff(oldTeam, team);
	oldTeam <- team;
	
	classes <- vdata$classes;
	pv_classes <- wilcox.test(oldClasses, classes)$p.value;
	oldClasses <- classes;
	
	packages <- vdata$packages;
	pv_packages <- wilcox.test(oldPackages, packages)$p.value;
	oldPackages <- packages;
	
	classes2 <- subset(vdata, classes > 1)$classes;
	pv_classes2 <- wilcox.test(oldClasses2, classes2)$p.value;
	oldClasses2 <- classes2;
	
	packages2 <- subset(vdata, classes > 1)$packages;
	pv_packages2 <- wilcox.test(oldPackages2, packages2)$p.value;
	oldPackages2 <- packages2;
	
	result[version_, "rev"] <- nrow(vdata);
	result[version_, "single"] <- nrow(subset(vdata, classes == 1)); 
	
	result[version_, "revmc"] <- mean(classes);
	result[version_, "revsc"] <- sd(classes);
	result[version_, "pv_c"] <- pv_classes; 
	
	result[version_, "revmp"] <- mean(packages);
	result[version_, "revsp"] <- sd(packages);
	result[version_, "pv_p"] <- pv_packages; 
	
	result[version_, "revmc2"] <- mean(classes2);
	result[version_, "revsc2"] <- sd(classes2);
	result[version_, "pv_c2"] <- pv_classes2; 
	
	result[version_, "revmp2"] <- mean(packages2);
	result[version_, "revsp2"] <- sd(packages2);
	result[version_, "pv_p2"] <- pv_packages2; 
	
	result[version_, "team"] <- length(team);
	result[version_, "inTeam"] <- length(inTeam);
	result[version_, "outTeam"] <- length(outTeam);
}

result
