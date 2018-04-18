package eu.supersede.orch.qpath;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class XPath {
	
	List<XPart> parts = new ArrayList<>();
	private XPart root;
	
	public static XPath load( String tql ) throws UnsupportedEncodingException, ParseException {
		return new XPathGrammar( new ByteArrayInputStream( tql.getBytes(StandardCharsets.UTF_8.name())) ).load();
	}
	
	public void add( XPart part) {
		parts.add( part );
	}
	
	public List<XPart> parts() {
		return parts;
	}

	public void setRoot( XPart root ) {
		this.root = root;
	}
	
	public XPart getRoot() {
		return this.root;
	}
	
	public String toString() {
		
		String ret = "";
		
		if( root != null ) ret += root;
		
		for( XPart part : parts() ) {
			ret += ( " / " + part );
		}
		
		return ret;
		
	}
	
}
