package eki.ekilex.service.db;

import static eki.ekilex.data.db.main.Tables.DATA_REQUEST;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DataRequestDbService {

	@Autowired
	protected DSLContext mainDb;

	public void createDataRequest(Long userId, String requestKey, String content) {

		mainDb
				.insertInto(DATA_REQUEST, DATA_REQUEST.USER_ID, DATA_REQUEST.REQUEST_KEY, DATA_REQUEST.CONTENT)
				.values(userId, requestKey, content)
				.execute();
	}

	public String getDataRequestContent(String requestKey) {
		return mainDb
				.select(DATA_REQUEST.CONTENT)
				.from(DATA_REQUEST)
				.where(DATA_REQUEST.REQUEST_KEY.eq(requestKey))
				.fetchOptionalInto(String.class).orElse(null);
	}

	public void accessRequestContent(String requestKey) {
		mainDb
				.update(DATA_REQUEST)
				.set(DATA_REQUEST.ACCESSED, DSL.currentTimestamp())
				.where(DATA_REQUEST.REQUEST_KEY.eq(requestKey))
				.execute();
	}
}
