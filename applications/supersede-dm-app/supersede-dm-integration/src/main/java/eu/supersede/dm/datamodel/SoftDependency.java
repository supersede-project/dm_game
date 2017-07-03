package eu.supersede.dm.datamodel;

public class SoftDependency {
	
	String				ref_id;
	DependencyReason	reason;
	int					value;
	
	public String getRefId() {
		return ref_id;
	}
	public void setRefId(String ref_id) {
		this.ref_id = ref_id;
	}
	public DependencyReason getReason() {
		return reason;
	}
	public void setReason(DependencyReason reason) {
		this.reason = reason;
	}
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
	
}
