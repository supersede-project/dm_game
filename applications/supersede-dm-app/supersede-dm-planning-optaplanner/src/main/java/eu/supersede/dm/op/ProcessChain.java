package eu.supersede.dm.op;

import java.util.ArrayList;
import java.util.List;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.drools.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.solution.drools.ProblemFactProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@PlanningSolution
@XStreamAlias("ProcessChain")
public class ProcessChain {
	
	List<String> availableRequirements = new ArrayList<>();
	
	private List<UserAssignment> users = new ArrayList<>();
	
	private List<ActivityExecution> activities = new ArrayList<>();
	private HardSoftScore score;

	private UserAssignment path = new UserAssignment();

	private String objective = "enact";

	private List<String> availableGamificationOptionPool = new ArrayList<>();
	
	
	public ProcessChain() {
		users.add( path );
	}
	
	@ProblemFactProperty
	public ProcessActivity getProcessPath() {
		return this.path;
	}
	
//	@ProblemFactProperty
	public UserAssignment getUser() {
		return path;
	}
	
	@ValueRangeProvider(id = "executionPool")
	@PlanningEntityCollectionProperty
    public List<ActivityExecution> getAvailableExecutions() {
        return activities;
    }
	
	@ValueRangeProvider(id = "userPool")
	@ProblemFactCollectionProperty
    public List<UserAssignment> getUsers() {
        return users;
    }
	
	public List<ActivityExecution> getActivities() {
		return activities;
	}

	public void setActivities(List<ActivityExecution> activities) {
		this.activities = activities;
	}
	
	@ValueRangeProvider(id = "requirementsPool")
	@PlanningEntityCollectionProperty
    public List<String> getAvailableRequirements() {
        return availableRequirements;
    }
	
//	@ValueRangeProvider(id = "gamificationOptionPool")
//	@PlanningEntityCollectionProperty
//    public List<String> getGamificationOptionPool() {
//        return availableGamificationOptionPool;
//    }
	
    @PlanningScore
    public HardSoftScore getScore() {
        return score;
    }

    public void setScore(HardSoftScore score) {
        this.score = score;
    }

	public void setObjective( String objective ) {
		this.objective = objective;
	}
	
	public String getObjective() {
		return this.objective;
	}

}
