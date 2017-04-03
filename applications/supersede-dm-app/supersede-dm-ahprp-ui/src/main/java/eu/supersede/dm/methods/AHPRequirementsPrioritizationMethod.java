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

package eu.supersede.dm.methods;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import eu.supersede.dm.DMCondition;
import eu.supersede.dm.DMMethod;
import eu.supersede.dm.DMObjective;
import eu.supersede.dm.DMOption;
import eu.supersede.dm.DMPhases;
import eu.supersede.dm.DMRole;
import eu.supersede.dm.DMRoleSpec;
import eu.supersede.dm.DMTask;
import eu.supersede.dm.ProcessManager;
import eu.supersede.dm.ahp.algorithm.AHPStructure;
import eu.supersede.dm.ahp.algorithm.Ahp;
import eu.supersede.gr.model.Requirement;

public class AHPRequirementsPrioritizationMethod implements DMMethod
{
    public static final String NAME = "AHP session";
    private static final String PAGE = "ahprp/home";

    private List<DMRoleSpec> list;
    private List<DMOption> options;

    public AHPRequirementsPrioritizationMethod()
    {
        list = new ArrayList<>();
        options = new ArrayList<>();

        list.add(new DMRoleSpec(new DMRole("Game Master"), 1, 1));
        list.add(new DMRoleSpec(new DMRole("Negotiator"), 0, 1));
        list.add(new DMRoleSpec(new DMRole("Opinion Provider"), 1, -1));

        options.add(new DMOption("gamification", new String[] { "on", "off" }));
        options.add(new DMOption("negotiator", new String[] { "active", "not active" }));
    }

    @Override
    public String getName()
    {
        return NAME;
    }

    @Override
    public DMObjective getObjective()
    {
        return DMObjective.PrioritizeRequirements;
    }

    @Override
    public List<DMRoleSpec> getRoleList()
    {
        return list;
    }

    public String getPage(String step)
    {
        return "";
    }

    @Override
    public List<DMOption> getOptions()
    {
        return this.options;
    }

    public void setOption(String optName, String optValue)
    {
        // TODO
    }

    public void init(ProcessManager status)
    {
        // String pid = executor.startBPMN( "supersedeAHPDM" );
        // status.setProperty( "pid", pid );
    }

    public void callAHP()
    {

        AHPStructure objAHP = new AHPStructure();

        // Preference from 0 to 8
        objAHP.setCriteria("C1", "C2");
        objAHP.setOptions("Op1", "Op2");

        // Preference from 0 to 8
        objAHP.setPreference("C1", "C2", 6);
        objAHP.setOptionPreference("Op1", "Op2", "C1", 2);
        objAHP.setOptionPreference("Op1", "Op2", "C2", 8);

        Ahp objCalculateRank = new Ahp(objAHP);

        objCalculateRank.execute();
    }

    public boolean isComplete(ProcessManager status)
    {
        return getActiveTasks(status).size() < 1;
    }

    public List<DMTask> getActiveTasks(ProcessManager status)
    {
        return new ArrayList<>();
    }

    public void createGame(ProcessManager status)
    {
        // TODO Auto-generated method stub

    }

    public void completeTask(ProcessManager status, DMTask task)
    {
        // executor.completeTask( status.getProperty( "pid", "" ), task );
    }

    public void callAHP(ProcessManager status)
    {

        AHPStructure objAHP = new AHPStructure();

        Set<String> topics = new HashSet<>();
        for (Requirement r : status.getRequirements())
        {
            if (!"".equals(r.getTopic()))
            {
                // if( r.getTopic() != DMTopic.none ) {
                if (topics.contains(r.getTopic()))
                {
                    topics.add(r.getTopic());
                }
            }
        }

        if (topics.size() < 1)
        {
            topics.add("Priority");
        }

        {
            List<String> tlist = new ArrayList<>();
            for (String topic : topics)
            {
                tlist.add(topic);
            }
            objAHP.setCriteria(tlist);
        }

        {
            List<String> reqs = new ArrayList<>();
            for (Requirement req : status.getRequirements())
            {
                reqs.add("" + req.getRequirementId());
            }
            objAHP.setOptions(reqs);
        }

        Random random = new Random(System.currentTimeMillis());

        for (String t1 : topics)
        {
            for (String t2 : topics)
            {
                // Preference from 0 to 8
                objAHP.setPreference(t1, t2, random.nextInt(9));
            }
        }

        for (String topic : topics)
        {
            for (Requirement r1 : status.getRequirements())
            {
                for (Requirement r2 : status.getRequirements())
                {
                    // Preference from 0 to 8
                    objAHP.setOptionPreference("" + r1.getRequirementId(), "" + r2.getRequirementId(), topic,
                            random.nextInt(9));
                }
            }
        }

        Ahp objCalculateRank = new Ahp(objAHP);

        Map<String, Double> result = objCalculateRank.execute();

        System.out.println("Results:");
        for (String key : result.keySet())
        {

            System.out.println(key + " = " + result.get(key));

        }
        System.out.println("SENDINGG RESULTS AHEAD FOR ENACTMENT");
    }

    @Override
    public List<DMCondition> preconditions()
    {
        List<DMCondition> list = new ArrayList<DMCondition>();
        list.add(new DMCondition()
        {
            @Override
            public boolean isTrue(ProcessManager mgr)
            {
                return mgr.getCurrentPhase().equals(DMPhases.PRIORITIZATION);
            }
        });
        return list;
    }

    @Override
    public String getPage(ProcessManager mgr)
    {
        return PAGE;
    }

    @Override
    public String getDescription(ProcessManager mgr)
    {
        return NAME + " in process " + mgr.getProcess().getName();
    }

    @Override
    public String getLabel(ProcessManager arg0)
    {
        return NAME;
    }
}