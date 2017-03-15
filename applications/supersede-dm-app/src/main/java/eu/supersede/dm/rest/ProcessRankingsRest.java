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
import eu.supersede.fe.security.DatabaseUser;
import eu.supersede.gr.model.HRequirementScore;
import eu.supersede.gr.model.Requirement;
import eu.supersede.gr.model.RequirementStatus;

@RestController
@RequestMapping("processes/rankings")
public class ProcessRankingsRest
{
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public void createRanking(Authentication authentication, @RequestParam Long procId, @RequestParam String name)
    {
        ProcessManager mgr = DMGame.get().getProcessManager(procId);
        RequirementsRanking rr = mgr.getRankingByName(name);

        if (rr == null)
        {
            Long id = mgr.createRanking(name);
            rr = mgr.getRanking(id);
        }

        // System.out.println("Sending requirements for enactment");

        String tenant = ((DatabaseUser) authentication.getPrincipal()).getTenantId();
        FeatureList list = new FeatureList();
        List<Requirement> requirements = new ArrayList<>();

        for (HRequirementScore score : rr.getScores())
        {
            Requirement r = DMGame.get().getJpa().requirements.findOne(score.getRequirementId());
            Feature feature = new Feature();
            feature.setName(r.getName());
            feature.setPriority(score.getPriority().asNumber());
            feature.setId("" + r.getRequirementId());
            list.list().add(feature);
            requirements.add(r);
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
    public List<RequirementsRanking> getRankings(@RequestParam Long procId)
    {
        ProcessManager mgr = DMGame.get().getProcessManager(procId);

        if (mgr == null)
        {
            return null;
        }

        return mgr.getRankings();
    }

    @RequestMapping(value = "/enact", method = RequestMethod.PUT)
    public void doEnactRanking(Authentication authentication, @RequestParam Long procId, @RequestParam Long rankingId)
    {
        ProcessManager mgr = DMGame.get().getProcessManager(procId);
        System.out.println("Sending requirements for enactment");
        List<RequirementsRanking> rlist = mgr.getRankings();

        for (RequirementsRanking rr : rlist)
        {
            if (!rr.isSelected())
            {
                continue;
            }

            String tenant = ((DatabaseUser) authentication.getPrincipal()).getTenantId();
            FeatureList list = new FeatureList();

            List<Requirement> requirements = new ArrayList<>();

            for (HRequirementScore score : rr.getScores())
            {
                Requirement r = DMGame.get().getJpa().requirements.findOne(score.getRequirementId());
                Feature feature = new Feature();
                feature.setName(r.getName());
                feature.setPriority(score.getPriority().asNumber());
                feature.setId("" + r.getRequirementId());
                list.list().add(feature);
                requirements.add(r);
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

        // RequirementsRanking rr = mgr.getRanking( rankingId );
    }
}