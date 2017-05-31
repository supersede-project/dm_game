package eu.supersede.dm;

public abstract class DMAction {
	
	public abstract String getMethodName();
	
	public abstract String getActionName();
	
    public abstract String getPage(ProcessManager mgr);

    public abstract String getLabel(ProcessManager mgr);

    public abstract String getDescription(ProcessManager mgr);
	
}
