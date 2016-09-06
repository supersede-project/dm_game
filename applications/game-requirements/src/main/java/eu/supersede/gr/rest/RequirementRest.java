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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import eu.supersede.gr.jpa.RequirementsJpa;
import eu.supersede.gr.jpa.RequirementsMatricesDataJpa;
import eu.supersede.gr.model.Requirement;
import eu.supersede.gr.model.RequirementsMatrixData;
import eu.supersede.fe.exception.NotFoundException;

@RestController
@RequestMapping("/requirement")
public class RequirementRest {
	
	@Autowired
    private RequirementsJpa requirements;
	
	@Autowired
    private RequirementsMatricesDataJpa requirementsMatricesData;
	
	// get a specific requirement 
	@RequestMapping("/{requirementId}")
	public Requirement getRequirement(@PathVariable Long requirementId)
	{
		Requirement c = requirements.findOne(requirementId);
		if(c == null)
		{
			throw new NotFoundException();
		}
		
		return c;
	}

	// all the requirements
	@RequestMapping(value = "", method = RequestMethod.GET)
	public List<Requirement> getRequirements() {
		return requirements.findAll();
	}
	
	// get number of requirements
	@RequestMapping("/count")
	public Long count() {
		return requirements.count();
	}
	
	// create new requirement
	@RequestMapping(value = "", method = RequestMethod.POST)
	public void createRequirement(@RequestBody Requirement r)
	{
		r.setRequirementId(null);
		requirements.save(r);
	}
	
	// TODO check because is not perfectly correct ##################################################
	// delete requirement
	@RequestMapping(value = "/{requirementId}", method = RequestMethod.DELETE)
	public boolean deleteRequirement(@PathVariable Long requirementId)
	{
		Requirement requirement = requirements.findOne(requirementId);
		
		List<RequirementsMatrixData> listRow = requirementsMatricesData.findByRowRequirement(requirement);
		List<RequirementsMatrixData> listColumn = requirementsMatricesData.findByColumnRequirement(requirement);

		if(listRow.isEmpty() && listColumn.isEmpty()){
			requirements.delete(requirementId);
			return true;
		}	
		return false;
	}
	
	// edit requirement
	@RequestMapping(value = "", method = RequestMethod.PUT)
	public void editRequirement(@RequestBody Requirement r)
	{
		Requirement requirement = requirements.findOne(r.getRequirementId());
		requirement.setName(r.getName());
		requirement.setDescription(r.getDescription());
		requirements.save(requirement);
	}
}