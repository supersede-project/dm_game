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
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import eu.supersede.dm.DMGame;
import eu.supersede.dm.ProcessManager;
import eu.supersede.dm.RequirementsRanking;
import eu.supersede.dm.datamodel.Feature;
import eu.supersede.dm.datamodel.FeatureList;
import eu.supersede.dm.services.EnactmentService;
import eu.supersede.fe.exception.NotFoundException;
import eu.supersede.fe.security.DatabaseUser;
import eu.supersede.gr.jpa.RequirementsRankingsJpa;
import eu.supersede.gr.model.HRequirementScore;
import eu.supersede.gr.model.HRequirementsRanking;
import eu.supersede.gr.model.Requirement;
import eu.supersede.gr.model.RequirementStatus;

@RestController
@RequestMapping("processes/rankings")
public class ProcessRankingsRest
{
    @Autowired
    private RequirementsRankingsJpa requirementsRankingsJpa;

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public void createRanking(Authentication authentication, @RequestParam Long processId, @RequestParam String name)
    {
        ProcessManager mgr = DMGame.get().getProcessManager(processId);
        RequirementsRanking rr = mgr.getRankingByName(name);

        if (rr == null)
        {
            HRequirementsRanking requirementsRanking = mgr.createRanking(name);
            rr = new RequirementsRanking();
            rr.setProcessId(processId);
            rr.setName(name);
            rr.setSelected(requirementsRanking.isSelected());
            rr.setEnacted(requirementsRanking.isEnacted());
        }

        // Send requirements for enacting

        String tenant = ((DatabaseUser) authentication.getPrincipal()).getTenantId();
        FeatureList list = new FeatureList();
        List<Requirement> requirements = new ArrayList<>();

        for (HRequirementScore score : rr.getScores())
        {
            Requirement requirement = DMGame.get().getJpa().requirements.findOne(score.getRequirementId());

            if (requirement == null)
            {
                throw new NotFoundException(
                        "Can't enact requirement with id " + score.getRequirementId() + " because it does not exist");
            }

            Feature feature = new Feature();
            feature.setName(requirement.getName());
            feature.setPriority(score.getPriority().asNumber());
            feature.setId("" + requirement.getRequirementId());
            list.list().add(feature);
            requirements.add(requirement);
        }

        try
        {
            EnactmentService.get().send(list, true, tenant);

            for (Requirement r : requirements)
            {
                RequirementStatus oldStatus = RequirementStatus.valueOf(r.getStatus());

                if (RequirementStatus.next(oldStatus).contains(RequirementStatus.Enacted))
                {
                    r.setStatus(RequirementStatus.Enacted.getValue());
                    DMGame.get().getJpa().requirements.save(r);
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public List<RequirementsRanking> getRankings(@RequestParam Long processId)
    {
        return DMGame.get().getProcessManager(processId).getRankings();
    }

    @RequestMapping(value = "/enact", method = RequestMethod.PUT)
    public void doEnactRanking(Authentication authentication, @RequestParam Long processId)
    {
        ProcessManager mgr = DMGame.get().getProcessManager(processId);
        List<RequirementsRanking> rlist = mgr.getRankings();

        for (RequirementsRanking rr : rlist)
        {
            if (!rr.isSelected() || rr.isEnacted())
            {
                continue;
            }

            String tenant = ((DatabaseUser) authentication.getPrincipal()).getTenantId();
            FeatureList list = new FeatureList();

            List<Requirement> requirements = new ArrayList<>();

            for (HRequirementScore score : rr.getScores())
            {
                Requirement requirement = DMGame.get().getJpa().requirements.findOne(score.getRequirementId());

                if (requirement == null)
                {
                    throw new NotFoundException("Can't enact requirement with id " + score.getRequirementId()
                            + " because it does not exist");
                }

                Feature feature = new Feature();
                feature.setName(requirement.getName());
                feature.setPriority(score.getPriority().asNumber());
                feature.setId("" + requirement.getRequirementId());
                list.list().add(feature);
                requirements.add(requirement);
            }

            EnactmentService.get().send(list, true, tenant);

            for (Requirement r : requirements)
            {
                RequirementStatus oldStatus = RequirementStatus.valueOf(r.getStatus());

                if (RequirementStatus.next(oldStatus).contains(RequirementStatus.Enacted))
                {
                    r.setStatus(RequirementStatus.Enacted.getValue());
                    DMGame.get().getJpa().requirements.save(r);
                }
            }

            HRequirementsRanking rankings = requirementsRankingsJpa.findRankingsByProcessIdAndName(rr.getProcessId(),
                    rr.getName());
            rankings.setEnacted(true);
            rankings.setEnactmentDate( new Date() );
            requirementsRankingsJpa.save(rankings);
        }
    }
}