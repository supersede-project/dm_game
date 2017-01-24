package eu.supersede.dm;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;

import eu.supersede.fe.application.ApplicationUtil;
import eu.supersede.gr.jpa.RequirementsJpa;
import eu.supersede.gr.model.Requirement;
import eu.supersede.integration.api.dm.types.Alert;
import eu.supersede.integration.api.dm.types.UserRequest;
import eu.supersede.integration.api.pubsub.SubscriptionTopic;
import eu.supersede.integration.api.pubsub.TopicSubscriber;

@Component
public class ModuleLoader {

	@Autowired
	private ApplicationUtil au;

	@Autowired
	private RequirementsJpa requirementsTable;


	public ModuleLoader() {

	}

	@PostConstruct
	public void init() {
		TopicSubscriber subscriber = null;
		try {
			subscriber = new TopicSubscriber(SubscriptionTopic.ANALISIS_DM_EVENT_TOPIC);
			subscriber.openTopicConnection();
			TextMessageListener messageListener = new TextMessageListener();
			subscriber.createTopicSubscriptionAndKeepListening (messageListener);
			//					try {
			//						while (!messageReceived) {
			//							Thread.sleep(1000); //FIXME Configure sleeping time
			//						}
			//					}catch (InterruptedException e) {
			//						e.printStackTrace();
			//					}
			//					subscriber.closeSubscription();
			//					subscriber.closeTopicConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if (subscriber != null){
				try {
					subscriber.closeTopicConnection();
				} catch (JMSException e) {
					throw new RuntimeException("Error in closing topic connection", e);
				}
			}
		}
	}

	public class TextMessageListener implements MessageListener {

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

		public void onMessage(Message message) {
			try {
				System.out.println("Got the Message : " + ((TextMessage) message).getText());

				String text = ((TextMessage) message).getText();

				Alert alert = new Gson().fromJson( text, Alert.class );


				List<Requirement> requirements = getRequirements(alert);

				for (Requirement r : requirements)
				{

					r.setRequirementId(null);
					if( requirementsTable != null ) {
						requirementsTable.save(r);
					}
					else {
						System.out.println( "requirementsTable is NULL" );
					}

					//		            datastore.storeAsNew(r);
				}

//				messageReceived = true;
			} catch (JMSException e) {
				e.printStackTrace();
			}
		}
	}
}
