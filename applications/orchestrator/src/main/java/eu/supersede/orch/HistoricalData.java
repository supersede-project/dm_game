package eu.supersede.orch;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import eu.supersede.orch.util.XmlNode;

public class HistoricalData {
	
	Map<String,String> map = new HashMap<>();
	
	public static HistoricalData load( File file ) {
		
		HistoricalData h = new HistoricalData();
		
		load( file, h );
		
		return h;
		
	}
	
	public static void load( File file, HistoricalData h ) {
		
		XmlNode xml = XmlNode.load( file );
		
		load( h, xml, "" );
		
	}
	
	private static void load( HistoricalData h, XmlNode node, String prefix ) {
		
		String content = node.getContent();
		
		if( content != null ) {
			
			h.map.put( prefix + node.getTag(), content );
			
		}
		
		for( XmlNode child : node.getChildren() ) {
			
			load( h, child, prefix + node.getTag() + "." );
			
		}
		
	}
	
}
