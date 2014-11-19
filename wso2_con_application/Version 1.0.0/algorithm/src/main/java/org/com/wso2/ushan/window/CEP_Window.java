package org.com.wso2.ushan.window;

import org.com.wso2.ushan.algorithm.Algoritham;
import org.com.wso2.ushan.bean.LocalEvent;
import org.com.wso2.ushan.comparator.LocalEventComparator;
import org.wso2.siddhi.core.config.SiddhiContext;
import org.wso2.siddhi.core.event.StreamEvent;
import org.wso2.siddhi.core.event.in.InEvent;
import org.wso2.siddhi.core.event.in.InListEvent;
import org.wso2.siddhi.core.query.QueryPostProcessingElement;
import org.wso2.siddhi.core.query.processor.window.WindowProcessor;
import org.wso2.siddhi.query.api.definition.AbstractDefinition;
import org.wso2.siddhi.query.api.expression.Expression;
import org.wso2.siddhi.query.api.expression.Variable;
import org.wso2.siddhi.query.api.extension.annotation.SiddhiExtension;

import java.util.*;

@SiddhiExtension(namespace = "custom", function = "getCordinate")
public class CEP_Window extends WindowProcessor {

    String variableUID = "";
    String variableSID = "";
    String variableDISTANCE = "";

    int variableUIDPosition = 0;
    int variableSIDPosition = 0;
    int variableDISPosition = 0;
    int noValue = 0;
    private Map<String, ArrayList<LocalEvent>> userDetails = null;

    private Map<String, ArrayList<LocalEvent>> sensorDetails = null;

    public void destroy() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void processEvent(InEvent event) {
        try {
            log.info("calling doprocess");
            doProcess(event);
        } finally {
            releaseLock();
        }
    }

    @Override
    protected void processEvent(InListEvent listEvent) {
        for (int i = 0; i < listEvent.getActiveEvents(); i++) {
            InEvent inEvent = (InEvent) listEvent.getEvent(i);
            log.info("calling process event");
            processEvent(inEvent);
        }

    }

    @Override
    public Iterator<StreamEvent> iterator() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Iterator<StreamEvent> iterator(String predicate) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Object[] currentState() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected void restoreState(Object[] data) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void init(Expression[] parameters,
                        QueryPostProcessingElement nextProcessor,
                        AbstractDefinition streamDefinition, String elementId,
                        boolean async, SiddhiContext siddhiContext) {
        log.info("inisialise attribute one");
        variableUID = ((Variable) parameters[1]).getAttributeName();
        log.info("inisialise attribute two");
        variableSID = ((Variable) parameters[0]).getAttributeName();
        log.info("inisialise attribute three");
        variableDISTANCE = ((Variable) parameters[2]).getAttributeName();
        // noValue = ((IntConstant) parameters[1]).getValue();

        userDetails = new HashMap<String, ArrayList<LocalEvent>>();


        sensorDetails = new HashMap<String, ArrayList<LocalEvent>>();

        log.info("getting first attribute position");
        variableUIDPosition = streamDefinition
                .getAttributePosition(variableUID);
        variableSIDPosition = streamDefinition
                .getAttributePosition(variableSID);
        variableDISPosition = streamDefinition
                .getAttributePosition(variableDISTANCE);

    }

    private void doProcess(InEvent event) {
        event.getData(variableUIDPosition);
        event.getData(variableSIDPosition);
        event.getData(variableDISPosition);


        String uid = (String) event.getData(variableUIDPosition);
        String sid = (String) event.getData(variableSIDPosition);
        Double distance = (Double) event.getData(variableDISPosition);

        String logVal = sid + "," + uid + "," + distance;
        log.info(logVal);

        LocalEvent user = new LocalEvent();
        user.setDistance(distance);
        user.setSensorId(sid);
        user.setUserID(uid);

        user.setTime(System.currentTimeMillis());
        String sensorKey = user.getUserID() + "" + user.getSensorId();

        if (sensorDetails.containsKey(sensorKey)) {

            ArrayList<LocalEvent> distances = sensorDetails.get(sensorKey);
            distances.add(user);
            sensorDetails.put(sensorKey, distances);
            long difference = user.getTime()
                    - sensorDetails.get(sensorKey).get(0).getTime();
            if (difference >= 3000) {
                double medean = getMeadean(distances);
                user.setDistance(medean);
                sensorDetails.remove(sensorKey);

                if (userDetails.containsKey(uid)) {
                    ArrayList<LocalEvent> users = userDetails.get(uid);
                    if (!users.contains(user)) {
                        users.add(user);
                    } else {
                        users.remove(user);
                        users.add(user);
                    }

                    if (users.size() == 3) {
                        userDetails.remove(uid);
                        LocalEvent us = new Algoritham().manageUser(users);

                        Object[] objects = {us.getUserID(), us.getX(),
                                us.getY()};
                        log.info(event);
                        InEvent inEvent = new InEvent("outputstream",
                                System.currentTimeMillis(), objects);

                        nextProcessor.process(inEvent);

                    }
                } else {
                    ArrayList<LocalEvent> users = new ArrayList<LocalEvent>();
                    users.add(user);
                    userDetails.put(uid, users);
                }

            }

        } else {
            ArrayList<LocalEvent> lists = new ArrayList<LocalEvent>();
            lists.add(user);
            sensorDetails.put(sensorKey, lists);
        }

    }

    private double calculateAvarage(ArrayList<LocalEvent> distances) {
        double total = 0;
        for (LocalEvent userDistance : distances) {
            total += userDistance.getDistance();
        }
        return (total / distances.size());
    }

    private double getLawestDistance(ArrayList<LocalEvent> distances) {
        double lawestDistance = 0;
        lawestDistance = distances.get(0).getDistance();
        for (LocalEvent userDistance : distances) {
            if (lawestDistance > userDistance.getDistance()) {
                lawestDistance = userDistance.getDistance();

            }
        }
        return lawestDistance;
    }


    private double getMeadean(ArrayList<LocalEvent> distances) {
        double medean = 0;
        Collections.sort(distances, new LocalEventComparator());
        if (distances.size() % 2 == 0) {
            int medeanPosition1 = (distances.size() / 2) - 1;
            int medeanPosition2 = medeanPosition1 + 1;
            medean = (distances.get(medeanPosition1).getDistance() + distances.get(medeanPosition2).getDistance()) / 2;


        } else {
            int medeanPosition = (distances.size() / 2) - 1;
            medean = distances.get(medeanPosition).getDistance();
        }
        return medean;

    }

}
