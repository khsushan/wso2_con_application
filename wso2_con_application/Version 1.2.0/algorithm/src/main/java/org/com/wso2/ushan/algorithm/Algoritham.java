package org.com.wso2.ushan.algorithm;

import org.com.wso2.ushan.bean.LocalEvent;
import org.com.wso2.ushan.comparator.LocalEventComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.logging.Logger;

public class Algoritham {

    private static HashMap<String, Double[]> sensors;
    private static Logger logger;

    static {
        logger = Logger.getLogger("LOG.txt");
        sensors = new HashMap<String, Double[]>();
        sensors.put("14", new Double[]{0.0, 0.0});
        sensors.put("12", new Double[]{2.9, 0.0});
        sensors.put("11", new Double[]{7.25, 0.0});
        sensors.put("10", new Double[]{0.0, 5.95});
        sensors.put("15", new Double[]{4.1, 5.95});
        sensors.put("13", new Double[]{6.8, 5.95});
        sensors.put("16", new Double[]{3.6, 5.05});
        sensors.put("17", new Double[]{3.6, 7.0});

    }

    private static boolean isIntersect(double x0, double y0, double r0, double x1,
                                       double y1, double r1) {
        double dx, dy;
        dx = (x1 - x0);
        dy = (y1 - y0);

		/* finding distance between the centers of two circles */
        double d = Math.sqrt((dx * dx) + (dy * dy));

		/* check whether two circles are intersecting or not */
        if (d < (r0 + r1)) {
            return true;
        }
        return false;

    }

    private static Double[] nonIntersectingPointsOfTwoCircles(double x0, double y0,
                                                              double r0, double x1, double y1, double r1) {

        double dx, dy;

        dx = (x1 - x0);
        dy = (y1 - y0);

		/* finding distance between the centers of two circles */
        double d = Math.sqrt((dx * dx) + (dy * dy));

		/* if two circles are not intersecting */
        double k = d - (r0 + r1);
        double distance = (r0 + (k / 2)) / d;

        if (k < 0) {
            /* if two circles are intersecting */
            k = (r0 + r1) - d;
            distance = (r0 - (k / 2)) / d;
        }

        double x, y;

		/*
         * If the line joining the centers of two circles adjacent cut the
		 * circle 1 at point A and cuts the circle 2 at point B, midpoint of AB
		 * is C = (x,y)
		 */
        x = x0 * (1 - distance) + distance * x1;
        y = y0 * (1 - distance) + distance * y1;
        System.out.println("x: " + x + " y :" + y);
        double e1 = Math.sqrt(r0 * r0 - (r0 - k / 2) * (r0 - k / 2));
        //logger.info("Cordinate of middle of the two points  X: " + x + " Y: "
        //   + y + " and error is " + e1);

        return new Double[]{x, y, e1};
    }

    private static Double[] nonIntersectingPointsOfThreeCircles(double x0, double y0,
                                                                double r0, double x1, double y1, double r1, double x2, double y2,
                                                                double r2) {

		/*
         * If line C1C2 cuts one circle at A and the other at B, and the mid-point of AB is D.
		 * This is done for all 3 pairs of circles and let those points be G and H.
		 * Get coordinates of G and H.
		 */
        Double[] points1 = nonIntersectingPointsOfTwoCircles(x0, y0, r0, x1,
                y1, r1);

        Double[] points2 = nonIntersectingPointsOfTwoCircles(x0, y0, r0, x2,
                y2, r2);

		/*
		 * A line is drawn joining the point D to the center of the third circle
		 * This is done for all 3 pairs of circles
		 * and let the point of intersection of those 3 lines be I = (x, y).
		 */
        double x, y;
        x = (((x1 - points2[0]) * y1 - (y1 - points2[1]) * x1)
                * (x2 - points1[0]) - ((x2 - points1[0]) * y2 - (y2 - points1[1])
                * x2)
                * (x1 - points2[0]))
                / ((x1 - points2[0]) * (y2 - points1[1]) - (y1 - points2[1])
                * (x2 - points1[0]));

        y = ((x2 - points1[0]) * y2 - (y2 - points1[1]) * x2 + (y2 - points1[1])
                * x)
                / (x2 - points1[0]);

		/*
		 * Find the error 'e2',
		 * Get the smallest radius.
		 */
        Double[] radii = new Double[]{r0, r1, r2};
        double min = radii[0];
        for (int i = 1; i < radii.length; i++) {
            if (radii[i] < min) {
                min = radii[i];
            }
        }

		/* Line C1I cuts the circle of center C1 at E = (xE, yE) */
        double xE = x0 * (1 - min) + (x * min);
        double yE = y0 * (1 - min) + (y * min);

		/* e2 = length EI */
        double e2 = Math.sqrt((xE - x) * (xE - x) + (yE - y) * (yE - y));

        return new Double[]{x, y, e2};
    }


    private static Double[] calculateThreeCircleIntersection(double x1, double y1,
                                                             double r1, double x2, double y2, double r2, double x3, double y3,
                                                             double r3) {

        double x, y, va, vb;

        va = ((r2 * r2 - r3 * r3) - (x2 * x2 - x3 * x3) - (y2 * y2 - y3 * y3)) / 2;
        vb = ((r2 * r2 - r1 * r1) - (x2 * x2 - x1 * x1) - (y2 * y2 - y1 * y1)) / 2;

        y = (vb * (x3 - x2) - va * (x1 - x2))
                / ((y1 - y2) * (x3 - x2) - (y3 - y2) * (x1 - x2));
        x = (va - y * (y3 - y2)) / (x3 - x2);
        return new Double[]{x, y, 0.0};
    }

    private static Double[] fourCircles(double x0, double y0, double r0,
                                        double x1, double y1, double r1,
                                        double x2, double y2, double r2,
                                        double x3, double y3, double r3) {

		/*
		 * Let the centers of four circles be
		 * C1 = (x0, y0)
		 * C2 = (x1, y1)
		 * C3 = (x2, y2)
		 * C4 = (x3, y3)
		 */
        double[][] tempArr = {{x0, y0}, {x1, y1}, {x2, y2}, {x3, y3}};

		/*
		 * Calculate the distance between each pair of points
		 * Let 'dx' and 'dy' be the horizontal and vertical distances
		 * between particular two points respectively
		 */
        Double[] magnitudes = new Double[6];
        double dx, dy;
        int count = 0;
        HashMap<Double, Double[][]> map = new HashMap<Double, Double[][]>();


        for (int i = 0; i < 4; i++) {
            for (int j = i + 1; j < 4; j++) {
                dx = tempArr[i][0] - tempArr[j][0];
                dy = tempArr[i][1] - tempArr[j][1];

                magnitudes[count] = Math.sqrt(dx * dx + dy * dy);
                map.put(magnitudes[count], new Double[][]{{tempArr[i][0], tempArr[i][1]}, {tempArr[j][0], tempArr[j][1]}});
                count++;
            }
        }

		/* Get the maximum distance from the above set of distances */
        double max = magnitudes[0];
        for (int i = 1; i < magnitudes.length; i++) {
            if (magnitudes[i] > max) {
                max = magnitudes[i];
            }
        }

		/*
		 * Get the pair of points related to the maximum distance
		 * and draw a line joining them
		 */
        Double[][] min_max = new Double[2][2];
        min_max = map.get(max);
        System.out.println(min_max[0][0] + "\t" + min_max[0][1]);
        System.out.println(min_max[1][0] + "\t" + min_max[1][1]);

		/* Get the remaining pair of points and draw a line joining them */
        Double[][] points = new Double[2][2];
        count = 0;
        if (x0 != min_max[0][0] && x0 != min_max[1][0]) {
            points[count][0] = x0;
            points[count][1] = y0;
            count++;
        }
        if (x1 != min_max[0][0] && x1 != min_max[1][0]) {
            points[count][0] = x1;
            points[count][1] = y1;
            count++;
        }
        if (x2 != min_max[0][0] && x2 != min_max[1][0]) {
            points[count][0] = x2;
            points[count][1] = y2;
            count++;
        }
        if (x3 != min_max[0][0] && x3 != min_max[1][0]) {
            points[count][0] = x3;
            points[count][1] = y3;
            count++;
        }
        System.out.println(points[0][0] + "\t" + points[0][1]);
        System.out.println(points[1][0] + "\t" + points[1][1]);

		/* Let the coordinates of the point of intersection of the lines be I = (x, y) */
        double x = (((min_max[0][0] - min_max[1][0]) * min_max[0][1] - (min_max[0][1] - min_max[1][1]) * min_max[0][0]) * (points[0][0] - points[1][0]) - ((points[0][0] - points[1][0])
                * points[0][1] - (points[0][1] - points[1][1]) * points[0][0])
                * (min_max[0][0] - min_max[1][0]))
                / ((min_max[0][0] - min_max[1][0]) * (points[0][1] - points[1][1]) - (min_max[0][1] - min_max[1][1])
                * (points[0][0] - points[1][0]));

        double y = ((points[0][0] - points[1][0]) * points[0][1] - (points[0][1] - points[1][1]) * points[0][0] + (points[0][1] - points[1][1])
                * x)
                / (points[0][0] - points[1][0]);
        System.out.println("\n" + x + "\t" + y);

		/*
		 * Find the error 'e',
		 * Get the smallest radius.
		 */
        Double[] radii = new Double[]{r0, r1, r2, r3};
        double min = radii[0];
        for (int i = 1; i < radii.length; i++) {
            if (radii[i] < min) {
                min = radii[i];
            }
        }

		/*
		 * Line drawn from the center of the circle with the minimum radius to I
		 * cuts the circle at point A = (xa, ya)
		 */
        double xa = x0 * (1 - min) + x * min;
        double ya = y0 * (1 - min) + y * min;

		/* e = Length AI */
        double e = Math.sqrt((xa - x) * (xa - x) + (ya - y) * (ya - y));

        return new Double[]{x, y, e};
    }

    public static LocalEvent manageUser(ArrayList<LocalEvent> userDistances) {
        Double[] arr = new Double[3];

        double x0, y0, r0, x1, y1, r1, x2 = 0, y2 = 0, r2 = 0, x3 = 0, y3 = 0, r3 = 0;

        Collections.sort(userDistances, new LocalEventComparator());
        LocalEvent localEvent = new LocalEvent();
        localEvent.setUserID(userDistances.get(0).getUserID());

        x0 = sensors.get(userDistances.get(0).getSensorId())[0];
        y0 = sensors.get(userDistances.get(0).getSensorId())[1];
        r0 = userDistances.get(0).getDistance();

        if (userDistances.size() == 1) {
            if (r0 == 0) {
                // user is on sensor
                logger.info("LocalEvent is on the sensor");
                arr[0] = x0;
                arr[1] = y0;
                arr[2] = 0.0;
            }

        } else {

            x1 = sensors.get(userDistances.get(1).getSensorId())[0];
            y1 = sensors.get(userDistances.get(1).getSensorId())[1];
            r1 = userDistances.get(1).getDistance();

            if (userDistances.size() == 2) {
                x2 = sensors.get(userDistances.get(2).getSensorId())[0];
                y2 = sensors.get(userDistances.get(2).getSensorId())[1];
                r2 = userDistances.get(2).getDistance();
            }
            if (userDistances.size() > 3) {
                r3 = userDistances.get(3).getDistance();
            }

            if (r2 == r3) {

                logger.info("There is four sensor");
                x3 = sensors.get(userDistances.get(3).getSensorId())[0];
                y3 = sensors.get(userDistances.get(3).getSensorId())[1];
                arr = fourCircles(x0, y0, r0, x1, y1, r1, x2, y2, r2, x3, y3, r3);

            } else if (userDistances.size() == 2) {
                logger.info("There are two sensor");
                arr = nonIntersectingPointsOfTwoCircles(x0, y0, r0, x1, y1, r1);

            } else if (isIntersect(x0, y0, r0, x1, y1, r1)
                    && isIntersect(x0, y0, r0, x2, y2, r2)
                    && isIntersect(x2, y2, r2, x1, y1, r1)) {
                Double[] cordinates = calculateThreeCircleIntersection(x0, y0,
                        r0, x1, y1, r1, x2, y2, r2);
                if (cordinates[2] == 0) {
					/*logger.info("Cordinate of intersecting points X :"
							+ cordinates[0] + " Y:" + cordinates[1]);*/
                } else {
					/*logger.info("one circle is contained in another and cordinates are X:"
							+ cordinates[0] + " Y:" + cordinates[1]);*/
                }

                arr = cordinates;
            } else {
                logger.info("There are three non intersecting circles");
                arr = nonIntersectingPointsOfThreeCircles(x0, y0, r0, x1, y1,
                        r1, x2, y2, r2);

            }

        }
        localEvent.setX(arr[0]);
        localEvent.setY(arr[1]);
        localEvent.setError(arr[2]);
        return localEvent;
    }


    public static Double[] calculateMidPoint(ArrayList<LocalEvent> calculatedEventList) {
        double x1, y1, x2, y2, x3, y3;

        x1 = calculatedEventList.get(0).getX();
        y1 = calculatedEventList.get(0).getY();
        x2 = calculatedEventList.get(1).getX();
        y2 = calculatedEventList.get(1).getY();
        x3 = calculatedEventList.get(2).getX();
        y3 = calculatedEventList.get(2).getY();

        double points1_x, points1_y, points2_x, points2_y;

        points1_x = (x1 + x2) / 2;
        points1_y = (y1 + y2) / 2;
        points2_x = (x1 + x3) / 2;
        points2_y = (y1 + y3) / 2;

        double x, y;

        x = (((x2 - points2_x) * y2 - (y2 - points2_y) * x2) * (x3 - points1_x) - ((x3 - points1_x)
                * y3 - (y3 - points1_y) * x3)
                * (x2 - points2_x))
                / ((x2 - points2_x) * (y3 - points1_y) - (y2 - points2_y)
                * (x3 - points1_x));

        y = ((x3 - points1_x) * y3 - (y3 - points1_y) * x3 + (y3 - points1_y)
                * x)
                / (x3 - points1_x);


        return new Double[]{x, y};
    }


    public static void main(String[] args){

        double x0, y0, r0, x1, y1, r1, x2 = 0, y2 = 0, r2 = 0, x3 = 0, y3 = 0, r3 = 0;
        x0 =  sensors.get("13")[0];
        y0 = sensors.get("13")[1];
        r0 = 3.45;

        x1 =  sensors.get("16")[0];
        y1 = sensors.get("16")[1];
        r1 =  1.05;

        x2 =  sensors.get("11")[0];
        y2 = sensors.get("11")[1];
        r2 = 3.15;

        Double[] arry = calculateThreeCircleIntersection(x0, y0, r0, x1, y1, r1, x2, y2, r2);
        System.out.print(arry[0]+":"+arry[1]);
//        13 -  3.45
//        16 - 1.05
//        11 - 3.15
//        12 - 3.5
//        14 - 5.8
//        10 - 6.2
//        15 - 3.45
//


    }


}
