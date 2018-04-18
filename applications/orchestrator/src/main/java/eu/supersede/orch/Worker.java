package eu.supersede.orch;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import eu.supersede.orch.kb.DomNode;
import eu.supersede.orch.kb.KnowledgeBase;
import eu.supersede.orch.qpath.ParseException;
import eu.supersede.orch.qpath.XPath;

public class Worker //implements IWorker 
{
	
	interface OutputDataProcessor {
		void process(KnowledgeBase kb, MethodInstance mi) throws UnsupportedEncodingException, ParseException;
	}
	
	static class OutputDataProcessorV1 implements OutputDataProcessor {

		public void process(KnowledgeBase kb, MethodInstance mi) throws UnsupportedEncodingException, ParseException {

			// For each output item
			for( DomNode node : mi.getDefinition().getOutputTemplate().getRoot().children() ) {

//				System.out.println( node.getName() );

				DomNode action = node.getChild( "action" );

				if( action == null ) continue;

				if( "create".equalsIgnoreCase( action.getAttribute( "type" ) ) ) {

					// Begin
					DomNode foreach = action.getChild( "foreach" );

					if( foreach != null ) {

						for( DomNode source : kb.getNodes( XPath.load( foreach.getAttribute( "source", "" ) ) ) ){
							//end

							DomNode target = kb.getNode( action.getAttribute( "target", null ) );

							for( DomNode template : foreach.children() ) {

								DomNode instance = template.clone( source.getName() );

								target.addChild( instance );

							}

						}

					}
					else { // No "foreach"
						// Do something
					}

				}
				else if( "delete".equalsIgnoreCase( action.getAttribute( "type" ) ) ) {

					if( action.getAttribute( "target", null ) == null ) continue;
					
					List<DomNode> nodes = kb.getNodes( XPath.load( action.getAttribute( "target", null ) ) );
					
					while( nodes.size() > 0 ) {
						kb.delete( nodes.get( 0 ).getPath() );
						nodes.remove( 0 );
					}
					
				}
				else if( "update".equalsIgnoreCase( action.getAttribute( "type" ) ) ) {

				}

			}

		}

	}

	static class OutputDataProcessorV1_countRand implements OutputDataProcessor {

		public void process(KnowledgeBase kb, MethodInstance mi) throws UnsupportedEncodingException, ParseException {

			// For each output item
			for( DomNode node : mi.getDefinition().getOutputTemplate().getRoot().children() ) {

//				System.out.println( node.getName() );

				DomNode action = node.getChild( "action" );

				if( action == null ) continue;

				if( "create".equalsIgnoreCase( action.getAttribute( "type" ) ) ) {
					
					String countFx = action.getAttribute( "count", "rand()" );
					
					Integer n = 0;
					try {
						n = Integer.parseInt( countFx );
					}
					catch( Exception ex ) {
						if( "rand()".equalsIgnoreCase( countFx ) ) {
							n = R.get().nextInt( 100 );
						}
					}
					
//					DomNode foreach = action.getChild( "foreach" );

//					if( foreach != null ) {
//						System.out.println( n );
						for( int i = 0; i < n; i++ ) {
//						for( DomNode source : kb.getNodes( XPath.load( foreach.getAttribute( "source", "" ) ) ) ){

							DomNode target = kb.getNode( action.getAttribute( "target", null ) );

							for( DomNode template : action.children() ) {

								DomNode instance = template.clone( UUID.randomUUID().toString().replaceAll( "[-]", "_" ) );

								target.addChild( instance );

							}

						}

//					}
//					else { // No "foreach"
//						// Do something
//					}

				}
				else if( "delete".equalsIgnoreCase( action.getAttribute( "type" ) ) ) {

					if( action.getAttribute( "target", null ) == null ) continue;
					
					List<DomNode> nodes = kb.getNodes( XPath.load( action.getAttribute( "target", null ) ) );
					
					while( nodes.size() > 0 ) {
						kb.delete( nodes.get( 0 ).getPath() );
						nodes.remove( 0 );
					}
					
				}
				else if( "update".equalsIgnoreCase( action.getAttribute( "type" ) ) ) {

				}

			}

		}

	}

	static Map<String,OutputDataProcessor> processors = new HashMap<>();

	static {

		processors.put( "v1.0", new OutputDataProcessorV1() );
		processors.put( "v1.0-countRand", new OutputDataProcessorV1_countRand() );

	}


	private HistoricalData historicalData;
	private MethodDefinition md;

	public Worker( MethodDefinition md, HistoricalData p ) {
		this.md = md;
		this.historicalData = p;
	}

	public MethodDefinition getMethodDefinition() {
		return this.md;
	}

	public HistoricalData getHistoricalData() {
		return this.historicalData;
	}

	//	@Override
	public void process(KnowledgeBase kb, MethodInstance mi) {

		//			System.out.println( "Running " + mi.getDefinition().getName() + " method" );

		// Import actual input
		for( String valueName : md.getInputValues() ) {

			MethodValue val = md.getInputValue( valueName );

			if( (val.isOptional() ? 0 : 1) + R.get().nextInt( 2 ) > 0 ) {

				DomNode input = mi.getInputs().create( "/" + valueName );

				try
				{

					List<DomNode> nodes = kb.getNodes( XPath.load( val.getSource() ) );

					ArrayList<Integer> shuffle = new ArrayList<>();
					for( int i = 0; i < nodes.size(); i++ ) {
						shuffle.add( i );
					}
					Collections.shuffle( shuffle, R.get().getRandom() );

					while( shuffle.size() >= val.getMin() && shuffle.size() < val.getMax() ) {

						DomNode node = nodes.get( shuffle.remove( 0 ) );

						input.addChild( node.clone() );

					}
					//					for( int i = val.getMin(); i < val.getMax() +1; i++ ) {
					//
					//						DomNode node = nodes.get( shuffle.remove( 0 ) );
					//
					//						input.addChild( node.clone() );
					//
					//					}

				}
				catch (UnsupportedEncodingException | eu.supersede.orch.qpath.ParseException e) {
					e.printStackTrace();
				}

			}

		}

		OutputDataProcessor proc = processors.get( md.getOutputTemplate().getRoot().getAttribute( "format", "" ) );

		if( proc != null ) {
			try
			{
				proc.process( kb, mi );
			}
			catch (UnsupportedEncodingException | ParseException e) {
				e.printStackTrace();
			}
		}
		else {
//			System.out.println( "No workers for method " + md.getName() );
		}

		//		// TODO: produce output
		//		for( String valueName : md.getOutputValues() ) {
		//
		//			MethodValue val = md.getInputValue( valueName );
		//
		//			DomNode outputNode = mi.getInputs().create( "/" + valueName );
		//
		//			try
		//			{
		//
		//				List<DomNode> sourceNodes = kb.getNodes( XPath.load( val.get( "source", "" ) ) );
		//				List<DomNode> targetNodes = kb.getNodes( XPath.load( val.get( "target", "" ) ) );
		//
		//				try {
		//					/*
		//					 * TODO:
		//					 * - produce a Document containing all the source nodes
		//					 * - transform the Document
		//					 * - re-assign the content of the Document to the DOM
		//					 */
		//
		//					Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		//
		//					for( DomNode node : sourceNodes ) {
		//
		//
		//
		//					}
		//
		//				} catch (ParserConfigurationException e) {
		//					// TODO Auto-generated catch block
		//					e.printStackTrace();
		//				}
		//
		//				for( int i = val.getMin(); i < val.getMax(); i++ ) {
		//
		//
		//
		//				}
		//
		//			}
		//			catch (UnsupportedEncodingException | ParseException e) {
		//				e.printStackTrace();
		//			}
		//
		//		}

	}

}