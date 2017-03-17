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
import eu.supersede.dm.ProcessRole;
import eu.supersede.fe.exception.NotFoundException;
import eu.supersede.gr.model.HProcessMember;
import eu.supersede.gr.model.User;

@RestController
@RequestMapping("processes/users")
public class ProcessUsersRest
{
    @RequestMapping(value = "/import", method = RequestMethod.POST)
    public void importUsers(@RequestParam Long procId, @RequestParam(required = false) List<Long> idlist)
    {
        ProcessManager proc = DMGame.get().getProcessManager(procId);
        List<HProcessMember> processMembers = proc.getProcessMembers();

        for (HProcessMember processMember : processMembers)
        {
            proc.removeProcessMember(processMember.getId(), processMember.getUserId(), processMember.getProcessId());
        }

        if (idlist == null)
        {
            // No user has been added to the process.
            return;
        }

        for (Long userid : idlist)
        {
            proc.addProcessMember(userid, ProcessRole.User.name());
        }
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public List<Long> getUserList(@RequestParam Long procId)
    {
        ProcessManager proc = DMGame.get().getProcessManager(procId);
        List<Long> list = new ArrayList<>();

        for (HProcessMember member : proc.getProcessMembers())
        {
            list.add(member.getUserId());
        }

        return list;
    }

    @RequestMapping(value = "/list/detailed", method = RequestMethod.GET)
    public List<User> getUserObjectList(@RequestParam Long procId)
    {
        ProcessManager proc = DMGame.get().getProcessManager(procId);
        List<User> list = new ArrayList<>();

        for (HProcessMember member : proc.getProcessMembers())
        {
            User u = DMGame.get().getJpa().users.findOne(member.getUserId());

            if (u == null)
            {
                throw new NotFoundException("Can't get details of process member with id " + member.getUserId()
                        + " because it does not exist");
            }

            list.add(u);
        }

        return list;
    }
}