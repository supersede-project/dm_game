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
    public void importCriteria(@RequestParam Long procId, @RequestParam(required = false) List<Long> idlist)
    {
        ProcessManager proc = DMGame.get().getProcessManager(procId);
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

        for (Long cid : idlist)
        {
            ValutationCriteria c = DMGame.get().getCriterion(cid);
            proc.addCriterion(c);
        }
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public List<Long> getCriteriaList(@RequestParam Long procId)
    {
        ProcessManager proc = DMGame.get().getProcessManager(procId);
        List<Long> list = new ArrayList<>();

        for (ValutationCriteria c : proc.getCriteria())
        {
            list.add(c.getCriteriaId());
        }

        return list;
    }

    @RequestMapping(value = "/list/detailed", method = RequestMethod.GET)
    public List<HProcessCriterion> getCriteriaObjectList(@RequestParam Long procId)
    {
        ProcessManager proc = DMGame.get().getProcessManager(procId);
        return proc.getProcessCriteria();
    }
}