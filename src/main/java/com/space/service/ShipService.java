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


import java.util.Date;
import java.util.List;
import java.util.function.Predicate;


@Service
public class ShipService {
    private List<Ship> ships;
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

        this.ships.clear();

        if (!StringUtils.isEmpty(name)) {
            if (ships.isEmpty())
                this.ships.addAll(shipRepository.findByNameContaining(name));
            else
                ships.removeIf(ship -> !ship.getName().contains(name));
        }

        if (!StringUtils.isEmpty(planet)) {
            if (ships.isEmpty())
                this.ships.addAll(shipRepository.findByPlanetContaining(planet));
            else ships.removeIf(ship -> !ship.getPlanet().contains(planet));

        }

        if (!StringUtils.isEmpty(shipType)) {
            if (ships.isEmpty())
                this.ships.addAll(shipRepository.findByShipType(shipType));
            else ships.removeIf(ship -> ship.getShipType() != shipType);
        }

        // можно переписать
        if (after != null && after > 0 &&  before == null) {
            if (ships.isEmpty()) {
                before = Utilities.MAX_PROD_DATE;
                addListDate(after, before);
            }
            else {
                Long finalAfter = after;
                ships.removeIf(ship -> ship.getProdDate().getTime() < finalAfter);
            }
        }

        if (before != null && before > 0 && after == null) {
            if (ships.isEmpty()) {
                after = Utilities.MIN_PROD_DATE;
                addListDate(after, before);
            } else {
                Long finalBefore = before;
                ships.removeIf(ship -> ship.getProdDate().getTime() > finalBefore);
            }
        }


        if (before != null && before > 0 && after != null && after > 0) {
            if (ships.isEmpty()) {
                addListDate(after, before);
            } else {
                Long finalBefore = before;
                Long finalAfter = after;
                ships.removeIf(ship -> ship.getProdDate().getTime() > finalBefore || ship.getProdDate().getTime() < finalAfter);
            }
        }

        if (isUsed != null) {
            if (ships.isEmpty()) {
                this.ships.addAll(shipRepository.findByIsUsed(isUsed));
            } else {
                ships.removeIf(ship -> ship.getUsed() != isUsed);
            }
        }

        if (maxSpeed != null && minSpeed != null && maxSpeed > 0 && minSpeed > 0) {
            if (ships.isEmpty()) {
                addListSpeed(minSpeed, maxSpeed);
            } else {
                Double finalMaxSpeed = maxSpeed;
                Double finalMinSpeed = minSpeed;
                ships.removeIf(ship -> ship.getSpeed() > finalMaxSpeed || ship.getSpeed() < finalMinSpeed);
            }
        }

        if (maxSpeed != null && minSpeed == null && maxSpeed > 0) {
           if (ships.isEmpty()) {
               minSpeed = Utilities.MIN_SPEED;
               addListSpeed(minSpeed, maxSpeed);
           } else {
               Double finalMaxSpeed = maxSpeed;
               ships.removeIf(ship -> ship.getSpeed() > finalMaxSpeed);
           }
        }

        if (minSpeed != null && maxSpeed == null && minSpeed > 0) {
            if (ships.isEmpty()) {
                maxSpeed = Utilities.MAX_SPEED;
                addListSpeed(minSpeed, maxSpeed);
            } else {

                Double finalMinSpeed = minSpeed;
                ships.removeIf(ship -> ship.getSpeed() < finalMinSpeed);
            }
        }

        if (minCrewSize != null && maxCrewSize != null && minCrewSize > 0 && maxCrewSize > 0) {
            if (ships.isEmpty()) {
                addListCrewSize(minCrewSize, maxCrewSize);
            } else {
                Integer finalMinCrewSize = minCrewSize;
                Integer finalMaxCrewSize = maxCrewSize;
                ships.removeIf(ship -> ship.getCrewSize() > finalMaxCrewSize || ship.getCrewSize() < finalMinCrewSize);
            }
        }

        if (minCrewSize != null && maxCrewSize == null && minCrewSize > 0) {
            if (ships.isEmpty()) {
                maxCrewSize = Utilities.MAX_CREW_SIZE;
                addListCrewSize(minCrewSize, maxCrewSize);
            } else {
                Integer finalMinCrewSize = minCrewSize;
                ships.removeIf(ship -> ship.getCrewSize() < finalMinCrewSize);
            }
        }

        if (maxCrewSize != null && minCrewSize == null && maxCrewSize > 0) {
            if (ships.isEmpty()) {
                minCrewSize = Utilities.MIN_CREW_SIZE;
                addListCrewSize(minCrewSize, maxCrewSize);
            } else {
                Integer finalMaxCrewSize = maxCrewSize;
                ships.removeIf(ship -> ship.getCrewSize() > finalMaxCrewSize);
            }
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
