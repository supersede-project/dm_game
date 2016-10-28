package eu.supersede.dm.services;

public class MonitoringService {

	private static MonitoringService instance = new MonitoringService();
	
	public static MonitoringService get() {
		return instance;
	}
	
	
}
