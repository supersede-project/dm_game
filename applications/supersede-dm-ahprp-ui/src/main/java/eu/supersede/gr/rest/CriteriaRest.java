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

/**
* @author Andrea Sosi
**/

package eu.supersede.gr.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import eu.supersede.gr.jpa.RequirementsMatricesDataJpa;
import eu.supersede.gr.jpa.ValutationCriteriaJpa;
import eu.supersede.gr.model.RequirementsMatrixData;
import eu.supersede.gr.model.ValutationCriteria;
import eu.supersede.fe.exception.NotFoundException;

@RestController
@RequestMapping("/ahprp/criteria")
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
	@RequestMapping(value = "", method = RequestMethod.GET)
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
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseEntity<?> createCriteria(@RequestBody ValutationCriteria vc)
	{
		vc.setCriteriaId(null);
		valutationCriterias.save(vc);
		
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setLocation(ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
				.buildAndExpand(vc.getCriteriaId()).toUri());
		return new ResponseEntity<>(null, httpHeaders, HttpStatus.CREATED);
	}
	
	// TODO check because is not perfectly correct (because maybe it's better check the existence of the criteria in the criteria's game table) ##################################################
	// delete criteria
	@RequestMapping(value = "/{criteriaId}", method = RequestMethod.DELETE)
	public boolean deleteCriteria(@PathVariable Long criteriaId)
	{
		ValutationCriteria criteria = valutationCriterias.findOne(criteriaId);
		
		List<RequirementsMatrixData> list = requirementsMatricesData.findByCriteria(criteria);
		
		if(list.isEmpty()){
			valutationCriterias.delete(criteriaId);
			return true;
		}	
		return false;
	}
	
	// edit criteria
	@RequestMapping(value = "", method = RequestMethod.PUT)
	public void editCriteria(@RequestBody ValutationCriteria vc)
	{
		ValutationCriteria criteria = valutationCriterias.findOne(vc.getCriteriaId());
		criteria.setName(vc.getName());
		criteria.setDescription(vc.getDescription());
		valutationCriterias.save(criteria);
	}
}