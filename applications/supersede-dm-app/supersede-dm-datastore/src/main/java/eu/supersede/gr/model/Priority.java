package eu.supersede.gr.model;

public enum Priority {
	
	One, Two, Three, Four, Five;
	
	public int asNumber() {
    	switch( this ) {
    	case One:
    		return 1;
    	case Two:
    		return 2;
		case Three:
			return 3;
		case Four:
			return 4;
		case Five:
		default:
			return 5;
    	}
    };
    
    public static Priority fromNumber( int num ) {
    	if( num < 1 ) num = 1;
    	if( num > 5 ) num = 5;
    	switch( num ) {
    	case 1:
    		return One;
    	case 2:
    		return Two;
    	case 3:
    		return Three;
    	case 4:
    		return Four;
    	case 5:
    	default:
    		return Five;
    	}
    }
	
}
