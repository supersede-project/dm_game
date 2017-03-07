package eu.supersede.gr.model;

public class HUserSkill {
	
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getTopicId() {
		return topicId;
	}

	public void setTopicId(Long topicId) {
		this.topicId = topicId;
	}

	public Double getSkillLevel() {
		return skillLevel;
	}

	public void setSkillLevel(Double skillLevel) {
		this.skillLevel = skillLevel;
	}

	Long userId;
	
	Long topicId;
	
	Double skillLevel;
	
	public HUserSkill() {}
	
	public HUserSkill( HTopic topic, Double level ) {
		this.topicId = topic.id;
		this.skillLevel = level;
	}
	
}
