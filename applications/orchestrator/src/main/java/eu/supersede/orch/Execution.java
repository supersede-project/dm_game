package eu.supersede.orch;

import java.util.Map;

import eu.supersede.orch.kb.AttributeSet;
import eu.supersede.orch.kb.DomNode;
import eu.supersede.orch.kb.KnowledgeBase;
import eu.supersede.orch.kb.KnowledgeBase.Listener;
import eu.supersede.orch.kb.Transaction;

public class Execution implements Listener {
	
	KnowledgeBase kb = new KnowledgeBase();
	
	Trace trace = new Trace();
	
	public void init( KnowledgeBase kb ) {
		
		this.kb = kb;
		
		kb.create( "/execution", "path" );
		kb.create( "/execution", "current" );
		
		this.kb.addListener( this );
		
	}

	@Override
	public void onNodeCreated(DomNode node) {
		record( CRUD.Create, node );
	}

	@Override
	public void onNodeUpdated(DomNode node, Map<String, String> diff) {
		record( CRUD.Update, node, diff );
	}

	@Override
	public void onNodeDeleted(DomNode node) {
		record( CRUD.Delete, node );
	}
	
	DomNode target = null;
	
	private void record( CRUD type, DomNode node ) {
		record( type, node, null );
	}
	
	private void record( CRUD type, DomNode node, Map<String, String> diff ) {
		Operation op = new Operation();
		op.setType( type );
		op.setPath( node.getPath() );
		if( diff != null ) {
			AttributeSet set = new AttributeSet();
			for( String key : diff.keySet() ) {
				set.put( key, diff.get( key ) );
			}
			op.setAttributes( set );
		}
		else {
			op.setAttributes( node.getAttributes().clone() );
		}
		op.setStep( getStep() );
		trace.append( op );
	}
	
	public long getStep() {
		return Integer.parseInt( kb.getNode( "/execution/current" ).getAttribute( "step", "0" ) );
	}
	
	public void setCurrent( MethodInstance mi ) {
//		kb.beginTransaction();
		kb.create( "/execution/path", "" + getStep(), "method", mi.getDefinition().getName() );
		kb.update( "/execution/current", "step", "" + (getStep() +1) );
		kb.update( "/execution/current", "method", mi.getDefinition().getName() );
		kb.create( "/execution/current/config", mi.getConfiguration().clone().getRoot() );
//		kb.endTransaction();
	}
	
	public Trace getTrace() {
		return trace;
	}

	public DomNode getCurrent() {
		return kb.getNode( "/execution/current" );
	}
	
//	public DomNode getCurrentMethodInstanceNode() {
//		return kb.getNode( "/execution/current" );
//	}
	
	@Override
	public void onTransactionExecuted(Transaction tx) {
		// TODO Auto-generated method stub
		
	}

	public KnowledgeBase getKB() {
		return this.kb;
	}

}
