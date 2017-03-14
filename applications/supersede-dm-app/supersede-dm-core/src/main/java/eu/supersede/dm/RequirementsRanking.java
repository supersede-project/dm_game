package eu.supersede.dm;

import java.util.ArrayList;
import java.util.List;

import eu.supersede.gr.model.HRequirementScore;

public class RequirementsRanking {
	
	private Long id;
	
	private Long processId;
	
	private String name;
	private boolean selected = false;
	
	private List<HRequirementScore> list = new ArrayList<>();
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	public Long getProcessId() {
		return processId;
	}
	public void setProcessId(Long processId) {
		this.processId = processId;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public List<HRequirementScore> getList() {
		return list;
	}
	public void setList(List<HRequirementScore> list) {
		this.list = list;
	}
	
}
