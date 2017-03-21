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

package eu.supersede.dm.services;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import eu.supersede.dm.agent.RESTClient;
import eu.supersede.dm.datamodel.Feature;
import eu.supersede.dm.datamodel.FeatureList;
import eu.supersede.fe.exception.InternalServerErrorException;
import eu.supersede.integration.api.replan.controller.exception.ReplanControllerDuplicatedIdException;
import eu.supersede.integration.api.replan.controller.proxies.IReplanController;
import eu.supersede.integration.api.replan.controller.proxies.ReplanControllerProxy;
import eu.supersede.integration.api.replan.controller.types.AddFeaturesForProjectPayload;
import eu.supersede.integration.api.replan.controller.types.FeatureWP3;
import eu.supersede.integration.api.replan.controller.types.Priority;

public class EnactmentService
{
    private static EnactmentService instance = new EnactmentService();

    public static EnactmentService get()
    {
        return instance;
    }

    /*
     * Temporary Method - to be finalized
     */
    public void send(FeatureList features, boolean useIF, String tenant)
    {
        List<FeatureWP3> list = new ArrayList<>();

        for (Feature f : features.list())
        {
            try
            {
                FeatureWP3 fwp3 = new FeatureWP3();
                fwp3.setArguments("");
                fwp3.setDescription("");
                fwp3.setEffort(1.0);
                fwp3.setHardDependencies(new ArrayList<>());
                fwp3.setId(Integer.parseInt(f.getId()));
                fwp3.setName(f.getName());
                fwp3.setPriority(getPriorityEnum(f.getPriority()));
                fwp3.setRequiredSkills(new ArrayList<>());
                fwp3.setSoftDependencies(new ArrayList<>());
                list.add(fwp3);
            }
            catch (Exception ex)
            {
                System.err.println("Skip feature with ID: " + f.getId() + " (" + f.getName() + ")");
            }
        }

        try
        {
            AddFeaturesForProjectPayload p = new AddFeaturesForProjectPayload();
            p.setConstraints(new ArrayList<>());
            p.setEvaluationTime("");
            p.setFeatures(list);

            if (useIF)
            {
                IReplanController replan = new ReplanControllerProxy();
                replan.addFeaturesToProjectById(p, tenant);
            }
            else
            {
                String json = new Gson().toJson(p);

                json = json.replaceAll("\\bevaluationTime\\b", "evaluation_time");
                json = json.replaceAll("\\bhardDependencies\\b", "hard_dependencies");
                json = json.replaceAll("\\bsoftDependencies\\b", "soft_dependencies");

                System.out.println(json);

                RESTClient client = new RESTClient("http://supersede.es.atos.net:8280/replan");

                client.post("projects/1/features").header("Content-Type", "application/json")
                        .header("Cache-Control", "no-cache").send(json);
            }
        }
        catch (ReplanControllerDuplicatedIdException e)
        {
            throw new InternalServerErrorException(e.getMessage());
        }
        catch (Exception e)
        {
            throw new InternalServerErrorException(e.getMessage());
        }
    }

    private Priority getPriorityEnum(int priority)
    {
        switch (priority)
        {
            case 5:
                return Priority.FIVE;
            case 4:
                return Priority.FOUR;
            case 3:
                return Priority.THREE;
            case 2:
                return Priority.TWO;
            case 1:
                return Priority.ONE;
            default:
                return Priority.ONE;
        }
    }
}