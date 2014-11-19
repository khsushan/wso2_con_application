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

import javax.swing.plaf.synth.SynthTextAreaUI;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@SiddhiExtension(namespace = "custom", function = "getCordinate")
public class CEP_Window extends WindowProcessor {

    String variableUID = "";
    String variableSID = "";
    String variableDISTANCE = "";

    int variableUIDPosition = 0;
    int variableSIDPosition = 0;
    int variableDISPosition = 0;
    private Map<String, Map<String, List<Double>>> localEvents = null;
    private Map<String, Long> times = null;


    public void destroy() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void processEvent(InEvent event) {
        acquireLock();
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

        variableUID = ((Variable) parameters[1]).getAttributeName();

        variableSID = ((Variable) parameters[0]).getAttributeName();

        variableDISTANCE = ((Variable) parameters[2]).getAttributeName();


        localEvents = new ConcurrentHashMap<String, Map<String, List<Double>>>();
        times = new HashMap<String, Long>();

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

        LocalEvent localEvent = new LocalEvent();
        localEvent.setDistance(distance);
        localEvent.setSensorId(sid);
        localEvent.setUserID(uid);

        localEvent.setTime(System.currentTimeMillis());
        //String keyValue = uid + ":" + sid;


        if (localEvents.containsKey(uid)) {

            Map<String, List<Double>> userDetails = localEvents.get(uid);
            if (userDetails.containsKey(sid)) {
                userDetails.get(sid).add(localEvent.getDistance());
                double difference = localEvent.getTime() - times.get(uid);
                if (difference >= 5000 && userDetails.size() >= 3) {
                    Object[] objects = calculateGatheredDetails(uid, userDetails);
                    localEvents.remove(uid);
                    InEvent inEvent = new InEvent("outputstream",
                            System.currentTimeMillis(), objects);
                    nextProcessor.process(inEvent);
                }

            } else {
                ArrayList<Double> eventsDetails = new ArrayList<Double>();
                eventsDetails.add(localEvent.getDistance());
                userDetails.put(sid, eventsDetails);


            }

        } else {
            Map<String, List<Double>> sensorDetails = new HashMap<String, List<Double>>();
            List<Double> distances = new ArrayList<Double>();
            distances.add(localEvent.getDistance());
            sensorDetails.put(sid, distances);
            times.put(uid, System.currentTimeMillis());
            localEvents.put(uid, sensorDetails);
        }


    }

    //
    private Object[] calculateGatheredDetails(String userID, Map<String, List<Double>> sensorDetails) {
        ArrayList<LocalEvent> sortedEvents = new ArrayList<LocalEvent>();
        for (Map.Entry entry : sensorDetails.entrySet()) {
            LocalEvent event;
            Double[] toBeSorted = ((List<Double>) entry.getValue()).toArray(new Double[((List<Double>) entry.getValue()).size()]);
            Arrays.sort(toBeSorted);
            if (toBeSorted.length % 2 == 0) {
                int middle = toBeSorted.length / 2;
                double medean = (toBeSorted[middle] + toBeSorted[middle - 1]) / 2;
                event = new LocalEvent(userID, (String) entry.getKey(), medean);
            } else {
                event = new LocalEvent(userID, (String) entry.getKey(), toBeSorted[(toBeSorted.length - 1) / 2]);
            }
            sortedEvents.add(event);
        }

        LocalEvent localEvent = Algoritham.manageUser(sortedEvents);

        log.info("location of " + "UserID : " + localEvent.getUserID() + " x: " + localEvent.getX() + " y: " + localEvent.getY());

        return new Object[]{localEvent.getUserID(), localEvent.getX(), localEvent.getY()};

    }

    private double getMeadean(ArrayList<LocalEvent> distances) {
        double medean = 0;
        Collections.sort(distances, new LocalEventComparator());
        if (distances.size() % 2 == 0) {
            int medeanPosition1 = (distances.size() / 2) - 1;
            int medeanPosition2 = medeanPosition1 + 1;
            medean = (distances.get(medeanPosition1).getDistance() + distances.get(medeanPosition2).getDistance()) / 2;
        } else {
            int medeanPosition = (distances.size() / 2);
            medean = distances.get(medeanPosition).getDistance();
        }
        return medean;

    }

}
