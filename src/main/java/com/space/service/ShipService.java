package com.space.service;


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

    @Autowired
    private ShipRepository shipRepository;

    public List<Ship> getShipsList(String name, String planet, ShipType shipType, Long after, Long before, Boolean isUsed,
            Double minSpeed, Double maxSpeed, Integer minCrewSize, Integer maxCrewSize, Double minRating, Double maxRating) {

        if (name == null && planet == null && shipType == null && after == null && before == null && isUsed == null
                && minSpeed == null && maxSpeed == null && minCrewSize == null && maxCrewSize == null
                && minRating == null && maxRating == null) {

            return shipRepository.findAll();
        }

        List<Ship> ships = new ArrayList<>();

        addListByName(ships, name);
        addListByPlanet(ships, planet);
        addListByShipType(ships, shipType);
        addListByUsed(ships, isUsed);
        addListByProdDate(ships, after, before);
        addListBySpeed(ships, minSpeed, maxSpeed);
        addListByCrewSize(ships, minCrewSize, maxCrewSize);
        addListByRating(ships, minRating, maxRating);

        return ships;
    }

    private void addListBySpeed(List<Ship> ships, Double minSpeed, Double maxSpeed) {
        if (maxSpeed != null && minSpeed != null && maxSpeed > 0 && minSpeed > 0) {
            if (ships.isEmpty()) {
                ships.addAll(shipRepository.findBySpeedBetween(minSpeed, maxSpeed));
                ships.addAll(shipRepository.findBySpeed(minSpeed));
                ships.addAll(shipRepository.findBySpeed(maxSpeed));
            } else {
                ships.removeIf(ship -> ship.getSpeed() > maxSpeed || ship.getSpeed() < minSpeed);
            }
        }

        if (maxSpeed != null && minSpeed == null && maxSpeed > 0) {
            if (ships.isEmpty())
                ships.addAll(shipRepository.findBySpeedLessThanEqual(maxSpeed));
            else
                ships.removeIf(ship -> ship.getSpeed() > maxSpeed);
        }

        if (minSpeed != null && maxSpeed == null && minSpeed > 0) {
            if (ships.isEmpty())
                ships.addAll(shipRepository.findBySpeedGreaterThanEqual(minSpeed));
            else
                ships.removeIf(ship -> ship.getSpeed() < minSpeed);
        }
    }

    private void addListByRating(List<Ship> ships, Double minRating, Double maxRating) {
        if (minRating != null && maxRating != null && minRating > 0 && maxRating > 0) {
            if (ships.isEmpty()) {
                ships.addAll(shipRepository.findByRatingBetween(minRating, maxRating));
                ships.addAll(shipRepository.findByRating(minRating));
                ships.addAll(shipRepository.findByRating(maxRating));
            }
            else {
                ships.removeIf(ship -> ship.getRating() > maxRating || ship.getRating() < minRating);
            }
        }

        if (minRating != null && maxRating == null && minRating > 0) {
            if (ships.isEmpty()) {
                ships.addAll(shipRepository.findByRatingGreaterThanEqual(minRating));
            } else {
                ships.removeIf(ship -> ship.getRating() < minRating);
            }
        }

        if (maxRating != null && minRating == null && maxRating > 0) {
            if (ships.isEmpty()) {
                ships.addAll(shipRepository.findByRatingLessThanEqual(maxRating));
            } else
                ships.removeIf(ship -> ship.getRating() > maxRating);
        }
    }

    private void addListByCrewSize (List<Ship> ships, Integer minCrewSize, Integer maxCrewSize) {
        if (minCrewSize != null && maxCrewSize != null && minCrewSize > 0 && maxCrewSize > 0) {
            if (ships.isEmpty()) {
                ships.addAll(shipRepository.findByCrewSizeBetween(minCrewSize, maxCrewSize));
                ships.addAll(shipRepository.findByCrewSize(maxCrewSize));
                ships.addAll(shipRepository.findByCrewSize(minCrewSize));
            } else {
                ships.removeIf(ship -> ship.getCrewSize() > maxCrewSize || ship.getCrewSize() < minCrewSize);
            }
        }

        if (minCrewSize != null && maxCrewSize == null && minCrewSize > 0) {
            if (ships.isEmpty()) {
                ships.addAll(shipRepository.findByCrewSizeGreaterThanEqual(minCrewSize));
            } else {
                ships.removeIf(ship -> ship.getCrewSize() < minCrewSize);
            }
        }

        if (maxCrewSize != null && minCrewSize == null && maxCrewSize > 0) {
            if (ships.isEmpty()) {
                ships.addAll(shipRepository.findByCrewSizeLessThanEqual(maxCrewSize));
            } else {
                ships.removeIf(ship -> ship.getCrewSize() > maxCrewSize);
            }
        }
    }

    private void addListByProdDate(List<Ship> ships, Long after, Long before) {
        if (after != null && after > 0 &&  before == null) {
            if (ships.isEmpty())
                ships.addAll(shipRepository.findByProdDateGreaterThanEqual(new Date(after)));
            else
                ships.removeIf(ship -> ship.getProdDate().getTime() < after);
        }

        if (before != null && before > 0 && after == null) {
            if (ships.isEmpty())
                ships.addAll(shipRepository.findByProdDateLessThanEqual(new Date(before)));
            else
                ships.removeIf(ship -> ship.getProdDate().getTime() > before);
        }

        if (before != null && before > 0 && after != null && after > 0) {
            if (ships.isEmpty()) {
                ships.addAll(shipRepository.findByProdDateBetween(new Date(after), new Date(before)));
                ships.addAll(shipRepository.findByProdDate(new Date(after)));
                ships.addAll(shipRepository.findByProdDate(new Date(before)));
            } else
                ships.removeIf(ship -> ship.getProdDate().getTime() > before || ship.getProdDate().getTime() < after);
        }
    }

    private void addListByUsed(List<Ship> ships, Boolean isUsed) {
        if (isUsed != null) {
            if (ships.isEmpty()) {
                ships.addAll(shipRepository.findByIsUsed(isUsed));
            } else {
                ships.removeIf(ship -> ship.getUsed() != isUsed);
            }
        }
    }

    private void addListByShipType(List<Ship> ships, ShipType shipType) {
        if (shipType != null) {
            if (ships.isEmpty())
                ships.addAll(shipRepository.findByShipType(shipType));
            else
                ships.removeIf(ship -> ship.getShipType() != shipType);
        }
    }

    private void addListByPlanet(List<Ship> ships,  String planet) {
        if (!StringUtils.isEmpty(planet)) {
            if (ships.isEmpty())
                ships.addAll(shipRepository.findByPlanetContaining(planet));
            else
                ships.removeIf(ship -> !ship.getPlanet().contains(planet));
        }
    }

    private void addListByName (List<Ship> ships,  String name) {
        if (!StringUtils.isEmpty(name)) {
            if (ships.isEmpty())
                ships.addAll(shipRepository.findByNameContaining(name));
            else
                ships.removeIf(ship -> !ship.getName().contains(name));
        }
    }


    public ResponseEntity createShip(Ship ship) {
        if (!Utilities.isValidShip(ship))
            return new ResponseEntity(HttpStatus.BAD_REQUEST);


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

        if (ship == null)
            return new ResponseEntity(HttpStatus.NOT_FOUND);

        return new ResponseEntity(ship, HttpStatus.OK);
    }

    public ResponseEntity updateShip(String id, Ship shipForm) {
        if (!Utilities.isValidId(id))
            return new ResponseEntity(HttpStatus.BAD_REQUEST);

        Ship ship = getValidShip(id);

        if (ship == null)
            return new ResponseEntity(HttpStatus.NOT_FOUND);

        if (shipForm.isEmptyShip())
            return new ResponseEntity(ship, HttpStatus.OK);

        if (shipForm.getName() != null) {
            if (Utilities.isValidString(shipForm.getName()))
                ship.setName(shipForm.getName());
            else return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        if (shipForm.getCrewSize() != null) {
            if (Utilities.isValidCrewSize(shipForm.getCrewSize()))
                ship.setCrewSize(shipForm.getCrewSize());
            else return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        if (shipForm.getProdDate() != null) {
            if (Utilities.isValidProdDate(shipForm.getProdDate()))
                ship.setProdDate(shipForm.getProdDate());
            else return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        if (shipForm.getPlanet() != null) {
            if (Utilities.isValidString(shipForm.getPlanet()))
                ship.setPlanet(shipForm.getPlanet());
            else return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        if (shipForm.getSpeed() != null) {
            if (Utilities.isValidSpeed(shipForm.getSpeed()))
                ship.setSpeed(shipForm.getSpeed());
            else return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        if (shipForm.getShipType() != null)
            ship.setShipType(shipForm.getShipType());

        ship.setRating(Utilities.getShipRating(ship.getSpeed(), ship.getUsed(), ship.getProdDate()));

        shipRepository.save(ship);
        return new ResponseEntity(ship, HttpStatus.OK);
    }

    public ResponseEntity deleteShip(String id) {
        if (!Utilities.isValidId(id))
            return new ResponseEntity(HttpStatus.BAD_REQUEST);

        Ship ship = getValidShip(id);

        if (ship == null)
            return new ResponseEntity(HttpStatus.NOT_FOUND);

        shipRepository.delete(ship);
        return new ResponseEntity(HttpStatus.OK);
    }

    private Ship getValidShip(String str) {
        long id = Long.parseLong(str);
        return shipRepository.findById(id);
    }
}
