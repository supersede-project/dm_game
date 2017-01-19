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
			
			XRequirement r = new XRequirement( "R1", "Requirement 1", new XTopic( "Topic" ) );
			
			r = db.save( r );
			
			List<XRequirement> result = db.query(
					  new OSQLSynchQuery<XRequirement>("select * from " + XRequirement.class.getSimpleName() ) ); // where text = 'Requirement 1'"));
			
			for( XRequirement req : result ) {
				System.out.println( req.getId() );
			}
			
		}
		catch( Exception ex ) {
			ex.printStackTrace();
		}
		
	}
	
}
