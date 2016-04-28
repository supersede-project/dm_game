package eu.supersede.algos;

import java.util.Map;

public class AHPTest {
	
	public static void main( String[] args ) {
		
		AHPStructure input = new AHPStructure();
		
		input.setCriteria( "c1", "c2" );
		input.setPreference( "c1", "c2", 4 );
		
		input.setOptions( "o1", "o2", "o3" );
		input.setOptionPreference( "o1", "o2", "c1", 0 );
		input.setOptionPreference( "o1", "o3", "c1", 4 );
		input.setOptionPreference( "o2", "o3", "c1", 0 );
		
		input.setOptionPreference( "o1", "o2", "c2", 0 );
		input.setOptionPreference( "o1", "o3", "c2", 4 );
		input.setOptionPreference( "o2", "o3", "c2", 8 );
		
		
		
//		input.setCriteria( "time", "price", "stress" );
//		input.setPreference( "time", "price", 1 );
//		input.setPreference( "time", "stress", 5 );
//		input.setPreference( "price", "stress", 4 );
//		
//		input.setOptions( "car", "train", "air", "bike" );
//		input.setOptionPreference( "car", "train", "time", 1 );
//		input.setOptionPreference( "car", "air", "time", 5 );
//		input.setOptionPreference( "car", "bike", "time", 3 );
//		input.setOptionPreference( "train", "air", "time", 2 );
//		input.setOptionPreference( "train", "bike", "time", 3 );
//		input.setOptionPreference( "air", "bike", "time", 6 );
//		
//		input.setOptionPreference( "car", "train", "price", 8 );
//		input.setOptionPreference( "car", "air", "price", 5 );
//		input.setOptionPreference( "car", "bike", "price", 4 );
//		input.setOptionPreference( "train", "air", "price", 0 );
//		input.setOptionPreference( "train", "bike", "price", 1 );
//		input.setOptionPreference( "air", "bike", "price", 2 );
//		
//		input.setOptionPreference( "car", "train", "stress", 4 );
//		input.setOptionPreference( "car", "air", "stress", 1 );
//		input.setOptionPreference( "car", "bike", "stress", 3 );
//		input.setOptionPreference( "train", "air", "stress", 7 );
//		input.setOptionPreference( "train", "bike", "stress", 8 );
//		input.setOptionPreference( "air", "bike", "stress", 6 );
		
		AHPAnalyser analyser = new AHPAnalyser();
		
		Map<String,Double> map = analyser.eval( input );
		
		for( String opt : map.keySet() ) {
			System.out.println( opt + " = " + map.get( opt ) );
		}
		
		System.out.println( "Density: " + analyser.density( input ) );
		
	}
	
}
