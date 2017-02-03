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
import eu.supersede.integration.api.pubsub.SubscriptionTopic;
import eu.supersede.integration.api.pubsub.TopicSubscriber;

@Component
public class ModuleLoader {

	@Autowired RequirementsJpa requirementsTable;
	
	@Autowired JpaApps					jpaApps;
	@Autowired JpaAlerts				jpaAlerts;
	@Autowired JpaReceivedUserRequests	jpaReceivedUserRequests;


	public ModuleLoader() {

	}

	@PostConstruct
	public void init() {
		new Thread( new Runnable() {
			@Override
			public void run() {
				TopicSubscriber subscriber = null;
				try {
					subscriber = new TopicSubscriber(SubscriptionTopic.ANALISIS_DM_EVOLUTION_EVENT_TOPIC);
					subscriber.openTopicConnection();
					TextMessageListener messageListener = new TextMessageListener();
					subscriber.createTopicSubscriptionAndKeepListening (messageListener);
					try {
						while( true ) {
							Thread.sleep(1000); //FIXME Configure sleeping time
						}
					} catch( InterruptedException e ) {
						e.printStackTrace();
					}
					subscriber.closeSubscription();
					subscriber.closeTopicConnection();
					
				} catch (Exception e) {
					e.printStackTrace();
				}finally{
					if (subscriber != null){
						try {
							subscriber.closeTopicConnection();
						} catch (JMSException e) {
							throw new RuntimeException( "Error in closing topic connection", e );
						}
					}
				}
			}} ).start();
	}

	public class TextMessageListener implements MessageListener {

		private List<Requirement> getRequirements(Alert alert) {

			// Either extract from the alert, or make a backward request to WP2

			List<Requirement> reqs = new ArrayList<>();
			
			for (UserRequest request : alert.getRequests()) {
				reqs.add(
						new Requirement(
								request.getId() + ": " + request.getDescription(), ""));
			}

			return reqs;
		}

		public void onMessage(Message message) {
			try {
				
				System.out.println( requirementsTable );
				
				System.out.println("Got the Message : " + ((TextMessage) message).getText());

				String text = ((TextMessage) message).getText();

				Alert alert = new Gson().fromJson( text, Alert.class );
				
				HApp app = jpaApps.findOne( alert.getApplicationID() );
				
				if( app == null ) {
					app = new HApp();
					app = jpaApps.save( app );
					
					HAlert halert = jpaAlerts.findOne( alert.getID() );
					
					if( halert == null ) {
						halert = new HAlert( alert.getID(), alert.getTimestamp() );
						halert = jpaAlerts.save( halert );
					}
					
					for (UserRequest request : alert.getRequests()) {
						
						HReceivedUserRequest hrur = new HReceivedUserRequest();
						
						hrur.setAccuracy( request.getAccuracy() );
						hrur.setClassification( request.getClassification().name());
						hrur.setDescription( request.getDescription() );
						hrur.setNegativeSentiment( request.getNegativeSentiment() );
						hrur.setPositiveSentiment( request.getPositiveSentiment() );
						hrur.setOverallSentiment( request.getOverallSentiment() );
						jpaReceivedUserRequests.save( hrur );
						
					}
					
				}
				
				
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
