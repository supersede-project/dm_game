package eu.supersede.orch;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import eu.supersede.orch.Measure.TRIGGER;
import eu.supersede.orch.Simulator.Callback;
import eu.supersede.orch.kb.DomNode;
import eu.supersede.orch.kb.KnowledgeBase;
import eu.supersede.orch.qpath.XPath;
import eu.supersede.orch.qpath.XPathGrammar;

public class Evaluator {
	
	Execution lastExec = null;
	
	public Optimality eval( Process process, KnowledgeBase kb, OptimalityModel model, Simulator simulator ) {
		
		Optimality o = new Optimality();
		
		lastExec = simulator.execute( process, kb, new Callback() {
			
			void handleTrigger( KnowledgeBase kb, Execution ex, TRIGGER trigger ) {
				
				for( Measure e : model.entities( "indicator" ) ) {
					
					if( e.getTrigger() == trigger ) {
						
						calculate( e, ex );
					}
					
				}
				
				for( Measure e : model.entities( "accumulator" ) ) {
					
					if( e.getTrigger() == trigger ) {
						accumulate( e, ex );
					}
					
				}
				
			}
			
			@Override
			public void beforeTaskExecution( KnowledgeBase kb, Execution ex ) {
				handleTrigger( kb, ex, TRIGGER.BEFORETASKSTARTS );
			}
			@Override
			public void afterTaskExecution( KnowledgeBase kb, Execution ex ) {
				handleTrigger( kb, ex, TRIGGER.AFTERTASKENDS );
			}
			@Override
			public void beforeSimulationStarts( KnowledgeBase kb, Execution ex ) {
				handleTrigger( kb, ex, TRIGGER.BEFORESIMULATIONSTARTS );
			}
			@Override
			public void afterSimulationEnds( KnowledgeBase kb, Execution ex ) {
				handleTrigger( kb, ex, TRIGGER.AFTERSIMULATIONENDS );
			}
			
		} );
		
		
		for( Measure e : model.entities( "accumulator" ) ) {
			
			DomNode node = kb.getNode( "/evaluation/" + e.getId() );
			
			if( node != null ) {
				
				try
				{
					Number val = NumberFormat.getInstance().parse( node.getAttribute( "value", "0" ) );
					
					o.add( e.getId(), val.doubleValue() );
					
				}
				catch (ParseException e1) {
					
					e1.printStackTrace();
					
					o.add( e.getId(), 0.0 );
					
				}
				
			}
			
		}
		
		return o;
	}
	
	public Execution getExecution() {
		return this.lastExec;
	}
	
	List<DomNode> doQuery( String tql, Execution execution ) {
		
		try
		{
			XPath path = new XPathGrammar( new ByteArrayInputStream( tql.getBytes(StandardCharsets.UTF_8.name())) ).load();
			
			List<DomNode> nodes = execution.getKB().getNodes( path );
			
			if( nodes != null ) return nodes;
			
		} catch( Exception e) {
			e.printStackTrace();
		}
		
		return new ArrayList<>();
	}
	
	public void calculate( Measure entity, Execution ex ) {
		
		{
			
			JavaScript.get().reset();
			for( String key : entity.variables() ) {
				String var = entity.getVariable( key, "" );
				if( var.indexOf( "*" ) == -1 ) {
					List<DomNode> nodes = doQuery( var, ex );
					if( nodes.size() < 1 ) {
						return;
//						JavaScript.get().put( key, null );
					}
					else {
						JavaScript.get().put( key, nodes.get( 0 ) );
					}
				}
				else {
					List<DomNode> nodes = doQuery( var, ex );
					JavaScript.get().put( key, nodes );
				}
			}
			try
			{
				String output = JavaScript.get().eval( entity.getCode() ).toString();
//				System.out.println( "[" + entity.getId() + "] :: " + output );
				Number val = null;
				try{ val = NumberFormat.getInstance().parse( output ); } catch( Exception exc ) { val = new Integer(0); }
				ex.getKB().update( "/evaluation/" + entity.getId(), "value", "" + val );
			}
			catch( Exception e ) {
				e.printStackTrace();
			}
			
		}
		
	}
	
	void accumulate( Measure entity, Execution ex ) {
		
		{
			
			JavaScript.get().reset();
			for( String key : entity.variables() ) {
				String var = entity.getVariable( key, "" );
				if( var.indexOf( "*" ) == -1 ) {
					List<DomNode> nodes = doQuery( var, ex );
					if( nodes.size() < 1 ) {
						continue;
					}
					else {
						JavaScript.get().put( key, nodes.get( 0 ) );
					}
				}
				else {
					List<DomNode> nodes = doQuery( var, ex );
					JavaScript.get().put( key, nodes );
				}
			}
			try
			{
				Number number = null;
				try{ number = NumberFormat.getInstance().parse( JavaScript.get().eval( entity.getCode() ).toString() ); } catch( Exception exc ) { number = new Integer(0); }
				
				DomNode target = ex.getKB().getOrCreateNode( "/evaluation", entity.getId() );
				
				Double val = number.doubleValue() + Double.parseDouble( target.getAttribute( "value", "0.0" ) );
				
				ex.getKB().update( "/evaluation/" + entity.getId(), "value", "" + val );
			}
			catch( Exception e ) {
				e.printStackTrace();
			}
			
		}
		
	}

}
