package eu.supersede.dm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eu.supersede.fe.application.ApplicationUtil;

@Component
public class ModuleLoader {
	
	@Autowired
	private ApplicationUtil au;
	
	public ModuleLoader() {
		
		System.out.println( ">>> ModuleLoader initialized " + au );
		
//		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(); 
//	    ctx.scan("eu.supersede.gr"); 
//	    ctx.refresh();
//	    AHPUILoader l;
//	    for( String s : ctx.getBeanDefinitionNames() ) {
//	    	System.out.println( s );
//	    }
//	    ctx.close();
//	    TheBean b =  ctx.getBean(TheBean.class); 
		
	}
	
}
