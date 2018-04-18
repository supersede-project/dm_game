package eu.supersede.orch.util;

import java.util.Random;

import eu.supersede.orch.R;

public class SimulatedText {
	
    private String[] words = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum".split("[ ]");
    
    private static Random r = R.get().getRandom(); // new Random( System.currentTimeMillis() );
    
    String string;
    
    public SimulatedText( int max )
    {
        String text = "";
        
        if( max < 2 ) max = 2;
        
        max = 2 + r.nextInt( (max -2) );
        
        for (int i = 0; i < max; i++) {
            text += words[r.nextInt(words.length)] + " ";
        }

        this.string = text.trim();
    }
    
    public String toString() {
    	return this.string;
    }

}
