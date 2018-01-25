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

package eu.supersede.dm.rest;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import eu.supersede.dm.datamodel.Alert;
import eu.supersede.dm.datamodel.RequestClassification;
import eu.supersede.dm.datamodel.UserRequest;
import eu.supersede.fe.exception.NotFoundException;
import eu.supersede.gr.jpa.AlertsJpa;
import eu.supersede.gr.jpa.AppsJpa;
import eu.supersede.gr.jpa.ReceivedUserRequestsJpa;
import eu.supersede.gr.model.HAlert;
import eu.supersede.gr.model.HApp;
import eu.supersede.gr.model.HReceivedUserRequest;

@RestController
@RequestMapping("alerts")
public class AlertsRest
{
    @Autowired
    private AppsJpa appsJpa;

    @Autowired
    private AlertsJpa alertsJpa;

    @Autowired
    private ReceivedUserRequestsJpa receivedUserRequestsJpa;

    /**
     * Return all the requirements.
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public List<Alert> getAlerts(@RequestParam(required = false) String id)
    {
    	
        List<Alert> list = new ArrayList<>();
        List<HApp> apps = appsJpa.findAll();

        for (HApp app : apps)
        {
            List<HAlert> alerts = alertsJpa.findAlertsForApp(app.getId());

            for (HAlert alert : alerts)
            {
            	//Use optional parameter in order to filter alert list, retrieving the provided ID only
            	if( id != null && !"".equals(id) && !(alert.getId().equals(id))) {
            		//If ID is not the selected one, ignore it
            		continue;
            	}
            	
            	List<HReceivedUserRequest> requests = receivedUserRequestsJpa.findRequestsForAlert(alert.getId());

                if (requests.size() < 1)
                {
                    continue;
                }

                Alert a = new Alert();

                a.setId(alert.getId());
                a.setApplicationId(app.getId());
                a.setTimestamp(alert.getTimestamp());

                for (HReceivedUserRequest ur : requests)
                {
                    UserRequest r = new UserRequest();
                    r.setAccuracy(ur.getAccuracy());
                    r.setClassification(RequestClassification.valueOf(ur.getClassification()));
                    r.setDescription(ur.getDescription());
                    r.setId(ur.getId());
                    r.setNegativeSentiment(ur.getNegativeSentiment());
                    r.setPositiveSentiment(ur.getPositiveSentiment());
                    r.setOverallSentiment(ur.getOverallSentiment());
                    a.getRequests().add(r);
                }

                list.add(a);
            }
        }

        return list;
    }

    @RequestMapping(value = "/discard/{alertId}", method = RequestMethod.DELETE)
    public void discard(@PathVariable String alertId)
    {
        HAlert alert = alertsJpa.findOne(alertId);

        if (alert == null)
        {
            throw new NotFoundException("Can't delete alert with id " + alertId + " because it does not exist");
        }
        alertsJpa.delete(alert);
    }

    public static class FlattenedAlert
    {
        public String id;
        public String alertId;
        public String applicationId;
        public long timestamp;
        public String description;
        public RequestClassification classification;
        public double accuracy;
        public int pos;
        public int neg;
        public int overall;
    }

    @RequestMapping(value = "/biglist", method = RequestMethod.GET)
    public List<FlattenedAlert> getAlertsTree()
    {
        List<FlattenedAlert> list = new ArrayList<>();
        List<HApp> apps = appsJpa.findAll();

        for (HApp app : apps)
        {
            List<HAlert> alerts = alertsJpa.findAlertsForApp(app.getId());

            for (HAlert alert : alerts)
            {
                List<HReceivedUserRequest> requests = receivedUserRequestsJpa.findRequestsForAlert(alert.getId());

                if (requests == null || requests.size() == 0)
                {
                    continue;
                }

                for (HReceivedUserRequest ur : requests)
                {
                    FlattenedAlert fa = new FlattenedAlert();

                    fa.applicationId = app.getId();
                    fa.alertId = alert.getId();
                    fa.timestamp = alert.getTimestamp();
                    fa.accuracy = ur.getAccuracy();
                    fa.classification = RequestClassification.valueOf(ur.getClassification());
                    fa.description = ur.getDescription();
                    fa.id = ur.getId();
                    fa.neg = ur.getNegativeSentiment();
                    fa.pos = ur.getPositiveSentiment();
                    fa.overall = ur.getOverallSentiment();

                    list.add(fa);
                }
            }
        }

        return list;
    }

    @RequestMapping(value = "/userrequests", method = RequestMethod.GET)
    public List<HReceivedUserRequest> getReceivedUserRequests(@RequestParam String alertId)
    {
        return receivedUserRequestsJpa.findRequestsForAlert(alertId);
    }

    @RequestMapping(value = "/userrequests/discard", method = RequestMethod.PUT)
    public void discardUserRequest(@RequestParam String id)
    {
        HReceivedUserRequest userRequest = receivedUserRequestsJpa.findOne(id);

        if (userRequest == null)
        {
            throw new NotFoundException("Can't delete user request with id " + id + " because it does not exist");
        }

        receivedUserRequestsJpa.delete(userRequest);
    }
}