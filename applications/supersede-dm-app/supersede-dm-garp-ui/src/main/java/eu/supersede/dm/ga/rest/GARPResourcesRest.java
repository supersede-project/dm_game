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

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerMapping;

import eu.supersede.dm.iga.IGAAlgorithm;

/*
 * If static resources do not need to be secured, this method is not necessary.
 * Otherwise, getResource can filter resource access on the basis of user role
 */

@RestController
@RequestMapping("/garp")
public class GARPResourcesRest
{

    interface ResourceProvider
    {
        public byte[] getResource(final HttpServletRequest request, String id);
    }

    static Map<String, ResourceProvider> providers = new HashMap<>();

    static
    {
        providers.put("results.html", new ResourceProvider()
        {
            @Override
            public byte[] getResource(HttpServletRequest request, String id)
            {
                long gameId = new Long(id);
                // long gameId = 1482177818095L;

                StringBuilder b = new StringBuilder();

                b.append("<script src=\"supersede-dm-app/garp/js/results.js\"></script>");
                b.append("<style>");
                b.append("h1 { text-align: center; }\n");
                b.append("#sortable { margin: 0 auto; padding: 5px; width: 520px; }\n");
                b.append("#sortable > div { border-radius: 5px; margin: 5px; }\n");
                b.append("#sortable div { padding: 5px; }\n");
                b.append("#sortable div:hover { border: 1px solid #356AA0; }\n");
                b.append("</style>\n");

                {
                    // IGAAlgorithm algo = new IGAAlgorithm();
                    // algo.setCriteria(GAVirtualDB.get().getCriteria(gameId));
                    //
                    // for (Long rid : GAVirtualDB.get().getRequirements( gameId ))
                    // {
                    // algo.addRequirement("" + rid, new ArrayList<>());
                    // b.append("Req: " + rid + "\n");
                    // }
                    List<Map<String, Double>> prioritizations = null;

                    IGAAlgorithm igaAlgorithm = new IGAAlgorithm();

                    // set criteria
                    List<String> criteria = new ArrayList<>();
                    String c1 = "c1";
                    String c2 = "c2";
                    criteria.add(c1);
                    criteria.add(c2);
                    igaAlgorithm.setCriteria(criteria);

                    // set criteria weight
                    igaAlgorithm.setCriteriaWeight(c1, 1.0);
                    igaAlgorithm.setCriteriaWeight(c2, 1.0);

                    // set requirements
                    String r1 = "R1";
                    List<String> r1Deps = new ArrayList<>();

                    String r2 = "R2";
                    String r3 = "R3";

                    List<String> r2Deps = new ArrayList<>();
                    r2Deps.add(r1);
                    List<String> r3Deps = new ArrayList<>();
                    r3Deps.add(r1);
                    igaAlgorithm.addRequirement(r1, r1Deps);
                    igaAlgorithm.addRequirement(r2, r2Deps);
                    igaAlgorithm.addRequirement(r3, r3Deps);

                    // set player rankings
                    String p1 = "P1";
                    Map<String, List<String>> rankP1 = new HashMap<>();
                    List<String> ranksc1 = new ArrayList<>();
                    ranksc1.add(r1);
                    ranksc1.add(r2);
                    ranksc1.add(r3);
                    List<String> ranksc2 = new ArrayList<>();
                    ranksc2.add(r1);
                    ranksc2.add(r2);
                    ranksc2.add(r3);
                    rankP1.put(c1, ranksc1);
                    rankP1.put(c2, ranksc2);
                    igaAlgorithm.addRanking(p1, rankP1);

                    String p2 = "P2";
                    Map<String, List<String>> rankP2 = new HashMap<>();
                    List<String> ranksp2c1 = new ArrayList<>();
                    ranksp2c1.add(r1);
                    ranksp2c1.add(r2);
                    ranksp2c1.add(r3);
                    List<String> ranksp2c2 = new ArrayList<>();
                    ranksp2c2.add(r1);
                    ranksp2c2.add(r2);
                    ranksp2c2.add(r3);
                    rankP2.put(c1, ranksp2c1);
                    rankP2.put(c2, ranksp2c2);
                    igaAlgorithm.addRanking(p2, rankP2);

                    // set player weights
                    Map<String, Double> weightsC1 = new HashMap<>();
                    weightsC1.put(p1, 1.0);
                    weightsC1.put(p2, 1.0);
                    igaAlgorithm.addPlayerRanking(c1, weightsC1);

                    Map<String, Double> weightsC2 = new HashMap<>();
                    weightsC2.put(p1, 1.0);
                    weightsC2.put(p2, 1.0);
                    igaAlgorithm.addPlayerRanking(c2, weightsC2);

                    try
                    {
                        prioritizations = igaAlgorithm.calc().subList(0, 3); // take only the first 3 results
                        // prioritizations = algo.calc();
                    }
                    catch (Exception ex)
                    {
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
                            b.append("<td>Req. '" + c + "'</td><td>" + map.get(c) + "</td>");
                            b.append("</tr>\n");
                        }
                        b.append("</table>\n");
                        b.append("</div>\n");
                    }
                    b.append("</div>\n");
                }

                System.out.println(b.toString());

                return b.toString().getBytes();
            }
        });
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
                System.out.println(resource.getFile().getAbsolutePath());
            }
            catch (IOException e1)
            {
                e1.printStackTrace();
            }
            // Resource resource = resourceLoader.getResource("classpath:static/game.html");

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
