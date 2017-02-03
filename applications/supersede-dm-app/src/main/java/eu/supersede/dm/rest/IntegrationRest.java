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

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import eu.supersede.fe.notification.NotificationUtil;
import eu.supersede.gr.jpa.JpaAlerts;
import eu.supersede.gr.jpa.JpaApps;
import eu.supersede.gr.jpa.JpaReceivedUserRequests;
import eu.supersede.gr.jpa.RequirementsJpa;
import eu.supersede.gr.logics.Datastore;
import eu.supersede.gr.model.HAlert;
import eu.supersede.gr.model.HApp;
import eu.supersede.gr.model.HReceivedUserRequest;
import eu.supersede.gr.model.Requirement;
import eu.supersede.integration.api.dm.types.Alert;
import eu.supersede.integration.api.dm.types.Condition;
import eu.supersede.integration.api.dm.types.UserRequest;
import eu.supersede.integration.api.replan.controller.types.FeatureWP3;

@RestController
@RequestMapping("/api")
public class IntegrationRest
{
	@Autowired
	private NotificationUtil notificationUtil;

	@Autowired
	private RequirementsJpa requirementsTable;

	@Autowired JpaApps					jpaApps;
	@Autowired JpaAlerts				jpaAlerts;
	@Autowired JpaReceivedUserRequests	jpaReceivedUserRequests;


	@Autowired
	private Datastore datastore;

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@RequestMapping(value = "/public/monitoring/alert", method = RequestMethod.POST)
	public void notifyPublicAlert(@RequestBody Alert alert)
	{

		HApp app = jpaApps.findOne( alert.getApplicationID() );

		if( app == null ) {
			app = new HApp( alert.getApplicationID() );
			app = jpaApps.save( app );
		}

		if( alert.getID() == null ) {
			System.err.println( "alert.getID() IS NULL!" );
			alert.setID( "id-" + System.currentTimeMillis() );
		}

		HAlert halert = jpaAlerts.findOne( alert.getID() );

		if( halert == null ) {
			halert = new HAlert( alert.getID(), alert.getTimestamp() );
			halert.applicationID = app.id;
			halert = jpaAlerts.save( halert );
		}

		for (UserRequest request : alert.getRequests()) {

			HReceivedUserRequest hrur = new HReceivedUserRequest( request.getId() );
			
			if( hrur.getID() == null ) {
				System.err.println( "hrur.getID() IS NULL!" );
				hrur.setID( "UR" + System.currentTimeMillis() );
			}
			
			hrur.alertID = alert.getID();
			hrur.setAccuracy( request.getAccuracy() );
			hrur.setClassification( request.getClassification().name());
			hrur.setDescription( request.getDescription() );
			hrur.setNegativeSentiment( request.getNegativeSentiment() );
			hrur.setPositiveSentiment( request.getPositiveSentiment() );
			hrur.setOverallSentiment( request.getOverallSentiment() );
			jpaReceivedUserRequests.save( hrur );

		}


		List<Requirement> requirements = getRequirements(alert);

		for (Requirement r : requirements)
		{
			r.setRequirementId(null);
			requirementsTable.save(r);

//			datastore.storeAsNew(r);
		}
	}

	@RequestMapping(value = "/monitoring/alert", method = RequestMethod.POST)
	public void notifyAlert(@RequestBody Alert alert)
	{
		System.out.println("Alert received: " + alert);
		log.debug("Alert received: " + alert);

		String msg = "Alert {";
		msg += "ID:" + alert.getID();
		msg += "appID;" + alert.getApplicationID();
		msg += "tenant;" + alert.getTenant();
		msg += "timestamp;" + alert.getTimestamp();
		msg += "} = ";

		for (Condition c : alert.getConditions())
		{
			msg += "(";
			msg += c.getIdMonitoredData() + c.getOperator().name() + c.getValue();
			msg += ")";
		}

		notificationUtil.createNotificationsForProfile("DECISION_SCOPE_PROVIDER", msg, "");

		List<Requirement> requirements = getRequirements(alert);

		for (Requirement r : requirements)
		{
			datastore.storeAsNew(r);
		}

		return;
	}

	private List<Requirement> getRequirements(Alert alert)
	{
		// Either extract from the alert, or make a backward request to WP2

		List<Requirement> reqs = new ArrayList<>();

		for (UserRequest request : alert.getRequests())
		{
			reqs.add(new Requirement(request.getId() + ": " + request.getDescription(), ""));
		}

		return reqs;
	}

	@RequestMapping(value = "/api/features/schedule", method = RequestMethod.POST)
	public void scheduleRequirement(List<FeatureWP3> features)
	{
		for (FeatureWP3 feature : features)
		{
			System.out.println("Received: " + feature);
		}
	}

	@RequestMapping(value = "/api/features/{feature_id}/modify", method = RequestMethod.PUT)
	public void scheduleFeature(FeatureWP3 feature)
	{
		System.out.println("Received: " + feature);
	}
}