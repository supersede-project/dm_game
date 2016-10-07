package eu.supersede.dm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eu.supersede.fe.application.ApplicationUtil;

@Component
public class AHPUILoader {
	
	@Autowired
	private ApplicationUtil au;
	
	@PostConstruct
	public void load() {
		
		System.out.println( "Registering AHP app" );
		
		Map<String, String> labels = new HashMap<>();
//		labels.put( "", "AHP Gamified Decision Making Process" );
//		au.addApplication( "supersede-dm-ahprp-ui", labels, "home" );
		
		labels = new HashMap<>();
		labels.put( "", "AHP Home" );
		List<String> roles = new ArrayList<>();
		roles.add( "DM_ADMIN" );
		au.addApplicationPage( "supersede-dm-app", "supersede-dm-ahprp-ui/home", labels, roles );
		
	}
	
}
