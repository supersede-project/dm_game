package demo.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import demo.model.User;
import demo.model.UserPoint;

public interface UserPointsJpa extends JpaRepository<UserPoint, Long>{

	UserPoint findByUser(User user);

}
