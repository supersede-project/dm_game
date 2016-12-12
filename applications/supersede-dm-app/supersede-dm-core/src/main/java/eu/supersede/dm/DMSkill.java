package eu.supersede.dm;

public class DMSkill {
	
	DMTopic topic;
	
	double level;
	
	public DMSkill( DMTopic topic, double level ) {
		this.topic = topic;
		this.level = level;
	}
	
	public String toString() {
		String string = topic.toString();
		string += ":" + level;
		return string;
	}
	
}
