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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import eu.supersede.dm.ActivityDetails;
import eu.supersede.dm.DMGame;
import eu.supersede.dm.DMLibrary;
import eu.supersede.dm.DMMethod;
import eu.supersede.dm.ProcessManager;
import eu.supersede.dm.PropertyBag;
import eu.supersede.fe.security.DatabaseUser;
import eu.supersede.gr.model.HActivity;
import eu.supersede.gr.model.HProperty;

@RestController
@RequestMapping("processes/activities")
public class ProcessActivitiesRest
{
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public List<ActivityDetails> getActivityList(Authentication auth)
    {
        List<ActivityDetails> list = new ArrayList<>();
        List<HActivity> activities = DMGame.get()
                .getPendingActivities(((DatabaseUser) auth.getPrincipal()).getUserId());

        for (HActivity a : activities)
        {
            ActivityDetails d = new ActivityDetails();
            ProcessManager mgr = DMGame.get().getProcessManager(a.getProcessId());
            DMMethod m = DMLibrary.get().getMethod(a.getMethodName());
            d.setActivityId(a.getId());
            d.setMethodName(m.getLabel(mgr));
            d.setProcessId(a.getProcessId());
            d.setUserId(a.getUserId());

            if (m != null)
            {
                d.setUrl(m.getPage(mgr));
                list.add(d);
            }

            PropertyBag bag = mgr.getProperties(a);

            for (HProperty p : bag.getProperties())
            {
                d.setProperty(p.getKey(), p.getValue());
            }
        }

        return list;
    }

    @RequestMapping(value = "/groups", method = RequestMethod.GET)
    public Map<String, List<Long>> getActivityGroups(@RequestParam Long procId)
    {
        ProcessManager mgr = DMGame.get().getProcessManager(procId);
        List<HActivity> activities = mgr.getOngoingActivities();
        Map<String, List<Long>> map = new HashMap<>();

        for (HActivity a : activities)
        {
            List<Long> list = map.get(a.getMethodName());

            if (list == null)
            {
                list = new ArrayList<>();
                map.put(a.getMethodName(), list);
            }

            list.add(a.getUserId());
        }

        return map;
    }
}