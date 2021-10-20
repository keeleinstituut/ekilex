package eki.ekilex.data;

import eki.common.data.AbstractDataObject;
import eki.ekilex.constant.QueueAction;

public class QueueItem extends AbstractDataObject {

	private static final long serialVersionUID = 1L;

	private QueueAction action;

	private EkiUser user;

	private QueueContent content;

	public QueueAction getAction() {
		return action;
	}

	public void setAction(QueueAction action) {
		this.action = action;
	}

	public EkiUser getUser() {
		return user;
	}

	public void setUser(EkiUser user) {
		this.user = user;
	}

	public QueueContent getContent() {
		return content;
	}

	public void setContent(QueueContent content) {
		this.content = content;
	}

}
