package org.com.wso2.ushan.comparator;


import org.com.wso2.ushan.bean.LocalEvent;

import java.util.Comparator;

public class LocalEventComparator implements Comparator<LocalEvent> {

    public int compare(LocalEvent o1, LocalEvent o2) {
        if (o1.getDistance() > o2.getDistance()) {
            return 1;
        } else if (o1.getDistance() < o2.getDistance()) {
            return -1;
        }
        return 0;
    }

}
