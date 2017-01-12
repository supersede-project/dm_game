package eu.supersede.gr.jpa;
//package eu.supersede.dm.ga.jpa;
//
//import java.util.List;
//
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//
//import eu.supersede.dm.ga.db.HGAGameParticipation;
//
//public interface GAGameParticipationJpa extends JpaRepository<HGAGameParticipation, Long> {
//	
//	@Query("SELECT userId FROM ga_game_participations WHERE gameId = ?1")
//	List<Long> findParticipants( Long gameId );
//	
//	@Query("SELECT * FROM ga_game_participations WHERE userId = ?1 AND role = ?2")
//	List<HGAGameParticipation> findGames( Long userId, String roleName );
//	
//}
