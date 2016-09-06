package eu.supersede.dm;

import eu.supersede.dm.algorithms.*;

public class testAlgorithm {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		AHPStructure objAHP = new AHPStructure();

		//objAHP.setCriteria("C1","C2", "C3");
		//objAHP.setOptions("Op1", "Op2", "Op3", "Op0");
		
		//Preference from 0 to 8
		objAHP.setCriteria("C1","C2");
		objAHP.setOptions("Op1", "Op2" );
		//Preference from 0 to 8
		objAHP.setPreference("C1", "C2", 6);
		objAHP.setOptionPreference("Op1", "Op2", "C1", 2);
		// objAHP.setOptionPreference("Op1", "Op3", "C1", 4);
		// objAHP.setOptionPreference("Op2", "Op3", "C1", 2);
		objAHP.setOptionPreference("Op1", "Op2", "C2", 8);
		// objAHP.setOptionPreference("Op1", "Op3", "C2", 5);
		// objAHP.setOptionPreference("Op2", "Op3", "C2", 3);
		
		/*objAHP.setPreference("C1", "C2", 9);
		objAHP.setPreference("C1", "C3", 5);
		objAHP.setPreference("C2", "C3", 6);
		
		objAHP.setOptionPreference("Op1", "Op2", "C1", 2);
		objAHP.setOptionPreference("Op1", "Op3", "C1", 4);
		objAHP.setOptionPreference("Op1", "Op0", "C1", 3);
		objAHP.setOptionPreference("Op2", "Op3", "C1", 2);
		objAHP.setOptionPreference("Op2", "Op0", "C1", 6);
		objAHP.setOptionPreference("Op3", "Op0", "C1", 7);
	
		objAHP.setOptionPreference("Op1", "Op2", "C2", 4);
		objAHP.setOptionPreference("Op1", "Op3", "C2", 5);
		objAHP.setOptionPreference("Op1", "Op0", "C2", 3);
		objAHP.setOptionPreference("Op2", "Op3", "C2", 9);
		objAHP.setOptionPreference("Op2", "Op0", "C2", 6);
		objAHP.setOptionPreference("Op3", "Op0", "C2", 7);
		

		objAHP.setOptionPreference("Op1", "Op2", "C3", 6);
		objAHP.setOptionPreference("Op1", "Op3", "C3", 7);
		objAHP.setOptionPreference("Op1", "Op0", "C3", 8);
		objAHP.setOptionPreference("Op2", "Op3", "C3", 8);
		objAHP.setOptionPreference("Op2", "Op0", "C3", 7);
		objAHP.setOptionPreference("Op3", "Op0", "C3", 6);*/
		
		Ahp objCalculateRank = new Ahp(objAHP);
		
		objCalculateRank.execute();
	}

}
