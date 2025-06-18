package eki.wordweb.service.db;

import static eki.wordweb.data.db.Tables.MVIEW_WW_DATASET_WORD_MENU;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DatasetContentDbService {

	@Autowired
	private DSLContext create;

	public List<Character> getDatasetFirstLetters(String datasetCode) {

		return create
				.select(MVIEW_WW_DATASET_WORD_MENU.FIRST_LETTER)
				.from(MVIEW_WW_DATASET_WORD_MENU)
				.where(MVIEW_WW_DATASET_WORD_MENU.DATASET_CODE.eq(datasetCode))
				.orderBy(MVIEW_WW_DATASET_WORD_MENU.FIRST_LETTER)
				.fetchInto(Character.class);
	}

	public List<String> getDatasetWords(String datasetCode, Character firstLetter) {

		String[] wordValues = create
				.select(MVIEW_WW_DATASET_WORD_MENU.WORD_VALUES)
				.from(MVIEW_WW_DATASET_WORD_MENU)
				.where(
						MVIEW_WW_DATASET_WORD_MENU.DATASET_CODE.eq(datasetCode)
						.and(MVIEW_WW_DATASET_WORD_MENU.FIRST_LETTER.eq(String.valueOf(firstLetter)))
				)
				.fetchOne(record -> (String[]) record.into(Object.class));

		if (ArrayUtils.isEmpty(wordValues)) {
			return Collections.emptyList();
		}
		return Arrays.asList(wordValues);
	}
}
