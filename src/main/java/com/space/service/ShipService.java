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
import org.springframework.web.util.NestedServletException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Service
public class ShipService {

    private List<Ship> ships = new ArrayList<>();

    private final ShipRepository shipRepository;

    @Autowired
    public ShipService(ShipRepository shipRepository) {
        this.shipRepository = shipRepository;
    }

    public List<Ship> getShipsList(String name, String planet, ShipType shipType, Long after, Long before, Boolean isUsed,
            Double minSpeed, Double maxSpeed, Integer minCrewSize, Integer maxCrewSize, Double minRating, Double maxRating) {

        if (name == null && planet == null && shipType == null && after == null && before == null
        && isUsed == null && minSpeed == null && maxSpeed == null && minCrewSize == null && maxCrewSize == null && minRating == null
        && maxRating == null) {
            ships = shipRepository.findAll();
            return ships;
        }

         this.ships = new ArrayList<>();

        if (!StringUtils.isEmpty(name)) {
            this.ships.addAll(shipRepository.findByNameContaining(name));
        }

        if (!StringUtils.isEmpty(planet)) {
            this.ships.addAll(shipRepository.findByPlanetContaining(planet));
        }

        if (!StringUtils.isEmpty(shipType)) {
            this.ships.addAll(shipRepository.findByShipType(shipType));
        }

        // можно переписать
        if (after != null && after > 0 && before == null) {
            before = Utilities.MAX_PROD_DATE;

            addListDate(after, before);
        }

        if (before != null && before > 0 && after == null) {
            after = Utilities.MIN_PROD_DATE;

            addListDate(after, before);
        }

        if (before != null && before > 0 && after != null && after > 0) {
            addListDate(after, before);
        }

        if (isUsed != null) {
            this.ships.addAll(shipRepository.findByIsUsed(isUsed));
        }

        if (maxSpeed != null && minSpeed != null && maxSpeed > 0 && minSpeed > 0) {
            addListSpeed(minSpeed, maxSpeed);
        }

        if (maxSpeed != null && minSpeed == null && maxSpeed > 0) {
            minSpeed = Utilities.MIN_SPEED;
            addListSpeed(minSpeed, maxSpeed);
        }

        if (minSpeed != null && maxSpeed == null && minSpeed > 0) {
            maxSpeed = Utilities.MAX_SPEED;
            addListSpeed(minSpeed, maxSpeed);
        }

        if (minCrewSize != null && maxCrewSize != null && minCrewSize > 0 && maxCrewSize > 0) {
            addListCrewSize(minCrewSize, maxCrewSize);
        }

        if (minCrewSize != null && maxCrewSize == null && minCrewSize > 0) {
            maxCrewSize = Utilities.MAX_CREW_SIZE;
            addListCrewSize(minCrewSize, maxCrewSize);
        }

        if (maxCrewSize != null && minCrewSize == null && maxCrewSize > 0) {
            minCrewSize = Utilities.MIN_CREW_SIZE;
            addListCrewSize(minCrewSize, maxCrewSize);
        }

        if (minRating != null && maxRating != null && minRating > 0 && maxRating > 0) {
            this.ships.addAll(shipRepository.findByRatingBetween(minRating, maxRating));
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

    public ResponseEntity updateShip(String id, Ship shipForm)  {
        if (!Utilities.isValidId(id)) return new ResponseEntity(HttpStatus.BAD_REQUEST);

        String name = shipForm.getName();
        String planet = shipForm.getPlanet();
        ShipType shipType = shipForm.getShipType();
        Date prodDate = shipForm.getProdDate();
        Boolean isUsed = shipForm.getUsed();
        Double speed = shipForm.getSpeed();
        Integer crewSize = shipForm.getCrewSize();

        Ship ship = getValidShip(id);

        if (ship == null)
            return new ResponseEntity(HttpStatus.NOT_FOUND);

        try {
            if (shipForm.isEmptyShip())
                return new ResponseEntity(ship, HttpStatus.OK);



            if (Utilities.isValidSpeed(shipForm.getSpeed())) {
                ship.setSpeed(shipForm.getSpeed());
            }

            if (shipForm.getUsed() != null) {
                ship.setUsed(shipForm.getUsed());
            }



            if (shipForm.getShipType() != null) {
                ship.setShipType(shipForm.getShipType());
            }

            if (Utilities.isValidString(shipForm.getPlanet())) {
                ship.setPlanet(shipForm.getPlanet());
            }

            if (Utilities.isValidProdDate(shipForm.getProdDate())) {
                ship.setProdDate(shipForm.getProdDate());
            } else return new ResponseEntity(ship, HttpStatus.BAD_REQUEST);

            if (Utilities.isValidString(shipForm.getName())) {
                ship.setName(shipForm.getName());
            } else return new ResponseEntity(ship, HttpStatus.BAD_REQUEST);

            if (Utilities.isValidCrewSize(shipForm.getCrewSize())) {
                ship.setCrewSize(shipForm.getCrewSize());
            } else return new ResponseEntity(ship, HttpStatus.BAD_REQUEST);


        } catch (NullPointerException n) {
            return new ResponseEntity(ship, HttpStatus.OK);
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





    public Integer getCount(){
        return ships.size();
    }

    public List<Ship> getShips() {
        return shipRepository.findAll();
    }
}
