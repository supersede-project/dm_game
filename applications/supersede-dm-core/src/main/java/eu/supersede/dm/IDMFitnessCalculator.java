package eu.supersede.dm;

public interface IDMFitnessCalculator {
	
	public DMFitness evaluate( DMActivity cfg, DMProblem problem );
	
}