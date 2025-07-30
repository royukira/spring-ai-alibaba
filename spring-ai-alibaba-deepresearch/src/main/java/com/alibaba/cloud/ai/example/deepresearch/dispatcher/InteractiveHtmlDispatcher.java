package com.alibaba.cloud.ai.example.deepresearch.dispatcher;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.EdgeAction;

import static com.alibaba.cloud.ai.graph.StateGraph.END;

public class InteractiveHtmlDispatcher implements EdgeAction {

	@Override
	public String apply(OverAllState state) {
		return (String) state.value("reporter_next_node", END);
	}

}
