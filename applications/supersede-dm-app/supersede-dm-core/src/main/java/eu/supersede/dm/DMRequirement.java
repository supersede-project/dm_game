package eu.supersede.dm;

import java.util.ArrayList;
import java.util.List;

public class DMRequirement extends DMItem implements Cloneable {
	
	String text;
	
	DMTopic topic = DMTopic.none;
	
	List<String> dependencies = new ArrayList<>();
	
	public DMRequirement( String id, String text ) {
		this( id, text, DMTopic.none );
	}
	
	public DMRequirement( String id, String text, DMTopic topic ) {
		super( id, DMItemType.Requirement );
		this.text = text;
		this.topic = topic;
	}
	
	public DMTopic getTopic() {
		return this.topic;
	}
	
	public String getText() {
		return this.text;
	}
	
	public DMRequirement clone() {
		DMRequirement r = new DMRequirement( getId(), text );
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
