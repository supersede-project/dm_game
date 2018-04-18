package eu.supersede.orch.req;

import eu.supersede.orch.kb.DomNode;
import eu.supersede.orch.kb.KnowledgeBase;

public class RequirementsPrioritizationScope
{
	
	private KnowledgeBase kb;
	
	public RequirementsPrioritizationScope( KnowledgeBase kb ) {
		this.kb = kb;
	}
	
	public void addRequirement( Long id, String title, String description ) {
		kb.create( "/requirements", "" + id, "title", title, "description", description );
		kb.update( "/requirements", "size", "" + getRequirementsCount() );
	}

	public int getRequirementsCount() {
		return kb.getNode( "/requirements" ).getChildCount();
	}

	public void setRequirementProperty( Long id, String key, String value ) {
		kb.update( "/requirements/" + id, key, value );
	}

	public void createRanking( String name ) {
		kb.create( "/rankings", name );
	}

	public void addRankingRequirement( String rankingName, long reqId ) {
		int count = kb.getOrCreateNode( "/rankings", rankingName ).getChildCount();
		kb.create( "/rankings/" + rankingName, "" + reqId, "order", "" + count );
	}

	public void addAlert( String userFeedback ) {
		DomNode node = kb.getOrCreateNode( "/", "alerts" );
		kb.create( "/alerts", "" + node.getChildCount(), "userFeedback", userFeedback );
	}

	public void updateRequirement( long reqId, String newText ) {
		kb.update( "/requirements/" + reqId, "title", newText );
	}
	
}
