package demo.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import demo.model.Profile;

public interface ProfilesJpa extends JpaRepository<Profile, Long> {
	
	Profile findByName(String name);
	
}
