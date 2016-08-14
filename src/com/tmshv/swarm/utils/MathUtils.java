package com.tmshv.swarm.utils;

/**
 * Created at 27/07/16
 *
 * @author tmshv
 */
public class MathUtils {
    public static boolean isClose(double v1, double v2, double precision) {
        return Math.abs(v1 - v2) <= precision;
    }
}
