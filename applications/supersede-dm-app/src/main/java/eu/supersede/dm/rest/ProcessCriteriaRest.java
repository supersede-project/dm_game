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

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import eu.supersede.dm.DMGame;
import eu.supersede.dm.ProcessManager;
import eu.supersede.gr.model.HProcessCriterion;
import eu.supersede.gr.model.ValutationCriteria;

@RestController
@RequestMapping("processes/criteria")
public class ProcessCriteriaRest
{
    @RequestMapping(value = "/import", method = RequestMethod.POST)
    public void importCriteria(@RequestParam Long processId, @RequestParam(required = false) List<Long> idlist)
    {
        ProcessManager proc = DMGame.get().getProcessManager(processId);
        List<HProcessCriterion> processCriteria = proc.getProcessCriteria();

        for (HProcessCriterion processCriterion : processCriteria)
        {
            proc.removeCriterion(processCriterion.getCriterionId(), processCriterion.getSourceId(),
                    processCriterion.getProcessId());
        }

        if (idlist == null)
        {
            // No criterion has been added to the process.
            return;
        }

        for (Long criterionId : idlist)
        {
            ValutationCriteria criterion = DMGame.get().getCriterion(criterionId);
            proc.addCriterion(criterion);
        }
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public List<Long> getCriteriaList(@RequestParam Long processId)
    {
        ProcessManager proc = DMGame.get().getProcessManager(processId);
        List<Long> list = new ArrayList<>();

        for (ValutationCriteria c : proc.getCriteria())
        {
            list.add(c.getCriteriaId());
        }

        return list;
    }

    @RequestMapping(value = "/list/detailed", method = RequestMethod.GET)
    public List<HProcessCriterion> getCriteriaObjectList(@RequestParam Long processId)
    {
        return DMGame.get().getProcessManager(processId).getProcessCriteria();
    }
}