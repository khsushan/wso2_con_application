package org.com.wso2.ushan.main;

import org.com.wso2.ushan.algorithm.Algoritham;
import org.com.wso2.ushan.bean.LocalEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.logging.Logger;


public class Test {

    private static HashMap<String, Double[]> sensors;
    private static Algoritham algoritham;
    private static double user = 0;
    private static Logger logger;

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        logger = Logger.getLogger("LOG");
        sensors = new HashMap<String, Double[]>();
        sensors.put("000", new Double[]{0.0, 0.0});
        sensors.put("001", new Double[]{0.0, 100.0});
        sensors.put("002", new Double[]{0.0, 200.0});
        sensors.put("003", new Double[]{180.0, 0.0});
        sensors.put("004", new Double[]{180.0, 100.0});
        sensors.put("005", new Double[]{180.0, 200.0});
        algoritham = new Algoritham();
        //createUser();

    }

    private static ArrayList<LocalEvent> generateUserCordinate() {
        double dx, dy;
        Random random = new Random();
        double x = random.nextInt(200);
        double y = random.nextInt(200);
        logger.info(" user " + user + " real cordinate x:" + x + "y:" + y);
        ArrayList<LocalEvent> userLength = new ArrayList<LocalEvent>();
        double r = 0;

        for (int i = 0; i < sensors.size(); i++) {
            Double[] doubles = sensors.get("00" + i);
            LocalEvent usr = new LocalEvent();
            usr.setUserID(user + "");
            dx = x - doubles[0];
            dy = y - doubles[1];
            r = Math.sqrt((dx * dx) + (dy * dy));
            usr.setDistance(r);
            usr.setSensorId("00" + i);
            usr.setX(x);
            usr.setY(y);
            userLength.add(usr);

        }

        return userLength;

    }


    /*private static void createUser() {

        //while (true) {

        try {
            HashMap<String, ArrayList<LocalEvent>> users = new HashMap<String, ArrayList<LocalEvent>>();
            for (int i = 0; i < 5; i++) {
                //System.out.print(i+" user ");
                user = i;
                users.put(i + "", generateUserCordinate());


            }
            algoritham.manageUser(users);
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //}
    }*/

}
