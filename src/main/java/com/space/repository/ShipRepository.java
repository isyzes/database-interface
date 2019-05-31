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
    List<Ship> findByIsUsed(Boolean isUsed);

    List<Ship> findByProdDateBetween(Date prodDate, Date prodDate2);
    List<Ship> findByProdDate(Date prodDate);
    List<Ship> findByProdDateGreaterThanEqual(Date prodDate); //>=
    List<Ship> findByProdDateLessThanEqual(Date prodDate); // <=

    List<Ship> findBySpeedBetween(Double minSpeed, Double maxSpeed);
    List<Ship> findBySpeed(Double speed);
    List<Ship> findBySpeedGreaterThanEqual(Double speed); //>=
    List<Ship> findBySpeedLessThanEqual(Double speed); // <=

    List<Ship> findByCrewSizeBetween(Integer minCrewSize, Integer maxCrewSize);
    List<Ship> findByCrewSize(Integer crewSize);
    List<Ship> findByCrewSizeGreaterThanEqual(Integer crewSize); //>=
    List<Ship> findByCrewSizeLessThanEqual(Integer crewSize); // <=

    List<Ship> findByRatingBetween(Double minRating, Double maxRating);
    List<Ship> findByRating(Double rating);
    List<Ship> findByRatingGreaterThanEqual(Double rating); //>=
    List<Ship> findByRatingLessThanEqual(Double rating); // <=

    Ship findById(long id);
}
