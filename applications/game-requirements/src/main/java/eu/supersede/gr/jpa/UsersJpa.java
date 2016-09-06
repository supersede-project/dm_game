package eu.supersede.gr.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import eu.supersede.gr.model.User;

public interface UsersJpa extends JpaRepository<User, Long> {

}
