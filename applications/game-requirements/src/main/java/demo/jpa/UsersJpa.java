package demo.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import demo.model.User;

public interface UsersJpa extends JpaRepository<User, Long> {
	
	User findByEmail(String email);
	User findByUserId(Long id);
}
