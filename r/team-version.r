data <- read.table("/Users/Marcio/Documents/GitHub/Pesquisa/SBSE/sbse-ant-unirio/log_versions.data", header=TRUE);
versions <- rev(unique(data$version));
columns <- c("team", "inTeam", "outTeam", "rev", "NAR");
result <- matrix(nrow=length(versions), ncol=length(columns), dimnames=list(versions, columns));

oldTeam <- c();

for (version_ in versions)
{
	vdata <- subset(data, version == version_);
	developers <- split(vdata, vdata$author);

	team <- unique(vdata$author);
	inTeam <- setdiff(team, oldTeam);
	outTeam <- setdiff(oldTeam, team);
	oldTeam <- team;

	commits <- unlist(lapply(developers, nrow));
	commits <- subset(commits, commits > 0);
	print(commits);

	result[version_, "team"] <- length(team);
	result[version_, "inTeam"] <- length(inTeam);
	result[version_, "outTeam"] <- length(outTeam);
	result[version_, "rev"] <- nrow(vdata);
	result[version_, "NAR"] <- sd(commits);
}

result
