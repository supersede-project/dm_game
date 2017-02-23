/*
   (C) Copyright 2015-2018 The SUPERSEDE Project Consortium

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at
     http://www.apache.org/licenses/LICENSE-2.0
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

package eu.supersede.gr.rest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerMapping;

/*
 * If static resources do not need to be secured, this method is not necessary.
 * Otherwise, getResource can filter resource access on the basis of user role
 */

@RestController
@RequestMapping("/ahprp")
public class ResourcesRest {

	@Autowired
	private ResourceLoader resourceLoader;

	@RequestMapping("/**")
	public byte[] getResource( final HttpServletRequest request ) {


		String path = (String) request.getAttribute(
				HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
		String bestMatchPattern = (String ) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);

		AntPathMatcher apm = new AntPathMatcher();
		String finalPath = apm.extractPathWithinPattern(bestMatchPattern, path);

//		System.out.println( "Serving page " + finalPath );

		Resource resource = resourceLoader.getResource("classpath:static/ahprp/" + finalPath );

//		try {
//			System.out.println( resource.getFile().getAbsolutePath() );
//		} catch (IOException e1) {
//			e1.printStackTrace();
//		}
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
		return ("Failed to load resource '" + finalPath).getBytes();
	}
}
