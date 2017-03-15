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

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import eu.supersede.dm.ActivityEntry;
import eu.supersede.dm.DMGame;
import eu.supersede.dm.DMLibrary;
import eu.supersede.dm.DMMethod;
import eu.supersede.dm.ProcessManager;
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
    public String getProcessStatus(@RequestParam Long procId)
    {
        ProcessManager mgr = DMGame.get().getProcessManager(procId);

        if (mgr == null)
        {
            return "";
        }

        return mgr.getProcessStatus().name();
    }

    @RequestMapping(value = "/status", method = RequestMethod.POST)
    public void getProcessStatus(@RequestParam Long procId, @RequestParam String status)
    {
        ProcessManager mgr = DMGame.get().getProcessManager(procId);

        if (mgr == null)
        {
            return;
        }

        try
        {
            mgr.setProcessStatus(ProcessStatus.valueOf(status));
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex.getMessage());
        }
    }

    @RequestMapping(value = "/close", method = RequestMethod.POST)
    public void closeProcess(@RequestParam Long procId) throws Exception
    {
        ProcessManager mgr = DMGame.get().getProcessManager(procId);

        if (mgr == null)
        {
            return;
        }

        if (mgr.getOngoingActivities().size() > 0)
        {
            throw new Exception("In order to close a process, you must close all the activities, first");
        }

        for (HActivity a : mgr.getOngoingActivities())
        {
            DMMethod m = DMLibrary.get().getMethod(a.getMethodName());

            if (m != null)
            {
                // TODO: let the method remote its data?
            }

            mgr.deleteActivity(a);
        }

        HProcess p = DMGame.get().getProcess(procId);

        if (p == null)
        {

            return;
        }

        p.setStatus(ProcessStatus.Closed);

        DMGame.get().getJpa().processes.save(p);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public void deleteProcess(@RequestParam Long procId)
    {
        HProcess p = DMGame.get().getProcess(procId);

        if (p == null)
        {
            return;
        }

        if (p.getStatus() == ProcessStatus.InProgress)
        {
            System.err.println("Can't delete process with id " + procId + ": you must close it first");
            return;
        }

        System.out.println("Deleted process " + procId);
        DMGame.get().deleteProcess(procId);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> exceptionHandler(Exception ex)
    {
        ErrorResponse error = new ErrorResponse();
        error.setErrorCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        error.setMessage(ex.getMessage());
        return new ResponseEntity<ErrorResponse>(error, HttpStatus.OK);
    }

    @RequestMapping(value = "/status", method = RequestMethod.GET, produces = "text/plain")
    public String getStatus(@RequestParam Long procId)
    {
        String ret = DMGame.get().getProcessManager(procId).getCurrentPhase();

        if (ret == null)
        {
            ret = DMGame.get().getLifecycle().getInitPhase().getName();
        }

        return ret;
    }

    @RequestMapping(value = "/available_activities", method = RequestMethod.GET)
    public List<ActivityEntry> getNextActivities(Long procId)
    {
        return DMGame.get().getProcessManager(procId).findNextActivities(DMLibrary.get().methods());
    }
}