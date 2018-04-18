package eu.supersede.orch.kb;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import eu.supersede.orch.kb.DomPath.PathPart;
import eu.supersede.orch.qpath.XCondition;
import eu.supersede.orch.qpath.XPart;
import eu.supersede.orch.qpath.XPath;


public class DOM
{
	
	DomNode			root;
	
	
	public DOM() {
		this( "/" );
	}

	private DOM( String rootName ) {
		root		= new DomNode( rootName );
		root.dom	= this;
	}

	public DomNode getRoot() {
		return this.root;
	}

	public DomNode create( String path, String attributes ) {

		DomNode node = create( path );

		String[] attrs = attributes.split( "[;]" );

		for( String attr : attrs ) {
			String[] pair = attr.split( "[:]" );
			if( pair.length < 2 ) continue;
			node.setAttribute( pair[0].trim(), pair[1].trim() );
		}

		return node;
	}

	public DomNode create( String path ) {
		return create( new DomPath( path ) );
	}

	public DomNode create( DomPath opath ) {
		DomNode parent = root;
		DomNode node = null;

		String partialPath = "";
		String sep = "";

		for( PathPart part : opath ) {
			node = parent.getChild( part.getName() );

			if( node == null ) {
				node = parent.addChild( part.getName() );

				partialPath = partialPath + sep + part.getName();
				sep = "/";
			}

			parent = node;
		}

		if( parent != null ) {
			return parent;
		}

		return null;
	}

	public void delete( String path ) {
		deleteNode( path );
	}

	public void setValue( DomPath path, String value ) {
		DomNode parent = root;

		for( DomPath.PathPart part : path ) {
			DomNode node = parent.getChild( part.getName() );

			if( node == null ) {
				node = parent.addChild( part.getName() );
			}

			if( part.isLeaf() ) {
				node.setAttribute( part.getAttr(), value );

				return;
			}

			parent = node;
		}
	}

	public DomNode getNode( DomPath path ) {
		
		DomNode node = root;

		for( DomPath.PathPart part : path ) {
			node = node.getChild( part.getName() ); //, part.getIndex() );

			if( node == null ) {
				return null;
			}
		}

		return node;
	}

	DomNode getFirstChild( DomNode parent, List<XCondition> clist ) {
		for( DomNode child : parent.children() ) {
			for( XCondition c : clist ) {
				if( child.getAttribute( c.getTarget(), null ) == null ) continue; 
				if( c.getValue().equalsIgnoreCase( c.getValue() ) == false ) continue;
				return child;
			}
		}
		return  null;
	}

	public boolean matches( DomNode node, List<XCondition> clist ) {
		if( clist.size() < 1 ) return true;
		if( "AHP".equals( node.getAttribute( "method", null ) ) ) {
			System.out.print("");
		}
		for( XCondition c : clist ) {
			if( node.getAttribute( c.getTarget(), null ) == null ) continue; 
			if( c.getValue().equalsIgnoreCase( node.getAttribute( c.getTarget(), null ) ) == false ) continue;
			return true;
		}
		return false;
	}
	
	void addBranch( DomNode node, List<XCondition> clist, List<DomNode> nodes ) {
		if( matches( node, clist ) ) {
			nodes.add( node );
		}
		for( DomNode child : node.children() ) {
			addBranch( child, clist, nodes );
		}
	}
	
	public void getNodes( DomNode node, XPath path, int index, List<DomNode> nodes ) {
		
		XPart part = path.parts().get( index );
		
		// We reached the end of the path; let's start collecting matching nodes
		if( path.parts().size() == index +1 ) {
			if( "*".equals( part.getNodeName() ) ) {
				for( DomNode child : node.children() ) {
					addBranch( child, part.getConditions(), nodes );
				}
			}
			else {
				for( DomNode child : node.children() ) {
					if( child.getName().equalsIgnoreCase( part.getNodeName() ) ) {
						addBranch( child, part.getConditions(), nodes );
					}
				}
			}
			return;
		}
		
		
		if( "*".equals( part.getNodeName() ) ) {
			for( DomNode child : node.children() ) {
				getNodes( child, path, index +1, nodes );
			}
		}
		else {
			for( DomNode child : node.children() ) {
				if( child.getName().equalsIgnoreCase( part.getNodeName() ) ) {
					getNodes( child, path, index +1, nodes );
				}
			}
		}
		
	}

	public List<DomNode> getNodes(XPath path) {
		
		List<DomNode> nodes = new ArrayList<>();
		
		getNodes( root, path, 0, nodes );
		
		return nodes;

	}

	public DomNode getNode( String path ) {
		return getNode( new DomPath( path ) );
	}

	public String getExistingPath( String stringpath ) {
		DomNode node = root;
		DomPath path = new DomPath( stringpath );
		String existing = "";

		for( PathPart part : path ) {
			node = node.getChild( part.getName() );

			if( node != null )
				existing += "/" + part.getName();
			else
				return existing;
		}

		return "/";
	}

	public void deleteNode( String path ) {
		DomNode node = getNode( path );
		if( node == null ) return;
		DomNode parent = node.getParent();
		if( parent == null ) return;
		parent.removeChild( node );
	}

	public void setAttribute( String path, String name, String value ) {
		DomNode node = getNode( path );
		if( node != null ) {
			node.setAttribute( name, value );
		}
	}

	public DomNode create(String path, Object element ) {
		DomNode node = create( path );
		node.setElement( element );
		return node;
	}

	public int getChildCount( String path ) {
		DomNode node = getNode( path );
		if( node == null ) return 0;
		return node.getChildCount();
	}
	
	public DOM clone() {
		
		DOM dom = new DOM( root.getName() );
		
		copyAttributes( root, dom.root );
		
		cloneChildren( root, dom.root );
		
		return dom;
		
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
	
	public String toString() {
		
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		
		print( new PrintStream( os ) );
		
		return os.toString();
		
	}
	
	public void print( PrintStream out ) {
		
		printNode( root, out, "" );
		
	}
	
	private void printNode( DomNode node, PrintStream out, String prefix ) {
		
		out.print( prefix );
		out.print( node.getName() );
		out.print( " {" );
		for( String key : node.attributes() ) {
			out.print( key + ":" + node.getAttribute( key, "" ) + ";" );
		}
		out.print( "}" );
		out.println();
		
		for( DomNode child : node.children() ) {
			printNode( child, out, prefix + "  " );
		}
		
	}
	
}