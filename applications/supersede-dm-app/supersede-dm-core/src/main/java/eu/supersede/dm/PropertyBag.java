package eu.supersede.dm;

import java.util.List;

import eu.supersede.gr.model.HActivity;
import eu.supersede.gr.model.HProperty;

public class PropertyBag {
	
	Long id;
	
	public PropertyBag() {}
	
	public PropertyBag( HActivity a ) {
		this.id = a.getPropertyBag(); //.getId();
	}
	
	public void set( String key, String value ) {
		HProperty p = DMGame.get().jpa.properties.findOne( id );
		if( p == null ) {
			p = new HProperty();
		}
		p.setPropertyBagId( this.id );
		p.setKey( key );
		p.setValue( value );
		DMGame.get().jpa.properties.save( p );
	}
	
	public String get( String key, String def ) {
		List<HProperty> list = DMGame.get().jpa.properties.find( id, key );
		if( list == null ) {
			return def;
		}
		if( list.size() < 1 ) {
			return def;
		}
		return list.get( 0 ).getValue();
	}
	
	public List<HProperty> properties() {
		return DMGame.get().getJpa().properties.findByPropertyBag( id );
	}
	
}
