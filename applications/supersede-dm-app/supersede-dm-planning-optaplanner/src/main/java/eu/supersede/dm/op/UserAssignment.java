package eu.supersede.dm.op;

public class UserAssignment implements ProcessActivity {
	
	private ProcessActivity next;
	
	
	public UserAssignment() {
	}
	
	public ProcessActivity getNext() {
		return next;
	}

	public String getName() {
		return "supervisor";
	}

	@Override
	public void setNext(ProcessActivity a) {
//		System.out.println( this.getClass().getSimpleName() + "@" + hashCode() + "::setNext( " + a + " );" );
		this.next = a;
	}

	@Override
	public ProcessActivity getPrev() {
		return null;
	}
	
	public String toString() {
		return this.getClass().getSimpleName() + " > " + (next != null ? next : "END");
	}
	
}
