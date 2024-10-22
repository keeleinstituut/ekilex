package eki.ekilex.service.db.api;

import static eki.ekilex.data.db.main.Tables.API_ERROR_COUNT;
import static eki.ekilex.data.db.main.Tables.API_REQUEST_COUNT;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ApiStatDbService {

	@Autowired
	private DSLContext mainDb;

	public void createOrUpdateRequestCount(String authName, String genericPath) {

		int updateCount = mainDb
				.update(API_REQUEST_COUNT)
				.set(API_REQUEST_COUNT.COUNT, API_REQUEST_COUNT.COUNT.plus(1))
				.where(
						API_REQUEST_COUNT.AUTH_NAME.eq(authName)
								.and(API_REQUEST_COUNT.GENERIC_PATH.eq(genericPath)))
				.execute();

		if (updateCount == 0) {

			mainDb
					.insertInto(
							API_REQUEST_COUNT,
							API_REQUEST_COUNT.AUTH_NAME,
							API_REQUEST_COUNT.GENERIC_PATH,
							API_REQUEST_COUNT.COUNT)
					.values(
							authName,
							genericPath,
							1L)
					.execute();
		}
	}

	public void createOrUpdateErrorCount(String authName, String genericPath, String message) {

		int updateCount = mainDb
				.update(API_ERROR_COUNT)
				.set(API_ERROR_COUNT.COUNT, API_ERROR_COUNT.COUNT.plus(1))
				.where(
						API_ERROR_COUNT.AUTH_NAME.eq(authName)
								.and(API_ERROR_COUNT.GENERIC_PATH.eq(genericPath))
								.and(API_ERROR_COUNT.MESSAGE.eq(message)))
				.execute();

		if (updateCount == 0) {

			mainDb
					.insertInto(
							API_ERROR_COUNT,
							API_ERROR_COUNT.AUTH_NAME,
							API_ERROR_COUNT.GENERIC_PATH,
							API_ERROR_COUNT.MESSAGE,
							API_ERROR_COUNT.COUNT)
					.values(
							authName,
							genericPath,
							message,
							1L)
					.execute();
		}
	}
}
