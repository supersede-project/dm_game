//package eu.supersede.dm;
//
//public class TestVariables {
//	
//	public static void main( String[] args ) {
//		
//		DMClassModel cm = new DMClassModel();
//		
//		cm.addClass( DMPrioritization.class );
//		
//		DMClass dmoutput = new DMClass( "dmoutput" );
//		
//		DMClass cRequirement = new DMClass( "Requirement" );
//		
//		dmoutput.addField( new DMClassField( "prioritization", DMClassFieldType.OBJECT, cRequirement ) );
//		
//		
//		DMDataBag data = new DMDataBag();
//		
//		data.addVariable( "dmoutput" );
//		data.addVariable( "dmoutput/prioritization" );
//		
//		data.setValue( "dmoutput/prioritization[0]/name", "req1" );
//		
//	}
//	
//}
