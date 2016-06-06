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

package demo.rest;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import demo.jpa.UserCriteriaPointsJpa;
import demo.jpa.UsersJpa;
import demo.jpa.ValutationCriteriaJpa;
import demo.model.User;
import demo.model.ValutationCriteria;
import eu.supersede.fe.exception.NotFoundException;
import eu.supersede.fe.integration.ProxyWrapper;
import eu.supersede.fe.security.DatabaseUser;
import eu.supersede.integration.api.datastore.fe.types.Profile;

@RestController
@RequestMapping("/user")
public class UserRest {

	@Autowired
	private ProxyWrapper proxy;
	
	@Autowired
    private UsersJpa users;
	
	@Autowired
    private ValutationCriteriaJpa valutationCriterias;
	
	@Autowired
    private UserCriteriaPointsJpa userCriteriaPoints;
	
	// get a specific user by the Id
	@RequestMapping("/current")
	public User getUser(Authentication authentication)
	{
		DatabaseUser currentUser = (DatabaseUser) authentication.getPrincipal();
		Long userId = currentUser.getUserId();
		
		eu.supersede.integration.api.datastore.fe.types.User proxyUser = 
				proxy.getFEDataStoreProxy().getUser(currentUser.getTenantId(), userId.intValue(), true, currentUser.getToken());
		
		if(proxyUser == null)
		{
			throw new NotFoundException();
		}
		
		User u = users.findOne(userId);
		if(u == null)
		{
			u = new User(userId);
			users.save(u);
			u = users.findOne(userId);
		}
		
		u.setName(proxyUser.getFirst_name() + " " + proxyUser.getLast_name());
		u.setEmail(proxyUser.getEmail());
		
		return u;
	}
	
	// get all the users
	@RequestMapping(value = "", method = RequestMethod.GET)
	public List<User> getUsers(Authentication authentication,
			@RequestParam(required = false) String profile) 
	{
		DatabaseUser currentUser = (DatabaseUser) authentication.getPrincipal();
		List<eu.supersede.integration.api.datastore.fe.types.User> proxyUsers = proxy.getFEDataStoreProxy().getUsers(currentUser.getTenantId(), false, currentUser.getToken());
		
		List<User> us = new ArrayList<>();
		if(profile != null)
		{
			for(eu.supersede.integration.api.datastore.fe.types.User proxyUser : proxyUsers)
			{
				if(userIs(proxyUser, profile))
				{
					us.add(new User(new Long(proxyUser.getUser_id()),
							proxyUser.getFirst_name() + " " + proxyUser.getLast_name(),
							proxyUser.getEmail()));
				}
			}
		}
		else
		{
			for(eu.supersede.integration.api.datastore.fe.types.User proxyUser : proxyUsers)
			{
				us.add(new User(new Long(proxyUser.getUser_id()),
						proxyUser.getFirst_name() + " " + proxyUser.getLast_name(),
						proxyUser.getEmail()));
			}
		}
		
		return us;
	}
	
	private boolean userIs(eu.supersede.integration.api.datastore.fe.types.User proxyUser, String profile)
	{
		if(proxyUser.getProfiles() != null)
		{
			for(Profile p : proxyUser.getProfiles())
			{
				if(p.getName().equals(profile))
				{
					return true;
				}
			}
		}
		return false;
	}
	
	// Get all users that have a specific ValutationCriteria
	@RequestMapping(value = "/criteria/{criteriaId}", method = RequestMethod.GET)
	public List<User> getCriteriaUsers(@PathVariable Long criteriaId)
	{
		ValutationCriteria v = valutationCriterias.findOne(criteriaId);
		if(v == null){
			throw new NotFoundException();
		}
		
		List<User> userList = userCriteriaPoints.findUsersByValutationCriteria(v);		
		return userList;
	}
}
