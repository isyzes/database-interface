package com.space.repository;

import com.space.model.Ship;
import com.space.model.ShipType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;



public interface ShipRepository extends JpaRepository<Ship, Long> {
    List<Ship> findByNameContaining(String name);
    List<Ship> findByPlanetContaining(String planet);
    List<Ship> findByShipType(ShipType shipType);
    List<Ship> findByProdDateBetween(Date prodDate, Date prodDate2);
    List<Ship> findByProdDate(Date prodDate);
    List<Ship> findByIsUsed(Boolean isUsed);
    List<Ship> findBySpeedBetween(Double minSpeed, Double maxSpeed);
    List<Ship> findBySpeed(Double speed);
    List<Ship> findByCrewSizeBetween(Integer minCrewSize, Integer maxCrewSize);
    List<Ship> findByCrewSize(Integer crewSize);

    List<Ship> findByRatingBetween(Double minRating, Double maxRating);

    Ship findById(long id);
}
