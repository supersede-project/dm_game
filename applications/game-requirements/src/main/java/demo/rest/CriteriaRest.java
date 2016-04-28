package demo.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import demo.jpa.RequirementsMatricesDataJpa;
import demo.jpa.ValutationCriteriaJpa;
import demo.model.RequirementsMatrixData;
import demo.model.ValutationCriteria;
import eu.supersede.fe.exception.NotFoundException;

@RestController
@RequestMapping("/criteria")
public class CriteriaRest {
	
	@Autowired
    private ValutationCriteriaJpa valutationCriterias;
	
	@Autowired
    private RequirementsMatricesDataJpa requirementsMatricesData;
		
	//get a specific ValutationCriteria
	@RequestMapping("/{criteriaId}")
	public ValutationCriteria getCriteria(@PathVariable Long criteriaId)
	{
		ValutationCriteria c = valutationCriterias.findOne(criteriaId);
		if(c == null)
		{	
			throw new NotFoundException();
		}
		
		return c;
	}
	
	// get all the ValutationCriterias
	@RequestMapping("")
	public List<ValutationCriteria> getCriterias() 
	{
		return valutationCriterias.findAll();
	}
	
	// get number of ValutationCriterias
	@RequestMapping("/count")
	public Long count() {
		return valutationCriterias.count();
	}
	
	// create new criteria
	@RequestMapping(value = "/create/{name}/description/{description}", method = RequestMethod.PUT)
	public void createCriteria(@PathVariable String name, @PathVariable String description)
	{
		ValutationCriteria vc = new ValutationCriteria();
		vc.setName(name);
		vc.setDescription(description);
		valutationCriterias.save(vc);
	}
	
	// TODO check because is not perfectly correct (because maybe it's better check the existence of the criteria in the criteria's game table) ##################################################
	// delete criteria
	@RequestMapping(value = "/delete/{criteriaId}", method = RequestMethod.PUT)
	public boolean deleteCriteria(@PathVariable Long criteriaId)
	{
		ValutationCriteria criteria = valutationCriterias.findOne(criteriaId);
		
		List<RequirementsMatrixData> list = requirementsMatricesData.findByCriteria(criteria);
		
		if(list.size() < 1){
			valutationCriterias.delete(criteriaId);
			return true;
		}	
		return false;
	}
	
	// edit criteria
	@RequestMapping(value = "/edit/{criteriaId}/name/{name}/description/{description}", method = RequestMethod.PUT)
	public void editCriteria(@PathVariable Long criteriaId, @PathVariable String name, @PathVariable String description)
	{
		ValutationCriteria criteria = valutationCriterias.findOne(criteriaId);
		criteria.setName(name);
		criteria.setDescription(description);
		valutationCriterias.save(criteria);
	}
}