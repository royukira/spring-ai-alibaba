package com.alibaba.cloud.ai.example.deepresearch.node;

import com.alibaba.cloud.ai.example.deepresearch.model.dto.Plan;
import com.alibaba.cloud.ai.example.deepresearch.service.ReportService;
import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import com.alibaba.cloud.ai.graph.streaming.StreamingChatGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;

import java.util.*;

public class InteractiveHtmlNode implements NodeAction {

	private static final Logger logger = LoggerFactory.getLogger(ReporterNode.class);

	private final ChatClient interactionAgent;

	private final ReportService reportService;

	public InteractiveHtmlNode(ChatClient interactionAgent, ReportService reportService) {
		this.interactionAgent = interactionAgent;
		this.reportService = reportService;
	}

	@Override
	public Map<String, Object> apply(OverAllState state) throws Exception {
		logger.info("reporter node is running.");
		Plan currentPlan = state.value("current_plan", Plan.class)
			.orElseThrow(() -> new IllegalArgumentException("current_plan is missing"));

		// 从 OverAllState 中获取线程ID
		String threadId = state.value("thread_id", String.class)
			.orElseThrow(() -> new IllegalArgumentException("thread_id is missing from state"));

		logger.info("Thread ID from state: {}", threadId);

		// 1. 添加消息
		List<Message> messages = new ArrayList<>();
		String reportInfo = reportService.getReport(threadId);
		messages.add(new UserMessage("请根据以下信息生成HTML报告：\n" + reportInfo));

		// 使用ChatClient来构建HTML报告
		var streamResult = interactionAgent.prompt().messages(messages).stream().chatResponse();
		var generator = StreamingChatGenerator.builder()
			.startingNode("reporter_llm_stream")
			.startingState(state)
			.mapResult(response -> {
				String htmlReport = Objects.requireNonNull(response.getResult().getOutput().getText());
				return Map.of("html_report", htmlReport, "thread_id", threadId);
			})
			.build(streamResult);
		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("html_report", generator);
		resultMap.put("thread_id", threadId);
		return resultMap;
	}

}
