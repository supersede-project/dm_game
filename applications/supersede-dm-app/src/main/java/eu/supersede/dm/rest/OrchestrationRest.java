package eu.supersede.dm.rest;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import eu.supersede.dm.DMSolution;
import eu.supersede.dm.OrchestratorDemo;
import eu.supersede.gr.jpa.RequirementsJpa;
import eu.supersede.gr.jpa.UsersJpa;
import eu.supersede.gr.jpa.ValutationCriteriaJpa;
import eu.supersede.gr.logics.Datastore;

@RestController
@RequestMapping("/orchestration")
public class OrchestrationRest implements Datastore.DataJpaProvider {
	
	@Autowired private UsersJpa users;
	
	@Autowired private RequirementsJpa requirements;
	
	@Autowired private ValutationCriteriaJpa criteria;
	
	public OrchestrationRest() {
		Datastore.get().setDataJpaProvider( this );
	}
	
	@RequestMapping(value = "/plan", method = RequestMethod.GET)
	public String doPlan() {
		
		DMSolution sol = new OrchestratorDemo().plan();

		Gson gson = new Gson();

		String ret = gson.toJson( sol );

		System.out.println( ret );

		return ret;
	}

	@ResponseBody
	@RequestMapping(value = "/planimage", method = RequestMethod.GET, produces = MediaType.IMAGE_PNG_VALUE)
	public byte[] getImage( DMSolution sol ) {
		
		BufferedImage img = new BufferedImage( 200, 400, BufferedImage.TYPE_INT_ARGB );
		
		WritableRaster raster = img .getRaster();
		DataBufferByte data   = (DataBufferByte) raster.getDataBuffer();
		
//		ImageIO.write( img, "png", out );
		
		return ( data.getData() );

	}
	
	@Override
	public UsersJpa getUsersJpa() {
		return this.users;
	}

	@Override
	public RequirementsJpa getRequirementsJpa() {
		return this.requirements;
	}

	@Override
	public ValutationCriteriaJpa getValutationCriteriaJpa() {
		return this.criteria;
	}
}
