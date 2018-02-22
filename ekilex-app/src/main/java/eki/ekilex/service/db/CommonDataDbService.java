package eki.ekilex.service.db;

import static eki.ekilex.data.db.Tables.DATASET;
import static eki.ekilex.data.db.Tables.LANG;

import java.util.Map;

import org.jooq.DSLContext;
import org.jooq.Record2;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CommonDataDbService {

	@Autowired
	private DSLContext create;

	public Map<String, String> getDatasetNameMap() {
		return create.select().from(DATASET).fetchMap(DATASET.CODE, DATASET.NAME);
	}

	public Result<Record2<String, String>> getDatasets() {
		return create.select(DATASET.CODE, DATASET.NAME).from(DATASET).fetch();
	}

	public Map<String, String> getLanguagesMap() {
		return create.select().from(LANG).fetchMap(LANG.CODE, LANG.VALUE);
	}

	public Result<Record2<String, String>> getLanguages() {
		return create.select(LANG.CODE, LANG.VALUE).from(LANG).fetch();
	}

}
