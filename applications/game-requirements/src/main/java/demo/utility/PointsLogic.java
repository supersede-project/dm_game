package demo.utility;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import demo.jpa.PointsJpa;
import demo.jpa.UserCriteriaPointsJpa;
import demo.jpa.UserPointsJpa;
import demo.jpa.ValutationCriteriaJpa;
import demo.model.Point;
import demo.model.User;
import demo.model.UserCriteriaPoint;
import demo.model.UserPoint;
import demo.model.ValutationCriteria;

@Component
public class PointsLogic {
	
	@Autowired
    private UserPointsJpa userPoints;
	
	@Autowired
    private PointsJpa points;
	
	@Autowired
    private ValutationCriteriaJpa criterias;
	
	@Autowired
    private UserCriteriaPointsJpa userCriteriaPoints;
	
	public void addPoint(User user,  Long pointId,  Long criteriaId){	
		
		Point point = points.findOne(pointId);
				
		if(criteriaId != -1){
		
			ValutationCriteria criteria = criterias.findOne(criteriaId);
			UserCriteriaPoint ucp = userCriteriaPoints.findByValutationCriteriaAndUser(criteria, user);
			
			if(ucp == null){
				UserCriteriaPoint newUcp = new UserCriteriaPoint();
				
				newUcp.setUser(user);
				newUcp.setValutationCriteria(criteria);
				newUcp.setPoints(point.getCriteriaPoints());
				userCriteriaPoints.save(newUcp);
			}else{
				ucp.setPoints(ucp.getPoints() + point.getCriteriaPoints());
				userCriteriaPoints.save(ucp);
			}
		}
		
		UserPoint up = userPoints.findByUser(user);
		
		if(up == null){
			UserPoint newUp = new UserPoint();
			
			newUp.setUser(user);
			newUp.setUserPoints(point.getGlobalPoints());
			userPoints.save(newUp);
		}else{
			up.setUserPoints(up.getUserPoints() + point.getGlobalPoints());
			userPoints.save(up);
		}
	}
}