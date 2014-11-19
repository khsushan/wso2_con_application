package org.com.wso2.ushan.function;

import java.util.Iterator;

import org.wso2.siddhi.core.config.SiddhiContext;
import org.wso2.siddhi.core.event.StreamEvent;
import org.wso2.siddhi.core.event.in.InEvent;
import org.wso2.siddhi.core.event.in.InListEvent;
import org.wso2.siddhi.core.query.QueryPostProcessingElement;
import org.wso2.siddhi.core.query.processor.window.WindowProcessor;
import org.wso2.siddhi.query.api.definition.AbstractDefinition;
import org.wso2.siddhi.query.api.expression.Expression;
import org.wso2.siddhi.query.api.extension.annotation.SiddhiExtension;



@SiddhiExtension(namespace = "cep_function", function = "getCordinate")
public class CEP_Function extends WindowProcessor{

	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void processEvent(InEvent event) {
		
		
	}

	@Override
	protected void processEvent(InListEvent listEvent) {
		// TODO Auto-generated method stub
		
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
		// TODO Auto-generated method stub
		
	}

	
	
	

}
