package eu.supersede.gr.rest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.support.GenericWebApplicationContext;

@RestController
@RequestMapping("/supersede-dm-ahprp-ui")
public class ResourcesRest {
	
//	@Autowired
//    private ResourceLoader resourceLoader;

    private ResourceLoader resourceLoader = new GenericWebApplicationContext();
	
	
	@RequestMapping("/{page}.{ext}")
	public byte[] getResource( @PathVariable("page") String name, @PathVariable("ext") String ext ) {
		
		System.out.println( "Serving page " + name );
		
//		Resource resource = resourceLoader.getResource("classpath:static/" + name + "." + ext);
		Resource resource = resourceLoader.getResource("classpath:static/" + name + "." + ext);
		
		try {
			System.out.println( resource.getFile().getAbsolutePath() );
		} catch (IOException e1) {
			e1.printStackTrace();
		}
//		Resource resource = resourceLoader.getResource("classpath:static/game.html");
		
		try {
			InputStream is = resource.getInputStream();
			
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			try {
			    int read = is.read();
			    while (read != -1) {
			        byteArrayOutputStream.write(read);
			        read = is.read();
			    }
			} catch (IOException e) {
			    e.printStackTrace();
			}
			byte[] bytes = byteArrayOutputStream.toByteArray();
			return bytes;
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Something went wrong
		return ("Failed to load resource '" + name + "." + ext + "'").getBytes();
	}
	
}
