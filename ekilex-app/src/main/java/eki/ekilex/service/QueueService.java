package eki.ekilex.service;

import java.util.Stack;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import eki.ekilex.constant.QueueAction;
import eki.ekilex.constant.SystemConstant;
import eki.ekilex.data.EkiUser;
import eki.ekilex.data.QueueContent;
import eki.ekilex.data.QueueItem;

@Component
public class QueueService implements InitializingBean, SystemConstant {

	@Autowired
	private AsyncQueueHandlerService asyncQueueHandlerService;

	private Stack<QueueItem> queueItems;

	@Override
	public void afterPropertiesSet() throws Exception {
		queueItems = new Stack<>();

	}

	public synchronized void queue(QueueItem queueItem) {
		queueItems.push(queueItem);
	}

	@Scheduled(initialDelay = 60000, fixedDelay = EXECUTE_QUEUE_DELAY_10_SEC)
	public void executeQueue() throws Exception {

		if (queueItems.empty()) {
			return;
		}

		QueueItem queueItem = queueItems.pop();
		QueueAction action = queueItem.getAction();
		EkiUser user = queueItem.getUser();
		QueueContent content = queueItem.getContent();

		if (QueueAction.TERM_SEARCH_RESULT_EMAIL.equals(action)) {
			asyncQueueHandlerService.handleTermSearchResultSerialisation(user, content);
		}
	}
}
