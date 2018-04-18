package eu.supersede.orch.main;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import eu.supersede.orch.Evaluation;
import eu.supersede.orch.HistoricalData;
import eu.supersede.orch.MethodDefinition;
import eu.supersede.orch.Objective;
import eu.supersede.orch.Orchestrator;
import eu.supersede.orch.Organization;
import eu.supersede.orch.Process;
import eu.supersede.orch.ProcessSpace;
import eu.supersede.orch.R;
import eu.supersede.orch.Worker;
import eu.supersede.orch.io.IO;
import eu.supersede.orch.util.XmlNode;


/*
 * 
 * -ps /Users/albertosiena/Documents/Home/Uni/Supersede/dev/SimOrch/problemspace.xml 
 * -i /Users/albertosiena/Documents/Home/Uni/Supersede/dev/SimOrch/input.xml 
 * -o /Users/albertosiena/Documents/Home/Uni/Supersede/dev/SimOrch/organization.xml 
 * -m /Users/albertosiena/Documents/Home/Uni/Supersede/dev/SimOrch/optimality.xml 
 * -rand 1 
 * { -pref /Users/albertosiena/Documents/Home/Uni/Supersede/dev/SimOrch/preferences.xml
 * | -trace
 * | -cmd calcProcSpaceSize }
 */


public class OrchApp 
{
	
	void run( String[] args ) {

		CommandLine cmd = parseCmd( args );

		Orchestrator orchestrator = new Orchestrator();

		load( cmd, orchestrator );
		
		if( cmd.hasOption( "cmd" ) ) {
			
			if( "calcProcSpaceSize".equals( cmd.getOptionValue( "cmd" ) ) ) {
				System.out.println( "Process space size: " + orchestrator.getSpace().calculateSize( System.out ) + " alternatives" );
				System.exit( 0 );
			}
			
		}
		
		if( cmd.hasOption( "trace" ) ) {

			Process process = orchestrator.getSpace().pickRandomProcess();

			System.out.println( process );

			Evaluation eval = orchestrator.evaluate( process );

			for( String id : eval.getOptimality().accumulators() ) {
				System.out.println( id + " = " + eval.getOptimality().getValue( id ) );
			}

			eval.getExecution().getTrace().print( System.out );

		}

		if( cmd.hasOption( "pref" ) ) {

			String filepath = cmd.getOptionValue( "pref" );
			File file = new File( filepath );
			if( !file.exists() ) {
				throw new RuntimeException( "Preferennce file '" + filepath + "' does not exist" );
			}

			List<Objective> objectives = IO.get().loadPreferences( file );

			Map<Process,Evaluation> evaluations = orchestrator.findBest( objectives );
			
			System.out.println( "=============" );
			System.out.println( "Results" );
			System.out.println();
			
			for( Process p : evaluations.keySet() ) {

				System.out.println( p );
				System.out.println( evaluations.get( p ).getOptimality() );
				System.out.println();

			}

		}

	}

	void load( CommandLine cmd, Orchestrator orchestrator ) {

		if( cmd.hasOption( "rand" ) ) {
			String string = cmd.getOptionValue( "rand", "" );
			try {
				Long seed = Long.parseLong( string );
				R.get().setSeed( seed );
			}
			catch( Exception ex ) {
				System.err.println( "Invalid option value for 'rand': " + string + "; should be an integer number" );
			}
		}

		{
			File file = new File( cmd.getOptionValue( "ps" ) );
			if( file.exists() ) {
				IO.get().loadProcessSpace( file, orchestrator.getSpace() );
			}
			else if( cmd.getOptionValue( "ps" ).startsWith( "$" ) ) {
				randomize( orchestrator.getSpace() );
			}
			else {
				System.err.println( "File not found: " + file.getAbsolutePath() );
			}
		}

		{
			String filepath = cmd.getOptionValue( "i" );
			{
				File file = new File( filepath );
				if( !file.exists() ) {
					throw new RuntimeException( "Input file '" + filepath + "' does not exist" );
				}
			}
			orchestrator.getKB().create( "/", IO.get().loadDOM( new File( filepath ) ).getRoot() );

		}

		{
			File file = new File( cmd.getOptionValue( "o" ) );
			if( file.exists() ) {
				Organization org = IO.get().loadOrganizationDescription( file );
				orchestrator.getKB().create( "/", org.getDescription().getRoot() );
			}
			else {
				System.err.println( "File not found: " + file.getAbsolutePath() );
			}
		}

		if( cmd.hasOption( "h" ) )
		{
			File file = new File( cmd.getOptionValue( "h" ) );
			if( file.exists() ) {
				HistoricalData.load( file, orchestrator.getHistoricalData() );
			}
			else {
				System.err.println( "File not found: " + file.getAbsolutePath() );
			}
		}

		for( MethodDefinition md : orchestrator.getSpace().methods() ) {

			HistoricalData p = new HistoricalData();
			Worker worker = new Worker( md, p );

			orchestrator.getSimulator().addWorker( worker );

			orchestrator.getSpace().add( md );
		}


//		System.out.println( orchestrator.getKB().asString() );


		//		Model model = IO.get().loadOptimalityModel( cmd.getOptionValue( "m" ) );


		{
			File file = new File( cmd.getOptionValue( "m" ) );

			if( !file.exists() ) {
				throw new RuntimeException( "Model file '" + file.getAbsolutePath() + "' does not exist" );
			}

			IO.get().loadOptimalityModel( XmlNode.load( file ), orchestrator.getModel() );
		}

	}

	public static void main( String[] args ) {
		new OrchApp().run( args );
	}

	CommandLine parseCmd( String[] args ) {

		Options options = new Options();

		{
			Option o = new Option( "cmd", "Command", true, "Specifies the command to execute");
			o.setRequired(false);
			options.addOption( o );
		}

		{
			Option rand = new Option( "rand", "Random", true, "Specifies the random seed to be used");
			rand.setRequired(false);
			options.addOption(rand);
		}

		{
			Option output = new Option("ps", "ProblemSpace", true, "Specifies the file containing the problem space description");
			output.setRequired(true);
			options.addOption(output);
		}

		{
			Option input = new Option("i", "InputData", true, "Specifies the process input data");
			input.setRequired(true);
			options.addOption(input);
		}

		{
			Option org = new Option("o", "Organization", true, "Specifies the description of the organization");
			org.setRequired(true);
			options.addOption(org);
		}

		{
			Option mod = new Option("m", "OptimalityModel", true, "Specifies the optimality model");
			mod.setRequired(true);
			options.addOption(mod);
		}

		{
			Option mod = new Option( "pref", "PreferenceModel", true, "Specifies the preference model (i.e., the obectives to be achieved)");
			mod.setRequired(false);
			options.addOption(mod);
		}

		{
			Option mod = new Option( "trace", "Prints the execution trace");
			mod.setRequired(false);
			options.addOption(mod);
		}

		{
			Option mod = new Option( "h", "History", true, "Specifies the description of the planning history");
			mod.setRequired(false);
			options.addOption(mod);
		}

		CommandLineParser parser = new DefaultParser();
		HelpFormatter formatter = new HelpFormatter();
		CommandLine cmd = null;

		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			System.out.println(e.getMessage());
			formatter.printHelp("java -jar simorch.jar <options>", options);

			System.exit(1);
		}

		return cmd;
	}

	void randomize( ProcessSpace space ) {
		Random r = R.get().getRandom();
		for( int i = 0; i < r.nextInt( 100 ); i++ ) {
			MethodDefinition md = new MethodDefinition( "Method " + i );
			space.add( md );
		}
	}

}
