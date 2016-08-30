package demo.rest;

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

import demo.jpa.CriteriasMatricesDataJpa;
import demo.jpa.GamesJpa;
import demo.jpa.GamesPlayersPointsJpa;
import demo.jpa.JudgeActsJpa;
import demo.jpa.PlayerMovesJpa;
import demo.jpa.ProfilesJpa;
import demo.jpa.RequirementsJpa;
import demo.jpa.RequirementsMatricesDataJpa;
import demo.jpa.UsersJpa;
import demo.jpa.ValutationCriteriaJpa;
import demo.model.CriteriasMatrixData;
import demo.model.Game;
import demo.model.GamePlayerPoint;
import demo.model.JudgeAct;
import demo.model.PlayerMove;
import demo.model.Requirement;
import demo.model.RequirementsMatrixData;
import demo.model.User;
import demo.model.ValutationCriteria;
import demo.utility.PointsLogic;
import eu.supersede.fe.exception.NotFoundException;
import eu.supersede.fe.mail.SupersedeMailSender;
import eu.supersede.fe.notification.NotificationUtil;
import eu.supersede.fe.security.DatabaseUser;

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
    private ProfilesJpa profiles;
	@Autowired
    private RequirementsJpa requirements;
	@Autowired
    private UsersJpa users;
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
	private NotificationUtil notificationUtil;
	
	
	@RequestMapping(value = "", method = RequestMethod.GET)
	public List<Game> getGames(Authentication authentication, 
			@RequestParam(required=false) Boolean finished,
			@RequestParam(defaultValue="false") Boolean byUser){
	
		DatabaseUser currentUser = (DatabaseUser) authentication.getPrincipal();
		User user = users.findOne(currentUser.getUserId());
		
		if(!byUser && userIsGameMaster(user))
		{
			if(finished == null)
			{
				return games.findAll();
			}
			else
			{
				return games.findByFinished(finished);
			}
		}
		else
		{	
			List<Game> gs;
			if(finished == null)
			{
				 gs = games.findByPlayerContains(user);
			}
			else
			{
				gs =  games.findByPlayerContainsAndFinished(user, finished);
			}
			
			for(Game g : gs)
			{
				g.setCurrentPlayer(user);
			}
			return gs;
		}
		
	}
	
	private boolean userIsGameMaster(User user)
	{
		return user.getProfiles().contains(profiles.findByName("DECISION_SCOPE_PROVIDER_GAMIFICATION"));
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
		
		for(User u : us)
		{
			// creation of email for the players when a game is started
			supersedeMailSender.sendEmail("New Decision Making Process", 
							"Hi " + u.getName() + ", this is an automatically generated mail. You have just been invited to participate in a prioritization process. To access the propritization process, connect to the URL 213.21.147.91:8081 and log in with your userid and password. Then click on Decision Making Process; then on Opinion Provider Actions and finally click Enter on the displayed process.", u.getEmail());
			
			notificationUtil.createNotificationForUser(u.getEmail(), "A new decision making process has been created, are you ready to vote?", "game-requirements-gamification/player_games");
			
			// ######################################################
			// create a GamePlayerPoint for this game and for all the players in the game
			GamePlayerPoint gpp = new GamePlayerPoint();
			gpp.setGame(game);
			gpp.setUser(u);
			gpp.setPoints(0l);
			gamesPlayersPoints.save(gpp);
			
			// ######################################################
		}
		notificationUtil.createNotificationsForProfile("OPINION_NEGOTIATOR_GAMIFICATION", "A new decision making process has been created, you are in charge to take decisions", "game-requirements-gamification/judge_acts");
		
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setLocation(ServletUriComponentsBuilder
				.fromCurrentRequest().path("/{id}")
				.buildAndExpand(game.getGameId()).toUri());
		return new ResponseEntity<>(game.getGameId(), httpHeaders, HttpStatus.CREATED);
	}
}
