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

package eu.supersede.dm.rest;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import eu.supersede.dm.DMGame;
import eu.supersede.fe.exception.InternalServerErrorException;
import eu.supersede.fe.exception.NotFoundException;
import eu.supersede.fe.integration.ProxyWrapper;
import eu.supersede.fe.security.DatabaseUser;
import eu.supersede.gr.jpa.UserCriteriaPointsJpa;
import eu.supersede.gr.jpa.UsersJpa;
import eu.supersede.gr.jpa.ValutationCriteriaJpa;
import eu.supersede.gr.model.User;
import eu.supersede.gr.model.ValutationCriteria;
import eu.supersede.integration.api.datastore.fe.types.Profile;

@RestController
@RequestMapping("/user")
public class UserRest
{
    @Autowired
    private ProxyWrapper proxy;

    @Autowired
    private UsersJpa users;

    @Autowired
    private ValutationCriteriaJpa valutationCriterias;

    @Autowired
    private UserCriteriaPointsJpa userCriteriaPoints;

    /**
     * Return the current user.
     * @param authentication
     */
    @RequestMapping("/current")
    public User getUser(Authentication authentication)
    {
        DatabaseUser currentUser = (DatabaseUser) authentication.getPrincipal();
        Long userId = currentUser.getUserId();

        return DMGame.get().getUser(userId, currentUser.getTenantId(), currentUser.getToken());
    }

    /**
     * Return all the users with the given profile.
     * @param authentication
     * @param profile
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public List<User> getUsers(Authentication authentication, @RequestParam(required = false) String profile)
    {
        DatabaseUser currentUser = (DatabaseUser) authentication.getPrincipal();
        List<eu.supersede.integration.api.datastore.fe.types.User> proxyUsers = null;

        try
        {
            proxyUsers = proxy.getFEDataStoreProxy().getUsers(currentUser.getTenantId(), false, currentUser.getToken());
        }
        catch (URISyntaxException e)
        {
            throw new InternalServerErrorException(e.getMessage());
        }

        List<User> us = new ArrayList<>();

        if (profile != null)
        {
            for (eu.supersede.integration.api.datastore.fe.types.User proxyUser : proxyUsers)
            {
                if (hasProfile(proxyUser, profile))
                {
                    us.add(new User(new Long(proxyUser.getUser_id()),
                            proxyUser.getFirst_name() + " " + proxyUser.getLast_name(), proxyUser.getEmail()));
                }
            }
        }
        else
        {
            for (eu.supersede.integration.api.datastore.fe.types.User proxyUser : proxyUsers)
            {
                us.add(new User(new Long(proxyUser.getUser_id()),
                        proxyUser.getFirst_name() + " " + proxyUser.getLast_name(), proxyUser.getEmail()));
            }
        }

        return us;
    }

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public List<User> getFrontendUsers()
    {
        return users.findAll();
    }

    /**
     * Check whether the given user has the given profile.
     * @param proxyUser
     * @param profile
     */
    private boolean hasProfile(eu.supersede.integration.api.datastore.fe.types.User proxyUser, String profile)
    {
        if (proxyUser.getProfiles() != null)
        {
            for (Profile p : proxyUser.getProfiles())
            {
                if (p.getName().equals(profile))
                {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Return the users with the given evaluation criterion.
     * @param criteriaId
     */
    @RequestMapping(value = "/criteria/{criteriaId}", method = RequestMethod.GET)
    public List<User> getCriteriaUsers(@PathVariable Long criteriaId)
    {
        ValutationCriteria v = valutationCriterias.findOne(criteriaId);

        if (v == null)
        {
            throw new NotFoundException("Criterion with id " + criteriaId + " does not exist");
        }

        List<User> userList = userCriteriaPoints.findUsersByValutationCriteria(v);
        return userList;
    }
}