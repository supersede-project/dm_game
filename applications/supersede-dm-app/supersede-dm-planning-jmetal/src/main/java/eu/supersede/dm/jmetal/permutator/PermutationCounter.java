package eu.supersede.dm.jmetal.permutator;

public class PermutationCounter {
	
	int[]	index;
	int[]	imax;
	boolean	done = false;
	
	public PermutationCounter( int variableCount, int[] imax )
	{
		index = new int[variableCount];
		this.imax = imax;
		for( int n : imax )
			if( n < 1 )
				done = true;
	}
	
	void increase( int n )
	{
		if( n >= index.length )
		{
			done = true;
			return;
		}
		
		index[n]++;
		
		if( index[n] >= imax[n] )
		{
			index[n] = 0;
			increase( n+1 );
		}
	}
	
	public void increase()
	{
		increase( 0 );
	}
	
	public int[] indexes()
	{
		return this.index;
	}

	public boolean isMax()
	{
		return done;
	}
}