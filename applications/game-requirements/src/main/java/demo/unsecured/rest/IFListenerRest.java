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

package demo.unsecured.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import demo.unsecured.model.Alert;
import eu.supersede.fe.notification.NotificationUtil;

@RestController
@RequestMapping("/api/public")
public class IFListenerRest {
	
	@Autowired
	private NotificationUtil notificationUtil;
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@RequestMapping(value = "/alert", method = RequestMethod.POST)
	public void notifyAlert(@RequestBody Alert alert) {
		log.debug("Alert received: " + alert);
		
		notificationUtil.createNotificationsForProfile("DECISION_SCOPE_PROVIDER", alert.getMessage(), "");
		
		return;
	}
	
}
