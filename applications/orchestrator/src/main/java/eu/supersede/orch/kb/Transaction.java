package eu.supersede.orch.kb;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Transaction implements Iterable<Command> {
	
	String name = "";
	
	Set<String> index = new HashSet<String>();
	ArrayList<Command> cmds = new ArrayList<Command>();
	
	public Transaction( String name ) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName( String name ) {
		this.name = name;
	}
	
	@Override
	public Iterator<Command> iterator() {
		return cmds.iterator();
	}
	
	public void addCommand(Command command) {
		if( this.index.contains( command.toString() ) ) return;
		this.cmds.add( command );
		this.index.add(command.toString() );
	}
	
	public int size() {
		return cmds.size();
	}
	
	public Command getCommand(int i) {
		return cmds.get( i );
	}
}
