package eu.supersede.dm.depcheck.data;

import java.util.ArrayList;
import java.util.List;

import com.orientechnologies.orient.core.annotation.OAfterDeserialization;
import com.orientechnologies.orient.core.annotation.OBeforeSerialization;

public class XRequirement {
	
	public List<String> getDependencies() {
		return dependencies;
	}

	public void setDependencies(List<String> dependencies) {
		this.dependencies = dependencies;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setTopic(XTopic topic) {
		this.topic = topic;
	}


	static long idcount;
	
	@OBeforeSerialization public void genId() {
		if( id == null ) {
			id = "" + (++idcount);
		}
	}
	
	@OAfterDeserialization public void deserialized() {
		System.out.println( "Deserialized" );
	}
	
	
	String id;
	
	public String text;
	
	XTopic topic;
	
	List<String> dependencies = new ArrayList<>();
	
	
	public XRequirement() {
		
	}
	
	public XRequirement( String id, String text ) {
		this( id, text, new XTopic( "<unspecified>" ) );
	}
	
	public XRequirement( String id, String text, XTopic topic ) {
		this.text = text;
		this.topic = topic;
	}
	
	public XTopic getTopic() {
		return this.topic;
	}
	
	public String getText() {
		return this.text;
	}
	
	public String getId() {
		return this.id;
	}
	
	public XRequirement clone() {
		XRequirement r = new XRequirement( getId(), text );
		for( String d : dependencies ) {
			r.dependencies.add( d );
		}
		r.topic = topic;
		return r;
	}
	
	public String toString() {
		String string = "";
		string += getId();
		string += ": " + getText();
		if( topic != null ) {
			string += " [" + topic.getName() + "]";
		}
		if( dependencies.size()> 0 ) {
			string += " deps:";
			for( String dep : dependencies ) {
				string += dep + ";";
			}
		}
		return string;
	}
	
}
