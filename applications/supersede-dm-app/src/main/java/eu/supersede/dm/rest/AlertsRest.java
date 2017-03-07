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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import eu.supersede.dm.datamodel.Alert;
import eu.supersede.dm.datamodel.RequestClassification;
import eu.supersede.dm.datamodel.UserRequest;
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
    // @Autowired
    // private RequirementsJpa requirements;

    @Autowired
    AppsJpa jpaApps;

    @Autowired
    AlertsJpa jpaAlerts;

    @Autowired
    ReceivedUserRequestsJpa jpaReceivedUserRequests;

    /**
     * Return all the requirements.
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public List<Alert> getAlerts()
    {
        List<Alert> list = new ArrayList<>();
        List<HApp> apps = jpaApps.findAll();

        for (HApp app : apps)
        {
            List<HAlert> alerts = jpaAlerts.findAlertsForApp(app.getId());

            for (HAlert alert : alerts)
            {
                List<HReceivedUserRequest> requests = jpaReceivedUserRequests.findRequestsForAlert(alert.getId());

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

    public static class FlattenedAlert
    {
        public String id;
        public String alertID;
        public String applicationID;
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
        // return new ArrayList<>();
        //
        List<FlattenedAlert> list = new ArrayList<>();
        List<HApp> apps = jpaApps.findAll();

        for (HApp app : apps)
        {
            List<HAlert> alerts = jpaAlerts.findAll();

            for (HAlert alert : alerts)
                try
                {
                    List<HReceivedUserRequest> requests = jpaReceivedUserRequests.findRequestsForAlert(alert.getId());

                    if (requests.size() < 1)
                    {
                        continue;
                    }

                    for (HReceivedUserRequest ur : requests)
                    {
                        FlattenedAlert fa = new FlattenedAlert();

                        fa.applicationID = app.getId();
                        fa.alertID = alert.getId();
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
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
        }

        return list;
    }
}