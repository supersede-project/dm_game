package eu.supersede.dm.datamodel;

import java.util.ArrayList;
import java.util.List;

public class Feature {
	
	String				id;
	String				name;
	int					effort = -1;	// -1 = not specified
	int					priority;		// 1..5
	List<String>		hdeps = new ArrayList<>();
	List<SoftDependency>	sdeps = new ArrayList<>();
	
}
