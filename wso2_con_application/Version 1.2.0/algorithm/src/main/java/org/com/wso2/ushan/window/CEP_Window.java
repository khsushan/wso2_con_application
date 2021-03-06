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
    private Map<String,ArrayList<LocalEvent>> localEvents = null;
    private Map<String,ArrayList<LocalEvent>> calculatedEvents = null;


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



        variableUID = ((Variable) parameters[1]).getAttributeName();

        variableSID = ((Variable) parameters[0]).getAttributeName();

        variableDISTANCE = ((Variable) parameters[2]).getAttributeName();


        localEvents = new HashMap<String,ArrayList<LocalEvent>>();

        calculatedEvents = new HashMap<String, ArrayList<LocalEvent>>();


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

        if (localEvents.containsKey(uid)) {
            ArrayList<LocalEvent> sensorDetails = localEvents.get(uid);
            if(!sensorDetails.contains(localEvent)){
               sensorDetails.add(localEvent);
            }else{
               sensorDetails.remove(localEvent);
               sensorDetails.add(localEvent);
            }
            log.info("Sensor detail size "+sensorDetails.size());
            if(sensorDetails.size() >= 3){
                LocalEvent calculatedEvent = Algoritham.manageUser(sensorDetails);
                localEvents.remove(uid);
                if(calculatedEvents.containsKey(uid)){
                    ArrayList<LocalEvent>  localCalculatedEventList = calculatedEvents.get(uid);
                    localCalculatedEventList.add(calculatedEvent);
                    log.info("Calculate event size "+localCalculatedEventList.size());
                    if(localCalculatedEventList.size() >= 3){

                        Double[] cordinates = Algoritham.calculateMidPoint(localCalculatedEventList);
                        Object[] objects = {uid,cordinates[0],cordinates[1]};
                        calculatedEvents.remove(uid);
                        InEvent inEvent = new InEvent("outputstream",
                                System.currentTimeMillis(), objects);
                        nextProcessor.process(inEvent);
                    }
                }else{
                    ArrayList<LocalEvent>  localCalculatedEventList = new ArrayList<LocalEvent>();
                    localCalculatedEventList.add(calculatedEvent);
                    calculatedEvents.put(uid,localCalculatedEventList);
                }
            }
        } else {
            ArrayList<LocalEvent> sensorDetails =  new  ArrayList<LocalEvent>();
            sensorDetails.add(localEvent);
            localEvents.put(uid,sensorDetails);
        }
    }


}
