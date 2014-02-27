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

# Publishes results from the normality test (95%)
publishNormal <- function(pvalue, name) {
	if (pvalue < 0.05) {
		cat("Existem evidências de que a distribuição de ", name, " não é normal (", pvalue, ").\n", sep="");
	} else {
		cat("Nao e possivel negar que a distribuição de ", name, " é normal (", pvalue, ").\n", sep="");
	}
}

# Normality tests
p <- c(4, 13, 8, 13, 13, 21, 21, 21, 21, 21, 24, 24, 24, 25, 25, 25, 29, 29, 30, 30, 59, 59, 59, 60);
publishNormal(shapiro.test(p)$p.value, "numero de pacotes");

c <- c(102, 173, 187, 265, 265, 401, 401, 406, 407, 407, 523, 524, 553, 576, 576, 576, 752, 769, 870, 873, 1090, 1093, 1094, 1116);
publishNormal(shapiro.test(c)$p.value, "numero de classes");

a <- c(141, 249, 294, 410, 410, 584, 584, 591, 593, 597, 797, 797, 843, 880, 880, 880, 1162, 1191, 1361, 1363, 1719, 1723, 1725, 1875);
publishNormal(shapiro.test(a)$p.value, "numero de atributos");

m <- c(816, 1521, 1548, 2193, 2202, 3379, 3389, 3457, 3465, 3465, 4651, 4679, 4884, 5124, 5126, 5129, 6581, 6793, 7725, 7770, 9879, 9922, 9934, 10103);
publishNormal(shapiro.test(m)$p.value, "numero de metodos");

pm <- c(633, 1186, 1223, 1729, 1735, 2639, 2644, 2703, 2708, 2708, 3639, 3665, 3817, 3971, 3973, 3973, 4978, 5088, 5686, 5720, 7248, 7275, 7275, 7475);
publishNormal(shapiro.test(pm)$p.value, "numero de metodos publicos");

d <- c(362, 803, 855, 1228, 1230, 1893, 1894, 1942, 1947, 1947, 2557, 2562, 2445, 2566, 2566, 2568, 3505, 3583, 4127, 4138, 5329, 5351, 5354, 5522);
publishNormal(shapiro.test(d)$p.value, "numero de dependencias");

nac <- c(8.305131, 11.919944, 13.014406, 16.849349, 16.849349, 20.834375, 20.834375, 21.118125, 21.231429, 21.231429, 26.048523, 26.132067, 27.700993, 29.272665, 29.272665, 29.272665, 34.451254, 34.936447, 38.476841, 38.567758, 38.27966, 38.35064, 38.346258, 37.336256);
publishNormal(shapiro.test(nac)$p.value, "elegancia de classes");

# prepare the plots
pdf("c:/Users/Marcio Barros/Desktop/size_boxplots.pdf", width=16, height=5)
par(mfrow=c(1, 4))

# box-plot for the number of classes per package
cp <- c(25.5, 13.3076923076923, 23.375, 20.3846153846154, 20.3846153846154, 19.0952380952381, 19.0952380952381, 19.3333333333333, 19.3809523809524, 19.3809523809524, 21.7916666666667, 21.8333333333333, 23.0416666666667, 23.04, 23.04, 23.04, 25.9310344827586, 26.5172413793103, 29, 29.1, 18.4745762711864, 18.5254237288136, 18.5423728813559, 18.6);
boxplot(cp, main="Class/Package");

# box-plot for the number of attributes per class
ac <- c(1.38235294117647, 1.4393063583815, 1.57219251336898, 1.54716981132075, 1.54716981132075, 1.45635910224439, 1.45635910224439, 1.45566502463054, 1.45700245700246, 1.46683046683047, 1.52390057361377, 1.52099236641221, 1.5244122965642, 1.52777777777778, 1.52777777777778, 1.52777777777778, 1.54521276595745, 1.54876462938882, 1.56436781609195, 1.56128293241695, 1.57706422018349, 1.57639524245197, 1.57678244972578, 1.68010752688172);
boxplot(ac, main="Attribute/Class");

# box-plot for the number of methods per class
mc <- c(8, 8.79190751445087, 8.27807486631016, 8.27547169811321, 8.30943396226415, 8.42643391521197, 8.45137157107232, 8.51477832512315, 8.51351351351351, 8.51351351351351, 8.89292543021032, 8.9293893129771, 8.83182640144665, 8.89583333333333, 8.89930555555556, 8.90451388888889, 8.75132978723404, 8.83355006501951, 8.87931034482759, 8.90034364261168, 9.06330275229358, 9.07776761207685, 9.08043875685558, 9.05286738351255);
pmc <- c(6.20588235294118, 6.85549132947977, 6.54010695187166, 6.52452830188679, 6.54716981132075, 6.58104738154613, 6.59351620947631, 6.6576354679803, 6.65356265356265, 6.65356265356265, 6.95793499043977, 6.99427480916031, 6.90235081374322, 6.89409722222222, 6.89756944444444, 6.89756944444444, 6.61968085106383, 6.61638491547464, 6.53563218390805, 6.55211912943872, 6.64954128440367, 6.65599268069533, 6.64990859232176, 6.69802867383513);
boxplot(mc, pmc, main="Functionality/Class");
axis(1, labels=c("m/c", "pm/c"), at=c(1,2));

# box-plot for the number of dependencies per class
dc <- c(3.54901960784314, 4.64161849710983, 4.57219251336898, 4.63396226415094, 4.64150943396226, 4.72069825436409, 4.72319201995012, 4.78325123152709, 4.78378378378378, 4.78378378378378, 4.88910133843212, 4.88931297709924, 4.42133815551537, 4.45486111111111, 4.45486111111111, 4.45833333333333, 4.66090425531915, 4.6592977893368, 4.74367816091954, 4.73997709049255, 4.88899082568807, 4.89569990850869, 4.89396709323583, 4.94802867383513);
boxplot(dc, main="Dependency/Class");

dev.off();
