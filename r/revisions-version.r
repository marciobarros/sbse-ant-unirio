data <- read.table("/Users/Marcio/Documents/GitHub/Pesquisa/SBSE/sbse-ant-unirio/revisions.data", header=TRUE);

versions <- rev(unique(data$version));
rel_versions <- factor(c("1.1.0", "1.2.0", "1.3.0", "1.4.0", "1.5.0", "1.6.0", "1.7.0", "1.8.0", "1.8.2", "1.9.0"));

columns <- c("rev", "single", "revmc", "revsc", "pv_c", "revmp", "revsp", "pv_p");
result <- matrix(nrow=length(rel_versions), ncol=length(columns), dimnames=list(rel_versions, columns));

lastVersion <- "";
accRevisions <- 0;
accSingleRevisions <- 0;
oldClasses <- c();
oldPackages <- c();
accClasses <- c();
accPackages <- c();

for (version_ in versions)
{
	for (findVersion_ in rel_versions)
	{
		if (findVersion_ == version_)
		{
			if (lastVersion != "")
			{
				result[lastVersion, "rev"] <- accRevisions;
				result[lastVersion, "single"] <- accSingleRevisions; 
				
				result[lastVersion, "revmc"] <- mean(accClasses);
				result[lastVersion, "revsc"] <- sd(accClasses);

				if (lastVersion != "1.1.0")
					result[lastVersion, "pv_c"] <- wilcox.test(oldClasses, accClasses)$p.value; 
				
				result[lastVersion, "revmp"] <- mean(accPackages);
				result[lastVersion, "revsp"] <- sd(accPackages);
				
				if (lastVersion != "1.1.0")
					result[lastVersion, "pv_p"] <- wilcox.test(oldPackages, accPackages)$p.value; 
			}
			
			lastVersion <- version_;
			accRevisions <- 0;
			accSingleRevisions <- 0;
			oldClasses <- accClasses;
			oldPackages <- accPackages;
			accClasses <- c();
			accPackages <- c();
		}
	}

	vdata <- subset(data, version == version_);

	accRevisions <- accRevisions + nrow(vdata);
	accSingleRevisions <- accSingleRevisions + nrow(subset(vdata, classes == 1));
	
	classes <- subset(vdata)$classes;
	accClasses <- c(accClasses, classes);
	
	packages <- subset(vdata)$packages;
	accPackages <- c(accPackages, packages);
}

result[lastVersion, "rev"] <- accRevisions;
result[lastVersion, "single"] <- accSingleRevisions; 

result[lastVersion, "revmc"] <- mean(accClasses);
result[lastVersion, "revsc"] <- sd(accClasses);
result[lastVersion, "pv_c"] <- wilcox.test(oldClasses, accClasses)$p.value; 

result[lastVersion, "revmp"] <- mean(accPackages);
result[lastVersion, "revsp"] <- sd(accPackages);
result[lastVersion, "pv_p"] <- wilcox.test(oldPackages, accPackages)$p.value; 

result
