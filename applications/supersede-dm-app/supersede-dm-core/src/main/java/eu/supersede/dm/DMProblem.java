package eu.supersede.dm;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eu.supersede.gr.model.Requirement;

public class DMProblem {
	
	public static class Config {
		
		// Resources
		public List<DMMethod>				candidateTechniques = new ArrayList<>();
		
		// Input
		List<Requirement>					requirements = new ArrayList<>();
		
		// Context
		Set<DMUser>							candidateUsers = new HashSet<>();
		
		public int							max = 1;
		private Duration					duration;
		
		Map<String,DMOption>				options = new HashMap<>();
		
		Map<String,String>					constraints = new HashMap<>();
		
		public void add( DMMethod t ) {
			candidateTechniques.add( t );
		}
		
		public final List<DMMethod> getAvailableMethods() {
			return this.candidateTechniques;
		}
		
		public void setMaxStep( int n ) {
			this.max = n;
		}
		
		public void addUser( DMUser dmUser ) {
			candidateUsers.add( dmUser );
		}
		
		public void add(Requirement dmRequirement) {
			this.requirements.add( dmRequirement );
		}
		
		public void setDeadline( Duration d ) {
			this.duration = d;
		}
		
		public void addOption( DMOption opt ) {
			this.options.put( opt.name, opt );
		}
		
		public Collection<DMOption> options() {
			return options.values();
		}
		
		public DMOption getOption( String name ) {
			return options.get( name );
		}
		
		public void setConstraint( String key, String value ) {
			this.constraints.put( key, value );
		}
		
		public List<Requirement> getRequirements() {
			return this.requirements;
		}

	}
	
	
	private DMObjective objective;
	
	private Config config;
	
	
	public DMProblem( DMObjective obj, Config cfg ) {
		this.objective = obj;
		this.config = cfg;
	}
	
	public DMObjective getObjective() {
		return this.objective;
	}
	
	public List<DMMethod> methods() {
		return this.config.candidateTechniques;
	}
	
	public Collection<DMUser> users() {
		return config.candidateUsers;
	}

	public List<Requirement> getRequirements() {
		return this.config.requirements;
	}

	public Duration getDeadline() {
		return this.config.duration;
	}
	
	public void addOption( DMOption opt ) {
		this.config.addOption( opt );
	}
	
	public Collection<DMOption> options() {
		return this.config.options();
	}
	
	public DMOption getOption( String name ) {
		return this.config.getOption( name );
	}
	
	public final List<DMMethod> getAvailableMethods() {
		return this.config.getAvailableMethods();
	}

}
