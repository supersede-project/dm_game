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

package eu.supersede.dm;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.jms.JMSException;
import javax.naming.NamingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eu.supersede.fe.multitenant.MultiJpaProvider;
import eu.supersede.gr.jpa.ActivitiesJpa;
import eu.supersede.gr.jpa.AlertsJpa;
import eu.supersede.gr.jpa.AppsJpa;
import eu.supersede.gr.jpa.ProcessCriteriaJpa;
import eu.supersede.gr.jpa.ProcessMembersJpa;
import eu.supersede.gr.jpa.ProcessesJpa;
import eu.supersede.gr.jpa.PropertiesJpa;
import eu.supersede.gr.jpa.PropertyBagsJpa;
import eu.supersede.gr.jpa.ReceivedUserRequestsJpa;
import eu.supersede.gr.jpa.RequirementsDependenciesJpa;
import eu.supersede.gr.jpa.RequirementsJpa;
import eu.supersede.gr.jpa.RequirementsPropertiesJpa;
import eu.supersede.gr.jpa.UsersJpa;
import eu.supersede.gr.jpa.ValutationCriteriaJpa;
import eu.supersede.gr.model.HAlert;
import eu.supersede.gr.model.HApp;
import eu.supersede.gr.model.HReceivedUserRequest;
import eu.supersede.gr.model.Requirement;
import eu.supersede.integration.api.dm.types.Alert;
import eu.supersede.integration.api.dm.types.UserRequest;
import eu.supersede.integration.api.pubsub.evolution.EvolutionAlertMessageListener;
import eu.supersede.integration.api.pubsub.evolution.EvolutionSubscriber;
import eu.supersede.integration.api.pubsub.evolution.iEvolutionSubscriber;

@Component
public class ModuleLoader
{
    @Autowired
    UsersJpa jpaUsers;

    @Autowired
    RequirementsJpa jpaRequirements;

    @Autowired
    ValutationCriteriaJpa jpaCriteria;

    @Autowired
    ProcessesJpa jpaProcesses;

    @Autowired
    ProcessMembersJpa jpaMembers;

    @Autowired
    ActivitiesJpa jpaActivities;

    @Autowired
    ProcessCriteriaJpa jpaProcessCriteria;

    @Autowired
    AppsJpa jpaApps;

    @Autowired
    AlertsJpa jpaAlerts;

    @Autowired
    ReceivedUserRequestsJpa jpaReceivedUserRequests;

    @Autowired
    RequirementsPropertiesJpa jpaRequirementProperties;

    @Autowired
    RequirementsDependenciesJpa jpaRequirementDependencies;

    @Autowired
    PropertiesJpa jpaProperties;

    @Autowired
    PropertyBagsJpa jpaPropertyBags;

    @Autowired
    MultiJpaProvider multiJpaProvider;

    public ModuleLoader()
    {
    }

    @PostConstruct
    public void init()
    {
        DMGame.JpaProvider jpa = new DMGame.JpaProvider();
        jpa.activities = jpaActivities;
        jpa.criteria = jpaCriteria;
        jpa.members = jpaMembers;
        jpa.processes = jpaProcesses;
        jpa.requirements = jpaRequirements;
        jpa.users = jpaUsers;
        jpa.processCriteria = jpaProcessCriteria;
        jpa.alerts = jpaAlerts;
        jpa.apps = jpaApps;
        jpa.receivedUserRequests = jpaReceivedUserRequests;
        jpa.requirementProperties = jpaRequirementProperties;
        jpa.requirementDependencies = jpaRequirementDependencies;
        jpa.properties = jpaProperties;
        jpa.propertyBags = jpaPropertyBags;

        DMGame.init(jpa);

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                iEvolutionSubscriber subscriber = null;

                try
                {
                    subscriber = new EvolutionSubscriber();
                    subscriber.openTopicConnection();
                    EvolutionAlertMessageListener messageListener = subscriber
                            .createEvolutionAlertSubscriptionAndKeepListening();

                    try
                    {
                        while (true)
                        {
                            if (messageListener.areMessageReceived())
                            {
                                handleAlert(messageListener.getNextAlert());
                            }
                            else
                            {
                                Thread.sleep(1000); // FIXME Configure sleeping time
                            }
                        }
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }

                    subscriber.closeSubscription();
                    subscriber.closeTopicConnection();
                }
                catch (JMSException e)
                {
                    e.printStackTrace();
                }
                catch (NamingException e1)
                {
                    e1.printStackTrace();
                }
                finally
                {
                    if (subscriber != null)
                    {
                        try
                        {
                            subscriber.closeTopicConnection();
                        }
                        catch (JMSException e)
                        {
                            throw new RuntimeException("Error in closing topic connection", e);
                        }
                    }
                }
            }
        }).start();
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

    public void handleAlert(Alert alert)
    {
        System.out.println("Handling alert: " + alert.getId() + ", " + alert.getApplicationId() + ", "
                + alert.getTenant() + ", " + alert.getTimestamp());

        // Override class JPA instances with multitenancy provided
        AppsJpa jpaApps = multiJpaProvider.getRepository(AppsJpa.class, alert.getTenant());
        AlertsJpa jpaAlerts = multiJpaProvider.getRepository(AlertsJpa.class, alert.getTenant());
        ReceivedUserRequestsJpa jpaReceivedUserRequests = multiJpaProvider.getRepository(ReceivedUserRequestsJpa.class,
                alert.getTenant());
        RequirementsJpa jpaRequirements = multiJpaProvider.getRepository(RequirementsJpa.class, alert.getTenant());

        if ((jpaApps == null))
        {
            System.out.println("Unknown tenant: '" + alert.getTenant() + "'");
            return;
        }

        HApp app = jpaApps.findOne(alert.getApplicationId());

        if (app == null)
        {
            app = new HApp();
            app.setId(alert.getApplicationId());
            app = jpaApps.save(app);

            HAlert halert = jpaAlerts.findOne(alert.getId());

            if (halert == null)
            {
                halert = new HAlert(alert.getId(), alert.getTimestamp());
                halert = jpaAlerts.save(halert);
            }

            for (UserRequest request : alert.getRequests())
            {
                HReceivedUserRequest hrur = new HReceivedUserRequest();
                hrur.setId(request.getId());
                hrur.setAccuracy(request.getAccuracy());
                hrur.setClassification(request.getClassification().name());
                hrur.setDescription(request.getDescription());
                hrur.setNegativeSentiment(request.getNegativeSentiment());
                hrur.setPositiveSentiment(request.getPositiveSentiment());
                hrur.setOverallSentiment(request.getOverallSentiment());
                jpaReceivedUserRequests.save(hrur);
            }
        }

        List<Requirement> requirements = getRequirements(alert);

        for (Requirement r : requirements)
        {
            r.setRequirementId(null);

            if (jpaRequirements != null)
            {
                jpaRequirements.save(r);
            }
            else
            {
                System.out.println("requirementsTable is NULL");
            }
        }
    }
}