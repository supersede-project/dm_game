package eu.supersede.dm;

public interface IDMFitnessCalculator {
	
	public DMFitness evaluate( DMActivityConfiguration cfg, DMProblem problem );
	
}