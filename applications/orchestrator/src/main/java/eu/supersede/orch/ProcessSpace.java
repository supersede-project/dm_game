package eu.supersede.orch;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class ProcessSpace {

	List<MethodDefinition>			methods = new ArrayList<>();
	Map<String,MethodDefinition>	methodMap = new HashMap<>();
	MethodDefinition				start = new MethodDefinition( "start");
	MethodDefinition				stop = new MethodDefinition( "stop" );
//	Map<String,List<Predictor>>		predictors = new HashMap<>();

	public ProcessSpace() {
	}

	public Process pickRandomProcess() {

		Process process = new Process();

		process.append( start.createRandomInstance() );

		Random r = R.get().getRandom();

		int length = 1 + r.nextInt( methods.size() );

		for( int i = 0; i < length; i++ ) {

			int index = r.nextInt( methods.size() );

			MethodDefinition md = methods.get( index );

			if( md.satisfiesPreconditions( process.tail().getDefinition() ) ) {

				MethodInstance mi = md.createRandomInstance();

				process.append( mi );

			}

		}

		process.append( stop.createRandomInstance() );

		return process;
	}

	public void add( MethodDefinition md ) {
		if( "start".equals( md.getName() ) ) {
			throw new RuntimeException( "'start' is a reserved method name" );
		}
		if( "stop".equals( md.getName() ) ) {
			throw new RuntimeException( "'stop' is a reserved method name" );
		}
		_add( md );
	}

	private void _add( MethodDefinition md ) {
		methods.add( md );
		methodMap.put( md.getName(), md );
	}

	public MethodDefinition getMethod( String methodName ) {
		return this.methodMap.get( methodName );
	}

	public Collection<MethodDefinition> methods() {
		return this.methodMap.values();
	}

	public MethodDefinition getMethod(int index) {
		return this.methods.get( index );
	}
	
	public static class DataBag {
		
		Set<String> data = new HashSet<>();
		Map<String,List<String>> methodOutputs = new HashMap<>();
		
		public boolean matches(MethodDefinition md) {
			for( String s : md.getInputValues() ) {
				if( !data.contains( s ) ) {
					return false;
				}
			}
			return true;
		}
		
		public void push( String name, List<String> values ) {
			List<String> outputs = new ArrayList<>();
			for( String s : values ) {
				if( !data.contains( s ) ) {
					outputs.add( s );
				}
			}
			methodOutputs.put( name, outputs );
			for( String o : outputs ) {
				data.add( o );
			}
		}
		
		public void pop( String name ) {
			List<String> outputs = methodOutputs.get( name );
			for( String s : outputs ) {
				data.remove( s );
			}
			methodOutputs.remove( name );
		}
		
	}
	
	public static class ProcessStructure {
		
		List<MethodDefinition>	methods = new ArrayList<>();
		Set<String>				methodNames = new HashSet<>();
		DataBag					dataBag = new DataBag();
		
		public void add(MethodDefinition md) {
			methods.add( md );
			methodNames.add( md.getName() );
			dataBag.push( md.getName(), md.getOutputValues() );
		}

		public void remove(MethodDefinition md) {
			dataBag.pop( md.getName() );
			methodNames.remove( md.getName() );
			methods.remove( md );
		}
		
		public String toString() {
			String ret = "";
			ret += "{ ";
			for( MethodDefinition md : methods ) {
				ret += md.getName() + " (" + dataBag.methodOutputs.get( md.getName() ) + "); ";
			}
			ret += "}";
			return ret;
		}
		
	}
	
	public long calculateSize( PrintStream out ) {
		
		long count = 1; // 1 -> the empty case
		
		ProcessStructure structure = new ProcessStructure();
		
		count = count * calculateProcessSpaceSize( structure, out );
		
		return count;
		
	}
	
	public long calculateSize() {
		
		return calculateSize( null );
		
	}
	
	long calculateProcessSpaceSize( ProcessStructure structure, PrintStream out ) {
		
		long count = 0;
		
		for( MethodDefinition md : methods() ) {
			
			if( structure.methodNames.contains( md.getName() ) ) {
				continue;
			}
			if( !structure.dataBag.matches( md ) ) {
				continue;
			}
			
			structure.add( md );
			
			if( out != null ) {
				out.println( structure );
			}
			
			count++;
			
			{
				long next = calculateProcessSpaceSize( structure, out );
				
				if( next != 0 ) {
					count = count + next; //* next;
				}
			}
			
			structure.remove( md );
			
		}
		
		return count;
		
	}

}
