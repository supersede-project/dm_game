package demo.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import demo.model.CriteriasMatrixData;
import demo.model.Game;

public interface CriteriasMatricesDataJpa extends JpaRepository<CriteriasMatrixData, Long>{

	List<CriteriasMatrixData> findByGame(Game g);

}
