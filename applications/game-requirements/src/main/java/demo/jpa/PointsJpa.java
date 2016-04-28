package demo.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import demo.model.Point;

public interface PointsJpa extends JpaRepository<Point, Long>  {

}
