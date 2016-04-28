package demo.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import demo.model.User;
import demo.model.UserCriteriaPoint;
import demo.model.ValutationCriteria;

public interface UserCriteriaPointsJpa extends JpaRepository<UserCriteriaPoint, Long> {

	@Query("SELECT DISTINCT user FROM UserCriteriaPoint ucp WHERE ucp.valutationCriteria = ?1")
	List<User> findUsersByValutationCriteria(ValutationCriteria valutationCriteria);

	UserCriteriaPoint findByValutationCriteriaAndUser(ValutationCriteria valutationCriteria, User user);
}
