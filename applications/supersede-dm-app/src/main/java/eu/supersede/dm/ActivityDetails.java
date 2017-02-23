package eu.supersede.dm;

import java.util.HashMap;
import java.util.Map;

public class ActivityDetails {

	private Long		activityId;

	private Long		processId;

	private String		methodName;

	private Long		userId;

	private String		url;
	
	private Map<String,String> properties = new HashMap<>();
	
	
	public Long getActivityId() {
		return activityId;
	}

	public void setActivityId(Long activityId) {
		this.activityId = activityId;
	}

	public Long getProcessId() {
		return processId;
	}

	public void setProcessId(Long processId) {
		this.processId = processId;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setProperty( String key, String value ) {
		this.getProperties().put( key, value );
	}

	public Map<String,String> getProperties() {
		return properties;
	}

	public void setProperties(Map<String,String> properties) {
		this.properties = properties;
	}

}
