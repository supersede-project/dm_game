package eu.supersede.dm;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.jms.JMSException;
import javax.naming.NamingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eu.supersede.gr.jpa.JpaAlerts;
import eu.supersede.gr.jpa.JpaApps;
import eu.supersede.gr.jpa.JpaReceivedUserRequests;
import eu.supersede.gr.jpa.RequirementsJpa;
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
    RequirementsJpa requirementsTable;

    @Autowired
    JpaApps jpaApps;
    @Autowired
    JpaAlerts jpaAlerts;
    @Autowired
    JpaReceivedUserRequests jpaReceivedUserRequests;

    public ModuleLoader()
    {

    }

    @PostConstruct
    public void init()
    {
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
                            if (messageListener.isMessageReceived())
                            {
                                handleAlert(messageListener.getAlert());
                                messageListener.resetMessageReceived();
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
        System.out.println("Handling alert:");
        System.out.println(alert.getID());
        System.out.println(alert.getApplicationID());
        System.out.println(alert.getTenant());
        System.out.println(alert.getTimestamp() + "");
        HApp app = jpaApps.findOne(alert.getApplicationID());

        if (app == null)
        {
            app = new HApp();
            app.setId(alert.getApplicationID());
            app = jpaApps.save(app);

            HAlert halert = jpaAlerts.findOne(alert.getID());

            if (halert == null)
            {
                halert = new HAlert(alert.getID(), alert.getTimestamp());
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
            if (requirementsTable != null)
            {
                requirementsTable.save(r);
            }
            else
            {
                System.out.println("requirementsTable is NULL");
            }

            // datastore.storeAsNew(r);
        }
    }

}
