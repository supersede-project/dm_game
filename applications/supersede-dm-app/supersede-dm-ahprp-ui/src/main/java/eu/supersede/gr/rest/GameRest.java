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

package eu.supersede.gr.rest;

import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import eu.supersede.dm.datamodel.Feature;
import eu.supersede.dm.datamodel.FeatureList;
import eu.supersede.dm.services.EnactmentService;
import eu.supersede.fe.exception.NotFoundException;
import eu.supersede.fe.integration.ProxyWrapper;
import eu.supersede.fe.mail.SupersedeMailSender;
import eu.supersede.fe.notification.NotificationUtil;
import eu.supersede.fe.security.DatabaseUser;
import eu.supersede.gr.jpa.AHPCriteriasMatricesDataJpa;
import eu.supersede.gr.jpa.AHPGamesJpa;
import eu.supersede.gr.jpa.AHPJudgeActsJpa;
import eu.supersede.gr.jpa.AHPPlayerMovesJpa;
import eu.supersede.gr.jpa.AHPRequirementsMatricesDataJpa;
import eu.supersede.gr.jpa.GamesPlayersPointsJpa;
import eu.supersede.gr.jpa.RequirementsJpa;
import eu.supersede.gr.jpa.UsersJpa;
import eu.supersede.gr.jpa.ValutationCriteriaJpa;
import eu.supersede.gr.model.HAHPCriteriasMatrixData;
import eu.supersede.gr.model.HAHPGame;
import eu.supersede.gr.model.GamePlayerPoint;
import eu.supersede.gr.model.HAHPJudgeAct;
import eu.supersede.gr.model.HAHPPlayerMove;
import eu.supersede.gr.model.HAHPRequirementsMatrixData;
import eu.supersede.gr.model.Requirement;
import eu.supersede.gr.model.User;
import eu.supersede.gr.model.ValutationCriteria;
import eu.supersede.gr.utility.PointsLogic;
import eu.supersede.integration.api.datastore.fe.types.Profile;

@RestController
@RequestMapping("/ahprp/game")
public class GameRest
{
    private static final String NEW_LINE = System.getProperty("line.separator");
    private static final String SEPARATOR = ";";
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
    private static final String ZERO_TIME = dateFormat.format(new Date(0));

    @Autowired
    private SupersedeMailSender supersedeMailSender;

    @Autowired
    private GamesPlayersPointsJpa gamesPlayersPoints;

    @Autowired
    private PointsLogic pointsLogic;

    @Autowired
    private AHPGamesJpa games;

    @Autowired
    private UsersJpa users;

    @Autowired
    private RequirementsJpa requirements;

    @Autowired
    private ValutationCriteriaJpa criterias;

    @Autowired
    private AHPCriteriasMatricesDataJpa criteriasMatricesData;

    @Autowired
    private AHPRequirementsMatricesDataJpa requirementsMatricesData;

    @Autowired
    private AHPPlayerMovesJpa playerMoves;

    @Autowired
    private AHPJudgeActsJpa judgeActs;

    @Autowired
    private ProxyWrapper proxy;

    @Autowired
    private NotificationUtil notificationUtil;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public List<HAHPGame> getGames(Authentication authentication, @RequestParam(required = false) Boolean finished,
            @RequestParam(defaultValue = "false") Boolean byUser)
    {
        DatabaseUser currentUser = (DatabaseUser) authentication.getPrincipal();
        User user = users.findOne(currentUser.getUserId());
        List<HAHPGame> gs;

        if (!byUser && userIsGameMaster(currentUser))
        {
            if (finished == null)
            {
                gs = games.findAll();
            }
            else
            {
                gs = games.findByFinished(finished);
            }
        }
        else
        {
            if (finished == null)
            {
                gs = games.findByPlayerContains(user);
            }
            else
            {
                gs = games.findByPlayerContainsAndFinished(user, finished);
            }

            for (HAHPGame g : gs)
            {
                g.setCurrentPlayer(user);
            }
        }

        return gs;
    }

    private boolean userIsGameMaster(DatabaseUser currentUser)
    {
        // TODO: fix for integration

        eu.supersede.integration.api.datastore.fe.types.User proxyUser = proxy.getFEDataStoreProxy()
                .getUser(currentUser.getTenantId(), currentUser.getUserId().intValue(), false, currentUser.getToken());

        if (proxyUser == null)
        {
            throw new NotFoundException();
        }

        for (Profile p : proxyUser.getProfiles())
        {
            if (p.getName().equals("DECISION_SCOPE_PROVIDER"))
            {
                return true;
            }
        }

        return false;
    }

    @RequestMapping(value = "/{gameId}", method = RequestMethod.GET)
    public HAHPGame getGame(Authentication authentication, @PathVariable Long gameId)
    {
        DatabaseUser currentUser = (DatabaseUser) authentication.getPrincipal();
        User user = users.findOne(currentUser.getUserId());
        HAHPGame g = games.findOne(gameId);

        if (g == null)
        {
            throw new NotFoundException();
        }

        g.setCurrentPlayer(user);

        return g;
    }

    @RequestMapping("/{gameId}/exportGameData")
    public ResponseEntity<?> exportGameData(@PathVariable Long gameId)
    {
        HAHPGame g = games.findOne(gameId);

        if (g == null)
        {
            throw new NotFoundException();
        }

        StringBuilder result = new StringBuilder();

        result.append("REQUIREMENT1").append(SEPARATOR).append("REQUIREMENT2").append(SEPARATOR).append("CRITERIA")
                .append(SEPARATOR).append("USER").append(SEPARATOR).append("VOTE").append(SEPARATOR).append("VOTE_TIME")
                .append(NEW_LINE);

        for (HAHPRequirementsMatrixData rmd : g.getRequirementsMatrixData())
        {
            for (HAHPPlayerMove pm : rmd.getPlayerMoves())
            {
                if (pm.getPlayed())
                {
                    result.append(rmd.getRowRequirement().getName()).append(SEPARATOR)
                            .append(rmd.getColumnRequirement().getName()).append(SEPARATOR)
                            .append(rmd.getCriteria().getName()).append(SEPARATOR).append(pm.getPlayer().getName())
                            .append(SEPARATOR).append(pm.getValue()).append(SEPARATOR);

                    if (pm.getPlayedTime() == null)
                    {
                        result.append(ZERO_TIME);
                    }
                    else
                    {
                        result.append(dateFormat.format(pm.getPlayedTime()));
                    }

                    result.append(NEW_LINE);
                }
            }
        }

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(new MediaType("text", "csv", Charset.forName("utf-8")));
        httpHeaders.setContentDispositionFormData("attachment", "dmp_" + g.getGameId() + "_data.csv");

        return new ResponseEntity<>(result.toString(), httpHeaders, HttpStatus.OK);
    }

    @RequestMapping("/{gameId}/exportGameResults")
    public ResponseEntity<?> exportGameResults(@PathVariable Long gameId)
    {
        HAHPGame g = games.findOne(gameId);

        if (g == null)
        {
            throw new NotFoundException();
        }

        StringBuilder result = new StringBuilder();

        result.append("REQUIREMENT").append(SEPARATOR).append("RESULT").append(NEW_LINE);
        List<HAHPCriteriasMatrixData> criteriasMatrixDataList = criteriasMatricesData.findByGame(g);
        Map<String, Double> rs = AHPRest.CalculateAHP(g.getCriterias(), g.getRequirements(), criteriasMatrixDataList,
                g.getRequirementsMatrixData());

        for (Entry<String, Double> e : rs.entrySet())
        {
            result.append(requirements.getOne(new Long(e.getKey())).getName()).append(SEPARATOR).append(e.getValue())
                    .append(NEW_LINE);
        }

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(new MediaType("text", "csv", Charset.forName("utf-8")));
        httpHeaders.setContentDispositionFormData("attachment", "dmp_" + g.getGameId() + "_results.csv");

        return new ResponseEntity<>(result.toString(), httpHeaders, HttpStatus.OK);
    }

    @RequestMapping(value = "/end/{gameId}", method = RequestMethod.PUT)
    public void setGameFinished(@PathVariable Long gameId)
    {
        HAHPGame g = games.findOne(gameId);
        g.setFinished(true);
        games.save(g);

        // set all judgeActs voted and playerMoves played
        List<HAHPRequirementsMatrixData> rmdList = g.getRequirementsMatrixData();

        for (int i = 0; i < rmdList.size(); i++)
        {
            HAHPRequirementsMatrixData rmd = rmdList.get(i);
            List<HAHPJudgeAct> jaList = judgeActs.findByRequirementsMatrixData(rmd);
            List<HAHPPlayerMove> pmList = playerMoves.findByRequirementsMatrixData(rmd);

            for (int j = 0; j < jaList.size(); j++)
            {
                jaList.get(j).setVoted(true);
                judgeActs.save(jaList.get(j));
            }

            for (int k = 0; k < pmList.size(); k++)
            {
                pmList.get(k).setPlayed(true);
                playerMoves.save(pmList.get(k));
            }
        }
    }

    @RequestMapping(value = "/enact/{gameId}", method = RequestMethod.PUT)
    public void doEnactGame(@PathVariable Long gameId, @RequestParam("useIF") Boolean useIf)
    {
        System.out.println("Sending requirements fo enactment - useIF = " + useIf.booleanValue());

        HAHPGame g = games.findOne(gameId);
        double max = 1;
        Map<String, Double> rs = AHPRest.CalculateAHP(g.getCriterias(), g.getRequirements(),
                criteriasMatricesData.findByGame(g), g.getRequirementsMatrixData());

        for (Double d : rs.values())
        {
            if (d > max)
            {
                max = d;
            }
        }

        FeatureList list = new FeatureList();

        for (Requirement r : g.getRequirements())
        {
            Feature feature = new Feature();
            feature.setName(r.getName());
            feature.setPriority((int) (1 + ((rs.get("" + r.getRequirementId()) / max) * 5)));
            feature.setId("" + r.getRequirementId());
            System.out.println("Added feature with id: " + feature.getId());
            list.list().add(feature);
        }

        EnactmentService.get().send(list, (useIf != null ? useIf.booleanValue() : true));
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<?> createGame(Authentication auth, @RequestBody HAHPGame game,
            @RequestParam(required = true) String criteriaValues)
            throws JsonParseException, JsonMappingException, IOException
    {
        TypeReference<HashMap<String, Object>> typeRef = new TypeReference<HashMap<String, Object>>()
        {
        };

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Map<String, Integer>> cvs = mapper.readValue(criteriaValues, typeRef);
        game.setStartTime(new Date());
        // re-attach detached requirements
        List<Requirement> rs = game.getRequirements();

        for (int i = 0; i < rs.size(); i++)
        {
            rs.set(i, requirements.findOne(rs.get(i).getRequirementId()));
        }
        // re-attach detached users
        List<User> us = game.getPlayers();

        for (int i = 0; i < us.size(); i++)
        {
            us.set(i, users.findOne(us.get(i).getUserId()));
        }
        // re-attach detached criterias
        List<ValutationCriteria> cs = game.getCriterias();

        for (int i = 0; i < cs.size(); i++)
        {
            cs.set(i, criterias.findOne(cs.get(i).getCriteriaId()));
        }

        Object user = auth.getPrincipal();

        if (user instanceof DatabaseUser)
        {
            DatabaseUser dbUser = (DatabaseUser) user;
            User u = users.getOne(dbUser.getUserId());
            game.setCreator(u);

            // add points for the creation of a game
            pointsLogic.addPoint(u, -3l, -1l);
        }

        game.setFinished(false);
        game = games.save(game);

        for (int i = 0; i < cs.size() - 1; i++)
        {
            for (int j = i + 1; j < cs.size(); j++)
            {
            	HAHPCriteriasMatrixData cmd = new HAHPCriteriasMatrixData();
                cmd.setGame(game);
                cmd.setRowCriteria(cs.get(j));
                cmd.setColumnCriteria(cs.get(i));
                String c1Id = cs.get(j).getCriteriaId().toString();
                String c2Id = cs.get(i).getCriteriaId().toString();

                if (cvs.containsKey(c1Id) && cvs.get(c1Id).containsKey(c2Id))
                {
                    cmd.setValue(new Long(cvs.get(c1Id).get(c2Id)));
                }
                else
                {
                    cmd.setValue(new Long(cvs.get(c2Id).get(c1Id)));
                }

                criteriasMatricesData.save(cmd);
            }
        }

        for (int c = 0; c < cs.size(); c++)
        {
            for (int i = 0; i < rs.size() - 1; i++)
            {
                for (int j = i + 1; j < rs.size(); j++)
                {
                    HAHPRequirementsMatrixData rmd = new HAHPRequirementsMatrixData();
                    rmd.setGame(game);
                    rmd.setRowRequirement(rs.get(j));
                    rmd.setColumnRequirement(rs.get(i));
                    rmd.setCriteria(cs.get(c));
                    rmd.setValue(-1l);
                    requirementsMatricesData.save(rmd);

                    for (int p = 0; p < us.size(); p++)
                    {
                        HAHPPlayerMove pm = new HAHPPlayerMove();
                        pm.setPlayer(us.get(p));
                        pm.setRequirementsMatrixData(rmd);
                        pm.setPlayed(false);
                        playerMoves.save(pm);
                    }

                    HAHPJudgeAct ja = new HAHPJudgeAct();
                    ja.setVoted(false);
                    ja.setRequirementsMatrixData(rmd);
                    judgeActs.save(ja);
                }
            }
        }

        DatabaseUser currentUser = (DatabaseUser) auth.getPrincipal();

        for (User u : us)
        {
            eu.supersede.integration.api.datastore.fe.types.User proxyUser = proxy.getFEDataStoreProxy()
                    .getUser(currentUser.getTenantId(), u.getUserId().intValue(), true, currentUser.getToken());
            // creation of email for the players when a game is started
            supersedeMailSender.sendEmail("New Decision Making Process",
                    "Hi " + proxyUser.getFirst_name() + " " + proxyUser.getLast_name()
                            + ", this is an automatically generated mail. You have just been invited to "
                            + "participate in a prioritization process. To access the propritization process, "
                            + "connect to the URL 213.21.147.91:8081 and log in with your userid and password. "
                            + "Then click on Decision Making Process; then on Opinion Provider Actions and "
                            + "finally click Enter on the displayed process.",
                    proxyUser.getEmail());

            notificationUtil.createNotificationForUser(proxyUser.getEmail(),
                    "A new decision making process has been created, are you ready to vote?",
                    "supersede-dm-app/ahprp/player_games");

            // create a GamePlayerPoint for this game and for all the players in the game
            GamePlayerPoint gpp = new GamePlayerPoint();
            gpp.setGame(game);
            gpp.setUser(u);
            gpp.setPoints(0l);
            gamesPlayersPoints.save(gpp);
        }

        notificationUtil.createNotificationsForProfile("OPINION_NEGOTIATOR",
                "A new decision making process has been created, you are in charge to take decisions",
                "supersede-dm-app/ahprp/judge_games");

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(game.getGameId()).toUri());
        return new ResponseEntity<>(game.getGameId(), httpHeaders, HttpStatus.CREATED);
    }
}