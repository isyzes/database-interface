package com.space.service;


import com.space.controller.Utilities;
import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.repository.ShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Service
public class ShipService {
    public List<Ship> ships;

    @Autowired
    private ShipRepository shipRepository;

    public List<Ship> getShipsList(String name, String planet, ShipType shipType, Long after, Long before, Boolean isUsed,
            Double minSpeed, Double maxSpeed, Integer minCrewSize, Integer maxCrewSize, Double minRating, Double maxRating) {

        if (name == null && planet == null && shipType == null && after == null && before == null && isUsed == null
                && minSpeed == null && maxSpeed == null && minCrewSize == null && maxCrewSize == null
                && minRating == null && maxRating == null) {
            ships = shipRepository.findAll();
            return ships;
        }

        this.ships = new ArrayList<>();

        if (!StringUtils.isEmpty(name)) {
            if (this.ships.isEmpty())
                this.ships.addAll(shipRepository.findByNameContaining(name));
            else
                this.ships.removeIf(ship -> !ship.getName().contains(name));
        }

        if (!StringUtils.isEmpty(planet)) {
            if (this.ships.isEmpty())
                this.ships.addAll(shipRepository.findByPlanetContaining(planet));
            else
                this.ships.removeIf(ship -> !ship.getPlanet().contains(planet));

        }

        if (!StringUtils.isEmpty(shipType)) {
            if (this.ships.isEmpty())
                this.ships.addAll(shipRepository.findByShipType(shipType));
            else
                this.ships.removeIf(ship -> ship.getShipType() != shipType);
        }

        // можно переписать
        if (after != null && after > 0 &&  before == null) {
            if (this.ships.isEmpty()) {
                before = Utilities.MAX_PROD_DATE;
                addListDate(after, before);
            }
            else {
                Long finalAfter = after;
                this.ships.removeIf(ship -> ship.getProdDate().getTime() < finalAfter);
            }
        }

        if (before != null && before > 0 && after == null) {
            if (this.ships.isEmpty()) {
                after = Utilities.MIN_PROD_DATE;
                addListDate(after, before);
            } else {
                Long finalBefore = before;
                this.ships.removeIf(ship -> ship.getProdDate().getTime() > finalBefore);
            }
        }


        if (before != null && before > 0 && after != null && after > 0) {
            if (this.ships.isEmpty()) {
                addListDate(after, before);
            } else {
                Long finalBefore = before;
                Long finalAfter = after;
                this.ships.removeIf(ship -> ship.getProdDate().getTime() > finalBefore || ship.getProdDate().getTime() < finalAfter);
            }
        }

        if (isUsed != null) {
            if (this.ships.isEmpty()) {
                this.ships.addAll(shipRepository.findByIsUsed(isUsed));
            } else {
                this.ships.removeIf(ship -> ship.getUsed() != isUsed);
            }
        }

        if (maxSpeed != null && minSpeed != null && maxSpeed > 0 && minSpeed > 0) {
            if (this.ships.isEmpty()) {
                addListSpeed(minSpeed, maxSpeed);
            } else {
                Double finalMaxSpeed = maxSpeed;
                Double finalMinSpeed = minSpeed;
                this.ships.removeIf(ship -> ship.getSpeed() > finalMaxSpeed || ship.getSpeed() < finalMinSpeed);
            }
        }

        if (maxSpeed != null && minSpeed == null && maxSpeed > 0) {
           if (this.ships.isEmpty()) {
               minSpeed = Utilities.MIN_SPEED;
               addListSpeed(minSpeed, maxSpeed);
           } else {
               Double finalMaxSpeed = maxSpeed;
               this.ships.removeIf(ship -> ship.getSpeed() > finalMaxSpeed);
           }
        }

        if (minSpeed != null && maxSpeed == null && minSpeed > 0) {
            if (this.ships.isEmpty()) {
                maxSpeed = Utilities.MAX_SPEED;
                addListSpeed(minSpeed, maxSpeed);
            } else {

                Double finalMinSpeed = minSpeed;
                this.ships.removeIf(ship -> ship.getSpeed() < finalMinSpeed);
            }
        }

        if (minCrewSize != null && maxCrewSize != null && minCrewSize > 0 && maxCrewSize > 0) {
            if (this.ships.isEmpty()) {
                addListCrewSize(minCrewSize, maxCrewSize);
            } else {
                Integer finalMinCrewSize = minCrewSize;
                Integer finalMaxCrewSize = maxCrewSize;
                this.ships.removeIf(ship -> ship.getCrewSize() > finalMaxCrewSize || ship.getCrewSize() < finalMinCrewSize);
            }
        }

        if (minCrewSize != null && maxCrewSize == null && minCrewSize > 0) {
            if (this.ships.isEmpty()) {
                maxCrewSize = Utilities.MAX_CREW_SIZE;
                addListCrewSize(minCrewSize, maxCrewSize);
            } else {
                Integer finalMinCrewSize = minCrewSize;
                this.ships.removeIf(ship -> ship.getCrewSize() < finalMinCrewSize);
            }
        }

        if (maxCrewSize != null && minCrewSize == null && maxCrewSize > 0) {
            if (this.ships.isEmpty()) {
                minCrewSize = Utilities.MIN_CREW_SIZE;
                addListCrewSize(minCrewSize, maxCrewSize);
            } else {
                Integer finalMaxCrewSize = maxCrewSize;
                this.ships.removeIf(ship -> ship.getCrewSize() > finalMaxCrewSize);
            }
        }

        if (minRating != null && maxRating != null && minRating > 0 && maxRating > 0) {
            System.out.println(1);

            if (this.ships.isEmpty())
                addListRating(minRating, maxRating);
            else {
                this.ships.removeIf(ship -> ship.getRating() > maxRating || ship.getRating() < minRating);
            }
        }

        if (minRating != null && maxRating == null && minRating > 0) {
            if (this.ships.isEmpty()) {
                this.ships.addAll(shipRepository.findByRatingGreaterThanEqual(minRating));
            } else {
                this.ships.removeIf(ship -> ship.getRating() < minRating);
            }
        }

        if (maxRating != null && minRating == null && maxRating > 0) {
            if (this.ships.isEmpty()) {
                this.ships.addAll(shipRepository.findByRatingLessThanEqual(maxRating));
            } else
                this.ships.removeIf(ship -> ship.getRating() > maxRating);
        }

        return ships;
    }

    private void addListCrewSize(Integer minCrewSize, Integer maxCrewSize) {
        this.ships.addAll(shipRepository.findByCrewSizeBetween(minCrewSize, maxCrewSize));
        this.ships.addAll(shipRepository.findByCrewSize(maxCrewSize));
        this.ships.addAll(shipRepository.findByCrewSize(minCrewSize));
    }

    private void addListDate(Long after, Long before) {
        this.ships.addAll(shipRepository.findByProdDateBetween(new Date(after), new Date(before)));
        this.ships.addAll(shipRepository.findByProdDate(new Date(after)));
        this.ships.addAll(shipRepository.findByProdDate(new Date(before)));
    }

    private void addListSpeed(Double minSpeed, Double maxSpeed) {
        this.ships.addAll(shipRepository.findBySpeedBetween(minSpeed, maxSpeed));
        this.ships.addAll(shipRepository.findBySpeed(minSpeed));
        this.ships.addAll(shipRepository.findBySpeed(maxSpeed));
    }

    private void addListRating(Double minRating, Double maxRating) {
        this.ships.addAll(shipRepository.findByRatingBetween(minRating, maxRating));
        this.ships.addAll(shipRepository.findByRating(minRating));
        this.ships.addAll(shipRepository.findByRating(maxRating));
    }

    public ResponseEntity createShip(Ship ship) {
        if (!Utilities.isValidShip(ship)) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        if (ship.getUsed() == null)
            ship.setUsed(false);

        ship.setRating(Utilities.getShipRating(ship.getSpeed(), ship.getUsed(), ship.getProdDate()));

        shipRepository.save(ship);

        return new ResponseEntity(ship, HttpStatus.OK);
    }

    public ResponseEntity getShip(String id) {
        if (!Utilities.isValidId(id))
            return new ResponseEntity(HttpStatus.BAD_REQUEST);

        Ship ship = getValidShip(id);

        if (ship != null)
            return new ResponseEntity(ship, HttpStatus.OK);
        else
            return new ResponseEntity(HttpStatus.NOT_FOUND);

    }

    public ResponseEntity updateShip(String id, Ship shipForm) {
        if (!Utilities.isValidId(id)) return new ResponseEntity(HttpStatus.BAD_REQUEST);

        Ship ship = getValidShip(id);

        if (ship == null)
            return new ResponseEntity(HttpStatus.NOT_FOUND);

        if (shipForm.isEmptyShip())
            return new ResponseEntity(ship, HttpStatus.OK);

        if (shipForm.getName() != null) {
            if (!Utilities.isValidString(shipForm.getName()))
                return new ResponseEntity(HttpStatus.BAD_REQUEST);
            else ship.setName(shipForm.getName());
        }

        if (shipForm.getCrewSize() != null) {
            if (!Utilities.isValidCrewSize(shipForm.getCrewSize()))
                return new ResponseEntity(HttpStatus.BAD_REQUEST);
            else ship.setCrewSize(shipForm.getCrewSize());
        }

        if (shipForm.getProdDate() != null) {
            if (!Utilities.isValidProdDate(shipForm.getProdDate()))
                return new ResponseEntity(HttpStatus.BAD_REQUEST);
            else ship.setProdDate(shipForm.getProdDate());
        }

        if (shipForm.getPlanet() != null) {
            if (Utilities.isValidString(shipForm.getPlanet()))
                ship.setPlanet(shipForm.getPlanet());
        }

        if (shipForm.getShipType() != null)
            ship.setShipType(shipForm.getShipType());


        if (shipForm.getSpeed() != null) {
            if (Utilities.isValidSpeed(shipForm.getSpeed()))
                ship.setSpeed(shipForm.getSpeed());
            else return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        ship.setRating(Utilities.getShipRating(ship.getSpeed(), ship.getUsed(), ship.getProdDate()));
        shipRepository.save(ship);
        return new ResponseEntity(ship, HttpStatus.OK);
    }

    public ResponseEntity deleteShip(String id) {
        if (!Utilities.isValidId(id))
            return new ResponseEntity(null, HttpStatus.BAD_REQUEST);

        Ship ship = getValidShip(id);

        if (ship != null){
            shipRepository.delete(ship);
            return new ResponseEntity(HttpStatus.OK);
        } else {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }

    private Ship getValidShip(String str) {
        long id = Long.parseLong(str);
        return shipRepository.findById(id);
    }
}
