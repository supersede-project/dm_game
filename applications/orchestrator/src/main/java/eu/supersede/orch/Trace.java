package eu.supersede.orch;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import eu.supersede.orch.kb.DOM;
import eu.supersede.orch.kb.DomNode;
import eu.supersede.orch.kb.NodeStatus;
import eu.supersede.orch.kb.NodeStatus.Presence;

public class Trace {
	
	public static class DepthFirstExplorer implements Iterable<DomNode> {
		
		DomNode root;
		
		public static class DepthFirstIterator implements Iterator<DomNode> {
			
			Stack<DomNode> stack = new Stack<>();
			private DomNode root;
			
			public DepthFirstIterator( DomNode root ) {
				this.root = root;
				mount( this.root );
			}
			
			private void mount( DomNode node ) {
				stack.push( node );
				while( node.getChildCount() > 0 ) {
					node = node.getChild( 0 );
					stack.push( node );
				}
			}
			
			private DomNode umount() {
				while( stack.size() > 0 ) {
					DomNode previous = stack.pop();
					DomNode parent = previous.getParent();
					if( parent == null ) {
						return null;
					}
					int index = parent.indexOf( previous );
					if( index < parent.getChildCount() -1 ) {
						index++;
						return parent.getChild( index );
					}
				}
				return null;
			}
			
			private void walk() {
				DomNode branch = umount();
				if( branch == null ) {
					return;
				}
				mount( branch );
			}
			
			@Override
			public boolean hasNext() {
				return !stack.isEmpty();
			}

			@Override
			public DomNode next() {
				DomNode node = stack.peek();
				walk();
				return node;
			}
			
		}
		
		public DepthFirstExplorer( DomNode root ) {
			this.root = root;
		}
		
		@Override
		public Iterator<DomNode> iterator() {
			return new DepthFirstIterator( this.root );
		}
		
	}
	
	
	DOM							dom = new DOM();
	
	
	public void append( Operation op ) {
		
		DomNode node = dom.create( op.getPath() );
		NodeStatus status = (NodeStatus)node.getElement();
		if( status == null ) {
			status = new NodeStatus();
			node.setElement( status );
		}
		if( op.getType() == CRUD.Delete ) {
			status.setPresence( Presence.Absent );
		}
		status.setLastOperation( op );
		
	}
	
//	public void print( PrintStream out ) {
//		
//		for( DomNode node : new DepthFirstExplorer( dom.getRoot() ) ) {
//			NodeStatus status = node.getElement();
//			if( status == null ) continue;
//			out.print( node.getPath() );
//			out.print( " [" );
//			for( Operation op : status.operations() ) {
//				out.print( "#" + op.getStep() + ": " + op.getType().name() );
//				out.print( " {" + op.getAttribtues() + "}" );
//				out.print( "" );
//			}
//			out.println( "]" );
//		}
//	}

	public List<Operation> getPath( String xPathExpr ) {
		List<Operation> list = null;
		DomNode node = dom.getNode( xPathExpr );
		if( node == null ) {
			list = new ArrayList<Operation>();
		}
		else {
			NodeStatus status = (NodeStatus)node.getElement();
			if( status == null ) {
				// This happens if a node has been created during the initialization
				// and never changed during the process execution
				list = new ArrayList<>();
			}
			else {
				list = status.operations();
			}
		}
		return list;
	}
	
	public List<String> getChildPaths( String path ) {
		DomNode parent = dom.getNode( path );
		if( parent == null ) return null;
		List<String> list = new ArrayList<>();
		for( DomNode child : parent.children() ) {
			list.add( child.getPath() );
		}
		return list;
	}
	
	public void print( PrintStream out ) {
		printTrace( dom.getRoot(), out, "" );
	}
	
	private void printTrace( DomNode node, PrintStream out, String prefix ) {
		
		System.out.print( prefix + node.getName() );
		
		for( Operation op : getPath( node.getPath() ) ) {
			
			System.out.print( " @" + op.getStep() + ": " + op.getType() + "{" );
			
			for( String key : op.getAttribtues() ) {
				System.out.print( key + ":" + op.getAttribtues().get( key ) + ";" );
			}
			
			System.out.print( "}" );
		}
		
		System.out.println( "" );
		
		for( DomNode child : node.children() ) {
			
			printTrace( child, out, prefix + " " );
			
		}
		
	}
	
}
