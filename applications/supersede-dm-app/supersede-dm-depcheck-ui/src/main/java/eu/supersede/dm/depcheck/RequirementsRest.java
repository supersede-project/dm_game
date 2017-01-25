package eu.supersede.dm.depcheck;

import java.util.List;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;

import eu.supersede.dm.depcheck.data.XRequirement;
import eu.supersede.dm.depcheck.data.XTopic;


public class RequirementsRest {

	public static void main( String[] args ) {
		new RequirementsRest().getRequirementsTest();
	}

	public void getRequirementsTest() {

		try( OObjectDatabaseTx db = new OObjectDatabaseTx( (ODatabaseDocumentTx)new ODatabaseDocumentTx ("memory:memdb").create() ) ) {

			db.getEntityManager().registerEntityClass(XRequirement.class);
			db.getEntityManager().registerEntityClass(XTopic.class);

			{
				XTopic t = db.save( new XTopic( "Topic" ) );
				XRequirement r = new XRequirement( "R1", "Requirement 1", t );
				System.out.println( r.getText() );
				r = db.save( r );
				db.commit();
			}

			{
				List<XRequirement> requirements = db.query(
						new OSQLSynchQuery<XRequirement>("select * from " + XRequirement.class.getSimpleName() ) ); // where text = 'Requirement 1'"));

				for( XRequirement r : requirements ) {
					System.out.println( r.getId() + " - " + r.getTopic() );
				}
			}

			{
				List<XTopic> topics = db.query(
						new OSQLSynchQuery<XRequirement>("select * from " + XTopic.class.getSimpleName() ) ); // where text = 'Requirement 1'"));

				for( XTopic t : topics ) {
					System.out.println( "Topic: " + t );
				}
			}

		}
		catch( Exception ex ) {
			ex.printStackTrace();
		}

	}

}
