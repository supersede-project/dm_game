package eu.supersede.gr.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="h_roles")
public class HGAGameParticipation {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	Long id;

	@Column(name="gameId")
	public Long	gameId;

	@Column(name="userId")
	public Long	userId;

	@Column(name="role")
	public String	role;

}
