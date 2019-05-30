package com.space.controller;

import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.repository.ShipRepository;
import com.space.service.ShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping(value = "/rest")
public class ShipController {
    @Autowired
    private ShipService shipService;

    @RequestMapping(value = "/ships", method = RequestMethod.GET)
    public ResponseEntity<Ship> getShipsList(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "planet", required = false) String planet,
            @RequestParam(value = "shipType", required = false) ShipType shipType,
            @RequestParam(value = "after", required = false) Long after,
            @RequestParam(value = "before", required = false) Long before,
            @RequestParam(value = "isUsed", required = false) Boolean isUsed,
            @RequestParam(value = "minSpeed", required = false) Double minSpeed,
            @RequestParam(value = "maxSpeed", required = false) Double maxSpeed,
            @RequestParam(value = "minCrewSize", required = false) Integer minCrewSize,
            @RequestParam(value = "maxCrewSize", required = false) Integer maxCrewSize,
            @RequestParam(value = "minRating", required = false) Double minRating,
            @RequestParam(value = "maxRating", required = false) Double maxRating,

            @RequestParam(value = "order", required = false, defaultValue = "ID") ShipOrder order,
            @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
            @RequestParam(value = "pageSize", required = false, defaultValue = "3") Integer pageSize
    ) {
        List<Ship> ships = shipService.getShipsList(name, planet, shipType, after, before, isUsed, minSpeed, maxSpeed, minCrewSize, maxCrewSize, minRating,maxRating);

        Comparator<Ship> comparator = new Comparator<Ship>() {
            @Override
            public int compare(Ship o1, Ship o2) {
                if (order.equals(ShipOrder.ID)) {
                    return o1.getId().compareTo(o2.getId());
                }

                if (order.equals(ShipOrder.SPEED)) {
                    return o1.getSpeed().compareTo(o2.getSpeed());
                }

                if (order.equals(ShipOrder.DATE)) {
                    return o1.getProdDate().compareTo(o2.getProdDate());
                }

                if (order.equals(ShipOrder.RATING)) {
                    return o1.getRating().compareTo(o2.getRating());
                }

                return 0;
            }
        };
        ships.sort(comparator);

        PagedListHolder<Ship> paged = new PagedListHolder(ships);
        paged.setPage(pageNumber);
        paged.setPageSize(pageSize);

        return new ResponseEntity(paged.getPageList(), HttpStatus.OK);
    }

    @RequestMapping(value = "/ships/count", method = RequestMethod.GET)
    public ResponseEntity getShipsCount(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "planet", required = false) String planet,
            @RequestParam(value = "shipType", required = false) ShipType shipType,
            @RequestParam(value = "after", required = false) Long after,
            @RequestParam(value = "before", required = false) Long before,
            @RequestParam(value = "isUsed", required = false) Boolean isUsed,
            @RequestParam(value = "minSpeed", required = false) Double minSpeed,
            @RequestParam(value = "maxSpeed", required = false) Double maxSpeed,
            @RequestParam(value = "minCrewSize", required = false) Integer minCrewSize,
            @RequestParam(value = "maxCrewSize", required = false) Integer maxCrewSize,
            @RequestParam(value = "minRating", required = false) Double minRating,
            @RequestParam(value = "maxRating", required = false) Double maxRating
    ) {
        Integer count = shipService.getShipsList(name, planet, shipType, after, before, isUsed, minSpeed, maxSpeed, minCrewSize, maxCrewSize, minRating, maxRating).size();

        return new ResponseEntity(count, HttpStatus.OK);
    }

    @RequestMapping(value = "/ships", method = RequestMethod.POST)
    public ResponseEntity createShip(@RequestBody Ship ship) {
        return shipService.createShip(ship);
    }

    @ResponseBody
    @RequestMapping(value = "/ships/{id}", method = RequestMethod.GET)
    public ResponseEntity getShip(@PathVariable String id) {
        return shipService.getShip(id);
    }


    @ResponseBody
    @RequestMapping(value = "/ships/{id}", method = RequestMethod.POST)
    public ResponseEntity updateShip(@PathVariable String id, @RequestBody Ship ship) {
        return shipService.updateShip(id, ship);
    }


    @RequestMapping(value = "/ships/{id}", method = RequestMethod.DELETE)
    public ResponseEntity deleteShip(@PathVariable String id) {
        return shipService.deleteShip(id);
    }
}


