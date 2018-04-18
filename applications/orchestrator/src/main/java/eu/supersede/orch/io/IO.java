package eu.supersede.orch.io;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import eu.supersede.orch.Measure;
import eu.supersede.orch.MethodDefinition;
import eu.supersede.orch.MethodOption;
import eu.supersede.orch.MethodValue;
import eu.supersede.orch.OptimalityModel;
import eu.supersede.orch.Objective;
import eu.supersede.orch.Organization;
import eu.supersede.orch.ProcessSpace;
import eu.supersede.orch.R;
import eu.supersede.orch.kb.DOM;
import eu.supersede.orch.kb.DomNode;
import eu.supersede.orch.util.XmlNode;

public class IO {
	
	private static IO instance = new IO();
	
	public static IO get() {
		return instance;
	}
	
	private IO() {}
	
	public void loadProcessSpace( File file, ProcessSpace space ) {
		XmlNode xml = null;
		xml = XmlNode.load( file );
		for( XmlNode node : xml.item( "methods" ).getChildren() ) {
			load( node, space );
		}
	}
	
	public ProcessSpace loadProcessSpace( File file ) {
		ProcessSpace space = new ProcessSpace();
		loadProcessSpace( file, space );
		return space;
	}
	
	private void load( XmlNode xml, ProcessSpace space) {
		MethodDefinition md = new MethodDefinition( xml.getTag() );
		md.setObjective( xml.item( "objective" ).getAttr( "name" ) );
		space.add( md );
		
		// Load configuration options
		for( XmlNode node : xml.item( "configuration" ).item( "options" ).getChildren( "option" ) ) {
			MethodOption option = new MethodOption( node.getAttr( "name" ) );
			option.setCardinality( MethodOption.CARDINALITY.valueOf( node.getAttr( "cardinality", "single" ).toUpperCase() ) );
			option.setDatatype( MethodOption.DATATYPE.valueOf( node.getAttr( "datatype", "string" ).toUpperCase() ) );
			md.addOption( option );
		}
		
		// Load expected input list
		for( XmlNode node : xml.item( "configuration" ).item( "inputlist" ).getChildren( "input" ) ) {
			MethodValue val = new MethodValue( node.getAttr( "name" ) );
			try{ val.setOptional( Boolean.parseBoolean( node.getAttr( "optional", "true" ) ) ); } catch( Exception ex ) {};
			val.setSource( node.getAttr( "source", "" ) );
			String str = node.getAttr( "cardinality", null );
			if( str != null && !"".equals( str ) ) {
				String[] parts = str.trim().split( "[;]" );
				if( parts.length == 2 ) {
					try {
						val.setCardinality( Integer.parseInt( parts[0] ), Integer.parseInt( parts[1] ) );
					}
					catch( Exception ex ) {}
				}
			}
			md.addInputValue( val );
		}
		
		// Load output list
//		for( XmlNode node : xml.item( "configuration" ).item( "outputlist" ).getChildren( "output" ) ) {
//			MethodValue val = new MethodValue( node.getAttr( "name" ) );
//			for( String key : node.listAttributes() ) {
//				val.set( key, node.getAttr( key ) );
//			}
//		}
		if( xml.item( "configuration" ).item( "outputdata" ).exists() ) {
			md.getOutputTemplate().setAttribute( "/", "format", xml.item( "configuration" ).item( "outputdata" ).getAttr( "format", "v1.0" ) );
		}
		for( XmlNode node : xml.item( "configuration" ).item( "outputdata" ).getChildren( "outputitem" ) ) {
			md.addOutputValue( node.getAttr( "id" ) );
			xml2Dom( node, md.getOutputTemplate(), "" );
		}
		
		// Load optimality fragment
		if( xml.item( "optimality" ).exists() ) {
			OptimalityModel model = loadOptimalityModel( xml.item( "optimality" ) );
			if( model != null ) {
				md.setOptimalityModel( model );
			}
		}
	}

	public OptimalityModel loadOptimalityModel( XmlNode xml ) {
		
		OptimalityModel model = new OptimalityModel();
		
		loadOptimalityModel( xml, model );
		
//		for( XmlNode node : xml.item( "measures" ).getChildren() ) {
//			
//			Measure e = new Measure( node.getTag(), node.getAttr( "id" ) );
//			
//			for( XmlNode child : node.item( "variables" ).getChildren() ) {
//				
//				String id = child.getAttr( "value", child.getTag() );
//				e.setVariable( id, child.getAttr( "location", child.getValue( "" ) ) );
//				
//			}
//			
//			if( node.item( "code" ).exists() ) {
//				e.setCode( node.item( "code" ).getValue() );
//			}
//			
//			e.setTrigger( Measure.TRIGGER.fromString( node.getAttr( "trigger", "beforeTaskStarts" ) ) );
//			
//			model.addMeasure( e );
//		}
		
		return model;
	}

	public OptimalityModel loadOptimalityModel( String path ) {

		File file = new File( path );
		if( !file.exists() ) {
			throw new RuntimeException( "Model file '" + path + "' does not exist" );
		}

		XmlNode xml = XmlNode.load( file );
		
		OptimalityModel model = loadOptimalityModel( xml );
		
		return model;
	}

	public void loadOptimalityModel( XmlNode xml, OptimalityModel model) {
		
		for( XmlNode node : xml.item( "measures" ).getChildren() ) {
			
			Measure e = new Measure( node.getTag(), node.getAttr( "id" ) );
			
			for( XmlNode child : node.item( "variables" ).getChildren() ) {
				
				String id = child.getAttr( "value", child.getTag() );
				e.setVariable( id, child.getAttr( "location", child.getValue( "" ) ) );
				
			}
			
			if( node.item( "code" ).exists() ) {
				e.setCode( node.item( "code" ).getValue() );
			}
			
			e.setTrigger( Measure.TRIGGER.fromString( node.getAttr( "trigger", "beforeTaskStarts" ) ) );
			
			model.addMeasure( e );
		}
		
	}
	
	public List<Objective> loadPreferences(File file) {
		List<Objective> set = new ArrayList<>();
		XmlNode xml = XmlNode.load( file );
		for( XmlNode node : xml.item( "objectives" ).getChildren( "objective" ) ) {
			set.add( new Objective( 
					node.getAttr( "name" ), 
					Objective.GOAL.valueOf( node.getAttr( "goal" ).toUpperCase() ) ) );
		}
		return set;
	}
	
	public DOM loadDOM( File file ) {
		DOM dom = new DOM();
		XmlNode xml = XmlNode.load( file );
		xml2Dom( xml, dom, "" );
		return dom;
	}
	
	public Organization loadOrganizationDescription( File file ) {
		return new Organization( loadDOM( file ) );
	}
	
	private void xml2Dom( XmlNode xml, DOM targetDom, String path ) {
		String id = xml.getAttr( "id", xml.getTag() );
		if( targetDom.getNode( path + "/" + id ) != null ) {
			id = "id" + R.get().getRandom().nextInt( Integer.MAX_VALUE );
		}
		DomNode domNode = targetDom.create( path + "/" + id );
		for( String key : xml.listAttributes() ) {
			if( !"id".equals( key.toLowerCase() ) ) {
				domNode.setAttribute( key, xml.getAttr( key ) );
			}
		}
		for( XmlNode child : xml.getChildren() ) {
			xml2Dom( child, targetDom, path + "/" + id );
		}
	}

}
