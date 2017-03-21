package eu.supersede.dm;

public abstract class DMCondition {
	
	public abstract boolean isTrue( ProcessManager status );
	
	public static class DMCondNoReqs extends DMCondition {
		public boolean isTrue( ProcessManager status ) {
			return status.getRequirementsCount() < 1;
		}
	}
	
}
