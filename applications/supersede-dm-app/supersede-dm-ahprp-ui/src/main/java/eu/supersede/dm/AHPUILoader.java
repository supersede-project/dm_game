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

package eu.supersede.dm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eu.supersede.fe.application.ApplicationUtil;

@Component
public class AHPUILoader
{
    @Autowired
    private ApplicationUtil au;

    @PostConstruct
    public void load()
    {
        System.out.println("Registering AHP app");

        Map<String, String> labels = new HashMap<>();
        List<String> roles;

        labels = new HashMap<>();
        roles = new ArrayList<>();
        labels.put("", "AHP Home");
        roles.add("DM_ADMIN");
        roles.add("OPINION_PROVIDER");
        roles.add("DECISION_SCOPE_PROVIDER");
        roles.add("OPINION_NEGOTIATOR");
        au.addApplicationPage("supersede-dm-app", "ahprp/home", labels, roles);

        labels = new HashMap<>();
        labels.put("", "APP Home");
        roles = new ArrayList<>();
        roles = new ArrayList<>();
        roles.add("DM_ADMIN");
        roles.add("OPINION_PROVIDER");
        roles.add("DECISION_SCOPE_PROVIDER");
        roles.add("OPINION_NEGOTIATOR");
        au.addApplicationPage("supersede-dm-app", "ahprp/game_page", labels, roles);

        labels = new HashMap<>();
        labels.put("", "Edit Requirements");
        roles = new ArrayList<>();
        roles = new ArrayList<>();
        roles.add("DM_ADMIN");
        roles.add("DECISION_SCOPE_PROVIDER");
        au.addApplicationPage("supersede-dm-app", "requirements_criterias_editing", labels, roles);
    }
}