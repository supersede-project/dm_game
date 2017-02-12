package eu.supersede.dm;

public abstract class DMCondition {
	
	public abstract boolean isTrue( DMStatus status );
	
	public static class DMCondNoReqs extends DMCondition {
		public boolean isTrue( DMStatus status ) {
			return status.getRequirementsCount() < 1;
		}
	}
	
}
