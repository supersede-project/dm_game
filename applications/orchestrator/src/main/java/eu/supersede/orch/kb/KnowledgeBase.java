package eu.supersede.orch.kb;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.supersede.orch.kb.DomPath.PathPart;
import eu.supersede.orch.qpath.XPath;


public class KnowledgeBase
{
	enum Status {
		Available, Buffered, Executing
	}

	public interface Listener
	{
		void onNodeCreated( DomNode node );
		void onNodeUpdated( DomNode node, Map<String,String> diff );
		void onNodeDeleted( DomNode node );
		void onTransactionExecuted( Transaction tx );
	}

	File					currentFile = new File( "" );


	DOM						dom = null;

	ArrayList<Listener>		listeners = new ArrayList<Listener>();

	Status					status = Status.Available;
	Transaction				currentTransaction = null;

	History					history = new History();
	private boolean			modified;
	private boolean			committing = false;


	public KnowledgeBase() {
		this( new DOM() );
	}

	public KnowledgeBase( DOM dom ) {
		this.dom = dom;
	}

	public void create( String path, String value, String ... args ) {
		Map<String,String> m = new HashMap<String,String>();
		for( int i = 0; i < args.length; i += 2 )
			m.put( args[i], args[i+1] );
		
		create( path, value, m );
		
		return;
	}
	
	public void create( String path, DomNode branch ) {
		
		DomPath domPath = new DomPath( path );
		
		Map<String,String> m = new HashMap<String,String>();
		
		for( String key : branch.getAttributes() ) {
			m.put( key, branch.getAttribute( key, "" ) );
		}
		
		create( domPath.getParentPath(), domPath.getLeaf().getName(), m );
		
		for( DomNode child : branch.children() ) {
			create( path + "/" + child.getName(), child );
		}
		
	}
	
	Transaction requestTransaction( String name ) {
		if( currentTransaction == null ) {
			currentTransaction = new Transaction( "" );
		}
		currentTransaction.setName( currentTransaction.getName() + ";" + name );
		return currentTransaction;
	}

	public void create( String path, String value, Map<String,String> args ) {
		Transaction tx = requestTransaction( "create " + path + "/" + value );

		DomPath domPath = new DomPath( path );
		String t = "";

		for( PathPart part : domPath ) {
			DomPath partialPath = new DomPath( t + "/" + part );
			if( dom.getNode( partialPath ) == null ) {
				Command cmd = mkcmd( Command.TYPE_CREATE, partialPath, null );
				tx.addCommand( cmd );
			}
			t += "/" + part;
		}

		tx.addCommand( mkcmd( Command.TYPE_CREATE, new DomPath( domPath, value ), args ) );
		
		try {
			commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	public void update( DomPath path, String ... args ) {
		Map<String,String> m = new HashMap<String,String>();
		for( int i = 0; i < args.length; i += 2 )
			m.put( args[i], args[i+1] );

		update( path, m );

		return;
	}

	public void update( DomPath domPath, Map<String,String> args ) {
		Transaction tx = requestTransaction( "update " + domPath  );

		String t = "";

		for( PathPart part : domPath )
		{
			DomPath partialPath = new DomPath( t + "/" + part );
			if( dom.getNode( partialPath ) == null )
			{
				Command cmd = mkcmd( Command.TYPE_CREATE, partialPath, null );
				tx.addCommand( cmd );
			}
			t += "/" + part;
		}

		tx.addCommand( mkcmd( Command.TYPE_UPDATE, domPath, args ) );

		try
		{
			commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void apply( String cmd ) throws Exception {
		apply( new Command( cmd ) );
	}

	public void apply( Command cmd )
	{
		if( cmd.type == Command.TYPE_CREATE )
			create( cmd.getPath(), cmd.getTag(), cmd.values );
		else if( cmd.type == Command.TYPE_UPDATE )
			update( cmd.domPath, cmd.values );
		else if( cmd.type == Command.TYPE_DELETE )
			;

	}

	private Command mkrev( Command cmd )
	{
		Command reverse = null;

		switch( cmd.getType() )
		{
		case Command.TYPE_READ:
			reverse = new Command(
					Command.TYPE_READ, cmd.domPath, cmd.values );
			break;
		case Command.TYPE_CREATE:
			reverse = new Command(
					Command.TYPE_DELETE, cmd.domPath, cmd.values );
			break;
		case Command.TYPE_UPDATE:
		{
			Map<String, String> old = new HashMap<String, String>();
			DomNode node = dom.getNode( cmd.domPath );
			if( node == null ) break;
			for( String k : cmd.values.keySet() )
				old.put( k, node.getAttribute( k, null ) );
			reverse = new Command(
					Command.TYPE_UPDATE, cmd.domPath, old );
			break;
		}
		case Command.TYPE_DELETE:
		{
			Map<String, String> old = new HashMap<String, String>();
			DomNode node = dom.getNode( cmd.domPath );
			for( String k : node.attributes() )
				old.put( k, node.getAttribute( k, null ) );
			reverse = new Command(
					Command.TYPE_CREATE, 
					new DomPath( cmd.domPath.getParentPath() ), 
					old );
			break;
		}
		}

		return reverse;
	}

	private Command mkcmd( int type, DomPath path, Map<String, String> values ) {
		Command cmd = new Command( type, path, values );

		cmd.setReverseCmd( mkrev( cmd ) );

		return cmd;
	}

	public boolean isTransactionOpen() {
		return status == Status.Buffered;
	}

	void commit() throws Exception
	{
		if( isTransactionOpen() ) return;

		status = Status.Executing;
		
		for( Command c : currentTransaction ) {
			_doCmd( c );
		}
		
		history.push( currentTransaction );
		
		for( Listener l : listeners ) {
			l.onTransactionExecuted( this.currentTransaction );
		}
		
		this.currentTransaction = null;

		status = Status.Available;

		//		System.out.println( "DONE" );
	}

	private void _doCmd( Command cmd ) throws Exception
	{
		
		if( committing == true ) {
			throw new Exception( "Recursion detected: attempting to update the KB while the KB is updating" );
		}
		
		committing = true;

		try {

			modified = true;

			switch( cmd.getType() )
			{
			case Command.TYPE_CREATE:
			{
				DomPath path = new DomPath( cmd.getPath() );

				DomNode parent = dom.getNode( path );
				if( parent == null ) 
					throw new Exception( "Non-existing parent path: " + path.getPath() );
				if( parent.getChild( cmd.getTag() ) != null ) {
					// Throw RuntimeException?
					return;
				}
				DomNode child = parent.addChild( cmd.getTag() );
				for( String key : cmd.values.keySet() ) {
					child.setAttribute( key, cmd.getValue( key ) );
				}
				for( Listener l : listeners ) {
					l.onNodeCreated( child );
				}
			}
			break;
			case Command.TYPE_DELETE:
			{
				DomPath path = new DomPath( cmd.getPath() );

				DomNode parent = dom.getNode( path );

				if( parent == null ) 
					throw new Exception( "Non-existing parent path: " + path.getPath() );

				DomNode child = parent.getChild( cmd.getDomPath().getName() ); //, cmd.getDomPath().getIndex() );

				for( Listener l : listeners ) {
					l.onNodeDeleted( child );
				}

				parent.removeChild( child );
			}
			break;
			case Command.TYPE_UPDATE:
			{
				DomPath path = new DomPath( cmd.getPath() + "/" + cmd.getQualifier() );

				DomNode node = dom.getNode( path );

				if( node == null ) 
					throw new Exception( "Non-existing path: " + path.getPath() );

				for( String key : cmd.values.keySet() )
				{
					String val = cmd.getValue( key );
					if( val == null )
						node.removeAttribute( key );
					else
						node.setAttribute( key, cmd.getValue( key, "" ) );
				}

				for( Listener l : listeners )
					l.onNodeUpdated( node, cmd.values );
			}
			break;
			}
		}
		catch( Exception ex ) {
			throw ex;
		}
		finally {
			committing = false;
		}


	}

	public History getHistory() {
		return this.history;
	}

	public String getName() {
		String name = currentFile.getName();
		if( name.length() > 1 ) return name;
		return "new graph";
	}
	public File getFile() {
		return currentFile;
	}
	public void setFile( File f ) {
		currentFile = f;
	}

	public void save() throws Exception {
		write( dom.getRoot(), new PrintStream( currentFile ) );
	}

	private void write( DomNode node, PrintStream ps ) {
		ps.print( "<" + node.getName() );
		for( String attrName : node.attributes() ) {
			ps.print( " " + attrName + "=" + node.getAttribute( attrName, "" ) );
		}
		if( node.getChildCount() > 0 ) {
			ps.print( ">\n" );
			for( DomNode child : node.children() ) {
				write( child, ps );
			}
			ps.print( "</" + node.getName() + ">\n" );
		}
		else {
			ps.print( "/>" );
		}
	}

	public void undo() {
		if( history.hasPast() ) return;
		Transaction tx = history.undo();
		try {
			for( int i = tx.size() -1; i >= 0; i-- ) {
				_doCmd( tx.getCommand( i ).reverse );
			}
		}
		catch( Exception e ) {
			e.printStackTrace();
		}
	}

	public void addListener( Listener l ) {
		listeners.add( l );
	}

	public void removeListener( Listener l ) {
		listeners.remove( l );
	}

	public void redo() {
		if( !history.hasFuture() ) return;
		Transaction tx = history.redo();
		try {
			for( int i = 0; i < tx.size(); i++ ) {
				_doCmd( tx.getCommand( i ) );
			}
		}
		catch( Exception e ) {
			e.printStackTrace();
		}
	}

	public boolean isModified() {
		return modified;
	}

	public void setModified( boolean b ) {
		modified = b;
	}

	public void update( String path, String key, String value ) {
		update( new DomPath( path ), key, value );
	}

	private void openTransaction() {
		this.currentTransaction = new Transaction( "" );
		status = Status.Buffered;
		//		openTransaction = true;
	}

	private void closeTransaction() {
		status = Status.Available;
		//		openTransaction = false;
	}

	public void beginTransaction() {
		openTransaction();
	}

	public void endTransaction() {
		closeTransaction();
		try {
			commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public DomNode getNode( String path ) {
		return dom.getNode( path );
	}
	
	public List<DomNode> getNodes(XPath path) {
		return dom.getNodes( path );
	}
	
	@Deprecated
	public DomNode getOrSilentlyCreateNode( String path ) {
		DomNode node = dom.getNode( path );
		if( node == null ) {
			node = dom.create( path );
		}
		return node;
	}

	public DomNode getOrCreateNode( String path, String tag ) {
		DomNode node = getNode( path + "/" + tag );
		if( node == null ) {
			create( path, tag );
			node = getNode( path + "/" + tag );
		}
		return node;
	}

	public void print( PrintStream out ) {
		dom.getRoot().dump( out );
	}

	public KnowledgeBase clone() {
		
		KnowledgeBase kb = new KnowledgeBase();
		
		kb.currentFile = new File( this.currentFile.getAbsolutePath() );
		kb.dom = this.dom.clone();
		
		return kb;
		
	}
	
	public String asString() {
		return dom.toString();
	}

	public void delete( String path ) {
		delete( new DomPath( path ) );
	}
	
	private void delete( DomPath domPath ) {
		
		Transaction tx = requestTransaction( "delete " + domPath  );

		String t = "";

		for( PathPart part : domPath )
		{
			DomPath partialPath = new DomPath( t + "/" + part );
			if( dom.getNode( partialPath ) == null )
			{
				// The path does not exist; abort
				return;
//				Command cmd = mkcmd( Command.TYPE_DELETE, partialPath, null );
//				tx.addCommand( cmd );
			}
			t += "/" + part;
		}

		tx.addCommand( mkcmd( Command.TYPE_DELETE, domPath, new HashMap<String, String>() ) );

		try
		{
			commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
