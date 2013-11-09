data <- read.table("/Users/Marcio/Documents/GitHub/Pesquisa/SBSE/sbse-ant-unirio/log_years.data", header=TRUE);
years <- rev(unique(data$year));
columns <- c("team", "inTeam", "outTeam", "rev", "NAR");
result <- matrix(nrow=length(years), ncol=length(columns), dimnames=list(years, columns));

oldTeam <- c();

for (year_ in years)
{
	vdata <- subset(data, year == year_);
	developers <- split(vdata, vdata$author);

	team <- unique(vdata$author);
	inTeam <- setdiff(team, oldTeam);
	outTeam <- setdiff(oldTeam, team);
	oldTeam <- team;

	commits <- unlist(lapply(developers, nrow));
	commits <- subset(commits, commits > 0);
	print(commits);

	result[year_ - 2000 + 1, "team"] <- length(team);
	result[year_ - 2000 + 1, "inTeam"] <- length(inTeam);
	result[year_ - 2000 + 1, "outTeam"] <- length(outTeam);
	result[year_ - 2000 + 1, "rev"] <- nrow(vdata);
	result[year_ - 2000 + 1, "NAR"] <- sd(commits);
}

result
