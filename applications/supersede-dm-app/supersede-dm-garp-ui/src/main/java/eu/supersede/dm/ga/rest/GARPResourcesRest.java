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

package eu.supersede.dm.ga.rest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerMapping;

import eu.supersede.dm.ga.GAPersistentDB;
import eu.supersede.dm.iga.IGAAlgorithm;
import eu.supersede.gr.jpa.RequirementsJpa;
import eu.supersede.gr.model.Requirement;

/*
 * If static resources do not need to be secured, this method is not necessary.
 * Otherwise, getResource can filter resource access on the basis of user role
 */

@RestController
@RequestMapping("/garp")
public class GARPResourcesRest
{
    private static Map<Long, String> requirements = new HashMap<>();

    @Autowired
    private RequirementsJpa availableRequirements;

    @Autowired
    private GAPersistentDB persistentDB;

    interface ResourceProvider
    {
        public byte[] getResource(final HttpServletRequest request, String id);
    }

    static Map<String, ResourceProvider> providers = new HashMap<>();

    @PostConstruct
    private void load()
    {
        providers.put("results.html", new ResourceProvider()
        {
            @Override
            public byte[] getResource(HttpServletRequest request, String id)
            {
                long gameId = new Long(id);

                StringBuilder b = new StringBuilder();

                b.append("<script src=\"supersede-dm-app/garp/js/results.js\"></script>");
                b.append("<style>");
                b.append("h1 { text-align: center; }\n");
                b.append("#sortable { margin: 0 auto; padding: 5px; width: 520px; }\n");
                b.append("#sortable > div { border-radius: 5px; margin: 5px; }\n");
                b.append("#sortable div { padding: 5px; }\n");
                b.append("#sortable div:hover { border: 1px solid #356AA0; }\n");
                b.append("</style>\n");

                IGAAlgorithm algo = new IGAAlgorithm();

                HashMap<Long, Double> criteriaWeights = persistentDB.getCriteriaWeights(gameId);
                List<String> gameCriteria = new ArrayList<>();

                for (Long criterionId : criteriaWeights.keySet())
                {
                    gameCriteria.add("" + criterionId);
                    algo.setCriterionWeight("" + criterionId, criteriaWeights.get(criterionId));
                }

                algo.setCriteria(gameCriteria);

                for (Long rid : persistentDB.getRequirements(gameId))
                {
                    algo.addRequirement(requirements.get(rid), new ArrayList<>());
                }

                // get all the players in this game
                List<Long> participantIds = persistentDB.getParticipants(gameId);

                // get the rankings of each player for each criterion
                List<String> players = new ArrayList<>();

                for (Long userId : participantIds)
                {
                    String player = "P" + userId; // users.getOne(userId).getName();
                    players.add(player);
                    Map<String, List<Long>> userRanking = persistentDB.getRanking(gameId, userId);

                    if (userRanking != null)
                    {
                        Map<String, List<String>> userRankingStr = new HashMap<>();

                        for (Entry<String, List<Long>> entry : userRanking.entrySet())
                        {
                            userRankingStr.put(entry.getKey(), idToString(entry.getValue()));
                        }

                        algo.addRanking(player, userRankingStr);
                    }
                }

                algo.setDefaultPlayerWeights(gameCriteria, players);

                List<Map<String, Double>> prioritizations = null;

                try
                {
                    prioritizations = algo.calc().subList(0, 3);
                }
                catch (Exception ex)
                {
                    System.err.println("Unable to compute prioritizations");
                    ex.printStackTrace();
                    prioritizations = new ArrayList<>();
                    Map<String, Double> m = new HashMap<>();
                    m.put("1", 0.2);
                    m.put("2", 0.21);
                    m.put("3", 0.15);
                    m.put("4", 0.01);
                    prioritizations.add(m);
                    m = new HashMap<>();
                    m.put("1", 0.21);
                    m.put("2", 0.2);
                    m.put("3", 0.15);
                    m.put("4", 0.19);
                    prioritizations.add(m);
                }

                b.append("<h2>Found: " + prioritizations.size() + " optimal solutions</h2>");

                b.append("<div id='jqxTabs'>\n");
                b.append("<ul style='margin-left: 20px;'>\n");

                for (int i = 0; i < prioritizations.size(); i++)
                {
                    b.append("<li>Solution " + i + "</li>\n");
                }

                b.append("</ul>\n");

                for (int i = 0; i < prioritizations.size(); i++)
                {
                    b.append("<div>\n");
                    b.append("<table width=0'100%'>\n");
                    Map<String, Double> map = prioritizations.get(i);

                    for (String c : map.keySet())
                    {
                        b.append("<tr>\n");
                        b.append("<td>'" + c + "'</td><td>" + map.get(c) + "</td>");
                        b.append("</tr>\n");
                    }

                    b.append("</table>\n");
                    b.append("</div>\n");
                }

                b.append("</div>\n");

                return b.toString().getBytes();
            }
        });
    }

    private static List<String> idToString(List<Long> ids)
    {
        List<String> strings = new ArrayList<>();

        for (Long id : ids)
        {
            strings.add(requirements.get(id));
        }

        return strings;
    }

    @Autowired
    private ResourceLoader resourceLoader;

    @RequestMapping("/**")
    public byte[] getResource(final HttpServletRequest request)
    {
        String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        String bestMatchPattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);

        AntPathMatcher apm = new AntPathMatcher();
        String finalPath = apm.extractPathWithinPattern(bestMatchPattern, path);

        String URI = request.getRequestURI();
        String[] tokens = URI.split("/");
        String pageName = tokens[tokens.length - 1];

        if (pageName.startsWith("results") && pageName.endsWith("html"))
        {
            finalPath = finalPath.replaceAll(pageName, "results") + ".html";
        }

        ResourceProvider rp = providers.get(finalPath);

        // FIXME A temporary workaround to load requirement names from database (instead of displaying requirement IDs
        // to the user, which is not meaningful)
        for (Requirement req : availableRequirements.findAll())
        {
            requirements.put(req.getRequirementId(), req.getName());
        }

        if (rp != null)
        {
            System.out.println("Serving page by provider: " + finalPath);
            String id = pageName.replaceAll("results", "").replaceAll(".html", "");
            return rp.getResource(request, id);
        }
        else
        {
            System.out.println("Serving page " + finalPath);

            Resource resource = resourceLoader.getResource("classpath:static/garp/" + finalPath);

            try
            {
                InputStream is = resource.getInputStream();

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                try
                {
                    int read = is.read();

                    while (read != -1)
                    {
                        byteArrayOutputStream.write(read);
                        read = is.read();
                    }
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }

                byte[] bytes = byteArrayOutputStream.toByteArray();
                return bytes;
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            // Something went wrong
            return ("Failed to load resource '" + finalPath).getBytes();
        }
    }
}