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

package eu.supersede.dm.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import eu.supersede.dm.datamodel.Alert;
import eu.supersede.dm.datamodel.Condition;
import eu.supersede.dm.datamodel.Feature;
import eu.supersede.dm.datamodel.FeatureList;
import eu.supersede.fe.notification.NotificationUtil;

@RestController
@RequestMapping("/api/public")
public class IntegrationRest {
	
	@Autowired
	private NotificationUtil notificationUtil;
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@RequestMapping(value = "/alert", method = RequestMethod.POST)
	public void notifyAlert(@RequestBody Alert alert) {
		log.debug("Alert received: " + alert);
		
		String msg = "Alert {";
		msg += "ID:" + alert.getID();
		msg += "appID;" + alert.getApplicationID();
		msg += "tenant;" + alert.getTenant();
		msg += "timestamp;" + alert.getTimestamp();
		msg += "resID;" + alert.getResourceID();
		msg += "} = ";
		
		for( Condition c : alert.getConditions() ) {
			msg += "(";
			msg += c.getIdMonitoredData() + c.getOperator().name() + c.getValue();
			msg += ")";
		}
		
		notificationUtil.createNotificationsForProfile("DECISION_SCOPE_PROVIDER", msg, "");
		
		return;
	}
	
	@RequestMapping(value = "/schedule", method = RequestMethod.POST)
	public void notifyFeatureScheduled( FeatureList features ) {
		
		for( Feature feature : features.list() ) {
			System.out.println( "Received: " + feature );
		}
		
	}
	
}
