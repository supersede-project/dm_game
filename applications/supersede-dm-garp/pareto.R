library(calibrate)

read.csv('pareto.csv') ->d
pdf("pareto.pdf")
with(d, {
plot(c1,c2,cex.main=1,cex.lab=1,cex.axis=1,xlab="C1 (Development Cost)", ylab="C2 (Value to Client)", main="Optimal Prioritisations",col="red")
textxy(c1,c2,ranking,cex = 1,offset=0.55,cex.axis=1)
})
dev.off()

