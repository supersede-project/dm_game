package eu.supersede.orch;

import java.util.Random;

public class R
{
	Random r = new Random();
	
	private static R instance = new R();
	
	public static R get() {
		return instance;
	}
	
	public void setSeed( long seed ) {
		r = new Random( seed );
	}

	public Random getRandom() {
		return this.r;
	}
	
	public int nextInt( int bound ) {
		return this.r.nextInt( bound );
	}
	
	public int createRandomChoice( int bound ) {
		return getRandom().nextInt( bound );
	}
	
}
