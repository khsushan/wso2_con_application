package org.com.wso2.ushan.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.logging.Logger;

import org.com.wso2.ushan.bean.LocalEvent;
import org.com.wso2.ushan.comparator.LocalEventComparator;

public class Algoritham {

	private static HashMap<String, Double[]> sensors;
	private static Logger logger;

	static {
		logger = Logger.getLogger("LOG.txt");
		sensors = new HashMap<String, Double[]>();
		sensors.put("14", new Double[] {0.0, 0.0});
		sensors.put("12", new Double[] {2.9, 0.0});
		sensors.put("11", new Double[] {7.25, 0.0});
		sensors.put("10", new Double[] {0.0, 5.95});
		sensors.put("15", new Double[] {4.1, 5.95});
		sensors.put("13", new Double[] {6.8, 5.95});
		sensors.put("16", new Double[] {3.6,5.05});
		sensors.put("17", new Double[] {3.6,7.0});
		
	}

	private boolean isIntersect(double x0, double y0, double r0, double x1,
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

	private Double[] nonIntersectingPointsOfTwoCircles(double x0, double y0,
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
		double e1 = Math.sqrt(r0 * r0 - (r0 - k/2) * (r0 - k/2));
		logger.info("Cordinate of middle of the two points  X: " + x + " Y: "
				+ y + " and error is " + e1);

		return new Double[] { x, y, e1 };
	}

	private Double[] nonIntersectingPointsOfThreeCircles(double x0, double y0,
			double r0, double x1, double y1, double r1, double x2, double y2,
			double r2) {

		Double[] points1 = nonIntersectingPointsOfTwoCircles(x0, y0, r0, x1,
				y1, r1);

		Double[] points2 = nonIntersectingPointsOfTwoCircles(x0, y0, r0, x2,
				y2, r2);

		/*
		 * A line is drawn joining the point C to the center of the other
		 * circle. This is done for all 3 pairs of adjacent circles and find the
		 * point of intersection of those 3 lines
		 */
		double x = (((x1 - points2[0]) * y1 - (y1 - points2[1]) * x1)
				* (x2 - points1[0]) - ((x2 - points1[0]) * y2 - (y2 - points1[1])
				* x2)
				* (x1 - points2[0]))
				/ ((x1 - points2[0]) * (y2 - points1[1]) - (y1 - points2[1])
						* (x2 - points1[0]));

		double y = ((x2 - points1[0]) * y2 - (y2 - points1[1]) * x2 + (y2 - points1[1])
				* x)
				/ (x2 - points1[0]);
		
		double xA, yA, e2;
		xA = r0 * x + (1 - r0) * x0;
		yA = r0 * y + (1 - r0) * y0;

		/* Find the error, e = AI */
		e2 = Math.sqrt((x-xA)*(x-xA) + (y-yA)*(y-yA));


		return new Double[]{x,y,e2};
	}

	private Double[] calculateThreeCircleIntersection(double x1, double y1,
			double r1, double x2, double y2, double r2, double x3, double y3,
			double r3) {

		double x, y, va, vb;

		va = ((r2 * r2 - r3 * r3) - (x2 * x2 - x3 * x3) - (y2 * y2 - y3 * y3)) / 2;
		vb = ((r2 * r2 - r1 * r1) - (x2 * x2 - x1 * x1) - (y2 * y2 - y1 * y1)) / 2;

		y = (vb * (x3 - x2) - va * (x1 - x2))
		/ ((y1 - y2) * (x3 - x2) - (y3 - y2) * (x1 - x2));
		x = (va - y * (y3 - y2)) / (x3 - x2);
		return new Double[] {x, y,0.0};
	}

	private Double[] fourCircles(double x0, double y0, double x1, double y1,
			double x2, double y2, double x3, double y3) {

		Double[] arr = new Double[3];

		double[][] tempArr = { { x0, y0 }, { x1, y1 }, { x2, y2 }, { x3, y3 }, };
		double min = 0.0, temp_x = 0.0, temp_y = 0.0;
		int index = 0;

		for (int i = 0; i < 3; i++) {
			min = tempArr[i][0];
			for (int j = i + 1; j < 4; j++) {
				if (tempArr[j][0] < min) {
					min = tempArr[j][0];
					index = j;
				}
			}
			temp_x = tempArr[i][0];
			temp_y = tempArr[i][1];
			tempArr[i][0] = min;
			tempArr[i][1] = tempArr[index][1];
			tempArr[index][0] = temp_x;
			tempArr[index][1] = temp_y;
		}

		if (tempArr[0][1] > tempArr[3][1]) {
			temp_x = tempArr[0][0];
			temp_y = tempArr[0][1];
			tempArr[0][0] = tempArr[3][0];
			tempArr[0][1] = tempArr[3][1];
			tempArr[3][0] = temp_x;
			tempArr[3][1] = temp_y;
		}

		if (tempArr[2][1] > tempArr[1][1]) {
			temp_x = tempArr[2][0];
			temp_y = tempArr[2][1];
			tempArr[2][0] = tempArr[1][0];
			tempArr[2][1] = tempArr[1][1];
			tempArr[1][0] = temp_x;
			tempArr[1][1] = temp_y;
		}

		for (int i = 0; i < 4; i++) {
			System.out.println(tempArr[i][0] + ", " + tempArr[i][1]);
		}

		/*
		 * x1 = points1[0] = tempArr[0][0] y1 = points1[1] = tempArr[0][1] x2 =
		 * x2 = tempArr[3][0] y2 = y2 = tempArr[3][1] x3 = points2[0] =
		 * tempArr[2][0] y3 = points2[1] = tempArr[2][1] x4 = x1 = tempArr[1][0]
		 * y4 = y1 = tempArr[1][1]
		 */

		double x = (((tempArr[1][0] - tempArr[2][0]) * tempArr[1][1] - (tempArr[1][1] - tempArr[2][1])
				* tempArr[1][0])
				* (tempArr[3][0] - tempArr[0][0]) - ((tempArr[3][0] - tempArr[0][0])
				* tempArr[3][1] - (tempArr[3][1] - tempArr[0][1])
				* tempArr[3][0])
				* (tempArr[0][0] - tempArr[2][0]))
				/ ((tempArr[0][0] - tempArr[2][0])
						* (tempArr[3][1] - tempArr[0][1]) - (tempArr[1][1] - tempArr[2][1])
						* (tempArr[3][0] - tempArr[0][0]));

		double y = ((tempArr[3][0] - tempArr[0][0]) * tempArr[3][1]
				- (tempArr[3][1] - tempArr[0][1]) * tempArr[3][0] + (tempArr[3][1] - tempArr[0][1])
				* x)
				/ (tempArr[3][0] - tempArr[0][0]);

		// System.out.println(x + "\n" + y);

		arr[0] = x;
		arr[1] = y;
		arr[2] = 0.0;

		return arr;
	}

	/*public ArrayList<LocalEvent> manageUser(
			HashMap<String, ArrayList<LocalEvent>> userDetails) {
		Double[] arr = new Double[3];
		ArrayList<LocalEvent> users = new ArrayList<LocalEvent>();
		double x0, y0, r0, x1, y1, r1, x2 = 0, y2 = 0, r2 = 0, x3 = 0, y3 = 0, r3 = 0;
		for (int i = 0; i < userDetails.size(); i++) {
			ArrayList<LocalEvent> userDistances = userDetails.get(i + "");
			Collections.sort(userDistances, new LocalEventComparator());
			LocalEvent user = new LocalEvent();
			user.setUserID(userDistances.get(0).getUserID());

			System.out.println();

			x0 = sensors.get(userDistances.get(0).getSensorId())[0];

			y0 = sensors.get(userDistances.get(0).getSensorId())[1];

			r0 = userDistances.get(0).getDistance();

			if (sensors.size() == 1) {
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

				if (userDistances.size() > 2) {
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
					arr = fourCircles(x0, y0, x1, y1, x2, y2, x3, y3);

				} else if (userDistances.size() == 2) {
					logger.info("There are two sensor");
					arr = nonIntersectingPointsOfTwoCircles(x0, y0, r0, x1, y1,
							r1);

				} else if (isIntersect(x0, y0, r0, x1, y1, r1)
						&& isIntersect(x0, y0, r0, x2, y2, r2)
						&& isIntersect(x2, y2, r2, x1, y1, r1)) {
					logger.info("There are three intersecting circles");
					Double[] cordinates = calculateThreeCircleIntersection(x0,
							y0, r0, x1, y1, r1, x2, y2, r2);
					if (cordinates[2] == 0) {
						logger.info("Cordinate of intersecting points X :"
								+ cordinates[0] + " Y:" + cordinates[1]);
					} else {
						logger.info("one circle is contained in another and cordinates are X:"
								+ cordinates[0] + " Y:" + cordinates[1]);
					}

					arr = cordinates;
				} else {
					logger.info("There are three non intersecting circles");
					arr = nonIntersectingPointsOfThreeCircles(x0, y0, r0, x1,
							y1, r1, x2, y2, r2);

				}
			}
			user.setX(arr[0]);
			user.setY(arr[1]);
			user.setError(arr[2]);
			users.add(user);
		}

		return users;
	}*/

	public LocalEvent manageUser(ArrayList<LocalEvent> userDistances) {
		Double[] arr = new Double[3];

		double x0, y0, r0, x1, y1, r1, x2 = 0, y2 = 0, r2 = 0, x3 = 0, y3 = 0, r3 = 0;

		Collections.sort(userDistances, new LocalEventComparator());
		LocalEvent user = new LocalEvent();
		user.setUserID(userDistances.get(0).getUserID());

		System.out.println();

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

			if (userDistances.size() > 2) {
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
				arr = fourCircles(x0, y0, x1, y1, x2, y2, x3, y3);

			} else if (userDistances.size() == 2) {
				logger.info("There are two sensor");
				arr = nonIntersectingPointsOfTwoCircles(x0, y0, r0, x1, y1, r1);

			} else if (isIntersect(x0, y0, r0, x1, y1, r1)
					&& isIntersect(x0, y0, r0, x2, y2, r2)
					&& isIntersect(x2, y2, r2, x1, y1, r1)) {
				logger.info("There are three intersecting circles");
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
		user.setX(arr[0]);
		user.setY(arr[1]);
		user.setError(arr[2]);
		return user;
	}

	/*public JSONObject getUserLocations(Object obj) throws Exception {
		HashMap<String, ArrayList<LocalEvent>> userDetails = new HashMap<String, ArrayList<LocalEvent>>();
		try {
			JSONObject jsonObject = (JSONObject) obj;
			JSONArray readings = (JSONArray) jsonObject.get("readings");

			Iterator i = readings.iterator();

			// take each value from the json array separately
			while (i.hasNext()) {
				JSONObject innerObj = (JSONObject) i.next();
				String sid = (String) innerObj.get("sid");
				String did = (String) innerObj.get("did");
				double distance = (Double) innerObj.get("distance");
			
				LocalEvent user = new LocalEvent();
				user.setUserID(did);
				user.setSensorId(sid);
				user.setDistance(distance);
				user.setTime(System.currentTimeMillis());

				if (userDetails.containsKey(did)) {
					ArrayList<LocalEvent> users = userDetails.get(did);
					users.add(user);
					userDetails.put(did, users);
				} else {
					ArrayList<LocalEvent> users = new ArrayList<LocalEvent>();
					users.add(user);
					userDetails.put(did, users);
				}

			}
			ArrayList<LocalEvent> users = manageUser(userDetails);
			JSONArray list = new JSONArray();
			for (LocalEvent user : users) {
				JSONObject innerObj = new JSONObject();
				innerObj.put("did", user.getUserID());
				innerObj.put("x", user.getX());
				innerObj.put("y", user.getY());
				innerObj.put("error", user.getError());

				list.add(innerObj);
			}
			JSONObject returnObj = new JSONObject();
			returnObj.put("output", list);
			return returnObj;
		} catch (Exception ex) {
			logger.info(ex + "");
			throw ex;
		}

	}*/
}
