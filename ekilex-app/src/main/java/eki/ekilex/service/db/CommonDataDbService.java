package eki.ekilex.service.db;

import static eki.ekilex.data.db.Tables.DATASET;

import java.util.Map;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CommonDataDbService {

	@Autowired
	private DSLContext create;

	public Map<String, String> getDatasetNameMap() {
		return create.select().from(DATASET).fetchMap(DATASET.CODE, DATASET.NAME);
	}
}
