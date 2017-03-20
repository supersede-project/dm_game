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

import eu.supersede.dm.ActivityEntry;
import eu.supersede.dm.DMGame;
import eu.supersede.dm.DMLibrary;
import eu.supersede.dm.DMMethod;
import eu.supersede.dm.DMPhase;
import eu.supersede.dm.ProcessManager;
import eu.supersede.fe.exception.InternalServerErrorException;
import eu.supersede.gr.model.HActivity;
import eu.supersede.gr.model.HProcess;
import eu.supersede.gr.model.ProcessStatus;

@RestController
@RequestMapping("processes")
public class ProcessRest
{
    static class JqxProcess
    {
        public String id;
        public String name;
        public String state;
        public String date;
        public String objective;
    }

    @RequestMapping(value = "new", method = RequestMethod.POST)
    public Long newProcess(@RequestParam(required = false) String name)
    {
        return DMGame.get().createEmptyProcess(name).getId();
    }

    @RequestMapping(value = "details", method = RequestMethod.GET)
    public HProcess getProcess(@RequestParam Long processId)
    {
        return DMGame.get().getProcess(processId);
    }

    @RequestMapping(value = "list", method = RequestMethod.GET)
    public List<JqxProcess> getProcessList()
    {
        List<JqxProcess> list = new ArrayList<JqxProcess>();

        for (HProcess p : DMGame.get().getJpa().processes.findAll())
        {
            JqxProcess qp = new JqxProcess();
            qp.id = "" + p.getId();
            qp.name = p.getName();
            qp.state = p.getStatus().name();
            qp.objective = p.getObjective();
            qp.date = p.getStartTime().toString();
            list.add(qp);
        }

        return list;
    }

    @RequestMapping(value = "/status", method = RequestMethod.GET)
    public String getProcessStatus(@RequestParam Long processId)
    {
        ProcessManager mgr = DMGame.get().getProcessManager(processId);
        return mgr.getProcessStatus().name();
    }

    @RequestMapping(value = "/status", method = RequestMethod.POST)
    public void setProcessStatus(@RequestParam Long processId, @RequestParam String status)
    {
        ProcessManager mgr = DMGame.get().getProcessManager(processId);
        mgr.setProcessStatus(ProcessStatus.valueOf(status));
    }

    @RequestMapping(value = "/close", method = RequestMethod.POST)
    public void closeProcess(@RequestParam Long processId) throws Exception
    {
        ProcessManager mgr = DMGame.get().getProcessManager(processId);

        for (HActivity a : mgr.getOngoingActivities())
        {
            DMMethod m = DMLibrary.get().getMethod(a.getMethodName());

            if (m != null)
            {
                // TODO: let the method remote its data?
            }

            mgr.deleteActivity(a);
        }

        HProcess p = DMGame.get().getProcess(processId);
        p.setStatus(ProcessStatus.Closed);
        DMGame.get().getJpa().processes.save(p);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public void deleteProcess(@RequestParam Long processId)
    {
        HProcess p = DMGame.get().getProcess(processId);

        if (p.getStatus() == ProcessStatus.InProgress)
        {
            throw new InternalServerErrorException(
                    "Can't delete process with id " + processId + ": you must close it first");
        }

        DMGame.get().deleteProcess(processId);
    }

    @RequestMapping(value = "/status", method = RequestMethod.GET, produces = "text/plain")
    public String getStatus(@RequestParam Long processId)
    {
        String status = DMGame.get().getProcessManager(processId).getCurrentPhase();

        if (status == null)
        {
            status = DMGame.get().getLifecycle().getInitPhase().getName();
        }

        return status;
    }

    @RequestMapping(value = "/available_activities", method = RequestMethod.GET)
    public List<ActivityEntry> getNextActivities(Long processId)
    {
        return DMGame.get().getProcessManager(processId).findNextActivities(DMLibrary.get().methods());
    }

    @RequestMapping(value = "/next", method = RequestMethod.GET)
    public HProcess setNextPhase(@RequestParam Long processId) throws Exception
    {
        ProcessManager mgr = DMGame.get().getProcessManager(processId);
        String phaseName = mgr.getCurrentPhase();
        DMPhase phase = DMGame.get().getLifecycle().getPhase(phaseName);

        if (phase.getNextPhases().isEmpty())
        {
            throw new InternalServerErrorException("No next phase available");
        }

        // Assume only one next phase is possible

        for (DMPhase n : phase.getNextPhases())
        {
            try
            {
                n.checkPreconditions(mgr);
                n.activate(mgr);
                mgr.setNextPhase(n.getName());
                return mgr.getProcess();
            }
            catch (Exception e)
            {
                throw new InternalServerErrorException("No next phase available");
            }
        }

        throw new InternalServerErrorException("No next phase available");
    }

    @RequestMapping(value = "/prev", method = RequestMethod.GET)
    public HProcess setPrevPhase(@RequestParam Long processId) throws Exception
    {
        ProcessManager mgr = DMGame.get().getProcessManager(processId);
        String phaseName = mgr.getCurrentPhase();
        DMPhase phase = DMGame.get().getLifecycle().getPhase(phaseName);

        if (phase.getPrevPhases().isEmpty())
        {
            System.out.println("Error!");
            throw new InternalServerErrorException("No previous phase available");
        }

        // Assume only one next phase is possible

        for (DMPhase n : phase.getPrevPhases())
        {
            try
            {
                n.checkPreconditions(mgr);
                n.activate(mgr);
                mgr.setNextPhase(n.getName());
                return mgr.getProcess();
            }
            catch (Exception e)
            {
                throw new InternalServerErrorException("No previous phase available");
            }
        }

        throw new InternalServerErrorException("No previous phase available");
    }
}