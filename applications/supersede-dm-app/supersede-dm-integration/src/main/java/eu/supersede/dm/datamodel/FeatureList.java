package eu.supersede.dm.datamodel;

import java.util.ArrayList;
import java.util.List;

public class FeatureList {
	
	long			timestamp;
	
	List<Feature>	features = new ArrayList<>();

	public List<Feature> list() {
		return this.features;
	}
	
	public int getProjectNum() {
		return 0;
	}
	
}
