data <- read.table("/Users/Marcio/Documents/GitHub/Pesquisa/SBSE/sbse-ant-unirio/size_metrics.data", header=TRUE);
versions <- unique(data$version);
colnames <- c("Packages", "Classes", "Attrs", "Meths", "PMeths", "NAC");
result <- matrix(nrow=length(versions), ncol=length(colnames), dimnames=list(versions, colnames));

for (version_ in versions)
{
	vdata <- subset(data, version == version_);
	classes <- split(vdata, vdata$package);
	
	result[version_, "Packages"] <- length(unique(vdata$package));
	result[version_, "Classes"] <- length(vdata$classe);
	result[version_, "Attrs"] <- sum(vdata$attr);
	result[version_, "Meths"] <- sum(vdata$meth);
	result[version_, "PMeths"] <- sum(vdata$pmeth);	
	result[version_, "NAC"] <- sd(unlist(lapply(classes, nrow)));
}

result
