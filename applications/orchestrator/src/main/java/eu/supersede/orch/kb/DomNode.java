package eu.supersede.orch.kb;

import java.io.PrintStream;

public class DomNode
{
	protected DOM									dom;
	private DomNode									parent		= null;
	private final String							name;
	private String									id			= null;
	private AttributeSet							attributes	= new AttributeSet();
	private final SortedMap<String,DomNode>			children	= new SortedMap<String,DomNode>();
	
	private Object									value;
	
	protected DomNode( String name ) {
		this.name = name;
	}
	
	/**
	 * Returns the ID of the node, if set.
	 * A node is not required to have an ID. If set, the node is indexed by the containing DOM, 
	 * and can therefore be quickly accessed through its ID, without having to specify its full path
	 * @see technicalia.dom.INode#getId()
	 */
	
	public String getId() {
		return this.id;
	}
	
	public AttributeSet getAttributes() {
		return attributes;
	}
	
	/**
	 * Returns the user-defined element associated to this node
	 * 
	 * @see technicalia.dom.INode#getElement()
	 */
	
	@SuppressWarnings("unchecked")
	public <T> T getElement() {
		return (T)value;
	}
	
	/** Associates a user-defined element to this node.
	 * @see technicalia.dom.INode#setElement(java.lang.Object)
	 */
	
	public void setElement( Object o ) {
		this.value = o;
	}
	
	/* (non-Javadoc)
	 * @see technicalia.dom.INode#getChild(int)
	 */
	
	public DomNode getChild( int index ) {
		return children.get( index );
	}
	
	/* (non-Javadoc)
	 * @see technicalia.dom.INode#getChildCount()
	 */
	
	public int getChildCount() {
		return children.size();
	}
	
	/* (non-Javadoc)
	 * @see technicalia.dom.INode#getChildCount(java.lang.String)
	 */
	
//	public int getChildCount( String tag ) {
//		return children.count( tag );
//	}
	
	/* (non-Javadoc)
	 * @see technicalia.dom.INode#getLastChild()
	 */
//	
	public DomNode getLastChild() {
		if( children.size() < 1 )
			return null;
		return children.get( children.size() -1 );
	}
	
	/* (non-Javadoc)
	 * @see technicalia.dom.INode#isRoot()
	 */
	
	public boolean isRoot() {
		return parent == null;
	}
	
	/* (non-Javadoc)
	 * @see technicalia.dom.INode#addChild(technicalia.dom.DomNode)
	 */
	
	public DomNode addChild( DomNode node ) {
		children.put( node.getName(), node );
		node.setParent( this );
		node.setDom( dom );
//		dom.register( node );
		
		return node;
	}
	
	public DomNode clone() {
		DomNode clone = new DomNode( this.name );
		copyAttributes( this, clone );
		cloneChildren( this, clone );
		return clone;
	}
	
	public DomNode clone( String newname) {
		DomNode clone = new DomNode( newname );
		copyAttributes( this, clone );
		cloneChildren( this, clone );
		return clone;
	}
	
	private void copyAttributes( DomNode from, DomNode to ) {
		
		for( String key : from.attributes() ) {
			to.setAttribute( key, from.getAttribute( key, "" ) );
		}
		
	}
	
	private void cloneChildren( DomNode from, DomNode to ) {
		
		for( DomNode child : from.children() ) {
			
			DomNode node = to.addChild( child.getName() );
			copyAttributes( child, node );
			cloneChildren( child, node );
			
		}
		
	}
	
	/* (non-Javadoc)
	 * @see technicalia.dom.INode#removeChild(java.lang.String)
	 */
	
	void removeChild( String name ) {
		DomNode child = children.get( name );
		removeChild( child );
	}
	
	/* (non-Javadoc)
	 * @see technicalia.dom.INode#removeChild(technicalia.dom.INode)
	 */
	
	void removeChild( DomNode child ) {
		if( children.containsValue( child ) == false ) return;
//		dom.unregister( child );
//		dom.onNodeDeleted( child );
		children.remove( child.getName() );
	}
	
	/* (non-Javadoc)
	 * @see technicalia.dom.INode#getDomAttribute(java.lang.String, java.lang.String)
	 */
	
	public DomAttribute getDomAttribute( String attribute, String def ) {
		DomAttribute attr = attributes.get( attribute );
		
		if( attr == null )
			return new DomAttribute( def );
		
		return attr;
	}
	
	protected String readAttribute( String name, String field, String def ) {
		DomAttribute attr = attributes.get( name );
		
		if( attr == null )
			return def;
		
		if( field == null )
			return attr.getValue();
		
		String ret = attr.getField( field );
		
		if( ret == null ) return def;
		
		return ret;
	}
	
	public String getAttribute( String attribute, String field, String def ) {
		return readAttribute(attribute, field, def);
	}
	
	public final String getAttribute( String attribute ) {
		return readAttribute( attribute, null, null );
	}
	
	public final String getAttribute( String attribute, String def ) {
		return readAttribute( attribute, null, def );
	}
	
	public String getName() {
		return this.name;
	}
	
	protected void writeAttribute( String key, String value ) {
		attributes.put( key, new DomAttribute( value ) );
	}
	
	public void setAttribute( String key, String value ) {
		writeAttribute( key, value );
	}
	
	public DomNode getParent() {
		return this.parent;
	}
	
	/* (non-Javadoc)
	 * @see technicalia.dom.INode#child(java.lang.String)
	 */
	
	public DomNode child( String key ) {
		DomNode node = children.get( key );
		if( node == null ) node = addChild( key );
		return node;
	}
	
	/* (non-Javadoc)
	 * @see technicalia.dom.INode#getChild(java.lang.String)
	 */
	
	public DomNode getChild( String key ) {
		return children.get( key );
	}
	
	/* (non-Javadoc)
	 * @see technicalia.dom.INode#getChild(java.lang.String, int)
	 */
	
//	public DomNode getChild( String key, int index ) {
//		return children.get( key, index );
//	}
	
	/* (non-Javadoc)
	 * @see technicalia.dom.INode#addChild(java.lang.String)
	 */
	
	public DomNode addChild( String childName ) {
		return addChild( new DomNode( childName ) );
	}
	
	/* (non-Javadoc)
	 * @see technicalia.dom.INode#attributes()
	 */
	
	public Iterable<String> attributes() {
		return attributes.keySet();
	}
	
	/* (non-Javadoc)
	 * @see technicalia.dom.INode#getAttributeCount()
	 */
	
	public int getAttributeCount() {
		return attributes.size();
	}
	
	/* (non-Javadoc)
	 * @see technicalia.dom.INode#toString()
	 */
	
	// Shortcut for JS interpretation
	public String value() {
		return getAttribute( "value", "0" );
	}
	
	public String toString() {
		StringBuilder b = new StringBuilder();
		
		b.append( "<" + name );
		if( attributes.size() > 0 )
		{
			b.append( " properties=\"" );
			for( String s : attributes() )
			{
				b.append( s + ":" + getAttribute(s,"") + ";" );
			}
			b.append( "\"" );
			
			for( String s : attributes() )
			{
				b.append( " " + s + "=\"" + getAttribute(s,"") + "\"" );
			}
		}
		b.append( "/>" );
		
		return b.toString();
	}
	
	/* (non-Javadoc)
	 * @see technicalia.dom.INode#indexOf(technicalia.dom.INode)
	 */
	
	public int indexOf( DomNode childNode ) {
		
		return children.indexOf( childNode );
		
	}
	
	/* (non-Javadoc)
	 * @see technicalia.dom.INode#dump(java.io.PrintStream)
	 */
	
	public void dump( PrintStream out ) {
		dump( out, "" );
	}
	
	/* (non-Javadoc)
	 * @see technicalia.dom.INode#dump(java.io.PrintStream, java.lang.String)
	 */
	
	public void dump( PrintStream out, String prefix ) {
		out.print( prefix + "+ " + name );
		for( String s : attributes() ) {
			out.print( " " + s + "=\"" + getAttribute(s,"") + "\"" );
		}
		out.println( "" );
		for( DomNode child : children() ) {
			child.dump( out, prefix + "  " );
		}
	}
	
	/* (non-Javadoc)
	 * @see technicalia.dom.INode#children()
	 */
	
	public Iterable<DomNode> children() {
		return children.values();
	}
	
	/* (non-Javadoc)
	 * @see technicalia.dom.INode#getPath()
	 */
	
	public String getPath() {
		if( parent == null ) return "";
		return getParent().getPath() + "/" + this.name;
	}
	
	/* (non-Javadoc)
	 * @see technicalia.dom.INode#getDomPath()
	 */
	
//	public DomPath getDomPath() {
//		if( getParent().getChildCount( name ) > 1 )
//			return new DomPath( parent.getPath() + "/" + this.name + "[" + parent.indexOf( this ) + "]" );
//		else
//			return new DomPath( parent.getPath() + "/" + this.name );
//	}
	
	/* (non-Javadoc)
	 * @see technicalia.dom.INode#removeAttribute(java.lang.String)
	 */
	
	void removeAttribute( String key ) {
		attributes.remove( key );
	}

	
	void setParent(DomNode domNode) {
		this.parent = domNode;
	}

	
	void setDom(DOM dom) {
		this.dom = dom;
	}

	
//	public DomNode index(String alias) {
//		if( this.id != null )
//			this.dom.unregister( this );
//		this.id = alias;
//		if( this.id != null )
//			this.dom.register( this );
//		return this;
//	}

	
	DomNode with(String attr, String value) {
		setAttribute( attr,  value );
		return this;
	}

	public DOM getDOM() {
		return this.dom;
	}
	
}