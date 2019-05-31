package com.space.service;

import com.space.model.Ship;

import java.util.Calendar;
import java.util.Date;

public class Utilities {
    private final static long MIN_PROD_DATE = 26192239200000L;
    private final static long MAX_PROD_DATE = 33134738399999L;

    private final static double MAX_SPEED = 0.99;
    private final static double MIN_SPEED = 0.01;

    private final static int MAX_CREW_SIZE = 9999;
    private final static int MIN_CREW_SIZE = 1;

    public static boolean isValidId(final String str) {
        boolean isNumeric = str.matches("-?\\d+(\\.\\d+)?");

        if (!isNumeric) {
            return false;
        }

        double num = Double.parseDouble(str);

        if ((num % 1) != 0) {
            return false;
        }

        long id = Long.parseLong(str);

        return id > 0;
    }

    public static Double getShipRating(Double speed, Boolean isUsed, Date prodDate) {
        double result;
        Calendar maxDate = Calendar.getInstance();
        maxDate.setTime(new Date(MAX_PROD_DATE));

        Calendar prodDat = Calendar.getInstance();
        prodDat.setTime(prodDate);

        int d1 = maxDate.get(Calendar.YEAR);
        int d2 = prodDat.get(Calendar.YEAR);

        if (isUsed) {
            result = (80 * speed * 0.5) / (d1 - d2 + 1);
        } else {
            result = (80 * speed * 1) / (d1 - d2 + 1);
        }

        return  Math.round(result * 100d) / 100d;
    }

    public static boolean isValidShip(Ship ship) {
        if (ship.getShipType() == null || ship.getProdDate() == null || ship.getSpeed() == null || ship.getCrewSize() == null
                || ship.getName() == null || ship.getPlanet() == null)
            return false;

        if (!isValidString(ship.getName())) return false;
        if (!isValidString(ship.getPlanet())) return false;

        if (!isValidSpeed(ship.getSpeed())) return false;
        if (!isValidCrewSize(ship.getCrewSize())) return false;
        if (!isValidProdDate(ship.getProdDate())) return false;

        return true;
    }

    public static boolean isValidString(final String str) {
        if (str.length() > 50) return false;

        return !str.equals("");
    }

    public static boolean isValidSpeed(Double speed) {
        return !(speed > MAX_SPEED) && !(speed < MIN_SPEED);
    }

    public static boolean isValidCrewSize(Integer crewSize) {
        return crewSize <= Utilities.MAX_CREW_SIZE && crewSize >= Utilities.MIN_CREW_SIZE;
    }

    public static boolean isValidProdDate(Date date) {
        if (date.getTime() < 0) return false;
        return date.getTime() <= Utilities.MAX_PROD_DATE && date.getTime() >= Utilities.MIN_PROD_DATE;
    }
}
