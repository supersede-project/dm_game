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

package eu.supersede.dm.rest;

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

import eu.supersede.fe.exception.NotFoundException;
import eu.supersede.gr.jpa.RequirementsJpa;
import eu.supersede.gr.jpa.AHPRequirementsMatricesDataJpa;
import eu.supersede.gr.model.Requirement;
import eu.supersede.gr.model.HAHPRequirementsMatrixData;

@RestController
@RequestMapping("requirement")
public class RequirementRest
{
    @Autowired
    private RequirementsJpa requirements;

    @Autowired
    private AHPRequirementsMatricesDataJpa requirementsMatricesData;

    /**
     * Return the requirement with the given id.
     * @param requirementId
     */
    @RequestMapping("/{requirementId}")
    public Requirement getRequirement(@PathVariable Long requirementId)
    {
        Requirement c = requirements.findOne(requirementId);

        if (c == null)
        {
            throw new NotFoundException();
        }

        return c;
    }

    /**
     * Return all the requirements.
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public List<Requirement> getRequirements()
    {
        return requirements.findAll();
    }

    /**
     * Return the number of requirements.
     */
    @RequestMapping("/count")
    public Long count()
    {
        return requirements.count();
    }

    /**
     * Create a new requirement.
     * @param r
     */
    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<?> createRequirement(@RequestBody Requirement r)
    {
        r.setRequirementId(null);
        requirements.save(r);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(r.getRequirementId()).toUri());
        return new ResponseEntity<>(null, httpHeaders, HttpStatus.CREATED);
    }

    /**
     * Delete the requirement with the given id. TODO: check that it is correct.
     * @param requirementId
     */
    @RequestMapping(value = "/{requirementId}", method = RequestMethod.DELETE)
    public boolean deleteRequirement(@PathVariable Long requirementId)
    {
        Requirement requirement = requirements.findOne(requirementId);

        List<HAHPRequirementsMatrixData> listRow = requirementsMatricesData.findByRowRequirement(requirement);
        List<HAHPRequirementsMatrixData> listColumn = requirementsMatricesData.findByColumnRequirement(requirement);

        if (listRow.isEmpty() && listColumn.isEmpty())
        {
            requirements.delete(requirementId);
            return true;
        }

        return false;
    }

    /**
     * Edit the given requirement.
     * @param r
     */
    @RequestMapping(value = "", method = RequestMethod.PUT)
    public void editRequirement(@RequestBody Requirement r)
    {
        Requirement requirement = requirements.findOne(r.getRequirementId());
        requirement.setName(r.getName());
        requirement.setDescription(r.getDescription());
        requirements.save(requirement);
    }
}