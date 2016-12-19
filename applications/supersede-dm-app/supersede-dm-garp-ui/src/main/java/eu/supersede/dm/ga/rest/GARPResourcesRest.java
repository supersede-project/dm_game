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

package eu.supersede.dm.ga.rest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerMapping;

import eu.supersede.dm.ga.GAVirtualDB;
import eu.supersede.dm.iga.IGAAlgorithm;

/*
 * If static resources do not need to be secured, this method is not necessary.
 * Otherwise, getResource can filter resource access on the basis of user role
 */

@RestController
@RequestMapping("/garp")
public class GARPResourcesRest {
	
	interface ResourceProvider {
		public byte[] getResource( final HttpServletRequest request );
	}
	
	static Map<String,ResourceProvider> providers = new HashMap<>();
	
	static {
		providers.put( "results.html", new ResourceProvider() {
			@Override
			public byte[] getResource(HttpServletRequest request) {
				
				long gameId = 1482177818095L;
				
				StringBuilder b = new StringBuilder();
				
				b.append( "<script src=\"supersede-dm-app/garp/js/gameplay.js\"></script>" );
				b.append( "<link rel=\"stylesheet\" type=\"text/css\" href=\"supersede-dm-app/garp/jqwidgets/styles/jqx.summer.css\" />" );
				b.append( "<style>" );
				b.append( "h1 { text-align: center; }\n" );
				b.append( "#sortable { margin: 0 auto; padding: 5px; width: 520px; }\n" );
				b.append( "#sortable > div { border-radius: 5px; margin: 5px; }\n" );
				b.append( "#sortable div { padding: 5px; }\n" );
				b.append( "#sortable div:hover { border: 1px solid #356AA0; }\n" );
				b.append( "</style>\n" );
				
				{
					IGAAlgorithm algo = new IGAAlgorithm();
					algo.setCriteria(GAVirtualDB.get().getCriteria(gameId));
					
					for (Long rid : GAVirtualDB.get().getRequirements( gameId ))
					{
						algo.addRequirement("" + rid, new ArrayList<>());
					}
					
					List<Map<String, Double>> prioritizations = null;
					
					try {
						prioritizations = algo.calc();
					}
					catch( Exception ex ) {
						prioritizations = new ArrayList<>();
						Map<String, Double> m = new HashMap<>();
						m.put( "1", 0.2 );
						m.put( "2", 0.21 );
						m.put( "3", 0.15 );
						m.put( "4", 0.01 );
						prioritizations.add( m );
						m = new HashMap<>();
						m.put( "1", 0.21 );
						m.put( "2", 0.2 );
						m.put( "3", 0.15 );
						m.put( "4", 0.19 );
						prioritizations.add( m );
					}
					
					b.append( "<h2>Found: " + prioritizations.size() + " optimal solutions</h2>" );
					
					b.append( "<div id='jqxTabs'>\n" );
					b.append( "<ul style='margin-left: 20px;'>\n" );
					for( int i = 0; i < prioritizations.size(); i++ ) {
						b.append( "<li>Solution " + i + "</li>\n" );
					}
					b.append( "</ul>\n" );
					for( int i = 0; i < prioritizations.size(); i++ ) {
						b.append( "<div>\n" );
						b.append( "<table width=0'100%'>\n" );
						Map<String,Double> map = prioritizations.get( i );
						for( String c : map.keySet() ) {
							b.append( "<tr>\n" );
							b.append( "<td>Req. '" + c + "'</td><td>" + map.get( c ) + "</td>" );
							b.append( "</tr>\n" );
						}
						b.append( "</table>\n" );
						b.append( "</div>\n" );
					}
					b.append( "</div>\n" );
				}
				
//				<h1>Mockup for Requirements Ordering</h1>
//				<div id='jqxTabs'>
//				    <ul style='margin-left: 20px;'>
//				        <li>Tab 1</li>
//				        <li>Tab 2</li>
//				        <li>Tab 3</li>
//				        <li>Tab 4</li>
//				        <li>Tab 5</li>
//				    </ul>
//				    <div>
//				<div id="sortable" ng-controller="reqsCtrl">
//				    <div ng-repeat="x in requirements" class="jqxexpander" style="min-width: 500px">
//				        <div><strong>{{x.id + ': ' + x.title}}</strong></div>
//				        <div style="background-color: #ffd11a">
//				            <strong>Description</strong>: {{x.description}}<br>
//				            <strong>Characteristics:</strong>: {{x.characteristics}}<br>
//				            <strong>Issue</strong>: <a href={{x.link}} target="_blank">{{x.link}}</a>
//				        </div>
//				    </div>
//				</div>
//				    </div>
//				    <div>
//				<div id="sortable" ng-controller="reqsCtrl">
//				    <div ng-repeat="x in requirements" class="jqxexpander" style="min-width: 500px">
//				        <div><strong>{{x.id + ': ' + x.title}}</strong></div>
//				        <div style="background-color: #ffd11a">
//				            <strong>Description</strong>: {{x.description}}<br>
//				            <strong>Characteristics:</strong>: {{x.characteristics}}<br>
//				            <strong>Issue</strong>: <a href={{x.link}} target="_blank">{{x.link}}</a>
//				        </div>
//				    </div>
//				</div>
//				    </div>
//				    <div>
//				<div id="sortable" ng-controller="reqsCtrl">
//				    <div ng-repeat="x in requirements" class="jqxexpander" style="min-width: 500px">
//				        <div><strong>{{x.id + ': ' + x.title}}</strong></div>
//				        <div style="background-color: #ffd11a">
//				            <strong>Description</strong>: {{x.description}}<br>
//				            <strong>Characteristics:</strong>: {{x.characteristics}}<br>
//				            <strong>Issue</strong>: <a href={{x.link}} target="_blank">{{x.link}}</a>
//				        </div>
//				    </div>
//				</div>
//				    </div>
//				    <div>
//				<div id="sortable" ng-controller="reqsCtrl">
//				    <div ng-repeat="x in requirements" class="jqxexpander" style="min-width: 500px">
//				        <div><strong>{{x.id + ': ' + x.title}}</strong></div>
//				        <div style="background-color: #ffd11a">
//				            <strong>Description</strong>: {{x.description}}<br>
//				            <strong>Characteristics:</strong>: {{x.characteristics}}<br>
//				            <strong>Issue</strong>: <a href={{x.link}} target="_blank">{{x.link}}</a>
//				        </div>
//				    </div>
//				</div>
//				    </div>
//				    <div>
//				<div id="sortable" ng-controller="reqsCtrl">
//				    <div ng-repeat="x in requirements" class="jqxexpander" style="min-width: 500px">
//				        <div><strong>{{x.id + ': ' + x.title}}</strong></div>
//				        <div style="background-color: #ffd11a">
//				            <strong>Description</strong>: {{x.description}}<br>
//				            <strong>Characteristics:</strong>: {{x.characteristics}}<br>
//				            <strong>Issue</strong>: <a href={{x.link}} target="_blank">{{x.link}}</a>
//				        </div>
//				    </div>
//				</div>
//				    </div>
//				</div>
//
//				<jqx-button ng-click="submit(gameId)">Submit prioritized requirements</jqx-button>
								
				System.out.println( b.toString() );
				
				return b.toString().getBytes();
			}} );
	}
	
	@Autowired
	private ResourceLoader resourceLoader;

	@RequestMapping("/**")
	public byte[] getResource( final HttpServletRequest request ) {


		String path = (String) request.getAttribute(
				HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
		String bestMatchPattern = (String ) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);

		AntPathMatcher apm = new AntPathMatcher();
		String finalPath = apm.extractPathWithinPattern(bestMatchPattern, path);
		
		ResourceProvider rp = providers.get( finalPath );
		
		if( rp != null ) {
			
			return rp.getResource( request );
			
		}
		else {
			
			System.out.println( "Serving page " + finalPath );

			Resource resource = resourceLoader.getResource("classpath:static/garp/" + finalPath );

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
			return ("Failed to load resource '" + finalPath).getBytes();
			
		}
	}
}
