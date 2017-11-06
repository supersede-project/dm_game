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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import eu.supersede.dm.DMGame;
import eu.supersede.dm.RequirementDetails;
import eu.supersede.fe.exception.NotFoundException;
import eu.supersede.gr.jpa.AHPRequirementsMatricesDataJpa;
import eu.supersede.gr.model.HAHPRequirementsMatrixData;
import eu.supersede.gr.model.HRequirementDependency;
import eu.supersede.gr.model.HRequirementProperty;
import eu.supersede.gr.model.Requirement;

@RestController
@RequestMapping("requirement")
public class RequirementRest {
	@Autowired
	private AHPRequirementsMatricesDataJpa requirementsMatricesData;

	@Autowired
	private EntityManager entityManager;

	/**
	 * Return the requirement with the given id.
	 * 
	 * @param requirementId
	 */
	@RequestMapping("/{requirementId}")
	public Requirement getRequirement(@PathVariable Long requirementId) {
		Requirement requirement = DMGame.get().getJpa().requirements.findOne(requirementId);

		if (requirement == null) {
			throw new NotFoundException("Requirement with id " + requirementId + " does not exist");
		}

		return requirement;
	}

	/**
	 * Return all the requirements.
	 */
	@RequestMapping(value = "", method = RequestMethod.GET)
	public List<Requirement> getRequirements(@RequestParam(required = false) String procFx,
			@RequestParam(required = false) Long processId, @RequestParam(required = false) String statusFx,
			@RequestParam(required = false) Integer status) {

		String root = "SELECT r FROM Requirement r";
		String filter = "";

		if ("Eq".equals(procFx)) {
			filter += " processId = " + processId;
		} else if ("Neq".equals(procFx)) {
			filter += " processId != " + processId;
		}

		if (procFx != null && statusFx != null) {
			filter += " AND ";
		}

		if ("Eq".equals(statusFx)) {
			filter += " status = " + status;
		} else if ("Neq".equals(statusFx)) {
			filter += " status != " + status;
		}

		String query = root;

		if (!filter.equals("")) {
			query = query + " WHERE" + filter;
		}

		return entityManager.createQuery(query, Requirement.class).getResultList();
	}

	@RequestMapping(value = "details/list", method = RequestMethod.GET)
	public List<RequirementDetails> getDetailedRequirements() {
		List<RequirementDetails> list = new ArrayList<>();
		List<Requirement> reqlist = DMGame.get().getJpa().requirements.findAll();

		for (Requirement r : reqlist) {
			RequirementDetails details = new RequirementDetails(r);
			List<HRequirementDependency> deps = DMGame.get().getJpa().requirementDependencies
					.findByRequirementId(r.getRequirementId());

			for (HRequirementDependency d : deps) {
				details.addDependency(d);
			}

			List<HRequirementProperty> props = DMGame.get().getJpa().requirementProperties
					.findPropertiesByRequirementId(r.getRequirementId());

			for (HRequirementProperty p : props) {
				details.setProperty(p);
			}

			list.add(details);
		}

		return list;
	}

	/**
	 * Return the number of requirements.
	 */
	@RequestMapping("/count")
	public Long count() {
		return DMGame.get().getJpa().requirements.count();
	}

	/**
	 * Create a new requirement.
	 * 
	 * @param r
	 */
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseEntity<?> createRequirement(@RequestBody Requirement r) {
		r.setRequirementId(null);
		DMGame.get().getJpa().requirements.save(r);

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setLocation(ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
				.buildAndExpand(r.getRequirementId()).toUri());
		return new ResponseEntity<>(null, httpHeaders, HttpStatus.CREATED);
	}

	/**
	 * Delete the requirement with the given id. TODO: check that it is correct.
	 * 
	 * @param requirementId
	 */
	@RequestMapping(value = "/{requirementId}", method = RequestMethod.DELETE)
	public boolean deleteRequirement(@PathVariable Long requirementId) {
		Requirement requirement = DMGame.get().getJpa().requirements.findOne(requirementId);

		// FIXME: this is obsolete code
		List<HAHPRequirementsMatrixData> listRow = requirementsMatricesData.findByRowRequirement(requirement);
		List<HAHPRequirementsMatrixData> listColumn = requirementsMatricesData.findByColumnRequirement(requirement);

		if (listRow.isEmpty() && listColumn.isEmpty()) {
			DMGame.get().getJpa().requirements.delete(requirementId);
			return true;
		}

		return false;
	}

	/**
	 * Edit the given requirement changing name and description.
	 * 
	 * @param r
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	public void editRequirement(@RequestParam(required = true) Long id, @RequestParam(required = true) String name,
			@RequestParam(required = true) String description) {
		Requirement requirement = DMGame.get().getJpa().requirements.findOne(id);

		if (requirement == null) {
			throw new NotFoundException("Can't edit requirement with id " + id + " because it does not exist");
		}

		requirement.setName(name);
		requirement.setDescription(description);
		DMGame.get().getJpa().requirements.save(requirement);
	}

	/**
	 * Edit the given requirement.
	 * 
	 * @param r
	 */
	@RequestMapping(value = "", method = RequestMethod.PUT)
	public void editRequirement(@RequestBody Requirement r) {
		Requirement requirement = DMGame.get().getJpa().requirements.findOne(r.getRequirementId());

		if (requirement == null) {
			throw new NotFoundException(
					"Can't edit requirement with id " + r.getRequirementId() + " because it does not exist");
		}

		requirement.setName(r.getName());
		requirement.setDescription(r.getDescription());
		DMGame.get().getJpa().requirements.save(requirement);
	}
}