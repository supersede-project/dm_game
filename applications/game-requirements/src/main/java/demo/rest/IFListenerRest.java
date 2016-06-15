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

/**
* @author Alberto Siena
**/

package demo.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import demo.utility.Alert;
import eu.supersede.fe.notification.NotificationUtil;

@RestController
@RequestMapping("/api/public")
public class IFListenerRest {
	
	@Autowired
	private NotificationUtil notificationUtil;
	
	@RequestMapping(value = "/alert", method = RequestMethod.POST, consumes = MediaType.ALL_VALUE )
	public boolean notifyAlert( @RequestBody String string ) {
		
		Alert alert = new Gson().fromJson( string, Alert.class );
		
		System.out.println( "Alert received: " + alert );
		
		notificationUtil.createNotificationsForProfile( "DECISION_SCOPE_PROVIDER", alert.getMessage(), "" );
		
		return true;
		
	}
	
}
