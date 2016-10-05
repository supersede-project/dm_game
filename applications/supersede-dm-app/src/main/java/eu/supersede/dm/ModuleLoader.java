package eu.supersede.dm;

import org.springframework.stereotype.Component;

@Component
public class ModuleLoader {
	
	public ModuleLoader() {
		
		System.out.println( ">>> ModuleLoader initialized" );
		
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
