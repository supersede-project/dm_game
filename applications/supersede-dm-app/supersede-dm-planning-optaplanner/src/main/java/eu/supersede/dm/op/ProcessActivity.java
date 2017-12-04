package eu.supersede.dm.op;

public interface ProcessActivity {
	
	void setNext( ProcessActivity a );
	ProcessActivity getNext();
	
	ProcessActivity getPrev();
	
}
