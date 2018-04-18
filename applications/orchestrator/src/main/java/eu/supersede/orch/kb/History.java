package eu.supersede.orch.kb;

import java.util.LinkedList;

public class History
{
	LinkedList<Transaction> past = new LinkedList<Transaction>();
	
	LinkedList<Transaction> future = new LinkedList<Transaction>();
	
	public void push( Transaction tx )
	{
		past.push( tx );
		
		if( past.size() > 100 )
			past.remove( 0 );
		
		future.clear();
	}
	
	public Transaction undo()
	{
		Transaction tx = past.pop();
		
		future.push( tx );
		
		return tx;
	}
	
	public Transaction redo()
	{
		Transaction tx = future.pop();
		
		past.push( tx );
		
		if( past.size() > 100 )
			past.remove( 0 );
		
		return tx;
	}

	public boolean hasPast()
	{
		return past.size() == 0;
	}

	public boolean hasFuture()
	{
		return future.size() > 0;
	}
}
