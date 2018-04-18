package eu.supersede.orch.kb;

public interface DomListener {
	public void onNodeCreated( DomNode node );
	public void onNodeUpdated( DomNode node, String attribute );
	public void onNodeDeleted( DomNode node );
}
