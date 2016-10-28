package eu.supersede.dm.agent;

public class Workflow {
	
	Context context = new Context();
	
	RESTClient rest;
	
	public Workflow( RESTClient rest ) {
		this.rest = rest;
	}
	
	public void execute( TestTask task ) {
		task.execute( rest, this.context );
	}

	public Context getContext() {
		return this.context;
	}
	
}
