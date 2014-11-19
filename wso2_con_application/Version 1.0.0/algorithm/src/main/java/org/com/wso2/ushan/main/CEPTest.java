package org.com.wso2.ushan.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import org.junit.Before;
import org.junit.Test;
import org.wso2.siddhi.core.SiddhiManager;
import org.wso2.siddhi.core.config.SiddhiConfiguration;
import org.wso2.siddhi.core.event.Event;
import org.wso2.siddhi.core.query.output.callback.QueryCallback;
import org.wso2.siddhi.core.stream.input.InputHandler;
import org.wso2.siddhi.core.util.EventPrinter;

public class CEPTest {

	private int count;
	private double betaZero;
	private static HashMap<String, Double[]> sensors;

	@Before
	public void init() {
		count = 0;
	}

	@Test
	public void testRegression() throws InterruptedException {

		SiddhiConfiguration siddhiConfiguration = new SiddhiConfiguration();

		List<Class> list = new ArrayList<Class>();
		list.add(org.com.wso2.ushan.window.CEP_Window.class);

		siddhiConfiguration.setSiddhiExtensions(list);
		SiddhiManager siddhiManager = new SiddhiManager(siddhiConfiguration);

		siddhiManager
				.defineStream("define stream cseEventStream (sid string, did string,distance double)");
		String queryReference = siddhiManager
				.addQuery("from cseEventStream#window.custom:getCordinate(sid,did,distance)"
						+ "insert into outputst ;");
		siddhiManager.addCallback(queryReference, new QueryCallback() {
			@Override
			public void receive(long timeStamp, Event[] inEvents,
					Event[] removeEvents) {
				EventPrinter.print(timeStamp, inEvents, removeEvents);
			}
		});

		sensors = new HashMap<String, Double[]>();
		sensors.put("01", new Double[] { 2.79,4.4});
		sensors.put("02", new Double[] { 9.14, 2.0});
		sensors.put("03", new Double[] {14.44,4.4});

		Random random = new Random();

		InputHandler inputHandler = siddhiManager
				.getInputHandler("cseEventStream");

		for (int i = 0; i < 5; i++) {
			String did = String.valueOf(i);

			double x = random.nextInt(10);
			double y = random.nextInt(10);
			System.out.println(i + "Users real location x: " + x + "y: " + y);
			HashMap<String, Double> map = new HashMap<String, Double>();
			map = generateUserCordinate(x, y);

			for (int j = 0; j < map.size(); j++) {
				inputHandler.send(new Object[] { "0" + String.valueOf(j + 1),
						String.valueOf(i), map.get("0" + (j + 1)) });
			}
		}

		Thread.sleep(1500);
		siddhiManager.shutdown();
	}

	private static HashMap<String, Double> generateUserCordinate(double x,
			double y) {
		double dx, dy, r = 0.0;
		HashMap<String, Double> map = new HashMap<String, Double>();

		for (int i =1; i < (sensors.size()+1); i++) {
			Double[] doubles = sensors.get("0" + i);
			dx = x - doubles[0];
			dy = y - doubles[1];
			r = Math.sqrt((dx * dx) + (dy * dy));
			map.put("0" + i, r);
		}

		return map;
	}

}
