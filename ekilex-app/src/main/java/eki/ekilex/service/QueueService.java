package eki.ekilex.service;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import eki.ekilex.constant.QueueAction;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.data.EkiUser;
import eki.ekilex.data.QueueContent;
import eki.ekilex.data.QueueItem;
import eki.ekilex.data.QueueStat;

@Component
public class QueueService implements InitializingBean, SystemConstant {

	@Autowired
	private QueueHandlerService queueHandlerService;

	private Queue<QueueItem> queueItems;

	@Override
	public void afterPropertiesSet() throws Exception {
		queueItems = new ArrayDeque<>();

	}

	public List<QueueStat> getQueueStats() {
		if (CollectionUtils.isEmpty(queueItems)) {
			return null;
		}
		List<QueueStat> queueStats = queueItems.stream()
				.collect(Collectors.groupingBy(QueueItem::getGroupId, Collectors.counting()))
				.entrySet().stream().map(entry -> new QueueStat(entry.getKey(), entry.getValue()))
				.collect(Collectors.toList());
		return queueStats;
	}

	public synchronized void queue(List<QueueItem> queueItems) {
		for (QueueItem queueItem : queueItems) {
			queue(queueItem);
		}
	}

	public synchronized void queue(QueueItem queueItem) {
		if (queueItems.contains(queueItem)) {
			return;
		}
		queueItems.add(queueItem);
	}

	@Scheduled(initialDelay = 30000, fixedDelay = EXECUTE_QUEUE_DELAY_1_SEC)
	public void executeQueue() throws Exception {

		if (queueItems.isEmpty()) {
			return;
		}

		QueueItem queueItem = queueItems.poll();
		QueueAction action = queueItem.getAction();
		EkiUser user = queueItem.getUser();
		QueueContent content = queueItem.getContent();

		if (QueueAction.TERM_SEARCH_RESULT_EMAIL.equals(action)) {
			queueHandlerService.handleTermSearchResultSerialisation(user, content);
		} else if (QueueAction.FEDTERM_UPLOAD.equals(action)) {
			queueHandlerService.handleFedTermUpload(user, content);
		}
	}
}
