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
import eu.supersede.gr.jpa.AHPRequirementsMatricesDataJpa;
import eu.supersede.gr.jpa.ValutationCriteriaJpa;
import eu.supersede.gr.model.HAHPRequirementsMatrixData;
import eu.supersede.gr.model.ValutationCriteria;

@RestController
@RequestMapping("/criteria")
public class CriteriaRest
{
    @Autowired
    private ValutationCriteriaJpa valutationCriterias;

    @Autowired
    private AHPRequirementsMatricesDataJpa requirementsMatricesData;

    /**
     * Return the criterion with the given id.
     * @param criteriaId
     */
    @RequestMapping("/{criteriaId}")
    public ValutationCriteria getCriteria(@PathVariable Long criteriaId)
    {
        ValutationCriteria criterion = valutationCriterias.findOne(criteriaId);

        if (criterion == null)
        {
            throw new NotFoundException("Criterion with id " + criteriaId + " does not exist");
        }

        return criterion;
    }

    /**
     * Return all criteria.
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public List<ValutationCriteria> getCriterias()
    {
        return valutationCriterias.findAll();
    }

    /**
     * Return the number of criteria.
     */
    @RequestMapping("/count")
    public Long count()
    {
        return valutationCriterias.count();
    }

    /**
     * Create a new criterion.
     * @param vc
     */
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

    /**
     * Delete the criterion with the given id. TODO: check that it works, maybe it's better to check the existence of
     * the criterion in the corresponding table.
     * @param criteriaId
     */
    @RequestMapping(value = "/{criteriaId}", method = RequestMethod.DELETE)
    public boolean deleteCriteria(@PathVariable Long criteriaId)
    {
        ValutationCriteria criteria = valutationCriterias.findOne(criteriaId);

        List<HAHPRequirementsMatrixData> list = requirementsMatricesData.findByCriteria(criteria);

        if (list.isEmpty())
        {
            valutationCriterias.delete(criteriaId);
            return true;
        }

        return false;
    }

    /**
     * Edit the given criterion.
     * @param vc
     */
    @RequestMapping(value = "", method = RequestMethod.PUT)
    public void editCriteria(@RequestBody ValutationCriteria vc)
    {
        ValutationCriteria criterion = valutationCriterias.findOne(vc.getCriteriaId());

        if (criterion == null)
        {
            throw new NotFoundException("Can't edit criterion with id " + vc.getCriteriaId() + ": it does not exist");
        }

        criterion.setName(vc.getName());
        criterion.setDescription(vc.getDescription());
        valutationCriterias.save(criterion);
    }
}