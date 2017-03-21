package eu.supersede.dm.datamodel;

import java.util.ArrayList;
import java.util.List;

public class Feature {
	
	String					id;
	String					name;
	int						effort = -1;	// -1 = not specified
	int						priority;		// 1..5
	List<String>			hdeps = new ArrayList<>();
	List<SoftDependency>	sdeps = new ArrayList<>();
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getEffort() {
		return effort;
	}

	public void setEffort(int effort) {
		this.effort = effort;
	}

	public List<String> getHdeps() {
		return hdeps;
	}

	public void setHdeps(List<String> hdeps) {
		this.hdeps = hdeps;
	}

	public List<SoftDependency> getSdeps() {
		return sdeps;
	}

	public void setSdeps(List<SoftDependency> sdeps) {
		this.sdeps = sdeps;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public String getName() {
		return this.name;
	}
	
	public int getPriority() {
		return this.priority;
	}
	
}
