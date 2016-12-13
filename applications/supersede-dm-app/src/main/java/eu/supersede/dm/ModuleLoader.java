package eu.supersede.dm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eu.supersede.fe.application.ApplicationUtil;

@Component
public class ModuleLoader {
	
	@Autowired
	private ApplicationUtil au;
	
	public ModuleLoader() {
		
	}
	
}
