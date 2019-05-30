package com.space.controller;

import com.space.model.Ship;
import org.springframework.util.StringUtils;

import java.util.Calendar;
import java.util.Date;

public class Utilities {
    public final static long MIN_PROD_DATE = 26192239200000L;
    public final static long MAX_PROD_DATE = 33134738399999L;

    public final static double MAX_SPEED = 0.99;
    public final static double MIN_SPEED = 0.01;

    public final static int MAX_CREW_SIZE = 9999;
    public final static int MIN_CREW_SIZE = 1;

    private static boolean isNumeric(final String str) {
        return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }

    private static boolean isPrime(final String str) {
        double num = Double.parseDouble(str);
        return (num % 1) == 0;
    }

    public static boolean isValidId(final String str) {
        if (!isNumeric(str)) {
            return false;
        }

        if (!isPrime(str)) {
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
            result = (80 * speed * 0.2) / (d1 - d2 + 1);
        } else {
            result = (80 * speed * 1) / (d1 - d2 + 1);
        }

        return  Math.round(result * 100d) / 100d;
    }

    public static boolean isValidShip(Ship ship) {
        if (ship.getShipType() == null || ship.getProdDate() == null || ship.getSpeed() == null || ship.getCrewSize() == null)
            return false;

        if (!isValidString(ship.getName())) return false;
        if (!isValidString(ship.getPlanet())) return false;
        if (!isValidSpeed(ship.getSpeed())) return false;
        if (!isValidCrewSize(ship.getCrewSize())) return false;
        if (!isValidProdDate(ship.getProdDate())) return false;

        return true;
    }

    public static boolean isValidString(final String str) {
//        if (str == null) return false;
        if (str.length() > 50) return false;

        return !str.equals("");
    }

    public static boolean isValidSpeed(Double speed) {
//        if (speed == null) return false;
        return !(speed > MAX_SPEED) && !(speed < MIN_SPEED);
    }

    public static boolean isValidCrewSize(Integer crewSize) {
//        if (crewSize == null) return false;
        return crewSize <= Utilities.MAX_CREW_SIZE && crewSize >= Utilities.MIN_CREW_SIZE;
    }

    public static boolean isValidProdDate(Date date) {
//        if (date == null) return false;
        if (date.getTime() < 0) return false;
        return date.getTime() <= Utilities.MAX_PROD_DATE && date.getTime() >= Utilities.MIN_PROD_DATE;
    }
}
