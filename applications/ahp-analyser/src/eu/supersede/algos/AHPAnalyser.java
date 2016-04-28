package eu.supersede.algos;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map; 

import org.python.core.PyList;
import org.python.core.PySystemState;
import org.python.util.PythonInterpreter;

public class AHPAnalyser {
	
	private String mkList( Matrix matrix ) {
		String criteriaList = "";
		final int[] numbers = new int[] { 9, 7, 5, 3, 1, 3, 5, 7, 9 };
		String sep = "";
		criteriaList += "[";
		for( int r = 1; r < matrix.getRowHeaders().size(); r++ ) {

			for( int c = 0; c < r; c++ ) {
				
				int val = matrix.getValue( r,  c, 4 );
				
				if( val > 4 ) {
					criteriaList += sep + "[" + r + "," + c + "," + numbers[val] + "]";
				}
				else {
					criteriaList += sep + "[" + c + "," + r + "," + numbers[8-val] + "]";
				}
				sep = ",";

			}
		}
		criteriaList += "]";
		return criteriaList;
	}

	public Map<String,Double> eval( AHPStructure ahpInput ) {
		
		System.out.println( "Running AHP" );
		
		try {
			
			// Prints the criteria and options matrices on stdout
			ahpInput.criteriaMatrix.dump( System.out );
			System.out.println();
			
			for( Matrix m : ahpInput.optionsMatrices.values() ) {
				m.dump( System.out );
				System.out.println();
			}
			
			String criteriaList = mkList( ahpInput.criteriaMatrix );
			
			String[] strList2 = new String[ahpInput.optionsMatrices.size()];
			{
				int i = 0;
				for( String cr : ahpInput.criteriaMatrix.getRowHeaders().order() ) {
					strList2[i] = mkList( ahpInput.optionsMatrices.get( cr ) );
					i++;
				}
			}
			
			System.out.println( "Preparing args" );
			PySystemState stm = new PySystemState();
			List<String> args = new ArrayList<String>();
			
			args.add( "jython" );		// first argument in python is the program name
			args.add( "" + ahpInput.getCriteriaCount() );
			args.add( "" + ahpInput.getOptionsCount() );
			args.add( criteriaList );
			for( int i = 0; i < strList2.length; i++ ) {
				args.add( strList2[i] );
			}

			for( String arg : args ) {
				System.out.print( " " + arg );
			}
			System.out.println();

			stm.argv = new PyList( args );

			System.out.println( "Creating Jython object" );
			PythonInterpreter jython =
					new PythonInterpreter( null, stm );

			System.out.println( "Setting output stream" );
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			jython.setOut( out );

			System.out.println( "Executing" );
			jython.execfile(Resources.class.getResourceAsStream( "ahpNoEig_command.py" ));

			String output = out.toString();
			
			output = output.replaceAll("\\r\\n", "\n").replaceAll("\\r", "\n");

			System.out.println( "Output:" );
			System.out.println( output );

			try {
				output = output.substring( 1, output.length() -2 );
				String[] parts = output.split( "[,]" );
				Map<String,Double> map = new HashMap<>();
				for( int i = 0; i < parts.length; i++ ) {
					map.put( ahpInput.getOptions().get( i ), Double.parseDouble( parts[i] ) );
				}
				return map;
			}
			catch( Exception ex ) {
				ex.printStackTrace();
			}
			finally {
				if( jython != null )
					jython.close();
			}

		}
		catch( Exception ex ) {
			ex.printStackTrace();
		}

		return null;
	}
	
	public double density( AHPStructure input ) {
		
		int criteria = input.getCriteriaCount();
		int options = input.getOptionsCount();
		
		int max = 
				((((options * options ) - options) /2) * criteria)
				+ (((criteria * criteria ) - criteria) /2);
		int val = density( input.criteriaMatrix );
		
		for( Matrix m : input.optionsMatrices.values() ) {
			val += density( m );
		}
		
		return (double)val / (double)max;
	}
	
	private int density( Matrix matrix ) {
		
		int density = 0;
		
		for( int r = 1; r < matrix.getRowHeaders().size(); r++ ) {

			for( int c = 0; c < r; c++ ) {

				int val = matrix.getValue( r,  c, -1 );
				
				if( val != -1 ) {
					density++;
				}
				
			}
		}
		
		return density;
		
	}
	
}
