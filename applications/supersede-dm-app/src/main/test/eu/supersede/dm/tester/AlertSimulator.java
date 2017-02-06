package eu.supersede.dm.tester;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.jms.JMSException;
import javax.naming.NamingException;

import com.fasterxml.jackson.core.JsonProcessingException;

import eu.supersede.integration.api.dm.types.Alert;
import eu.supersede.integration.api.dm.types.Condition;
import eu.supersede.integration.api.dm.types.DataID;
import eu.supersede.integration.api.dm.types.Operator;
import eu.supersede.integration.api.dm.types.RequestClassification;
import eu.supersede.integration.api.dm.types.UserRequest;
import eu.supersede.integration.api.pubsub.evolution.EvolutionPublisher;
import eu.supersede.integration.api.pubsub.evolution.iEvolutionPublisher;

public class AlertSimulator
{
    private String[] words = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum"
            .split("[ ]");
    private Random r = new Random(System.currentTimeMillis());

    public static void main(String[] args)
    {
        new AlertSimulator().run("http://127.0.0.1:80");
    }

    private String createRandomDescription()
    {
        String text = "";

        for (int i = 0; i < 3 + r.nextInt(10); i++)
        {
            text += words[r.nextInt(words.length)] + " ";
        }

        return text.trim();
    }

    private RequestClassification createRandomRequestClassification()
    {
        int c = r.nextInt(3);

        switch (c)
        {
            case 0:
                return RequestClassification.BugFixRequest;
            case 1:
                return RequestClassification.EnhancementRequest;
            case 2:
                return RequestClassification.FeatureRequest;
            default:
                return RequestClassification.FeatureRequest;
        }
    }

    public void run(String base_addr)
    {
        try
        {
            startPublisher();
        }
        catch (JsonProcessingException e)
        {
            e.printStackTrace();
        }
        catch (NamingException e)
        {
            e.printStackTrace();
        }
    }

    private Alert createAlert()
    {
        Alert alert = new Alert();

        alert.setID("id1");
        alert.setApplicationID("appId1");
        alert.setTimestamp(1481717773760L);
        alert.setTenant("Delta");

        List<Condition> conditions = new ArrayList<>();
        conditions.add(new Condition(DataID.UNSPECIFIED, Operator.GEq, 10.5));
        alert.setConditions(conditions);

        List<UserRequest> requests = new ArrayList<>();
        String[] feedbackIDs = new String[] { "feedbackId1" };
        String[] features = new String[] { "UI", "backend" };
        requests.add(new UserRequest("id1", createRandomRequestClassification(), 0.5, createRandomDescription(), 1, 2,
                0, feedbackIDs, features));
        alert.setRequests(requests);

        return alert;
    }

    private void startPublisher() throws NamingException, JsonProcessingException
    {
        iEvolutionPublisher publisher = null;

        try
        {
            publisher = new EvolutionPublisher(true);
            Alert alert = createAlert();
            publisher.publishEvolutionAlertMesssage(alert);
            System.out.println("Sending alert:");
            System.out.println(alert.getID());
            System.out.println(alert.getApplicationID());
            System.out.println(alert.getTenant());
            System.out.println(alert.getTimestamp() + "");
        }
        catch (JMSException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (publisher != null)
            {
                try
                {
                    publisher.closeTopicConnection();
                }
                catch (JMSException e)
                {
                    throw new RuntimeException("Error in closing topic connection", e);
                }
            }
        }
    }
}