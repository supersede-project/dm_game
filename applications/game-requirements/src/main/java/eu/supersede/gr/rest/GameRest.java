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

/**
* @author Andrea Sosi
**/

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

import eu.supersede.gr.jpa.CriteriasMatricesDataJpa;
import eu.supersede.gr.jpa.GamesJpa;
import eu.supersede.gr.jpa.GamesPlayersPointsJpa;
import eu.supersede.gr.jpa.JudgeActsJpa;
import eu.supersede.gr.jpa.PlayerMovesJpa;
import eu.supersede.gr.jpa.RequirementsJpa;
import eu.supersede.gr.jpa.RequirementsMatricesDataJpa;
import eu.supersede.gr.jpa.UsersJpa;
import eu.supersede.gr.jpa.ValutationCriteriaJpa;
import eu.supersede.gr.model.CriteriasMatrixData;
import eu.supersede.gr.model.Game;
import eu.supersede.gr.model.GamePlayerPoint;
import eu.supersede.gr.model.JudgeAct;
import eu.supersede.gr.model.PlayerMove;
import eu.supersede.gr.model.Requirement;
import eu.supersede.gr.model.RequirementsMatrixData;
import eu.supersede.gr.model.User;
import eu.supersede.gr.model.ValutationCriteria;
import eu.supersede.gr.utility.PointsLogic;
import eu.supersede.fe.exception.NotFoundException;
import eu.supersede.fe.integration.ProxyWrapper;
import eu.supersede.fe.mail.SupersedeMailSender;
import eu.supersede.fe.notification.NotificationUtil;
import eu.supersede.fe.security.DatabaseUser;
import eu.supersede.integration.api.datastore.fe.types.Profile;

@RestController
@RequestMapping("/game")
public class GameRest {

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
    private GamesJpa games;
	@Autowired
    private UsersJpa users;
	@Autowired
    private RequirementsJpa requirements;
	@Autowired
    private ValutationCriteriaJpa criterias;
	@Autowired
    private CriteriasMatricesDataJpa criteriasMatricesData;
	@Autowired
    private RequirementsMatricesDataJpa requirementsMatricesData;
	@Autowired
    private PlayerMovesJpa playerMoves;
	@Autowired
    private JudgeActsJpa judgeActs;
	
	@Autowired
	private ProxyWrapper proxy;
	
	@Autowired
	private NotificationUtil notificationUtil;
	
	
	@RequestMapping(value = "", method = RequestMethod.GET)
	public List<Game> getGames(Authentication authentication, 
			@RequestParam(required=false) Boolean finished,
			@RequestParam(defaultValue="false") Boolean byUser){
	
		DatabaseUser currentUser = (DatabaseUser) authentication.getPrincipal();
		User user = users.findOne(currentUser.getUserId());
		
		List<Game> gs;
		if(!byUser && userIsGameMaster(currentUser))
		{
			if(finished == null)
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
			
			if(finished == null)
			{
				gs = games.findByPlayerContains(user);
			}
			else
			{
				gs = games.findByPlayerContainsAndFinished(user, finished);
			}
			
			for(Game g : gs)
			{
				g.setCurrentPlayer(user);
			}
		}
		
		Map<Long, User> usersCache = new HashMap<>();
		for(Game g : gs)
		{
			if(usersCache.containsKey(g.getCreator().getUserId()))
			{
				g.getCreator().setName(usersCache.get(g.getCreator().getUserId()).getName());
				g.getCreator().setEmail(usersCache.get(g.getCreator().getUserId()).getEmail());
			}
			else
			{
				User u = g.getCreator();
				eu.supersede.integration.api.datastore.fe.types.User proxyUser = proxy.getFEDataStoreProxy().getUser(
						currentUser.getTenantId(), u.getUserId().intValue(), true, currentUser.getToken());
				u.setName(proxyUser.getFirst_name() + " " + proxyUser.getLast_name());
				u.setEmail(proxyUser.getEmail());
				
				usersCache.put(u.getUserId(), u);
			}
		}
		
		return gs;
	}
	
	private boolean userIsGameMaster(DatabaseUser currentUser)
	{
		//TODO: fix for integration

		eu.supersede.integration.api.datastore.fe.types.User proxyUser = 
				proxy.getFEDataStoreProxy().getUser(currentUser.getTenantId(), currentUser.getUserId().intValue(), false, currentUser.getToken());
		
		if(proxyUser == null)
		{
			throw new NotFoundException();
		}
		
		for(Profile p : proxyUser.getProfiles())
		{
			if(p.getName().equals("DECISION_SCOPE_PROVIDER"))
			{
				return true;
			}
		}
		return false;
	}
	
	@RequestMapping(value = "/{gameId}", method = RequestMethod.GET)
	public Game getGame(Authentication authentication, @PathVariable Long gameId)
	{
		DatabaseUser currentUser = (DatabaseUser) authentication.getPrincipal();
		User user = users.findOne(currentUser.getUserId());
		
		Game g = games.findOne(gameId);
		if(g == null)
		{
			throw new NotFoundException();
		}
		g.setCurrentPlayer(user);
		
		for(User u : g.getPlayers())
		{
			eu.supersede.integration.api.datastore.fe.types.User proxyUser = proxy.getFEDataStoreProxy().getUser(
					currentUser.getTenantId(), u.getUserId().intValue(), true, currentUser.getToken());
			u.setName(proxyUser.getFirst_name() + " " + proxyUser.getLast_name());
			u.setEmail(proxyUser.getEmail());
		}
		
		return g;
	}
	
	@RequestMapping("/{gameId}/exportGameData")
	public ResponseEntity<?> exportGameData(@PathVariable Long gameId)
	{
		Game g = games.findOne(gameId);
		if(g == null)
		{
			throw new NotFoundException();
		}
		StringBuilder result = new StringBuilder();
		
		result.append("REQUIREMENT1").append(SEPARATOR).
			append("REQUIREMENT2").append(SEPARATOR).
			append("CRITERIA").append(SEPARATOR).
			append("USER").append(SEPARATOR).
			append("VOTE").append(SEPARATOR).
			append("VOTE_TIME").append(NEW_LINE);
		
		for(RequirementsMatrixData rmd : g.getRequirementsMatrixData())
		{
			for(PlayerMove pm : rmd.getPlayerMoves())
			{
				if(pm.getPlayed())
				{
					result.append(rmd.getRowRequirement().getName()).append(SEPARATOR).
						append(rmd.getColumnRequirement().getName()).append(SEPARATOR).
						append(rmd.getCriteria().getName()).append(SEPARATOR).
						append(pm.getPlayer().getName()).append(SEPARATOR).
						append(pm.getValue()).append(SEPARATOR);
					if(pm.getPlayedTime() == null)
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
		Game g = games.findOne(gameId);
		if(g == null)
		{
			throw new NotFoundException();
		}
		StringBuilder result = new StringBuilder();
		
		result.append("REQUIREMENT").append(SEPARATOR).
		append("RESULT").append(NEW_LINE);
		
		List<CriteriasMatrixData> criteriasMatrixDataList = criteriasMatricesData.findByGame(g);
		
		Map<String, Double> rs = AHPRest.CalculateAHP(g.getCriterias(), g.getRequirements(), criteriasMatrixDataList, g.getRequirementsMatrixData());

		for(Entry<String, Double> e : rs.entrySet())
		{
			result.append(requirements.getOne(new Long(e.getKey())).getName()).append(SEPARATOR).
				append(e.getValue()).append(NEW_LINE);
		}
		
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(new MediaType("text", "csv", Charset.forName("utf-8")));
		httpHeaders.setContentDispositionFormData("attachment", "dmp_" + g.getGameId() + "_results.csv");
		
		return new ResponseEntity<>(result.toString(), httpHeaders, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/end/{gameId}", method = RequestMethod.PUT)
	public void setGameFinished(@PathVariable Long gameId)
	{
		Game g = games.findOne(gameId);
		g.setFinished(true);
		games.save(g);
		
		// set all judgeActs voted and playerMoves played
		List<RequirementsMatrixData> rmdList =  g.getRequirementsMatrixData();
		for(int i = 0; i < rmdList.size(); i++)
		{
			RequirementsMatrixData rmd = rmdList.get(i);
			List<JudgeAct> jaList = judgeActs.findByRequirementsMatrixData(rmd);
			List<PlayerMove> pmList = playerMoves.findByRequirementsMatrixData(rmd);
			
			for(int j = 0; j < jaList.size(); j++)
			{
				jaList.get(j).setVoted(true);
				judgeActs.save(jaList.get(j));
			}
			
			for(int k = 0; k < pmList.size(); k++)
			{
				pmList.get(k).setPlayed(true);
				playerMoves.save(pmList.get(k));
			}
		}
	}
	
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseEntity<?> createGame(Authentication auth, @RequestBody Game game,
			@RequestParam(required = true) String criteriaValues) throws JsonParseException, JsonMappingException, IOException
	{
		TypeReference<HashMap<String,Object>> typeRef = new TypeReference<HashMap<String,Object>>() {};
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Map<String, Integer>> cvs = mapper.readValue(criteriaValues, typeRef);
		game.setStartTime(new Date());
		//re-attach detached requirements
		List<Requirement> rs = game.getRequirements();
		for(int i = 0; i < rs.size(); i++)
		{
			rs.set(i, requirements.findOne(rs.get(i).getRequirementId()));
		}
		//re-attach detached users
		List<User> us = game.getPlayers();
		for(int i = 0; i < us.size(); i++)
		{
			us.set(i, users.findOne(us.get(i).getUserId()));
		}
		//re-attach detached criterias
		List<ValutationCriteria> cs = game.getCriterias();
		for(int i = 0; i < cs.size(); i++)
		{
			cs.set(i, criterias.findOne(cs.get(i).getCriteriaId()));
		}
	
		Object user = auth.getPrincipal();
		
		if(user instanceof DatabaseUser)
		{
			DatabaseUser dbUser = (DatabaseUser)user;
			User u = users.getOne(dbUser.getUserId());
			game.setCreator(u);
			
			// add points for the creation of a game
			pointsLogic.addPoint(u, -3l, -1l);
		}
		game.setFinished(false);
		game = games.save(game);
		
		for(int i = 0; i < cs.size() - 1; i++)
		{
			for(int j = i + 1; j < cs.size(); j++)
			{
				CriteriasMatrixData cmd = new CriteriasMatrixData();
				cmd.setGame(game);
				cmd.setRowCriteria(cs.get(j));
				cmd.setColumnCriteria(cs.get(i));
				String c1Id = cs.get(j).getCriteriaId().toString();
				String c2Id = cs.get(i).getCriteriaId().toString();
				
				if(cvs.containsKey(c1Id) && cvs.get(c1Id).containsKey(c2Id))
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
		
		for(int c = 0; c < cs.size(); c++)
		{
			for(int i = 0; i < rs.size() - 1; i++)
			{
				for(int j = i + 1; j < rs.size(); j++)
				{
					RequirementsMatrixData rmd = new RequirementsMatrixData();
					rmd.setGame(game);
					rmd.setRowRequirement(rs.get(j));
					rmd.setColumnRequirement(rs.get(i));
					rmd.setCriteria(cs.get(c));
					rmd.setValue(-1l);
					requirementsMatricesData.save(rmd);
					
					for(int p = 0; p < us.size(); p++)
					{
						PlayerMove pm = new PlayerMove();
						pm.setPlayer(us.get(p));
						pm.setRequirementsMatrixData(rmd);
						pm.setPlayed(false);
						playerMoves.save(pm);
					}
					
					JudgeAct ja = new JudgeAct();
					ja.setVoted(false);
					ja.setRequirementsMatrixData(rmd);
					judgeActs.save(ja);
				}
			}
		}
		
		DatabaseUser currentUser = (DatabaseUser) auth.getPrincipal();
		for(User u : us)
		{
			eu.supersede.integration.api.datastore.fe.types.User proxyUser = proxy.getFEDataStoreProxy().getUser(currentUser.getTenantId(), u.getUserId().intValue(), true, currentUser.getToken());
			// creation of email for the players when a game is started
			supersedeMailSender.sendEmail("New Decision Making Process", 
							"Hi " + proxyUser.getFirst_name() + " " + proxyUser.getLast_name() + ", this is an automatically generated mail. You have just been invited to participate in a prioritization process. To access the propritization process, connect to the URL 213.21.147.91:8081 and log in with your userid and password. Then click on Decision Making Process; then on Opinion Provider Actions and finally click Enter on the displayed process.", proxyUser.getEmail());
			
			notificationUtil.createNotificationForUser(proxyUser.getEmail(), "A new decision making process has been created, are you ready to vote?", "game-requirements/player_games");
			
			// ######################################################
			// create a GamePlayerPoint for this game and for all the players in the game
			GamePlayerPoint gpp = new GamePlayerPoint();
			gpp.setGame(game);
			gpp.setUser(u);
			gpp.setPoints(0l);
			gamesPlayersPoints.save(gpp);
			
			// ######################################################
		}
		notificationUtil.createNotificationsForProfile("OPINION_NEGOTIATOR", "A new decision making process has been created, you are in charge to take decisions", "game-requirements/judge_acts");
		
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setLocation(ServletUriComponentsBuilder
				.fromCurrentRequest().path("/{id}")
				.buildAndExpand(game.getGameId()).toUri());
		return new ResponseEntity<>(game.getGameId(), httpHeaders, HttpStatus.CREATED);
	}
}
