package eu.supersede.dm.datamodel;

public class Condition {
	
	DataID		idMonitoredData;
	Operator	operator;
	Double		value;
	
	public Condition(DataID idMonitoredData, Operator operator, Double value) {
		super();
		this.idMonitoredData = idMonitoredData;
		this.operator = operator;
		this.value = value;
	}
	
	public DataID getIdMonitoredData() {
		return idMonitoredData;
	}
	public Operator getOperator() {
		return operator;
	}
	public Double getValue() {
		return value;
	}
	
}
