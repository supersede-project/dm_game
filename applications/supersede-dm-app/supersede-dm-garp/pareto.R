library(calibrate)

systems <- c("System","Timeline","SceneSelection")
distances <- c("DELTA","KENDALL") #,"SPEARMAN")
#weights <- c("RANDOM", "EQUAL", "INPUT")
weights <- c("EQUAL", "INPUT")
for (system in systems){
	for (distance in distances){
		for (weight in weights){
			read.csv(paste("output/",system,"_",distance,"_",weight,'_pareto.csv',sep="")) ->d
			read.csv(paste("output/",system,"_",distance,"_",weight,'_ahp_pareto_average.csv',sep="")) -> dahp_avg
			read.csv(paste("output/",system,"_",distance,"_",weight,'_ahp_pareto_negotiator.csv',sep="")) -> dahp_neg
			read.csv(paste("output/",system,"_",distance,"_",weight,'_soga_pareto.csv',sep="")) -> dsoga
			# determine maximum/minimum x/y values
			min(d$c1,dahp_neg$c1,dahp_avg$c1,dsoga$c1) -> xmin
			max(d$c1,dahp_neg$c1,dahp_avg$c1,dsoga$c1) -> xmax
			min(d$c2,dahp_neg$c2,dahp_avg$c2,dsoga$c2) -> ymin
			max(d$c2,dahp_neg$c2,dahp_avg$c2,dsoga$c2) -> ymax

			pdf(paste("output/",system,"_",distance,"_",weight,"_pareto.pdf",sep=""))

			# Add extra space to right of plot area; change clipping to figure
			par(mar=c(5.1, 4.1, 4.1, 8.1), xpd=TRUE)

			with(d, {
			plot(c1,c2,cex.main=1,cex.lab=1,cex.axis=1,xlab="C1 (Development Effort)", ylab="C2 (User Impact)", main=paste("Optimal Prioritizations: ", system, sep=""),col="red", xlim = c(xmin, xmax), ylim = c(ymin, ymax), pch=3)
			#textxy(c1,c2,ranking,cex = 1,offset=0.55,cex.axis=1)
			})
			points(dahp_avg, col='blue',pch=7)
			points(dahp_neg, col='green',pch=4)
			points(dsoga, col='black',pch=0)

			# add legend
			#legend("topright", inset=c(-0.3,0),title="Techniques",c("NSGA","SGA","AHP_AVG","AHP_NEG"),pch=c(3,0,7,4), col=c("red","black","blue","green"))
			legend("topright", title="Techniques",c("NSGA","SGA","AHP_AVG","AHP_NEG"),pch=c(3,0,7,4), col=c("red","black","blue","green"))

			dev.off()
		}
	}
}

